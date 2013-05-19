package nomuschat.modelo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import nomuschat.hibernate.Entidade;

@Entity
public class Empresa implements Entidade {

	@Id
	@GeneratedValue
	private Integer id;

	@NotNull
	private String nome;

	public Empresa() {
	}

	public Empresa(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
