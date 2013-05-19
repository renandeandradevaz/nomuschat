package nomuschat.interceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import nomuschat.anotacoes.Funcionalidade;
import nomuschat.anotacoes.Public;
import nomuschat.controller.LoginController;
import nomuschat.hibernate.HibernateUtil;
import nomuschat.modelo.Usuario;
import nomuschat.sessao.SessaoUsuario;
import nomuschat.util.Util;

import org.hibernate.criterion.MatchMode;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.ValidationMessage;

@Intercepts
public class InterceptadorDeAutorizacao implements Interceptor {

	private final SessaoUsuario sessaoUsuario;
	private Result result;
	private HttpServletRequest request;
	private HibernateUtil hibernateUtil;
	private static HashMap<String, String> usuariosLogados;

	public InterceptadorDeAutorizacao(SessaoUsuario sessaoUsuario, Result result, HttpServletRequest request, HibernateUtil hibernateUtil) {
		this.sessaoUsuario = sessaoUsuario;
		this.result = result;
		this.request = request;
		this.hibernateUtil = hibernateUtil;
	}

	public boolean accepts(ResourceMethod method) {

		boolean contemAnotacaoPublic = !method.containsAnnotation(Public.class);

		return contemAnotacaoPublic;

	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {

		if (Util.vazio(sessaoUsuario.getUsuario())) {

			String login = request.getParameter("login");
			String senha = request.getParameter("senha");

			if (Util.vazio(login) || Util.vazio(senha)) {

				usuarioNaoLogadoNoSistema();
				return;
			}

			Usuario usuario = new Usuario();
			usuario.setLogin(login);
			usuario.setSenha(senha);

			try {

				usuario = hibernateUtil.selecionar(usuario, MatchMode.EXACT);

				if (Util.vazio(usuario)) {

					loginOuSenhaIncorretos();
				}

				else {

					sessaoUsuario.login(usuario);

					if (usuariosLogados == null) {

						usuariosLogados = new HashMap<String, String>();
					}

					usuariosLogados.put(usuario.getLogin(), "");

					if (possuiPermissao(stack, method, resourceInstance, usuario)) {

						stack.next(method, resourceInstance);
					}

					else {

						permissaoNegada();
						return;
					}
				}
			}

			catch (Exception e) {

				usuarioNaoLogadoNoSistema();
			}
		}

		else {

			if (possuiPermissao(stack, method, resourceInstance, sessaoUsuario.getUsuario())) {

				stack.next(method, resourceInstance);
			}

			else {

				permissaoNegada();
				return;
			}
		}
	}

	private boolean possuiPermissao(InterceptorStack stack, ResourceMethod method, Object resourceInstance, Usuario usuario) {

		Method metodo = method.getMethod();

		if (metodo.isAnnotationPresent(Funcionalidade.class)) {

			Funcionalidade anotacao = metodo.getAnnotation(Funcionalidade.class);

			if (Util.preenchido(anotacao.administrativa())) {

				if (usuario.getAdministrador()) {

					return true;
				}

				else {

					return false;
				}
			}
		}

		return false;
	}

	private void usuarioNaoLogadoNoSistema() {

		hibernateUtil.fecharSessao();

		result.include("errors", Arrays.asList(new ValidationMessage("O usuario não está logado no sistema", "Erro")));
		result.redirectTo(LoginController.class).telaLogin();
	}

	private void loginOuSenhaIncorretos() {

		hibernateUtil.fecharSessao();
		result.include("errors", Arrays.asList(new ValidationMessage("Login ou senha incorretos", "Erro")));
		result.redirectTo(LoginController.class).telaLogin();
	}

	private void permissaoNegada() {

		hibernateUtil.fecharSessao();
		result.redirectTo(LoginController.class).permissaoNegada();
	}

	public static HashMap<String, String> getUsuariosLogados() {

		if (usuariosLogados == null) {

			usuariosLogados = new HashMap<String, String>();
		}

		return usuariosLogados;
	}
}
