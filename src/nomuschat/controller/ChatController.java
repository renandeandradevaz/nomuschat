package nomuschat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import nomuschat.anotacoes.Funcionalidade;
import nomuschat.auxiliar.ChatAuxiliar;
import nomuschat.hibernate.HibernateUtil;
import nomuschat.interceptor.InterceptadorDeAutorizacao;
import nomuschat.modelo.Usuario;
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
	public void chat() {

	}

	@Funcionalidade
	public void recebeMensagem(String destinatario, String mensagem) {

		iniciaHashChat();

		chat.get(destinatario).add(new ChatAuxiliar(this.sessaoUsuario.getUsuario().getKeyEmpresaUsuario(), mensagem, this.sessaoUsuario.getUsuario().getEmpresa().getNome(), this.sessaoUsuario.getUsuario().getNome()));

		result.use(Results.jsonp()).withCallback("jsonMensagemEnviada").from("ok").serialize();
	}

	@Funcionalidade
	public void verificaExistenciaNovasMensagens() {

		iniciaHashChat();

		result.use(Results.jsonp()).withCallback("jsonVerificacaoExistenciaNovasMensagens").from(chat.get(this.sessaoUsuario.getUsuario().getKeyEmpresaUsuario())).serialize();

		chat.get(this.sessaoUsuario.getUsuario().getKeyEmpresaUsuario()).clear();

	}

	@Funcionalidade
	public void verificaExistenciaNovasMensagensSemDevolverJsonComMensagens() {

		iniciaHashChat();

		if (Util.preenchido(chat.get(this.sessaoUsuario.getUsuario().getKeyEmpresaUsuario()))) {

			result.use(Results.jsonp()).withCallback("jsonVerificacaoExistenciaNovasMensagensSemDevolverJsonComMensagens").from(true).serialize();
		}

		else {

			result.use(Results.jsonp()).withCallback("jsonVerificacaoExistenciaNovasMensagensSemDevolverJsonComMensagens").from(true).serialize();
		}

	}

	@Funcionalidade
	public void exibirUsuariosLogados() {

		if (!InterceptadorDeAutorizacao.getUsuariosLogados().containsKey(this.sessaoUsuario.getUsuario().getKeyEmpresaUsuario())) {

			InterceptadorDeAutorizacao.getUsuariosLogados().put(this.sessaoUsuario.getUsuario().getKeyEmpresaUsuario(), this.sessaoUsuario.getUsuario());
		}

		List<Usuario> usuariosLogados = new ArrayList<Usuario>();

		for (Entry<String, Usuario> usuarioLogado : InterceptadorDeAutorizacao.getUsuariosLogados().entrySet()) {

			// if
			// (!usuarioLogado.getKey().equals(this.sessaoUsuario.getUsuario().getKeyEmpresaUsuario()))
			// {

			usuariosLogados.add(usuarioLogado.getValue());
			// }
		}

		result.use(Results.jsonp()).withCallback("jsonUsuariosLogados").from(usuariosLogados).include("empresa").serialize();
	}

	private void iniciaHashChat() {

		if (chat == null) {

			chat = new HashMap<String, List<ChatAuxiliar>>();
		}

		if (!chat.containsKey(this.sessaoUsuario.getUsuario().getKeyEmpresaUsuario())) {

			chat.put(this.sessaoUsuario.getUsuario().getKeyEmpresaUsuario(), new ArrayList<ChatAuxiliar>());
		}
	}

}
