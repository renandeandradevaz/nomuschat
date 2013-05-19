package nomuschat.controller;

import nomuschat.anotacoes.Public;
import nomuschat.hibernate.HibernateUtil;
import nomuschat.interceptor.InterceptadorDeAutorizacao;
import nomuschat.listener.CounterListener;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

@Resource
public class AnaliseController {

	private final Result result;

	public AnaliseController(Result result) {

		this.result = result;

	}

	@Public
	@Path("/analise")
	public void analisar() {

		result.include("sessoesTomcat", CounterListener.getCount());
		result.include("quantidadeUsuariosOnline", InterceptadorDeAutorizacao.getUsuariosLogados().size());

		HibernateUtil.gerarEstatisticas();

		result.include("sessoesAbertasHibernate", HibernateUtil.getQuantidadeSessoesAbertasHibernate());
		result.include("sessoesFechadasHibernate", HibernateUtil.getQuantidadeSessoesFechadasHibernate());
	}

}
