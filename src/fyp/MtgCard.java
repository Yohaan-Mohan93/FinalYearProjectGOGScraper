package fyp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MtgCard {
	private String name;
	private String set;
	private String color;
	private String rarity;
	private String nm;
	private String ex;
	private String vg;
	private String g;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	//default constructor needed for jackson
	public MtgCard() {
		
	}
	
	public MtgCard(String name, String set, String color, String rarity, String nm, String ex, String vg, String g) {
		this.name 	= name;
		this.set    = set;
		this.color  = color;
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
	public String getcolor() {
		return color;
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
		String oldName = this.name;
		this.name = name;
		pcs.firePropertyChange("name", oldName, name);
	}
	public void setset(String set) {
		String oldSet = this.set;
		this.set = set;
		pcs.firePropertyChange("set", oldSet, set);
	}
	public void setcolor(String color) {
		String oldColor = this.color;
		this.color = color;
		pcs.firePropertyChange("color", oldColor, color);
	}
	public void setrarity(String rarity) {
		String oldRarity = this.rarity;
		this.rarity = rarity;
		pcs.firePropertyChange("rarity", oldRarity, rarity);
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
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
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
	public boolean isEmpty() {
		boolean result = false;
		
		if(name == null) {
			if(set == null) {
				if(rarity == null) {
					if(nm == null) {
						result = true;
					}
				}
			}
		}
		
		return result;
	}
}
