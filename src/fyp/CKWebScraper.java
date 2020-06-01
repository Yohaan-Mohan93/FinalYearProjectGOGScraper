package fyp;

import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Task;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CKWebScraper extends Task<Void> {
	
	
	protected Void call() throws Exception{
		if(Main.cknf == true) {
			Main.logger.info("Card Kingdom Non Foil Scrape");
			PriceScraping(UrlLists.ckStartingUrl, Main.cknfFilename, Main.cknfUrlFilename, Main.cknfCardsFilename);
		}
		if(Main.ckf) {
			Main.logger.info("Card Kingdom Foil Scrape");
			PriceScraping(UrlLists.ckStartingFoilUrl, Main.ckfFilename, Main.ckfUrlFilename, Main.ckfCardsFilename);
		}
		if(Main.ckBuylist) {
			Main.logger.info("Card Kingdom Buylist Scrape");
			BuylistScraping(Main.buylistFilename, Main.buylistUrlFilename, Main.buyCardsFilename);
		}
		return null;
	}
	
	//Get the prices for all non-foil cards from CardKingdom 
	public void PriceScraping(String startingUrl, String filename, String urlFilename, String cardFilename) throws IOException{
		final long startTime = System.currentTimeMillis();                           	 											 //Get the starting time to calculate time taken
		FileWriter fw 		  = new FileWriter(filename,false);                              							 			//Open filewriter for the CSV file
		FileWriter urlWriter  = new FileWriter(urlFilename,false);                    							 					//Open filewriter for URLs visited file
		FileWriter cardWriter = new FileWriter(cardFilename, false);
		PrintWriter pw 		  = new PrintWriter(fw);                                        							 			//Open printwriter for the CSV file
		PrintWriter urlPW 	  = new PrintWriter(urlWriter);                              							 				//Open printwriter for the URLs visited file
		PrintWriter cardPW	  = new PrintWriter(cardWriter);
		int pageNumber 		  = 2;                                                          							 					 //Page Number for moving to next page 
		String nextUrl 		  = "";                                                         												 //The url for the next page
		String[] pageNumbers;                                                        							 					 //Array for page numbers for the while check 
		
		try {
			Connection con 				 = Jsoup.connect(startingUrl).userAgent("Mozilla/5.0");
			Connection.Response response = con.execute();
			Document doc 				 = null;
			
			//Double check to see if we are at the right url
			System.out.println(startingUrl);                                             							 					
			
			//Write the headings to the csv file
			pw.print("name,set,rarity,nm,ex,vg,g\n");
			cardPW.print("name, set, pagePlace, pageNumber \n");
			
			//Write the first url to the url file
			urlPW.print(startingUrl + "\n");
			System.out.println(response.statusCode());
			Main.logger.info("Scrape Status Code: " + response.statusCode() + ", Status Message: " + response.statusMessage());
			if(response.statusCode() == 200) {
				doc = con.get();
				Main.logger.info("Scrape Pg " + (pageNumber - 1) + ": " + startingUrl );
				
				do {
					Elements cardNames  = doc.select("span[class=productDetailTitle]");			 //Get the card names
					Elements prices 	= doc.select("span[class=stylePrice]");					 //Get the prices
			        Elements sets		= doc.select("div[class=productDetailSet]");			 //Get card's set
			        Elements pages 		= doc.select("button[class=btn btn-default col-xs-4]");	 //Get the Page numbers 
			        int priceCount		= 0;													 //Start the count for prices
			        int setCount 		= 0;													 //Start the count for sets
			        for(Element cardName: cardNames) { 
			          String name = cardName.text();                                           //CK Card name
			          String nmPrice = "";                                                     //USD Near Mint Price
			          String exPrice = "";                                                     //USD Excellent Price
			          String vgPrice = "";                                                     //USD Very Good Price
			          String gPrice  = "";                                                     //USD Good Price
			          String set 	   = "";                                                     //CK Set Name
			          String rarity  = "";                                                     //CK Card Rarity
			          String[] setRarity;                                                      //String Array to get the SetName and Rarity Split
			            
			          //Get the correct set and rarity for the card
			          if(pageNumber == 1888 && setCount == 14) {
			            setRarity  = sets.get(setCount).text().split("\\(");
			            set = setRarity[0].substring(0, setRarity[0].length() - 1);
			            rarity = "(" + setRarity[1];
			          }else {
			            setRarity  = sets.get(setCount).text().split("\\(");
			            set = setRarity[0];
			            rarity = "(" + setRarity[1];
			          }
			            
			          Main.logger.info(" CK Scrape Card Name: " + name + ", Set: " + set);
			           
			          //Get all of the USD prices for the cards
			          for(int i = 0; i < 4; i++) {
			            switch(i) {
			              case 0:
			              nmPrice = prices.get(priceCount).text().substring(1);
			              break;
			              case 1:
			              exPrice = prices.get(priceCount).text().substring(1);
			              break;
			              case 2:
			              vgPrice = prices.get(priceCount).text().substring(1);
			              break;
			              case 3:
			              gPrice = prices.get(priceCount).text().substring(1);
			              break;
			              }
			            priceCount++;
			          }
			            
			          //Correctly increase the set count to assign the correct set
			          setCount++;
			          
			          //If a name contains a comma
			          if(name.indexOf(',') >= 0) {
			          	name = '"' + name + '"';
			          }
			          if(name.indexOf(',') >= 0 && name.indexOf('"') >= 0) {
			          	name = name.replaceAll("\"", "");
			          	name = '"' + name + "\"\"\"\"\"";
			            }
			            pw.print(name + "," + set + "," + rarity + "," 
			            		+ nmPrice + "," + exPrice + "," + vgPrice + "," + gPrice + "\n");
			            
			            cardPW.print(name + ", " + set + "," + setCount + ", " + (pageNumber - 1) + "\n");
			            }
			            
			            //Find the next page
			            Elements aRoles = doc.select("a[role=button]");
			            for(Element aRole: aRoles) {
			            	if(aRole.attr("href").contains("page="+pageNumber)) {
			            		nextUrl = aRole.attr("href");
			            }
			            else {
			            	nextUrl = null;
			            	}
			            }
			            
			            //Get the next page
			            doc = Jsoup.connect(nextUrl).userAgent("Mozilla/5.0").timeout(8000).get();
			            urlPW.print(nextUrl + "\n");
			            
			            //Get the condition for the while check
			            pageNumbers = pages.text().split(" ");
			            
			            //Update scrapProgress
			            double currentPage = Double.parseDouble(pageNumbers[0]);
			            double lastPage    = Double.parseDouble(pageNumbers[2]);
			            
			            //Update the Text Area with the progress
			            if(Main.cknf) {
			            	this.updateProgress(currentPage,lastPage);
			            	this.updateMessage("Card Kingdom Non-Foil Progress: " + currentPage + " of " + lastPage);
			            }else {
			            	this.updateProgress(currentPage,lastPage);
			            	this.updateMessage("Card Kingdom Foil Progress: " + currentPage + " of " + lastPage);
			            }
			            
			            Main.logger.info("Scrape Pg " + pageNumber + ": " + nextUrl );
			            //Increment for the next loop
			            pageNumber++;
		      	}while(!pageNumbers[0].equals(pageNumbers[2]));
			}
			else {
				Main.logger.error("Error in First Attempt at CK Scraping ");
				
				doc = con.timeout(8000).get();
				Main.logger.info("Scrape Pg " + (pageNumber - 1) + ": " + startingUrl );
				
				
				do {
					Elements cardNames  = doc.select("span[class=productDetailTitle]");			 //Get the card names
					Elements prices 	= doc.select("span[class=stylePrice]");					 //Get the prices
			        Elements sets		= doc.select("div[class=productDetailSet]");			 //Get card's set
			        Elements pages 		= doc.select("button[class=btn btn-default col-xs-4]");	 //Get the Page numbers 
			        int priceCount		= 0;													 //Start the count for prices
			        int setCount 		= 0;													 //Start the count for sets
			        for(Element cardName: cardNames) { 
			          String name = cardName.text();                                           //CK Card name
			          String nmPrice = "";                                                     //USD Near Mint Price
			          String exPrice = "";                                                     //USD Excellent Price
			          String vgPrice = "";                                                     //USD Very Good Price
			          String gPrice  = "";                                                     //USD Good Price
			          String set 	   = "";                                                     //CK Set Name
			          String rarity  = "";                                                     //CK Card Rarity
			          String[] setRarity;                                                      //String Array to get the SetName and Rarity Split
			            
			          //Get the correct set and rarity for the card
			          if(pageNumber == 1888 && setCount == 14) {
			            setRarity  = sets.get(setCount).text().split("\\(");
			            set = setRarity[0].substring(0, setRarity[0].length() - 1);
			            rarity = "(" + setRarity[1];
			          }else {
			            setRarity  = sets.get(setCount).text().split("\\(");
			            set = setRarity[0];
			            rarity = "(" + setRarity[1];
			          }
			            
			          Main.logger.info(" CK Scrape Card Name: " + name + ", Set: " + set + "Count: " + setCount);
			           
			          //Get all of the USD prices for the cards
			          for(int i = 0; i < 4; i++) {
			            switch(i) {
			              case 0:
			              nmPrice = prices.get(priceCount).text().substring(1);
			              break;
			              case 1:
			              exPrice = prices.get(priceCount).text().substring(1);
			              break;
			              case 2:
			              vgPrice = prices.get(priceCount).text().substring(1);
			              break;
			              case 3:
			              gPrice = prices.get(priceCount).text().substring(1);
			              break;
			              }
			            priceCount++;
			          }
			            
			          //Correctly increase the set count to assign the correct set
			          setCount++;
			          
			          //If a name contains a comma
			          if(name.indexOf(',') >= 0) {
			          	name = '"' + name + '"';
			          }
			          if(name.indexOf(',') >= 0 && name.indexOf('"') >= 0) {
			          	name = name.replaceAll("\"", "");
			          	name = '"' + name + "\"\"\"\"\"";
			            }
			            pw.print(name + "," + set + "," + rarity + "," 
			            		+ nmPrice + "," + exPrice + "," + vgPrice + "," + gPrice + "\n");
			            
			            cardPW.print(name + ", " + set + "," + setCount + ", " + (pageNumber - 1) + "\n");
			            }
			            
			            //Find the next page
			            Elements aRoles = doc.select("a[role=button]");
			            for(Element aRole: aRoles) {
			            	if(aRole.attr("href").contains("page="+pageNumber)) {
			            		nextUrl = aRole.attr("href");
			            }
			            else {
			            	nextUrl = null;
			            	}
			            }
			            
			            //Get the next page
			            doc = Jsoup.connect(nextUrl).userAgent("Mozilla/5.0").timeout(8000).get();
			            urlPW.print(nextUrl + "\n");
			            
			            //Get the condition for the while check
			            pageNumbers = pages.text().split(" ");
			            
			            //Update scrapProgress
			            double currentPage = Double.parseDouble(pageNumbers[0]);
			            double lastPage    = Double.parseDouble(pageNumbers[2]);
			            
			            //Update the Text Area with the progress
			            if(Main.cknf) {
			            	this.updateProgress(currentPage,lastPage);
			            	this.updateMessage("Card Kingdom Non-Foil Progress: " + currentPage + " of " + lastPage);
			            }else {
			            	this.updateProgress(currentPage,lastPage);
			            	this.updateMessage("Card Kingdom Foil Progress: " + currentPage + " of " + lastPage);
			            }
			            
			            Main.logger.info("Scrape Pg " + pageNumber + ": " + nextUrl );
			            //Increment for the next loop
			            pageNumber++;
		      	}while(!pageNumbers[0].equals(pageNumbers[2]));
				
			}
			
		}catch(Exception e) {
			Main.logger.error("CK  Webscrape error: ", e);
		}
		
		
		//close the writers to prevent any memory leaks
		pw.close();
		fw.close();
		
		//close the writers to prevent any memory leaks
		cardWriter.close();
		cardPW.close();
		
		System.out.println(nextUrl);
		
		//Calculate time taken
		final long endTime = System.currentTimeMillis();
		double executionTime = (endTime - startTime)/60000.0;
		Main.logger.info("Total execution time: " + executionTime + " minutes");
		urlPW.print("Total execution time: " + executionTime + " minutes");
		
		//close the writers to prevent any memory leaks
		urlPW.close();
		urlWriter.close();
	}
	
	public void BuylistScraping(String filename, String urlFilename, String cardFilename) throws IOException {
		String startingUrl = "https://www.cardkingdom.com/purchasing/mtg_singles?filter%5Bipp%5D=50&filter%5Bsort%5D=name&filter%5Bnonfoil%5D=1&filter%5Bfoil%5D=1";
		
		final long startTime = System.currentTimeMillis();                           	 						 //Get the starting time to calculate time taken
    	Document doc = Jsoup.connect(startingUrl).userAgent("Chrome/80.0.3987.149").timeout(5000).get();      	 //Get the first page's html 
		int pageNumber = 2;                                                          							 //Page Number for moving to next page 
		String nextUrl = "";                                                        							 //The url for the next page 
		List<BuyListCards> cardList = new ArrayList<>();														 //List to store card data
		String[] pageNumbers;                                                        							 //Array for page numbers for the while check 
		
		Main.logger.info("Scrape Pg " + (pageNumber - 1) + ": " + startingUrl );
		
		System.out.println(startingUrl);                                            							 //Double check to see if we are at the right ur
		FileWriter urlWriter  = new FileWriter(urlFilename,false);                    							 //Open filewriter for URLs visited file
		PrintWriter urlPW 	  = new PrintWriter(urlWriter);                              						 //Open printwriter for the URLs visited file
		FileWriter cardWriter = new FileWriter(cardFilename, false);
		PrintWriter cardPW	  = new PrintWriter(cardWriter);
		
		//Write the first url to the url file
		urlPW.print(startingUrl + "\n");
		cardPW.print("name, set \n");
		
		do {
			Elements cardNames  = doc.select("span[class=productDetailTitle]");			 //Get the card names
			Elements cents		= doc.select("span[class=sellCentsAmount]");			 //Get the cents amount for the price
			Elements dollars 	= doc.select("span[class=sellDollarAmount]");			 //Get the dollar amount for the price
			Elements sets		= doc.select("div[class=productDetailSet]");			 //Get card's set
			Elements pages 		= doc.select("button[class=btn btn-default col-xs-4]");	 //Get the Page numbers
			int setCount 		= 0;													 //Start the count for sets
			for(Element cardName: cardNames) { 
				String name = cardName.text();                                           //CK Card name
				String nmPrice = "";                                                     //USD Near Mint Price
				String set 	   = "";                                                     //CK Set Name
				String rarity  = "";                                                     //CK Card Rarity
				double price   = 0.0;													 // CK Price in double format
				String[] setRarity;                                                      //String Array to get the SetName and Rarity Split
				
				
				//Get the correct set and rarity for the card
				if(pageNumber == 1888 && setCount == 14) {
					setRarity  = sets.get(setCount).text().split("\\(");
					set = setRarity[0].substring(0, setRarity[0].length() - 1);
					rarity = "(" + setRarity[1];
				}else {
					setRarity  = sets.get(setCount).text().split("\\(");
					set = setRarity[0];
					rarity = "(" + setRarity[1];
				}
				
				Main.logger.info("Scrape Card Name: " + name + ", Set: " + set);
				cardPW.print(name + ", " + set + "\n");
				
				//Correctly increase the set count to assign the correct set
				setCount++;
				
				nmPrice = dollars.get(setCount).text() + "." + cents.get(setCount).text();
				price = Double.parseDouble(nmPrice);
				
				cardList.add(new BuyListCards(name,set,rarity,price));
			}
						
			//Find the next page
			Elements aRoles = doc.select("a[role=button]");
			for(Element aRole: aRoles) {
				if(aRole.attr("href").contains("page="+pageNumber)) {
					nextUrl = aRole.attr("href");
				}
				else {
					nextUrl = null;
				}
			}
			
			//Get the next page
			doc = Jsoup.connect(nextUrl).userAgent("Chrome/80.0.3987.149").timeout(5000).get();
			urlPW.print(nextUrl + "\n");
			Main.logger.info("Scrape Pg " + pageNumber + ": " + nextUrl );
			
			//Get the condition for the while check
			pageNumbers = pages.text().split(" ");
			
			//Update scrapProgress
			double currentPage = Double.parseDouble(pageNumbers[0]);
			double lastPage = Double.parseDouble(pageNumbers[2]);
			
			//Update the Text Area with the progress
			this.updateProgress(currentPage,lastPage);
			this.updateMessage("Card Kingdom Buylist Progress: " + currentPage + " of " + lastPage);

			
			//Increment for the next loop
			pageNumber++;
		}while(!pageNumbers[0].equals(pageNumbers[2]));
		System.out.println(nextUrl);
		
		Helper.writeDataToFile(filename, cardList);
		
		//Calculate time taken
		final long endTime = System.currentTimeMillis();
		double executionTime = (endTime - startTime)/60000.0;
		Main.logger.info("Total Buylist execution time: " + executionTime + " minutes");
		urlPW.print("Total Buylist execution time: " + executionTime + " minutes");
		
		//close the writers to prevent any memory leaks
		cardWriter.close();
		cardPW.close();
		
		//close the writers to prevent any memory leaks
		urlPW.close();
		urlWriter.close();
			
	}
}
