package mcuca.cliente;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Cliente {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(length = 32)
	private String nombre;

	@Column(length = 64)
	private String apellidos;
	
	@Column(length = 128)
	private String domicilio;
	
	@Column(length = 13)
	private String telefono;

	protected Cliente() {}

	public Cliente(String nombre, String apellidos, String domicilio, String telefono) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.domicilio = domicilio;
		this.telefono = telefono;
	}

	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}
	
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	
	public String getDomicilio() {
		return domicilio;
	}
	
	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}
	
	public String getTelefono() {
		return telefono;
	}
	
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	@Override
	public String toString() {
		return String.format("%s %s (%s)", nombre, apellidos, telefono);
	}
	

}
