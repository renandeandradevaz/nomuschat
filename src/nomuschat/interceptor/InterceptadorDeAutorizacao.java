package nomuschat.interceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import nomuschat.anotacoes.Funcionalidade;
import nomuschat.anotacoes.Public;
import nomuschat.controller.LoginController;
import nomuschat.hibernate.HibernateUtil;
import nomuschat.modelo.Empresa;
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
	private static HashMap<String, Usuario> usuariosLogados;

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

			String login = request.getParameter("loginNomusChat");
			String senha = request.getParameter("senhaNomusChat");
			String nome = request.getParameter("nomeNomusChat");
			String nomeEmpresa = request.getParameter("nomeEmpresaNomusChat");

			if (Util.vazio(login) || Util.vazio(senha) || Util.vazio(nomeEmpresa)) {

				usuarioNaoLogadoNoSistema();
				return;
			}

			Usuario usuarioLogado = null;

			Empresa empresa = new Empresa();
			empresa.setNome(nomeEmpresa);

			if (this.hibernateUtil.contar(empresa, MatchMode.EXACT) == 0) {

				this.hibernateUtil.salvarOuAtualizar(empresa);

				Usuario usuario = new Usuario();
				usuario.setLogin(login);
				usuario.setSenha(senha);
				usuario.setNome(nome);
				usuario.setAdministrador(false);
				usuario.setEmpresa(empresa);

				this.hibernateUtil.salvarOuAtualizar(usuario);

				usuarioLogado = usuario;
			}

			else {

				empresa = this.hibernateUtil.selecionar(empresa);

				Usuario usuario = new Usuario();
				usuario.setLogin(login);
				usuario.setEmpresa(empresa);

				Usuario usuarioBanco = this.hibernateUtil.selecionar(usuario, MatchMode.EXACT);

				if (Util.vazio(usuarioBanco)) {

					usuario.setSenha(senha);
					usuario.setNome(nome);
					usuario.setAdministrador(false);
					this.hibernateUtil.salvarOuAtualizar(usuario);
				}

				else {

					if (!usuarioBanco.getSenha().equals(senha)) {

						result.redirectTo(LoginController.class).telaLogin();
						return;
					}
				}

				usuarioLogado = usuario;
			}

			this.sessaoUsuario.login((Usuario) usuarioLogado);

			if (usuariosLogados == null) {

				usuariosLogados = new HashMap<String, Usuario>();
			}

			usuariosLogados.put(usuarioLogado.getKeyEmpresaUsuario(), usuarioLogado);

			if (possuiPermissao(stack, method, resourceInstance, usuarioLogado)) {

				stack.next(method, resourceInstance);
			}

			else {

				permissaoNegada();
				return;
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

			else
				return true;
		}

		return false;
	}

	private void usuarioNaoLogadoNoSistema() {

		result.include("errors", Arrays.asList(new ValidationMessage("O usuario não está logado no sistema", "Erro")));
		result.redirectTo(LoginController.class).telaLogin();
	}

	private void permissaoNegada() {

		result.redirectTo(LoginController.class).permissaoNegada();
	}

	public static HashMap<String, Usuario> getUsuariosLogados() {

		if (usuariosLogados == null) {

			usuariosLogados = new HashMap<String, Usuario>();
		}

		return usuariosLogados;
	}

	public static void setUsuariosLogados(HashMap<String, Usuario> usuariosLogados) {

		InterceptadorDeAutorizacao.usuariosLogados = usuariosLogados;
	}
}
