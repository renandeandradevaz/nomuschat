package nomuschat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import nomuschat.anotacoes.Funcionalidade;
import nomuschat.auxiliar.ChatAuxiliar;
import nomuschat.hibernate.HibernateUtil;
import nomuschat.interceptor.InterceptadorDeAutorizacao;
import nomuschat.sessao.SessaoUsuario;
import nomuschat.util.Util;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;

@Resource
public class ChatController {

	public static HashMap<String, List<ChatAuxiliar>> chat;
	private final Result result;
	private HibernateUtil hibernateUtil;
	private SessaoUsuario sessaoUsuario;

	public ChatController(Result result, HibernateUtil hibernateUtil, SessaoUsuario sessaoUsuario) {

		this.result = result;
		this.sessaoUsuario = sessaoUsuario;
		this.hibernateUtil = hibernateUtil;
		this.hibernateUtil.setResult(result);
	}

	@Funcionalidade
	public void recebeMensagem(String remetente, String destinatario, String mensagem) {

		iniciaHashChat(destinatario);

		if (remetente.equals(this.sessaoUsuario.getUsuario().getLogin())) {

			chat.get(destinatario).add(new ChatAuxiliar(remetente, mensagem));

			result.use(Results.jsonp()).withCallback("jsonMensagemEnviada").from("ok").serialize();
		}
	}

	@Funcionalidade
	public void verificaExistenciaNovasMensagens(String login) {

		iniciaHashChat(login);

		if (login.equals(this.sessaoUsuario.getUsuario().getLogin())) {

			result.use(Results.jsonp()).withCallback("jsonVerificacaoExistenciaNovasMensagens").from(chat.get(login)).serialize();

			chat.get(login).clear();
		}
	}

	@Funcionalidade
	public void verificaExistenciaNovasMensagensSemDevolverJsonComMensagens(String loginNomusChat) {

		iniciaHashChat(loginNomusChat);

		if (loginNomusChat.equals(this.sessaoUsuario.getUsuario().getLogin())) {

			if (Util.preenchido(chat.get(loginNomusChat))) {

				result.use(Results.jsonp()).withCallback("jsonVerificacaoExistenciaNovasMensagensSemDevolverJsonComMensagens").from(true).serialize();
			}

			else {

				result.use(Results.jsonp()).withCallback("jsonVerificacaoExistenciaNovasMensagensSemDevolverJsonComMensagens").from(true).serialize();
			}
		}
	}

	@Funcionalidade
	public void exibirUsuariosLogados(String login, String empresa) {

		if (!InterceptadorDeAutorizacao.getUsuariosLogados().containsKey(login)) {

			InterceptadorDeAutorizacao.getUsuariosLogados().put(empresa + "_" + login, "");
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
