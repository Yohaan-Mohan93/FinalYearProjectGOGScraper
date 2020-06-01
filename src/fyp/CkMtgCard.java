package fyp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CkMtgCard {
	private String name;
	private String set;
	private String rarity;
	private String nm;
	private String ex;
	private String vg;
	private String g;
	
	//default constructor needed for jackson
	public CkMtgCard() {
		
	}
	
	public CkMtgCard(String name, String set, String rarity, String nm, String ex, String vg, String g) {
		this.name 	= name;
		this.set    = set;
		this.rarity = rarity;
		this.nm   	= nm;
		this.ex   	= ex;
		this.vg   	= vg;
		this.g    	= g;
	}
	
	public String getname() {
		return name;
	}
	public String getset() {
		return set;
	}
	public String getrarity() {
		return rarity;
	}
	public String getnm() {
		return nm;
	}
	public String getex() {
		return ex;
	}
	public String getvg() {
		return vg;
	}
	public String getg() {
		return g;
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
	public void setnm(String nm) {
		this.nm = nm;
	}
	public void setex(String ex) {
		this.ex = ex;
	}
	public void setvg(String vg) {
		this.vg = vg;
	}
	public void setg(String g) {
		this.g = g;
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
	
	public StringProperty nmProperty() {
		StringProperty sProp = new SimpleStringProperty(this.nm);
		return sProp;
	}
	
	public StringProperty exProperty() {
		StringProperty sProp = new SimpleStringProperty(this.ex);
		return sProp;
	}
	
	public StringProperty vgProperty() {
		StringProperty sProp = new SimpleStringProperty(this.vg);
		return sProp;
	}
	
	public StringProperty gProperty() {
		StringProperty sProp = new SimpleStringProperty(this.g);
		return sProp;
	}
	@Override
	public String toString() {
		return "name: "    + name    + "\n"+
				"set: "    + set     + "\n"+
				"rarity: " + rarity  + "\n"+
				"nm: "     + nm      + "\n"+
				"ex: "     + ex      + "\n"+
				"vg: "     + vg      + "\n"+
				"g: "      + g       + "\n"+
				"-----------------------------------";
	}
}
