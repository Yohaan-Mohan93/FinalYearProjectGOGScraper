package fyp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class CardData implements Comparable<CardData>{
	private String name;
	private String set;
	private String color;
	private String rarity;
	private String nm;
	private String ex;
	private String vg;
	private String g;
	private String scgnm;
	private double ckNmSGD;
	private double ckExSGD;
	private double ckVgSGD;
	private double ckGSGD;
	private double scgNmSGD;
	private double ckBuyNm;
	
	
	//default constructor needed for jackson
	public CardData() {
		
	}
	
	public CardData(String name) {
		this.name = name;
		this.set      = "";
		this.color    = "";
		this.rarity   = "";
		this.nm   	  = "";
		this.ex   	  = "";
		this.vg   	  = "";
		this.g    	  = "";
		this.scgnm    = "";
		this.ckNmSGD  = 0.0;
		this.ckExSGD  = 0.0;
		this.ckVgSGD  = 0.0;
		this.ckGSGD   = 0.0;
		this.scgNmSGD = 0.0;
		this.ckBuyNm  = 0.0; 
	}
	public CardData(String name, String set, String color, String rarity, String nm, String ex, String vg, String g, String scgnm, double ckBuyNm) {
		this.name 	  = name;
		this.set      = set;
		this.color    = color;
		this.rarity   = rarity;
		this.nm   	  = nm;
		this.ex   	  = ex;
		this.vg   	  = vg;
		this.g    	  = g;
		this.scgnm    = scgnm;
		this.ckNmSGD  = Helper.ConvertToSGD(nm);
		this.ckExSGD  = Helper.ConvertToSGD(ex);
		this.ckVgSGD  = Helper.ConvertToSGD(vg);
		this.ckGSGD   = Helper.ConvertToSGD(g);
		this.scgNmSGD = Helper.ConvertToSGD(scgnm);
		this.ckBuyNm  = ckBuyNm;
	}
	
	public CardData(String name, String set, String color, String rarity, String nm, String ex, String vg, String g, String scgnm, double ckNmSGD, double ckExSGD, double ckVgSGD, double ckGSGD, double scgNmSGD, double ckBuyNm) {
		this.name 	  = name;
		this.set      = set;
		this.color    = color;
		this.rarity   = rarity;
		this.nm   	  = nm;
		this.ex   	  = ex;
		this.vg   	  = vg;
		this.g    	  = g;
		this.scgnm    = scgnm;
		this.ckNmSGD  = ckNmSGD;
		this.ckExSGD  = ckExSGD;
		this.ckVgSGD  = ckVgSGD;
		this.ckGSGD   = ckGSGD;
		this.scgNmSGD = scgNmSGD ;
		this.ckBuyNm  = ckBuyNm;
	}
	
	public CardData(MtgCard card) {
		this.name 	  = card.getname();
		this.set      = card.getset();
		this.color    = null;
		this.rarity   = card.getrarity();
		this.nm   	  = card.getnm();
		this.ex   	  = card.getex();
		this.vg   	  = card.getvg();
		this.g    	  = card.getg();
		this.scgnm    = null;
		this.ckNmSGD  = Helper.ConvertToSGD(card.getnm());
		this.ckExSGD  = Helper.ConvertToSGD(card.getex());
		this.ckVgSGD  = Helper.ConvertToSGD(card.getvg());
		this.ckGSGD   = Helper.ConvertToSGD(card.getg());
		this.scgNmSGD = 0.0;
		this.ckBuyNm  = 0.0;
	}
	
	
	public String getName() {
		return name;
	}
	public String getSet() {
		return set;
	}
	public String getColor() {
		return color;
	}
	public String getRarity() {
		return rarity;
	}
	public String getNm() {
		return nm;
	}
	public String getEx() {
		return ex;
	}
	public String getVg() {
		return vg;
	}
	public String getG() {
		return g;
	}
	public String getScgNm() {
		return scgnm;
	}
	public double getCkNmSGD() {
		return ckNmSGD;
	}
	public double getCkExSGD() {
		return ckExSGD;
	}
	public double getCkVgSGD() {
		return ckVgSGD;
	}
	public double getCkGSGD() {
		return ckGSGD;
	}
	public double getScgNmSGD() {
		return scgNmSGD;
	}
	public double getCkBuyNm() {
		return ckBuyNm;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setSet(String set) {
		this.set = set;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public void setRarity(String rarity) {
		this.rarity = rarity;
	}
	public void setNm(String nm) {
		this.nm = nm;
	}
	public void setEx(String ex) {
		this.ex = ex;
	}
	public void setVg(String vg) {
		this.vg = vg;
	}
	public void setG(String g) {
		this.g = g;
	}
	public void setScgNm(String scgnm) {
		this.scgnm = scgnm;
	}
	public void setCkNmSGD(String cknmSGD) {
		this.ckNmSGD = Helper.ConvertToSGD(cknmSGD);
	}
	public void setCkExSGD(String ckexSGD) {
		this.ckExSGD = Helper.ConvertToSGD(ckexSGD);
	}
	public void setCkVgSGD(String ckvgSGD) {
		this.ckVgSGD = Helper.ConvertToSGD(ckvgSGD);
	}
	public void setCkGSGD(String ckgSGD) {
		this.ckGSGD = Helper.ConvertToSGD(ckgSGD);
	}
	public void setScgNmSGD(String scgnmSGD) {
		this.scgNmSGD = Helper.ConvertToSGD(scgnmSGD);
	}
	public void setCkBuyNM(double ckBuyNm) {
		this.ckBuyNm = ckBuyNm;
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
	
	public StringProperty scgnmProperty() {
		StringProperty sProp = new SimpleStringProperty(this.scgnm);
		return sProp;
	}
	
	public DoubleProperty cknmSGDProperty() {
		DoubleProperty sProp = new SimpleDoubleProperty(this.ckNmSGD);
		return sProp;
	}
	
	public DoubleProperty ckexSGDProperty() {
		DoubleProperty sProp = new SimpleDoubleProperty(this.ckExSGD);
		return sProp;
	}
	
	public DoubleProperty ckvgSGDProperty() {
		DoubleProperty sProp = new SimpleDoubleProperty(this.ckVgSGD);
		return sProp;
	}
	
	public DoubleProperty ckgSGDProperty() {
		DoubleProperty sProp = new SimpleDoubleProperty(this.ckGSGD);
		return sProp;
	}
	
	public DoubleProperty scgnmSGDProperty() {
		DoubleProperty sProp = new SimpleDoubleProperty(this.scgNmSGD);
		return sProp;
	}
	
	public DoubleProperty ckBuyNmSGDProperty() {
		DoubleProperty sProp = new SimpleDoubleProperty(this.ckBuyNm);
		return sProp;
	}
	public int compareTo(CardData other) {
		return Double.compare(this.ckNmSGD, other.getCkNmSGD());
	}
	
	@Override
	public String toString() {
		return "-----------------------------------\n"+
				"name: "    + name      + "\n"+
				"set: "     + set       + "\n"+
				"color: "   + color     + "\n"+
				"rarity: "  + rarity    + "\n"+
				"nm: "      + ckNmSGD   + "\n"+
				"ex: "      + ckExSGD   + "\n"+
				"vg: "      + ckVgSGD   + "\n"+
				"g: "       + ckVgSGD   + "\n"+
				"scgnm: "   + scgNmSGD  + "\n"+
				"ckBuyNm: " + ckBuyNm   + "\n"+
				"-----------------------------------";
	}
}
