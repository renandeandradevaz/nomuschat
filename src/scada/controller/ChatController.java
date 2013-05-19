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

	@Funcionalidade
	public void recebeMensagem(String remetente, String destinatario, String mensagem) {

		iniciaHashChat(destinatario);

		if (remetente.equals(this.sessaoOperador.getOperador().getLogin())) {

			chat.get(destinatario).add(new ChatAuxiliar(remetente, mensagem));

			result.use(Results.jsonp()).withCallback("jsonMensagemEnviada").from("ok").serialize();
		}
	}

	@Funcionalidade
	public void verificaExistenciaNovasMensagens(String login) {

		iniciaHashChat(login);

		if (login.equals(this.sessaoOperador.getOperador().getLogin())) {

			result.use(Results.jsonp()).withCallback("jsonVerificacaoExistenciaNovasMensagens").from(chat.get(login)).serialize();

			chat.get(login).clear();
		}
	}

	@Funcionalidade
	public void exibirUsuariosLogados(String login) {

		if (!InterceptadorDeAutorizacao.getUsuariosLogados().containsKey(login)) {

			InterceptadorDeAutorizacao.getUsuariosLogados().put(login, "");
		}

		List<String> usuariosLogados = new ArrayList<String>();

		for (Entry<String, String> usuarioLogado : InterceptadorDeAutorizacao.getUsuariosLogados().entrySet()) {

			if (!usuarioLogado.getKey().equals(login)) {

				usuariosLogados.add(usuarioLogado.getKey());
			}
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
