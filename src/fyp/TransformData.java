package fyp;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javafx.concurrent.Task;



public class TransformData extends Task<Void>{
	
	public Void call() {
		 if(Main.fillCKing){
			  fillCKCardData();
		  }
		 if(Main.fillCKBuylist || Main.updateCKBuylist) {
			 fillCKBuylist();
		 }
		 if(Main.fillSCGames) {
			  fillSCGCardData();
		  }
		 if(Main.updateData) {
			  updateCardData();
		  }
		  if(Main.updateCKing) {
			  updateCKData();
		  }
		  if(Main.updateSCGames) {
			  updateSCGData();
		  }
		  if(Main.writeFile) {
			  writeCardDataToFile(Main.allCards, Main.cardDataFilename);
		  }
		  if(Main.updateFoilData) {
			  updateFoilCardData();
		  }
		  if(Main.fillFoilCKing) {
			  fillFoilCKCards();
		  }
		  if(Main.fillFoilSCGames) {
			  fillFoilSCGCard();
		  }
		  if(Main.updateFoilCKing) {
			  updateFoilCKData();
		  }
		  if(Main.updateFoilSCGames) {
			  updateFoilSCGData();
		  }
		  if(Main.writeFoilFile) {
			  writeCardDataToFile(Main.allFoilCards, Main.foilDataFilename);
		  }
		  if(Main.correctData) {
			  correctData(Main.allCards, Main.allFoilCards);
		  }
		return null;
	}
	
	public void fillCKCardData() {
		int count = 0;
		for(MtgCard card: Main.CKCards) {
			Main.allCards.add(new CardData(card));
			Main.logger.info("Name: " + card.getname() + ", Set: " + card.getset() + " added");
			count++;
			this.updateProgress(count, Main.CKCards.size());
		}
	}
	
	public void fillFoilCKCards() {
		int count = 0;
		for(MtgCard card: Main.CKFoilCards	) {
			Main.allFoilCards.add(new CardData(card));
			Main.logger.info("Name: " + card.getname() + ", Set: " + card.getset() + " added");
			count++;
			this.updateProgress(count, Main.CKFoilCards.size());
		}
		System.out.println(Main.allFoilCards.size());
	}
	
	public void correctData(List<CardData> nonFoil, List<CardData> Foil) {
		int count = 0;
		List<CardData> foils 	= nonFoil.stream().filter(e -> e.getName().contains("Foil")).collect(Collectors.toList());
		System.out.println("foils size: " + foils.size());
		foils.removeIf(e -> e.getName().contains("Non-Foil"));
		List<CardData> boxToppers = nonFoil.stream().filter(e -> e.getSet().contains("Ultimate Box Topper ")).collect(Collectors.toList());
		System.out.println("Box Toppers size: " + boxToppers.size());
		foils.addAll(boxToppers);
		System.out.println("foils size: " + foils.size());
		for(CardData card: foils) {
			Foil.add(card);
			System.out.println(card.getName());
			Main.logger.info("Name: " + card.getName() + ", Set: " + card.getSet() + " added");
			count++;
			this.updateProgress(count, foils.size());
		}
		System.out.println("remove foils");
		nonFoil.removeIf(e -> foils.contains(e));
		System.out.println("sort all foils");
		Collections.sort(Main.allFoilCards, Comparator.comparing(CardData::getName));
	}
	
	public static void fillFoilSCGCard() {
		AtomicInteger counter = new AtomicInteger(0);
		AtomicInteger total = new AtomicInteger(Main.allFoilCards.size());
		Main.allFoilCards.parallelStream().forEachOrdered(current ->{
			ScgMtgCard one = new ScgMtgCard();
			String thisName = current.getName().replaceAll(" ", "");
			String thisSet = current.getSet();
			for(ScgMtgCard currentCard: Main.SCGFoilCards) {
				String name = currentCard.getname().replaceAll(" ", "");
				String set  = currentCard.getset();
				if(name.contains(thisName)) {
					if(set.contains(thisSet)) {
					one = currentCard;
					System.out.println(one.getname());
					break;
					}
				}
			}
			if(one.isEmpty()) {
				return;
			}
			current.setColor(one.getcolor());
			current.setScgNm(one.getnm());
			current.setScgNmSGD(one.getnm());
			counter.getAndIncrement();
			System.out.println("Done " + counter.get() + " of " + total.get() + " cards");
		});
	}
	
	public static void fillSCGCardData() {
		AtomicInteger counter = new AtomicInteger(0);
		AtomicInteger total   = new AtomicInteger(Main.allCards.size());
		Main.allCards.parallelStream().forEachOrdered(current ->{
			ScgMtgCard one 	= new ScgMtgCard();
			String thisName = current.getName();
			thisName 		= thisName.replaceAll(" ", "").toLowerCase();
			String thisSet 	= current.getSet();
			thisSet			= thisSet.replaceAll(" ", "").toLowerCase();
			for(ScgMtgCard currentCard: Main.SCGCards) {
				String name = currentCard.getname();
				name		= name.replaceAll(" ", "").toLowerCase();
				String set  = currentCard.getset();
				set			= set.replaceAll(" ", "").toLowerCase();
				if(name.contains(thisName)) {
					if(set.contains(thisSet)) {
					one = currentCard;
					System.out.println(one.getname());
					break;
					}
				}
			}
			if(one.isEmpty()) {
				return;
			}
			current.setColor(one.getcolor());
			current.setScgNm(one.getnm());
			current.setScgNmSGD(one.getnm());
			counter.getAndIncrement();
			System.out.println("Done " + counter.get() + " of " + total.get() + " cards");
		});
	}
	
	public static void writeCardDataToFile(List<CardData> cardList, String filename) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			writer.writeValue(Paths.get(filename).toFile(),cardList);
		}catch(Exception e) {
			
		}
	}
	
	public void updateCardData() {
		List<String> cards = Main.allCards.stream().map(CardData::getName).collect(Collectors.toList());
		List<MtgCard> unavailable = Main.CKCards.stream()
									.filter(e -> cards.contains(e.getname()))
									.collect(Collectors.toList());
		AtomicInteger counter = new AtomicInteger(0);
		AtomicInteger total = new AtomicInteger(unavailable.size());
		if(unavailable.isEmpty()) {
			return;
		}
		else {
			for(MtgCard card: unavailable) {
				Main.allCards.add(new CardData(card));
			}
			Main.allCards.parallelStream().forEach(current ->{
				if(current.getColor() == null) {
					String thisName = current.getName().replaceAll(" ", "");
					String thisSet = current.getSet();
					ScgMtgCard one = Main.SCGCards.stream().filter(currentCard -> 
						currentCard.getname().replaceAll(" ", "").contains(thisName) && currentCard.getset().contains(thisSet)
						
					).findFirst().orElse(null);
					current.setColor(one.getcolor());
					current.setScgNm(one.getnm());
					current.setScgNmSGD(one.getnm());
					counter.getAndIncrement();
					this.updateProgress(counter.get(),total.get());
				}
				else {
					return;
				}
			});
		}
		
	}
	
	public void updateFoilCardData() {
		List<String> cards = Main.allFoilCards.stream().map(CardData::getName).collect(Collectors.toList());
		List<MtgCard> unavailable = Main.CKFoilCards.stream()
									.filter(e -> cards.contains(e.getname()))
									.collect(Collectors.toList());
		AtomicInteger counter = new AtomicInteger(0);
		AtomicInteger total = new AtomicInteger(unavailable.size());
		if(unavailable.isEmpty()) {
			return;
		}
		else {
			for(MtgCard card: unavailable) {
				Main.allFoilCards.add(new CardData(card));
			}
			Main.allFoilCards.parallelStream().forEach(current ->{
				if(current.getColor() == null) {
					String thisName = current.getName().replaceAll(" ", "");
					String thisSet = current.getSet();
					ScgMtgCard one = Main.SCGFoilCards.stream().filter(currentCard -> 
						currentCard.getname().replaceAll(" ", "").contains(thisName) && currentCard.getset().contains(thisSet)
						
					).findFirst().orElse(null);
					current.setColor(one.getcolor());
					current.setScgNm(one.getnm());
					current.setScgNmSGD(one.getnm());
					counter.getAndIncrement();
					this.updateProgress(counter.get(),total.get());
				}
				else {
					return;
				}
			});
		}
		
	}
	
	public void updateCKData() {
		AtomicInteger counter = new AtomicInteger(0);
		AtomicInteger total = new AtomicInteger(Main.allCards.size());
		Main.allCards.parallelStream().forEach(current ->{
			String thisName = current.getName().replaceAll(" ", "");
			String thisSet = current.getSet();
			MtgCard one = Main.CKCards.stream().filter(currentCard -> 
				currentCard.getname().replaceAll(" ", "").contains(thisName) && currentCard.getset().contains(thisSet)
				
			).findFirst().orElse(null);
			if(one.isEmpty()) {
				return;
			}
			Main.logger.info("Name: " + one.getname() + ", Set: " + one.getset());
			current.setNm(one.getnm());
			current.setEx(one.getex());
			current.setVg(one.getvg());
			current.setG(one.getg());
			current.setCkNmSGD(one.getnm());
			current.setCkExSGD(one.getex());
			current.setCkVgSGD(one.getvg());
			current.setCkGSGD(one.getg());
			counter.getAndIncrement();
			this.updateProgress(counter.get(),total.get());
		});
	}
	
	public void updateFoilCKData() {
		AtomicInteger counter = new AtomicInteger(0);
		AtomicInteger total = new AtomicInteger(Main.allFoilCards.size());
		Main.allFoilCards.parallelStream().forEach(current ->{
			String thisName = current.getName().replaceAll(" ", "");
			String thisSet = current.getSet();
			MtgCard one = Main.CKFoilCards.stream().filter(currentCard -> 
				currentCard.getname().replaceAll(" ", "").contains(thisName) && currentCard.getset().contains(thisSet)
				
			).findFirst().orElse(null);
			if(one.isEmpty()) {
				return;
			}
			Main.logger.info("Name: " + one.getname() + ", Set: " + one.getset());			
			current.setNm(one.getnm());
			current.setEx(one.getex());
			current.setVg(one.getvg());
			current.setG(one.getg());
			current.setCkNmSGD(one.getnm());
			current.setCkExSGD(one.getex());
			current.setCkVgSGD(one.getvg());
			current.setCkGSGD(one.getg());
			counter.getAndIncrement();
			this.updateProgress(counter.get(),total.get());
		});
	}
	
	public void updateSCGData() {
		AtomicInteger counter = new AtomicInteger(0);
		AtomicInteger total = new AtomicInteger(Main.allCards.size());
		Main.allCards.parallelStream().forEach(current ->{
			String thisName = current.getName().replaceAll(" ", "");
			String thisSet = current.getSet();
			ScgMtgCard one = Main.SCGCards.stream().filter(currentCard -> 
				currentCard.getname().replaceAll(" ", "").contains(thisName) && currentCard.getset().contains(thisSet)
				
			).findFirst().orElse(null);
			if(one.isEmpty()) {
				return;
			}
			current.setColor(one.getcolor());
			current.setScgNm(one.getnm());
			current.setScgNmSGD(one.getnm());
			counter.getAndIncrement();
			this.updateProgress(counter.get(),total.get());
		});
	}
	
	public void updateFoilSCGData() {
		AtomicInteger counter = new AtomicInteger(0);
		AtomicInteger total = new AtomicInteger(Main.allFoilCards.size());
		Main.allFoilCards.parallelStream().forEach(current ->{
			String thisName = current.getName().replaceAll(" ", "");
			String thisSet = current.getSet();
			ScgMtgCard one = Main.SCGFoilCards.stream().filter(currentCard -> 
				currentCard.getname().replaceAll(" ", "").contains(thisName) && currentCard.getset().contains(thisSet)
				
			).findFirst().orElse(null);
			if(one.isEmpty()) {
				return;
			}
			current.setColor(one.getcolor());
			current.setScgNm(one.getnm());
			current.setScgNmSGD(one.getnm());
			counter.getAndIncrement();
			this.updateProgress(counter.get(),total.get());
		});
	}
	
	public void fillCKBuylist() {
		System.out.println("Starting");
		AtomicInteger counter = new AtomicInteger(0);
		AtomicInteger total = new AtomicInteger(Main.allCards.size());
		Main.CKBuylistCards.parallelStream().forEach(current ->{
			CardData one = null;
			String thisName = current.getName().replaceAll(" ", "");
			String thisSet = current.getSet();
			for(CardData currentCard: Main.allCards) {
				String name = currentCard.getName().replaceAll(" ", "");
				String set  = currentCard.getSet();
				if(name.contains(thisName)) {
					if(set.contains(thisSet)) {
					one = currentCard;
					System.out.println(one.getName());
					break;
					}
				}
			}
			
			if(one == null) {
				return;
			}
			one.setCkBuyNM(current.getNm());
			counter.getAndIncrement();
			this.updateProgress(counter.get(),total.get());
		});
	}
}
