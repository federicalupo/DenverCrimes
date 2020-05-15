package it.polito.tdp.crimes.model;

public class Reato {
	private String tipoReato;

	public Reato(String tipoReato) {
		super();
		this.tipoReato = tipoReato;
	}

	public String getTipoReato() {
		return tipoReato;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tipoReato == null) ? 0 : tipoReato.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reato other = (Reato) obj;
		if (tipoReato == null) {
			if (other.tipoReato != null)
				return false;
		} else if (!tipoReato.equals(other.tipoReato))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return tipoReato;
	}
	
	
	
}
