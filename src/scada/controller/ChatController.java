package scada.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import scada.anotacoes.Funcionalidade;
import scada.auxiliar.ChatAuxiliar;
import scada.hibernate.HibernateUtil;
import scada.util.Util;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;

@Resource
public class ChatController {

	public static HashMap<String, List<ChatAuxiliar>> chat;
	private final Result result;
	private HibernateUtil hibernateUtil;

	public ChatController(Result result, HibernateUtil hibernateUtil) {

		this.result = result;
		this.hibernateUtil = hibernateUtil;
		this.hibernateUtil.setResult(result);
	}

	@Funcionalidade(nome = "Recebe a mensagem do cliente e salva na variável")
	public void recebeMensagem(String remetente, String destinatario, String mensagem) {

		iniciaHashChat(destinatario);

		// VERIFICAR SE CLIENTE É IGUAL AO CLIENTE LOGADO

		chat.get(destinatario).add(new ChatAuxiliar(remetente, mensagem));
	}

	@Funcionalidade(nome = "Verifica a existência de novas mensagens")
	public void verificaExistenciaNovasMensagens(String cliente) {

		if (Util.vazio(chat)) {

			chat = new HashMap<String, List<ChatAuxiliar>>();
		}

		iniciaHashChat(cliente);

		// VERIFICAR SE CLIENTE É IGUAL AO CLIENTE LOGADO

		result.use(Results.jsonp()).withCallback("jsonpCallback").from(chat.get(cliente)).serialize();

		chat.get(cliente).clear();
	}

	private void iniciaHashChat(String destinatario) {

		if (Util.vazio(chat)) {

			chat = new HashMap<String, List<ChatAuxiliar>>();
		}

		if (!chat.containsKey(destinatario)) {

			chat.put(destinatario, new ArrayList<ChatAuxiliar>());
		}
	}
}
