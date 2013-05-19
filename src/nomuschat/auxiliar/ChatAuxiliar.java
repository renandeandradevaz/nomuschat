package nomuschat.auxiliar;

public class ChatAuxiliar {

	private String remetente;
	private String mensagem;
	private String empresa;
	private String nome;

	public ChatAuxiliar(String remetente, String mensagem, String empresa, String nome) {

		this.remetente = remetente;
		this.mensagem = mensagem;
		this.empresa = empresa;
		this.nome = nome;
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

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
