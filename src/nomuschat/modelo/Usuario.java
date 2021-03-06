package nomuschat.modelo;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import nomuschat.hibernate.Entidade;

@Entity
public class Usuario implements Entidade {

	@Id
	@GeneratedValue
	private Integer id;

	@NotNull
	private String login;

	@NotNull
	private String nome;

	@NotNull
	private String senha;

	private Boolean administrador;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Empresa empresa;

	@Transient
	private String keyEmpresaUsuario;

	public Usuario() {

	}

	public Usuario(Integer id) {

		this.setId(id);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Boolean getAdministrador() {
		return administrador;
	}

	public void setAdministrador(Boolean administrador) {
		this.administrador = administrador;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public String getKeyEmpresaUsuario() {
		return keyEmpresaUsuario;
	}

	public void setKeyEmpresaUsuario(String keyEmpresaUsuario) {
		this.keyEmpresaUsuario = keyEmpresaUsuario;
	}

}
