package fyp;


import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javafx.concurrent.Task;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class SCGWebScraper extends Task<Void>{

	protected Void call() throws Exception{
		if(Main.scgnf) {
			PriceScraping(UrlLists.SCGUrls, Main.scgnfFilename, Main.scgnfUrlFilename, Main.scgnfCardsFilename);
		}
		if(Main.scgf) {
			PriceScraping(UrlLists.SCGFoilUrls,Main.scgfFilename, Main.scgfUrlFilename, Main.scgfCardsFilename);
		}
		return null;
	}
	
	public void PriceScraping(String[] urlList, String filename, String urlFilename, String cardFilename) throws IOException{
		final long startTime 				  = System.currentTimeMillis();
		List<String> urls 					  = Arrays.asList(urlList);
		List<String> urlTime 				  = new ArrayList<>();
		List<String> cards 					  = new ArrayList<>();
		List<String> names 				  	  = new ArrayList<>();
		List<String> missedUrls				  = new ArrayList<>();
		AtomicInteger counter 				  = new AtomicInteger(0);
		AtomicInteger pageCount				  = new AtomicInteger(1);
		AtomicInteger total 				  = new AtomicInteger(urls.size());
		AtomicReference<String> messageString = new AtomicReference<>();
		FileWriter fw 						  = new FileWriter(filename,false);                        //Open filewriter for the CSV file
		FileWriter urlWriter 				  = new FileWriter(urlFilename,false);                    //Open filewriter for URLs visited file
		FileWriter cardWriter				  = new FileWriter(cardFilename, false);
		PrintWriter pw 						  = new PrintWriter(fw);                                  //Open printwriter for the CSV file
		PrintWriter urlPW 					  = new PrintWriter(urlWriter);                           //Open printwriter for the URLs visited file
		PrintWriter cardPW					  = new PrintWriter(cardWriter);
		
		pw.print("name,set,colour,rarity,nm\n");
		
		if(Main.scgnf) {
			messageString.set("Star City Games Non-Foil Scrape Progress: " + counter.get() + " of " + total.get());
		}else {
			messageString.set("Star City Games Foil Scrape Progress: " + counter.get() + " of " + total.get());
		}
		
		urls.parallelStream().forEach(url -> {
		try {
			Connection con = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
			Connection.Response response = con.execute();
			Document doc = null;
			Main.logger.info("SCG Scrape Url Number: " + counter.get() + ", Page Number: " + pageCount.get() + ", Url: " + url);
			Main.logger.info("URL: " + url + ", Response Code: " + response.statusCode());
			if(response.statusCode() == 200) {
				doc = con.get();		 								//Get the first page's html
				String nextUrl = "";                                    //The url for the next page 
				int count      = 0;
				this.updateMessage(messageString.get());
				this.updateProgress(counter.get(),total.get());
				
				System.out.println(url);
				Main.logger.info("SCG Url Visited: " + url);
				urlTime.add(url + "\n");
				do {
					Elements cardNames   = doc.select("tr[data-name]");
					Elements prices		 = doc.select("div[class=listItem-details]").select("a[href]");
					Elements sets        = doc.select("section[data-category]");
					Elements colours 	 = doc.select("td[class=td-listItem --Color]");
					Elements rarities 	 = doc.select("td[class=td-listItem --Rarity]");
					
					
					for(Element cardName : cardNames) {
						String name     = cardName.attr("data-name");
						String set 	    = sets.get(0).attr("data-category");
						String nmPrice  = "";
						String rarity   = "";
						String colour	= "";
						
						//Treat Cards with comma's correctly
						if(name.indexOf(',') >= 0 && name.indexOf('"') >= 0) {
							name = name.replaceAll("\"", "");
							name = '"' + name + "\"\"\"\"\"";
						}
						if(name.contains(",")) {
							name = '"' + name + '"';
						}
						
						//Go the individual card page and get card's price
						String priceUrl 			  = prices.get(count).attr("href");
						Connection priceCon 		  = Jsoup.connect(priceUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
						Connection.Response priceResp = priceCon.execute();
						Elements cardPrices 		  = null;
						Document priceList 			  = null;
						if(priceResp.statusCode() == 200) {
							priceList = priceCon.get();
							cardPrices = priceList.select("div[data-product-price]");
							nmPrice = cardPrices.get(0).attr("data-product-price");
						}
						else {
							priceList = priceCon.timeout(5000).get();
							cardPrices = priceList.select("div[data-product-price]");
							nmPrice = cardPrices.get(0).attr("data-product-price");
						}
						
						
						//Get the Color and Rarity of the card
						if(colours.get(count).text().isEmpty()) {
							colour = "N.A.";
						}
						else {
							colour = colours.get(count).text().substring(7);
						}
						if(colour.contains(",") == true) {
							colour = "Multi Colour";
						}
						if(rarities.get(count).text().equals("")) {
							rarity = ("N.A.");
						}
						else {
							rarity = rarities.get(count).text().substring(6);
						}
						
						String temp = name + ", " + set + "\n";
						names.add(temp);
						Main.logger.info("SCG Scrape Card Name: " + name + ", Set: " + set);
						
						count++;
						cards.add(name + ", " + set +  ", " +  colour + ", " + rarity + ", "
								+ nmPrice + "\n");
					}
					
					Elements pages = doc.select("link[rel=next]");
					if(!pages.isEmpty()) {
						nextUrl = pages.first().attr("href");
						urlTime.add(nextUrl + "\n");
						pageCount.getAndIncrement();
						Main.logger.info("SCG Scrape Url Number: " + counter.get() + ", Page Number: " + pageCount.get() + ", Url: " + nextUrl);
						
						System.out.println(nextUrl);
						Connection nextCon = Jsoup.connect(nextUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
						Connection.Response nextResp = nextCon.execute();
						if(nextResp.statusCode() == 200) {
							doc = nextCon.get();
						}
						else {
							doc = nextCon.timeout(10000).get();
						}
					}
					else {
						nextUrl = "";
						counter.getAndIncrement();
						Main.logger.info("SCG Scrape Url Number: " + counter.get() + ", Page Number: " + pageCount.get() + ", Url: " + nextUrl);
						pageCount.set(1);
					
						if(Main.scgnf) {
							messageString.set("Star City Games Non-Foil Scrape Progress: " + counter.get() + " of " + total.get());
						}else {
							messageString.set("Star City Games Foil Scrape Progress: " + counter.get() + " of " + total.get());
						}
					}
					count = 0;
				}while(!nextUrl.isEmpty());
			}
			else {
				doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36").timeout(10000).get();
				String nextUrl = "";                                    //The url for the next page 
				int count      = 0;
				this.updateMessage(messageString.get());
				this.updateProgress(counter.get(),total.get());
				
				System.out.println(url);
				Main.logger.info("SCG Url Visited: " + url);
				urlTime.add(url + "\n");
				do {
					Elements cardNames   = doc.select("tr[data-name]");
					Elements prices		 = doc.select("div[class=listItem-details]").select("a[href]");
					Elements sets        = doc.select("section[data-category]");
					Elements colours 	 = doc.select("td[class=td-listItem --Color]");
					Elements rarities 	 = doc.select("td[class=td-listItem --Rarity]");
					
					
					for(Element cardName : cardNames) {
						String name     = cardName.attr("data-name");
						String set 	    = sets.get(0).attr("data-category");
						String nmPrice  = "";
						String rarity   = "";
						String colour	= "";
						
						//Treat Cards with comma's correctly
						if(name.indexOf(',') >= 0 && name.indexOf('"') >= 0) {
							name = name.replaceAll("\"", "");
							name = '"' + name + "\"\"\"\"\"";
						}
						if(name.contains(",")) {
							name = '"' + name + '"';
						}
						
						//Go the individual card page and get card's price
						String priceUrl 			  = prices.get(count).attr("href");
						Connection priceCon 		  = Jsoup.connect(priceUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
						Connection.Response priceResp = priceCon.execute();
						Elements cardPrices 		  = null;
						Document priceList 			  = null;
						if(priceResp.statusCode() == 200) {
							priceList = priceCon.get();
							cardPrices = priceList.select("div[data-product-price]");
							nmPrice = cardPrices.get(0).attr("data-product-price");
							Main.logger.info("URL: " + priceUrl + " connected on first try");
						}
						else {
							priceList = priceCon.timeout(5000).get();
							cardPrices = priceList.select("div[data-product-price]");
							nmPrice = cardPrices.get(0).attr("data-product-price");
							Main.logger.error("URL: " + priceUrl + " needed second try.");
						}
						
						
						//Get the Color and Rarity of the card
						if(colours.get(count).text().isEmpty()) {
							colour = "N.A.";
						}
						else {
							colour = colours.get(count).text().substring(7);
						}
						if(colour.contains(",") == true) {
							colour = "Multi Colour";
						}
						if(rarities.get(count).text().equals("")) {
							rarity = ("N.A.");
						}
						else {
							rarity = rarities.get(count).text().substring(6);
						}
						
						String temp = name + ", " + set + pageCount + "\n";
						names.add(temp);
						Main.logger.info("SCG Scrape Card Name: " + name + ", Set: " + set);
						
						count++;
						cards.add(name + ", " + set +  ", " +  colour + ", " + rarity + ", "
								+ nmPrice + "\n");
					}
					
					Elements pages = doc.select("link[rel=next]");
					if(!pages.isEmpty()) {
						nextUrl = pages.first().attr("href");
						urlTime.add(nextUrl + "\n");
						pageCount.getAndIncrement();
						Main.logger.info("SCG Scrape Url Number: " + counter.get() + ", Page Number: " + pageCount.get() + ", Url: " + nextUrl);
						
						System.out.println(nextUrl);
						Connection nextCon = Jsoup.connect(nextUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
						Connection.Response nextResp = nextCon.execute();
						if(nextResp.statusCode() == 200) {
							doc = nextCon.get();
						}
						else {
							doc = nextCon.timeout(8000).get();
						}
					}
					else {
						nextUrl = "";
						counter.getAndIncrement();
						Main.logger.info("SCG Scrape Url Number: " + counter.get() + ", Page Number: " + pageCount.get() + ", Url: " + nextUrl);
						pageCount.set(1);
						
						if(Main.scgnf) {
							messageString.set("Star City Games Non-Foil Scrape Progress: " + counter.get() + " of " + total.get());
						}else {
							messageString.set("Star City Games Foil Scrape Progress: " + counter.get() + " of " + total.get());
						}
					}
					count = 0;
				}while(!nextUrl.isEmpty());
			}
		}catch(Exception ioe) {
			urlTime.add("Error with URL: " + url + ", Error: " + ioe.getMessage() +"\n");
			missedUrls.add(url);
			Main.logger.error("Error with URL: " + url, ioe);
		}
			
		});
		
		while(!missedUrls.isEmpty()) {
			missedUrls.parallelStream().forEach(url ->{
				try {
					Connection con = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
					Connection.Response response = con.execute();
					Document doc = null;
					Main.logger.info("SCG Scrape Url Number: " + counter.get() + ", Page Number: " + pageCount.get() + ", Url: " + url);
					if(response.statusCode() == 200) {
						doc = con.get();		 								//Get the first page's html
						String nextUrl = "";                                    //The url for the next page 
						int count      = 0;
						this.updateMessage(messageString.get());
						this.updateProgress(counter.get(),total.get());
						
						System.out.println(url);
						Main.logger.info("SCG Url Visited: " + url);
						urlTime.add(url + "\n");
						do {
							Elements cardNames   = doc.select("tr[data-name]");
							Elements prices		 = doc.select("div[class=listItem-details]").select("a[href]");
							Elements sets        = doc.select("section[data-category]");
							Elements colours 	 = doc.select("td[class=td-listItem --Color]");
							Elements rarities 	 = doc.select("td[class=td-listItem --Rarity]");
							
							
							for(Element cardName : cardNames) {
								String name     = cardName.attr("data-name");
								String set 	    = sets.get(0).attr("data-category");
								String nmPrice  = "";
								String rarity   = "";
								String colour	= "";
								
								//Treat Cards with comma's correctly
								if(name.indexOf(',') >= 0 && name.indexOf('"') >= 0) {
									name = name.replaceAll("\"", "");
									name = '"' + name + "\"\"\"\"\"";
								}
								if(name.contains(",")) {
									name = '"' + name + '"';
								}
								
								//Go the individual card page and get card's price
								String priceUrl 			  = prices.get(count).attr("href");
								Connection priceCon 		  = Jsoup.connect(priceUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
								Connection.Response priceResp = priceCon.execute();
								Elements cardPrices 		  = null;
								Document priceList 			  = null;
								if(priceResp.statusCode() == 200) {
									priceList = priceCon.get();
									cardPrices = priceList.select("div[data-product-price]");
									nmPrice = cardPrices.get(0).attr("data-product-price");
								}
								else {
									priceList = priceCon.timeout(5000).get();
									cardPrices = priceList.select("div[data-product-price]");
									nmPrice = cardPrices.get(0).attr("data-product-price");
								}
								
								
								//Get the Color and Rarity of the card
								if(colours.get(count).text().isEmpty()) {
									colour = "N.A.";
								}
								else {
									colour = colours.get(count).text().substring(7);
								}
								if(colour.contains(",") == true) {
									colour = "Multi Colour";
								}
								if(rarities.get(count).text().equals("")) {
									rarity = ("N.A.");
								}
								else {
									rarity = rarities.get(count).text().substring(6);
								}
								
								String temp = name + ", " + set + "\n";
								names.add(temp);
								Main.logger.info("SCG Scrape Card Name: " + name + ", Set: " + set);
								
								count++;
								cards.add(name + ", " + set +  ", " +  colour + ", " + rarity + ", "
										+ nmPrice + "\n");
							}
							
							Elements pages = doc.select("link[rel=next]");
							if(!pages.isEmpty()) {
								nextUrl = pages.first().attr("href");
								urlTime.add(nextUrl + "\n");
								pageCount.getAndIncrement();
								Main.logger.info("SCG Scrape Url Number: " + counter.get() + ", Page Number: " + pageCount.get() + ", Url: " + nextUrl);
								
								System.out.println(nextUrl);
								Connection nextCon = Jsoup.connect(nextUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
								Connection.Response nextResp = nextCon.execute();
								if(nextResp.statusCode() == 200) {
									doc = nextCon.get();
								}
								else {
									doc = nextCon.timeout(10000).get();
								}
							}
							else {
								nextUrl = "";
								counter.getAndIncrement();
								Main.logger.info("SCG Scrape Url Number: " + counter.get() + ", Page Number: " + pageCount.get() + ", Url: " + nextUrl);
								pageCount.set(1);
							
								if(Main.scgnf) {
									messageString.set("Star City Games Non-Foil Scrape Progress: " + counter.get() + " of " + total.get());
								}else {
									messageString.set("Star City Games Foil Scrape Progress: " + counter.get() + " of " + total.get());
								}
							}
							count = 0;
						}while(!nextUrl.isEmpty());
					}
					else {
						doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36").timeout(10000).get();
						String nextUrl = "";                                    //The url for the next page 
						int count      = 0;
						this.updateMessage(messageString.get());
						this.updateProgress(counter.get(),total.get());
						
						System.out.println(url);
						Main.logger.info("SCG Url Visited: " + url);
						urlTime.add(url + "\n");
						do {
							Elements cardNames   = doc.select("tr[data-name]");
							Elements prices		 = doc.select("div[class=listItem-details]").select("a[href]");
							Elements sets        = doc.select("section[data-category]");
							Elements colours 	 = doc.select("td[class=td-listItem --Color]");
							Elements rarities 	 = doc.select("td[class=td-listItem --Rarity]");
							
							
							for(Element cardName : cardNames) {
								String name     = cardName.attr("data-name");
								String set 	    = sets.get(0).attr("data-category");
								String nmPrice  = "";
								String rarity   = "";
								String colour	= "";
								
								//Treat Cards with comma's correctly
								if(name.indexOf(',') >= 0 && name.indexOf('"') >= 0) {
									name = name.replaceAll("\"", "");
									name = '"' + name + "\"\"\"\"\"";
								}
								if(name.contains(",")) {
									name = '"' + name + '"';
								}
								
								//Go the individual card page and get card's price
								String priceUrl 			  = prices.get(count).attr("href");
								Connection priceCon 		  = Jsoup.connect(priceUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
								Connection.Response priceResp = priceCon.execute();
								Elements cardPrices 		  = null;
								Document priceList 			  = null;
								if(priceResp.statusCode() == 200) {
									priceList = priceCon.get();
									cardPrices = priceList.select("div[data-product-price]");
									nmPrice = cardPrices.get(0).attr("data-product-price");
									Main.logger.info("URL: " + priceUrl + " connected on first try");
								}
								else {
									priceList = priceCon.timeout(5000).get();
									cardPrices = priceList.select("div[data-product-price]");
									nmPrice = cardPrices.get(0).attr("data-product-price");
									Main.logger.error("URL: " + priceUrl + " needed second try.");
								}
								
								
								//Get the Color and Rarity of the card
								if(colours.get(count).text().isEmpty()) {
									colour = "N.A.";
								}
								else {
									colour = colours.get(count).text().substring(7);
								}
								if(colour.contains(",") == true) {
									colour = "Multi Colour";
								}
								if(rarities.get(count).text().equals("")) {
									rarity = ("N.A.");
								}
								else {
									rarity = rarities.get(count).text().substring(6);
								}
								
								String temp = name + ", " + set + pageCount + "\n";
								names.add(temp);
								Main.logger.info("SCG Scrape Card Name: " + name + ", Set: " + set);
								
								count++;
								cards.add(name + ", " + set +  ", " +  colour + ", " + rarity + ", "
										+ nmPrice + "\n");
							}
							
							Elements pages = doc.select("link[rel=next]");
							if(!pages.isEmpty()) {
								nextUrl = pages.first().attr("href");
								urlTime.add(nextUrl + "\n");
								pageCount.getAndIncrement();
								Main.logger.info("SCG Scrape Url Number: " + counter.get() + ", Page Number: " + pageCount.get() + ", Url: " + nextUrl);
								
								System.out.println(nextUrl);
								Connection nextCon = Jsoup.connect(nextUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
								Connection.Response nextResp = nextCon.execute();
								if(nextResp.statusCode() == 200) {
									doc = nextCon.get();
								}
								else {
									doc = nextCon.timeout(8000).get();
								}
							}
							else {
								nextUrl = "";
								counter.getAndIncrement();
								Main.logger.info("SCG Scrape Url Number: " + counter.get() + ", Page Number: " + pageCount.get() + ", Url: " + nextUrl);
								pageCount.set(1);
								
								if(Main.scgnf) {
									messageString.set("Star City Games Non-Foil Scrape Progress: " + counter.get() + " of " + total.get());
								}else {
									messageString.set("Star City Games Foil Scrape Progress: " + counter.get() + " of " + total.get());
								}
							}
							count = 0;
						}while(!nextUrl.isEmpty());
						missedUrls.remove(url);
					}
				}
				catch(Exception e) {
					urlTime.add(e.getMessage() +"\n");
					urlTime.add(url + " was missed" + "\n");
				}
			});
		}
		
		for(String card: cards) {
			pw.print(card);
		}
		pw.close();
		fw.close();
		
		cardPW.print("name, set, pageCount \n");
		for(String name: names) {
			cardPW.print(name);
		}
		cardWriter.close();
		cardPW.close();
		
		final long endTime = System.currentTimeMillis();
		double executionTime = (endTime - startTime)/60000.0;
		urlTime.add("Total execution time: " + executionTime + " minutes");
		Main.logger.info("Total execution time: " + executionTime + " minutes");
		System.out.println("Total execution time: " + executionTime + " minutes");
		
		for(String line: urlTime) {
			urlPW.print(line);
		}
		urlPW.close();
		urlWriter.close();
	}
}