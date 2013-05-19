package nomuschat.modelo;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import nomuschat.hibernate.Entidade;

@Entity
public class Operador implements Entidade {

	@Id
	@GeneratedValue
	private Integer id;

	@NotNull
	private String login;

	@NotNull
	private String senha;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Empresa empresa;

	public Operador() {

	}

	public Operador(Integer id) {

		this.setId(id);
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
