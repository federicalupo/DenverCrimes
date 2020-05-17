/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.crimes;

import java.net.URL;
import java.time.Month;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.crimes.model.Arco;
import it.polito.tdp.crimes.model.Model;
import it.polito.tdp.crimes.model.Reato;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxCategoria"
    private ComboBox<String> boxCategoria; // Value injected by FXMLLoader

    @FXML // fx:id="boxMese"
    private ComboBox<String> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="btnAnalisi"
    private Button btnAnalisi; // Value injected by FXMLLoader

    @FXML // fx:id="boxArco"
    private ComboBox<Arco> boxArco; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	this.txtResult.clear();
    	
    	Arco arco = this.boxArco.getValue();
    	
    	List<Reato> percorso = model.calcolaPercorso(arco.getReato1(), arco.getReato2());
    	
    	this.txtResult.appendText("Percorso migliore:\n\n");
    	for(Reato r: percorso) {
    		this.txtResult.appendText(r+"\n");	
    	}
    	
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	this.txtResult.clear();
    	this.boxArco.getItems().clear();
    	
    	int mese = Month.valueOf(this.boxMese.getValue()).getValue();
    	String categoria = this.boxCategoria.getValue();
    	
    	model.creaGrafo(mese, categoria);
    	
    	this.txtResult.appendText("Elenco archi con peso > peso medio\n\n");
    	for(Arco a: model.archiFiltro()) {
    		txtResult.appendText(String.format("Reato: %-20s Reato: %-20s numeroQuartieri: %-25d\n" , a.getReato1(),a.getReato2(),a.getQuartieri()));
    		
    	}
    	
    	//this.boxArco.getItems().addAll(model.archi()); => tutti archi
    	this.boxArco.getItems().addAll(model.archiFiltro()); //archi con filtro
    	this.boxArco.setValue(model.archiFiltro().get(0));
    	

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxCategoria != null : "fx:id=\"boxCategoria\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnAnalisi != null : "fx:id=\"btnAnalisi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxArco != null : "fx:id=\"boxArco\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
    	this.boxCategoria.getItems().addAll(model.categorieReato());
    	this.boxCategoria.setValue(model.categorieReato().get(0));
    	
    	this.boxMese.getItems().addAll(model.mesi());
    	this.boxMese.setValue(model.mesi().get(0));
    	
    }
}
