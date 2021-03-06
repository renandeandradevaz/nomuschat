package nomuschat.controller;

import java.util.List;

import nomuschat.anotacoes.Funcionalidade;
import nomuschat.hibernate.HibernateUtil;
import nomuschat.modelo.Empresa;
import nomuschat.modelo.Usuario;
import nomuschat.seguranca.Criptografia;
import nomuschat.sessao.SessaoGeral;
import nomuschat.util.Util;
import nomuschat.util.UtilController;

import org.hibernate.criterion.MatchMode;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;

@Resource
public class UsuarioController {

	private final Result result;
	private Validator validator;
	private SessaoGeral sessaoGeral;
	private HibernateUtil hibernateUtil;

	public UsuarioController(Result result, Validator validator, SessaoGeral sessaoGeral, HibernateUtil hibernateUtil) {

		this.result = result;
		this.validator = validator;
		this.sessaoGeral = sessaoGeral;
		this.hibernateUtil = hibernateUtil;
		this.hibernateUtil.setResult(result);

	}

	@Funcionalidade(administrativa = "true")
	public void criarUsuario() {

		sessaoGeral.adicionar("idUsuario", null);
		result.forwardTo(this).criarEditarUsuario();
	}

	@Path("/usuario/editarUsuario/{usuario.id}")
	@Funcionalidade(administrativa = "true")
	public void editarUsuario(Usuario usuario) {

		usuario = hibernateUtil.selecionar(usuario);

		if (usuario.getLogin().equals("administrador")) {

			validator.add(new ValidationMessage("Não é possível editar o usuario administrador", "Erro"));

			validator.onErrorForwardTo(this).listarUsuarios(null, null);
		}

		sessaoGeral.adicionar("idUsuario", usuario.getId());
		result.include(usuario);
		result.forwardTo(this).criarEditarUsuario();
	}

	@Funcionalidade(administrativa = "true")
	public void criarEditarUsuario() {

		result.include("empresas", this.hibernateUtil.buscar(new Empresa()));
	}

	@Path("/usuario/excluirUsuario/{usuario.id}")
	@Funcionalidade(administrativa = "true")
	public void excluirUsuario(Usuario usuario) {

		Usuario usuarioselecionado = hibernateUtil.selecionar(usuario);

		if (usuarioselecionado.getLogin().equals("administrador")) {

			validator.add(new ValidationMessage("Não é possível excluir o usuario administrador", "Erro"));

			validator.onErrorForwardTo(this).listarUsuarios(null, null);
		}

		hibernateUtil.deletar(usuarioselecionado);
		result.include("sucesso", "Usuario excluído com sucesso");
		result.forwardTo(this).listarUsuarios(null, null);
	}

	@Funcionalidade(administrativa = "true")
	public void salvarUsuario(Usuario usuario) {

		if (Util.vazio(sessaoGeral.getValor("idUsuario"))) {

			validar(usuario);

			usuario.setSenha(new Criptografia().criptografaSenha(usuario.getSenha()));
		}

		else {

			Usuario usuarioselecionado = hibernateUtil.selecionar(new Usuario((Integer) sessaoGeral.getValor("idUsuario")));

			if (!usuario.getLogin().equals(usuarioselecionado.getLogin())) {

				validar(usuario);
			}

			usuario.setId((Integer) sessaoGeral.getValor("idUsuario"));
			usuario.setSenha(usuarioselecionado.getSenha());
		}

		hibernateUtil.salvarOuAtualizar(usuario);
		result.include("sucesso", "Usuario salvo com sucesso");
		result.redirectTo(this).listarUsuarios(new Usuario(), null);
	}

	private void validar(Usuario usuario) {

		if (Util.vazio(usuario.getLogin())) {

			validator.add(new ValidationMessage("Login requerido", "Erro"));
		}

		if (Util.vazio(usuario.getSenha())) {

			validator.add(new ValidationMessage("Senha requerida", "Erro"));
		}

		if (Util.vazio(usuario.getEmpresa().getId())) {

			validator.add(new ValidationMessage("Empresa requerida", "Erro"));
		}

		Usuario usuarioFiltro = new Usuario();
		usuarioFiltro.setLogin(usuario.getLogin());
		usuarioFiltro.setEmpresa(usuario.getEmpresa());

		if (Util.preenchido(hibernateUtil.buscar(usuarioFiltro, MatchMode.EXACT))) {
			validator.add(new ValidationMessage("Já existe um usuario com este login nesta empresa", "Erro"));
		}

		validator.onErrorForwardTo(this).criarEditarUsuario();
	}

	@Funcionalidade(administrativa = "true")
	public void listarUsuarios(Usuario usuario, Integer pagina) {

		usuario = (Usuario) UtilController.preencherFiltros(usuario, "usuario", sessaoGeral);
		if (Util.vazio(usuario)) {
			usuario = new Usuario();
		}

		List<Usuario> usuarios = hibernateUtil.buscar(usuario, pagina);

		result.include("usuarios", usuarios);
	}

	@Path("/usuario/trocarSenha/{usuario.id}")
	@Funcionalidade(administrativa = "true")
	public void trocarSenha(Usuario usuario) {

		usuario = hibernateUtil.selecionar(usuario);

		if (usuario.getLogin().equals("administrador")) {

			validator.add(new ValidationMessage("Não é possível trocar a senha do administrador", "Erro"));

			validator.onErrorForwardTo(this).listarUsuarios(null, null);
		}

		sessaoGeral.adicionar("idUsuario", usuario.getId());
	}

	@Funcionalidade(administrativa = "true")
	public void salvarTrocaSenha(String senha) {

		Usuario usuario = hibernateUtil.selecionar(new Usuario((Integer) this.sessaoGeral.getValor("idUsuario")));

		usuario.setSenha(new Criptografia().criptografaSenha(senha));

		hibernateUtil.salvarOuAtualizar(usuario);

		result.include("sucesso", "Senha trocada com sucesso");

		result.forwardTo(this).listarUsuarios(null, null);
	}
}
