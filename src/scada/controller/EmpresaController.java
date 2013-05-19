package scada.controller;

import java.util.List;

import scada.anotacoes.Funcionalidade;
import scada.hibernate.HibernateUtil;
import scada.modelo.Empresa;
import scada.sessao.SessaoGeral;
import scada.util.Util;
import scada.util.UtilController;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

@Resource
public class EmpresaController {

	private final Result result;
	private SessaoGeral sessaoGeral;
	private HibernateUtil hibernateUtil;

	public EmpresaController(Result result, SessaoGeral sessaoGeral, HibernateUtil hibernateUtil) {
		this.result = result;
		this.sessaoGeral = sessaoGeral;
		this.hibernateUtil = hibernateUtil;
		this.hibernateUtil.setResult(result);
	}

	@Funcionalidade(administrativa = "true")
	public void criarEmpresa() {

		sessaoGeral.adicionar("idEmpresa", null);
		result.forwardTo(this).criarEditarEmpresa();
	}

	@Path("/empresa/editarEmpresa/{empresa.id}")
	@Funcionalidade(administrativa = "true")
	public void editarEmpresa(Empresa empresa) {

		empresa = hibernateUtil.selecionar(empresa);

		sessaoGeral.adicionar("idEmpresa", empresa.getId());
		result.include(empresa);
		result.forwardTo(this).criarEditarEmpresa();
	}

	@Funcionalidade(administrativa = "true")
	public void criarEditarEmpresa() {
	}

	@Path("/empresa/excluirEmpresa/{empresa.id}")
	@Funcionalidade(administrativa = "true")
	public void excluirEmpresa(Empresa empresa) {

		hibernateUtil.deletar(empresa);
		result.include("sucesso", "Empresa excluído(a) com sucesso");
		result.forwardTo(this).listarEmpresas(null, null);
	}

	@Funcionalidade(administrativa = "true")
	public void salvarEmpresa(Empresa empresa) {

		if (Util.preenchido(sessaoGeral.getValor("idEmpresa"))) {

			empresa.setId((Integer) sessaoGeral.getValor("idEmpresa"));
		}

		hibernateUtil.salvarOuAtualizar(empresa);
		result.include("sucesso", "Empresa salvo(a) com sucesso");
		result.redirectTo(this).listarEmpresas(new Empresa(), null);
	}

	@Funcionalidade(administrativa = "true")
	public void listarEmpresas(Empresa empresa, Integer pagina) {

		empresa = (Empresa) UtilController.preencherFiltros(empresa, "empresa", sessaoGeral);
		if (Util.vazio(empresa)) {
			empresa = new Empresa();
		}

		List<Empresa> empresas = hibernateUtil.buscar(empresa, pagina);
		result.include("empresas", empresas);
	}
}
