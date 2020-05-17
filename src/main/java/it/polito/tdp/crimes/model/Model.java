package it.polito.tdp.crimes.model;

import java.time.Month;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private EventsDao dao ;
	private SimpleWeightedGraph<Reato, DefaultWeightedEdge> grafo;
	private Map<String, Reato> idMap;
	private int nVerticiBest;
	private List<Reato> bestPercorso;
	
	public Model() {
		dao = new EventsDao();
	}
	
	public List<String> categorieReato(){
		return dao.categorieReato();
	}
	
	public List<String> mesi(){
		return dao.mesi();
	}
	
	public void creaGrafo(int mese, String categoria) {
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//vertici + idMap
		idMap = new HashMap<>(); 
		
		for(Reato r : dao.vertici(mese, categoria)) {
			idMap.put(r.getTipoReato(), r);
		}
		
		Graphs.addAllVertices(this.grafo, dao.vertici(mese, categoria));
		
		//archi
		/* - > approccio 1
		//----------------------------DUBBIO : IL MESE SI CONSIDERA??? 
		
		for(Reato r1 : grafo.vertexSet()) {
			for(Reato r2: grafo.vertexSet()) {
				if(r1.getTipoReato().compareTo(r2.getTipoReato())<0) { //controllo A-B e non B-A
					if(dao.quartieriDiversi(r1, r2, mese)>0) {
						Graphs.addEdge(grafo, r1, r2, dao.quartieriDiversi(r1, r2, mese));
					}				
				}				
			}
		}
		*/
		//FACCIO APPROCCIO 2!!!!
		
		/*- > approccio 3*/
		
		for (Arco a : dao.coppiaReati(mese, categoria, idMap)) {
			if((! this.grafo.containsEdge(a.getReato1(), a.getReato2())) && a.getQuartieri()>0) // superfluo
				{
				 Graphs.addEdge(this.grafo, a.getReato1(), a.getReato2(), a.getQuartieri());
				}
	
		}
		
	
		
	}
	
	public List<Arco> archiFiltro(){
		List<Arco> archi = new LinkedList<>();
		
		double somma = 0;
		double media;
		
		for(DefaultWeightedEdge e : grafo.edgeSet()) {
			somma+= this.grafo.getEdgeWeight(e);	
		}
		
		media = somma / (grafo.edgeSet().size());
	
		for(DefaultWeightedEdge e : grafo.edgeSet()) {
			if(grafo.getEdgeWeight(e)>media) {
				Arco arco = new Arco(grafo.getEdgeSource(e), grafo.getEdgeTarget(e), ((int)grafo.getEdgeWeight(e)));
				archi.add(arco);
			}
		}
		
		return archi;
		
		
	}
	
	public List<Arco> archi(){ //non serve 
		 
		List<Arco> archi = new LinkedList<>();
		
		for(DefaultWeightedEdge e : grafo.edgeSet()) {
			
				Arco arco = new Arco(grafo.getEdgeSource(e), grafo.getEdgeTarget(e), ((int)grafo.getEdgeWeight(e)));
				archi.add(arco);
			
		}
		return archi;
	}
	
	/**RICORSIONE
	 * 
	 * livello = nodi, reato, adiacente
	 * sol parziale = lista nodi
	 * 
	 * sol completa = soluzione con max numero nodi
	 * 
	 * livello + 1 = trovo adiacenti, per ogni nodo, lo aggiungo se non è presente in parziale
	 * 	 backtracking -> rimuovo elemento => perchè voglio provare tutte le strade 
	 * 
	 *terminazione :  se adiacenti = 0, return; se nodo passato = secondo reato, controllo numero nodi (non vado piu avanti)
	 *		se soluzione migliore.size() = 0 e sono arrivata al secondo reato, salva il parziale e il numero di nodi
	 *		altrimenti, se numero di nodi parziale > numero nodi migliore -> salva 
	 *
	 *parziale -> lista Reato
	 */
	
	public List<Reato> calcolaPercorso(Reato r1, Reato r2){
		
		List<Reato> parziale = new LinkedList<>();
		
		this.bestPercorso = new LinkedList<>();
		this.nVerticiBest=0; //avrei potuto inizializzare nVerticiBest = bestPercorso.size() (=0)
		
		parziale.add(r1);
		cerca(r1, r2, parziale, 0);
		
		
		return bestPercorso;
	
	}
	
	
	

	private void cerca(Reato r1, Reato r2, List<Reato> parziale, int livello){
		
		// if(r1 uguale r2) ==> if(parziale.size()>nVerticiBest) ==> riaggiorna
		
		if(bestPercorso.size() == 0 && r1.equals(r2)) { //r2 già aggiunto
			
			nVerticiBest = parziale.size();
			bestPercorso = new LinkedList<>(parziale); //!! 
			
			return;
		}
		
		if(bestPercorso.size() > 0 && r1.equals(r2)) {
		
			
			if(parziale.size() > nVerticiBest) {
				nVerticiBest = parziale.size();
				bestPercorso = new LinkedList<>(parziale);
			}
			
			return;
			
		}
		
		if(Graphs.neighborListOf(this.grafo, r1).size()==0) {
			return;
		}
		
		
		for(Reato r : Graphs.neighborListOf(this.grafo, r1)) {
			if(! parziale.contains(r)) {
				parziale.add(r);
				cerca(r, r2, parziale, livello+1);
				parziale.remove(r);
			}
		}
}
		

	
	
	
	
}
