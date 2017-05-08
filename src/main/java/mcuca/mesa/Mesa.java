package mcuca.mesa;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import mcuca.establecimiento.Establecimiento;
import mcuca.zona.Zona;

@Entity
public class Mesa {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private String numero;
	
	@ManyToOne
	private Zona zona;

	protected Mesa() {
	}

	public Mesa(String numero) {
		this.numero = numero;

	}

	public Long getId() {
		return id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}	

	@Override
	public String toString() {
		return String.format("Mesa[id=%d, numero='%s']", id, numero);
	}
	
	public Zona getZona() {
		return zona;
	}

	public void setZona(Zona zona) {
		this.zona = zona;
	}
	
	public Establecimiento getEstablecimiento() {
		return zona.getEstablecimiento();
	}
}