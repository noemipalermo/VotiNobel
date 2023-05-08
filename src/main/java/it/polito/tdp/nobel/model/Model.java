package it.polito.tdp.nobel.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.tdp.nobel.db.EsameDAO;

public class Model {	

	private List<Esame> allEsami; //tutti gli esami del database
	private Set<Esame> migliore; //Collection in cui raccolgo la mia soluzione migliore -> uso Set perchè nonha senso ripetere lo stesso esame più volte
	private double mediaMigliore;
	
	public Model() { // nel costruttore creo la lista di tutti gli esami a partire dal DAO
		EsameDAO dao = new EsameDAO();
		this.allEsami = dao.getTuttiEsami();
	}
	
	public Set<Esame> calcolaSottoinsiemeEsami(int numeroCrediti) { // preparo l'ambiente per la ricorsione
		
		// inizializzo variabile migliore a inizio ricorsione così 
		// sono sicura che a inizio metodo la variabile è vuota
		migliore = new HashSet<>();
		mediaMigliore = 0.0; // valore arbitrario che posso maggiorare
		 
		Set<Esame> parziale = new HashSet<>(); // pezzo della soluzione 
		
		// metodo ricorsivo
		cercaMeglio(parziale, 0, numeroCrediti); // 0 -> livello della ricorsione, alla prima chiamata è 0
		
		return migliore;		

	}
	
	// Questa soluzione non è partifcolarmente intelligente in quanto ha molte ripetizioni
		// e se metto un numero di crediti troppo alto si 'ROMPE TUTTO '
		private void cerca(Set<Esame> parziale, int L, int numeroCrediti) { // metodo effettivo della ricorsione
			
			
			int sommaCrediti = sommaCrediti(parziale);
			
			// su somma crediti posso fare le mie condizioni di uscita
			// verifico che il numero di crediti della soluzione sia inferiore alla somma di tutti i crediti degli esami 
			if (sommaCrediti > numeroCrediti) //non posso avere una soluzione perchè una delle condizioni di uscita è il numero max di crediti 
				return;
			
			if (sommaCrediti == numeroCrediti) {//potrei avere una soluzione qui
				double mediaVoti = calcolaMedia(parziale);
				if (mediaVoti > mediaMigliore) { // la media dei voti nella mia soluzione parziale
					mediaMigliore = mediaVoti;
					// inizializzo migliore, facendo una fotografia di parziale
					migliore = new HashSet<>(parziale);
				}
				return;
			}
			
			// verifico che il livello L(livello della ricorsione) sia uguale al numero di esami
			// quindi ho aggiunto tutti gli esami che avevo a disposizione
			// e non ha senso andare avanti
			
			if (L == allEsami.size())
				return;
			
			// se arrivo qui, numeroCrediti > sommaCrediti
			
			for (Esame e : allEsami) { 
				
				if (!parziale.contains(e)) {
					parziale.add(e);
					cerca(parziale, L+1, numeroCrediti); // itero la ricorsione
					parziale.remove(e); // Backtracking
					
				}
				
			}
			
			
		}

	
	// Implemento una soluzione migliore, con meno ripetizioni
	//  devo dar modo alla funzione ricorsiva di scegliere se un esame vada aggiunto oppure no
	private void cercaMeglio(Set<Esame> parziale, int L, int numeroCrediti) { 
		
		int sommaCrediti = sommaCrediti(parziale);
		
		
		if (sommaCrediti > numeroCrediti) 
			return;
		
		if (sommaCrediti == numeroCrediti) {//potrei avere una soluzione qui
			double mediaVoti = calcolaMedia(parziale); 
			if (mediaVoti > mediaMigliore) {
				mediaMigliore = mediaVoti;
				migliore = new HashSet<>(parziale);
			}
			return;
		}
		
		
		if (L == allEsami.size()) 
			return;
		
		// provo ad aggiungere il prossimo elemento
		//L=0 {e1} 			  / {}
		//L=1 {e1, e2} - {e1} / {e2} - {}	
		
		// scorro il vettore esami solo una volta
		parziale.add(allEsami.get(L)); // prendo l'esame con posizione uguale al livello
		cercaMeglio(parziale, L+1, numeroCrediti); // itero la ricorsione
		parziale.remove(allEsami.get(L)); // backtracking
		
		// provo a non aggiungere il prossimo elemento
		cercaMeglio(parziale, L+1, numeroCrediti);	
		
		// in questa funzione chiamo due volte la funzione ricorziva -> MOLTO IMPORTANTE IL BACKTRACKING
	}

	
	public double calcolaMedia(Set<Esame> esami) {
		
		int crediti = 0;
		int somma = 0;
		
		for(Esame e : esami){
			crediti += e.getCrediti();
			somma += (e.getVoto() * e.getCrediti());
		}
		
		return somma/crediti;
	}
	
	public int sommaCrediti(Set<Esame> esami) { // scorre gli elementi del Set e somma i crediti
		int somma = 0;
		
		for(Esame e : esami)
			somma += e.getCrediti();
		
		return somma;
	}

}
