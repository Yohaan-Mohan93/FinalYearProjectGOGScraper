package fyp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class BuyListCards {
	private String name;
	private String set;
	private String rarity;
	private double nm;
	
	//default constructor needed for jackson
	public BuyListCards() {
		
	}
	
	public BuyListCards(String name, String set, String rarity, double nm) {
		this.name 	= name;
		this.set    = set;
		this.rarity = rarity;
		this.nm   	= nm;
	}
	
	public String getName() {
		return name;
	}
	public String getSet() {
		return set;
	}
	public String getRarity() {
		return rarity;
	}
	public double getNm() {
		return nm;
	}
	public void setname(String name) {
		this.name = name;
	}
	public void setset(String set) {
		this.set = set;
	}
	public void setrarity(String rarity) {
		this.rarity = rarity;
	}
	public void setnm(double nm) {
		this.nm = nm;
	}
	
	public StringProperty nameProperty() {
		StringProperty sProp = new SimpleStringProperty(this.name);
		return sProp;
	}
	
	public StringProperty setProperty() {
		StringProperty sProp = new SimpleStringProperty(this.set);
		return sProp;
	}
	
	public StringProperty rarityProperty() {
		StringProperty sProp = new SimpleStringProperty(this.rarity);
		return sProp;
	}
	
	public DoubleProperty nmProperty() {
		DoubleProperty sProp = new SimpleDoubleProperty(this.nm);
		return sProp;
	}
	
	@Override
	public String toString() {
		return "name: "    + name    + "\n"+
				"set: "    + set     + "\n"+
				"rarity: " + rarity  + "\n"+
				"nm: "     + nm      + "\n"+
				"-----------------------------------";
	}
}
