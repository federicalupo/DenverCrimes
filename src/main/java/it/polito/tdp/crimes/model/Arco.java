package it.polito.tdp.crimes.model;

public class Arco {
	private Reato reato1;
	private Reato reato2;
	private int quartieri;
	
	public Arco(Reato reato1, Reato reato2, int quartieri) {
		super();
		this.reato1 = reato1;
		this.reato2 = reato2;
		this.quartieri = quartieri;
	}

	public Reato getReato1() {
		return reato1;
	}

	public Reato getReato2() {
		return reato2;
	}

	public int getQuartieri() {
		return quartieri;
	}

	@Override
	public String toString() {
		return  reato1 + " - " + reato2 ;
	}
	
	
	
	
}
