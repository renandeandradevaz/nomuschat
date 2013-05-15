package scada.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import scada.anotacoes.Funcionalidade;
import scada.auxiliar.ChatAuxiliar;
import scada.hibernate.HibernateUtil;
import scada.interceptor.InterceptadorDeAutorizacao;
import scada.sessao.SessaoOperador;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;

@Resource
public class ChatController {

	public static HashMap<String, List<ChatAuxiliar>> chat;
	private final Result result;
	private HibernateUtil hibernateUtil;
	private SessaoOperador sessaoOperador;

	public ChatController(Result result, HibernateUtil hibernateUtil, SessaoOperador sessaoOperador) {

		this.result = result;
		this.sessaoOperador = sessaoOperador;
		this.hibernateUtil = hibernateUtil;
		this.hibernateUtil.setResult(result);
	}

	@Funcionalidade(nome = "Recebe a mensagem do cliente e salva na variável")
	public void recebeMensagem(String remetente, String destinatario, String mensagem) {
		
		System.out.println(mensagem);

		iniciaHashChat(destinatario);

		if (remetente.equals(this.sessaoOperador.getOperador().getLogin())) {

			chat.get(destinatario).add(new ChatAuxiliar(remetente, mensagem));

			result.use(Results.jsonp()).withCallback("jsonMensagemEnviada").from("ok").serialize();
		}
	}

	@Funcionalidade(nome = "Verifica a existência de novas mensagens")
	public void verificaExistenciaNovasMensagens(String cliente) {

		iniciaHashChat(cliente);

		if (cliente.equals(this.sessaoOperador.getOperador().getLogin())) {

			result.use(Results.jsonp()).withCallback("jsonVerificacaoExistenciaNovasMensagens").from(chat.get(cliente)).serialize();

			chat.get(cliente).clear();
		}
	}

	@Funcionalidade(nome = "")
	public void exibirUsuariosLogados() {

		List<String> usuariosLogados = new ArrayList<String>();

		for (Entry<String, String> usuarioLogado : InterceptadorDeAutorizacao.getUsuariosLogados().entrySet()) {

			usuariosLogados.add(usuarioLogado.getKey());
		}

		result.use(Results.jsonp()).withCallback("jsonUsuariosLogados").from(usuariosLogados).serialize();
	}

	private void iniciaHashChat(String destinatario) {

		if (chat == null) {

			chat = new HashMap<String, List<ChatAuxiliar>>();
		}

		if (!chat.containsKey(destinatario)) {

			chat.put(destinatario, new ArrayList<ChatAuxiliar>());
		}
	}
}
