package nomuschat.controller;

import java.util.Arrays;
import java.util.HashMap;

import nomuschat.anotacoes.Public;
import nomuschat.hibernate.HibernateUtil;
import nomuschat.interceptor.InterceptadorDeAutorizacao;
import nomuschat.modelo.Empresa;
import nomuschat.modelo.Usuario;
import nomuschat.seguranca.Criptografia;
import nomuschat.sessao.SessaoUsuario;
import nomuschat.util.Util;

import org.hibernate.criterion.MatchMode;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;

@Resource
public class LoginController {

	private static final String HASH_SENHA_ADMINISTRADOR = "e51c96aa97b9562d06b3f8031fc6af58";

	private final Result result;
	private SessaoUsuario sessaoUsuario;
	private Validator validator;
	private HibernateUtil hibernateUtil;

	public LoginController(Result result, SessaoUsuario sessaoUsuario, Validator validator, HibernateUtil hibernateUtil) {
		this.result = result;
		this.sessaoUsuario = sessaoUsuario;
		this.validator = validator;
		this.hibernateUtil = hibernateUtil;
	}

	@Public
	@Path("/")
	public void telaLogin() {

		result.include("empresas", this.hibernateUtil.buscar(new Empresa()));
	}

	@Public
	public void efetuarLogin(Usuario usuario) {

		verificaExistenciaAdministrador(usuario);
		tentarEfetuarLogin(usuario);
		colocarUsuarioNaSessao(usuario);
		result.redirectTo(HomeController.class).home();
	}

	private void verificaExistenciaAdministrador(Usuario usuario) {

		if (usuario.getLogin().equals("administrador") && new Criptografia().criptografaSenha(usuario.getSenha()).equals(HASH_SENHA_ADMINISTRADOR)) {

			Usuario usuarioFiltro = new Usuario();
			usuarioFiltro.setLogin("administrador");

			Usuario usuarioBanco = hibernateUtil.selecionar(usuarioFiltro, MatchMode.EXACT);

			if (Util.vazio(usuarioBanco)) {

				Empresa empresa = new Empresa();
				empresa.setNome("Nomus");
				hibernateUtil.salvarOuAtualizar(empresa);

				usuario.setNome("Administrador");
				usuario.setAdministrador(true);
				usuario.setSenha(HASH_SENHA_ADMINISTRADOR);
				usuario.setEmpresa(empresa);
				hibernateUtil.salvarOuAtualizar(usuario);
			}
		}
	}

	private void tentarEfetuarLogin(Usuario usuario) {

		if (Util.vazio(usuario.getLogin())) {

			validator.add(new ValidationMessage("Login requerido", "Erro"));
		}

		if (Util.vazio(usuario.getSenha())) {

			validator.add(new ValidationMessage("Senha requerida", "Erro"));
		}

		if (Util.vazio(usuario.getEmpresa().getId())) {

			validator.add(new ValidationMessage("Empresa requerida", "Erro"));
		}

		validator.onErrorRedirectTo(this).telaLogin();

		usuario.setSenha(new Criptografia().criptografaSenha(usuario.getSenha()));

		usuario = hibernateUtil.selecionar(usuario, MatchMode.EXACT);

		if (Util.vazio(usuario)) {

			validator.add(new ValidationMessage("Combinação login, senha, empresa incorretos", "Erro"));
		}

		validator.onErrorRedirectTo(this).telaLogin();
	}

	private void colocarUsuarioNaSessao(Usuario usuario) {

		usuario = this.hibernateUtil.selecionar(usuario, MatchMode.EXACT);

		usuario.setKeyEmpresaUsuario(usuario.getEmpresa().getNome() + "_" + usuario.getLogin());

		this.sessaoUsuario.login(usuario);

		if (InterceptadorDeAutorizacao.getUsuariosLogados() == null) {

			InterceptadorDeAutorizacao.setUsuariosLogados(new HashMap<String, Usuario>());
		}

		InterceptadorDeAutorizacao.getUsuariosLogados().put(usuario.getKeyEmpresaUsuario(), usuario);
	}

	@Public
	public void permissaoNegada() {

	}

	@Public
	public void logout() {

		sessaoUsuario.logout();

		result.redirectTo(this).telaLogin();
	}

	@Public
	public void trocarPropriaSenha() {

		verificarUsuarioLogado();
	}

	@Public
	public void salvarTrocarPropriaSenha(String senhaAntiga, String senhaNova) {

		if (!verificarUsuarioLogado()) {

			return;
		}

		Usuario usuario = hibernateUtil.selecionar(new Usuario(sessaoUsuario.getUsuario().getId()));

		if (!new Criptografia().criptografaSenha(senhaAntiga).equals(usuario.getSenha())) {

			validator.add(new ValidationMessage("Senha antiga incorreta", "Erro"));

			validator.onErrorRedirectTo(this).trocarPropriaSenha();
		}

		usuario.setSenha(new Criptografia().criptografaSenha(senhaNova));

		hibernateUtil.salvarOuAtualizar(usuario);

		result.include("sucesso", "Senha trocada com sucesso");

		result.redirectTo(HomeController.class).home();
	}

	private boolean verificarUsuarioLogado() {

		if (sessaoUsuario.getUsuario() == null) {

			result.redirectTo(this).telaLogin();
			return false;
		}

		return true;
	}

	@Public
	public void loginVindoDoPCP(String loginNomusChat, String senhaNomusChat, String nomeNomusChat, String nomeEmpresaNomusChat) {

		String login = loginNomusChat;
		String senha = senhaNomusChat;
		String nome = nomeNomusChat;
		String nomeEmpresa = nomeEmpresaNomusChat;

		if (Util.vazio(login) || Util.vazio(senha) || Util.vazio(nomeEmpresa)) {

			result.include("errors", Arrays.asList(new ValidationMessage("Necessário informar login, senha e empresa para entrar no chat", "Erro")));
			result.redirectTo(this).telaLogin();
			return;
		}

		if (login.equals("administrador")) {

			result.include("errors", Arrays.asList(new ValidationMessage("Administrador não se loga no chat", "Erro")));
			result.redirectTo(this).telaLogin();
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
				usuarioBanco = usuario;
			}

			else {

				if (!usuarioBanco.getSenha().equals(senha)) {

					validator.add(new ValidationMessage("Percebemos que você modificou sua senha nos softwares Nomus recentemente. Por medidas de segurança, pedimos que acesse o chat com seu login e senha antigos e troque sua senha para acessar o Nomus Chat. É necessário que a senha do Software Nomus seja a mesma do Nomus Chat para que consiga fazer login automático", "Erro"));

					validator.onErrorRedirectTo(this).telaLogin();
					return;
				}
			}

			usuarioLogado = usuarioBanco;
		}

		usuarioLogado.setKeyEmpresaUsuario(usuarioLogado.getEmpresa().getNome() + "_" + usuarioLogado.getLogin());

		this.sessaoUsuario.login(usuarioLogado);

		if (InterceptadorDeAutorizacao.getUsuariosLogados() == null) {

			InterceptadorDeAutorizacao.setUsuariosLogados(new HashMap<String, Usuario>());
		}

		InterceptadorDeAutorizacao.getUsuariosLogados().put(usuarioLogado.getKeyEmpresaUsuario(), usuarioLogado);

		result.redirectTo(HomeController.class).home();
	}
}
