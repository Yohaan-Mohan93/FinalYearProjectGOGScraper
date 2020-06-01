package fyp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ScgMtgCard implements Comparable<ScgMtgCard>{
	private String name;
	private String set;
	private String color;
	private String rarity;
	private String    nm;
	
	//default constructor needed for jackson
	public ScgMtgCard() {
		
	}

	public ScgMtgCard(String name, String set, String color, String rarity, String nm) {
		this.name   = name;
		this.set    = set;
		this.color  = color;
		this.rarity = rarity;
		this.nm   	= nm;
	}
	
	public String getname() {
		return name;
	}
	public String getset() {
		return set;
	}
	public String getcolor() {
		return color;
	}
	public String getrarity() {
		return rarity;
	}
	public String getnm() {
		return nm;
	}
	
	
	public void setname(String name) {
		this.name = name;
	}
	public void setset(String set) {
		this.set = set;
	}
	public void setcolor(String color) {
		this.color = color;
	}
	public void setrarity(String rarity) {
		this.rarity = rarity;
	}
	public void setnm(String nm) {
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
	
	public StringProperty colorProperty() {
		StringProperty sProp = new SimpleStringProperty(this.color);
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
	
	public int compareTo(ScgMtgCard other) {
		return this.getname().compareTo(other.getname());
	}
	
	public boolean isEmpty() {
		boolean result = false;
		
		if(name == null) {
			if(set == null) {
				if(color == null) {
					if(rarity == null) {
						if(nm == null) {
							result = true;
						}
					}
				}
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return "name: "    + name    + "\n"+
				"set: "    + set     + "\n"+
				"color"    + color   + "\n"+
				"rarity: " + rarity  + "\n"+
				"nm: "     + nm      + "\n"+
				"-----------------------------------";
	}
}
