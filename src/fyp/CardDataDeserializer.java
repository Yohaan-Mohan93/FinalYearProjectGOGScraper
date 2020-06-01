package fyp;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.DoubleNode;

@SuppressWarnings("serial")
public class CardDataDeserializer extends StdDeserializer<CardData>{
	
	public CardDataDeserializer() {
		this(null);
	}
	
	public CardDataDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public CardData deserialize(JsonParser jp, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
		JsonNode node   = jp.getCodec().readTree(jp);
		String name 	= node.get("name").asText();
		String set  	= node.get("set").asText();
		String color    = node.get("color").asText();
		String rarity   = node.get("rarity").asText();
		String nm 		= node.get("nm").asText();
		String ex 		= node.get("ex").asText();
		String vg 		= node.get("vg").asText();
		String g 		= node.get("g").asText();
		String scgnm    = "";
		if(node.get("scgnm").asText() == null){
			scgnm = "";
		}
		else {
			scgnm    = node.get("scgnm").asText();
		}
		double ckNmSGD  = (Double) ((DoubleNode)node.get("ckNmSGD")).doubleValue();
		double ckExSGD  = (Double) ((DoubleNode)node.get("ckExSGD")).doubleValue();
		double ckVgSGD  = (Double) ((DoubleNode)node.get("ckVgSGD")).doubleValue();
		double ckGSGD   = (Double) ((DoubleNode)node.get("ckGSGD")).doubleValue();
		double scgNmSGD = (Double) ((DoubleNode)node.get("scgNmSGD")).doubleValue();
		double ckBuyNm  = (Double) ((DoubleNode)node.get("ckBuyNm")).doubleValue();

		return new CardData(name, set, color, rarity, nm, ex, vg, g, scgnm, ckNmSGD, ckExSGD, ckVgSGD, ckGSGD, scgNmSGD, ckBuyNm);
	}
	
}
