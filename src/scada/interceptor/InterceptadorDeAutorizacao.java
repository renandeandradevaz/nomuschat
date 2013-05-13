package scada.interceptor;

import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.MatchMode;

import scada.anotacoes.Public;
import scada.controller.LoginController;
import scada.hibernate.HibernateUtil;
import scada.modelo.Operador;
import scada.sessao.SessaoOperador;
import scada.util.Util;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.ValidationMessage;

@Intercepts
public class InterceptadorDeAutorizacao implements Interceptor {

	private final SessaoOperador sessaoOperador;
	private Result result;
	private HttpServletRequest request;
	private HibernateUtil hibernateUtil;
	private static HashMap<String, String> usuariosLogados;

	public InterceptadorDeAutorizacao(SessaoOperador sessaoOperador, Result result, HttpServletRequest request, HibernateUtil hibernateUtil) {
		this.sessaoOperador = sessaoOperador;
		this.result = result;
		this.request = request;
		this.hibernateUtil = hibernateUtil;
	}

	public boolean accepts(ResourceMethod method) {

		boolean contemAnotacaoPublic = !method.containsAnnotation(Public.class);

		return contemAnotacaoPublic;

	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {

		if (Util.vazio(sessaoOperador.getOperador())) {

			String login = request.getParameter("login");
			String senha = request.getParameter("senha");

			if (usuariosLogados == null) {

				usuariosLogados = new HashMap<String, String>();
			}

			if (usuariosLogados.containsKey(login)) {

				if (usuariosLogados.get(login).equals(senha)) {

					stack.next(method, resourceInstance);
					return;
				}
			}

			Operador operador = new Operador();
			operador.setLogin(login);
			operador.setSenha(senha);

			try {

				operador = hibernateUtil.selecionar(operador, MatchMode.EXACT);

				if (Util.vazio(operador)) {

					hibernateUtil.fecharSessao();
					result.include("errors", Arrays.asList(new ValidationMessage("Login ou senha incorretos", "Erro")));
					result.redirectTo(LoginController.class).telaLogin();
				}

				else {

					sessaoOperador.login(operador);
					usuariosLogados.put(operador.getLogin(), operador.getSenha());
					stack.next(method, resourceInstance);
				}
			}

			catch (Exception e) {

				hibernateUtil.fecharSessao();

				result.include("errors", Arrays.asList(new ValidationMessage("O operador não está logado no sistema", "Erro")));
				result.redirectTo(LoginController.class).telaLogin();
			}
		}

		else {

			stack.next(method, resourceInstance);
		}
	}
}
