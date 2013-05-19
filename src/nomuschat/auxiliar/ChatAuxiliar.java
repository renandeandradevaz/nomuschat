package nomuschat.auxiliar;

public class ChatAuxiliar {

	private String remetente;
	private String mensagem;

	public ChatAuxiliar(String remetente, String mensagem) {

		this.remetente = remetente;
		this.mensagem = mensagem;
	}

	public String getRemetente() {
		return remetente;
	}

	public void setRemetente(String remetente) {
		this.remetente = remetente;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

}
