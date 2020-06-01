package fyp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.fxmisc.richtext.CodeArea;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Helper {
	
	//Rounding to ten cents
	public static double roundToTen(double number) {
		double round = 0.1;
		double twoDP = 100.0;
		
		number = round * Math.round(number/round);
		number = Math.round(number * twoDP) / twoDP;
		
		return number;
	}
	
	//Rounding to 50 cents
	public static double roundToFifty(double number) {
		double round = 0.5;
		
		number = round * Math.round(number/round);
		
		return number;
	}
	
	//function to convert USD prices to SGD Prices
	public static double ConvertToSGD(String price) {
		double rounding;
		
		rounding = Double.parseDouble(price);
		rounding = roundToTen(rounding);
		rounding = roundToFifty(rounding * 1.3);
		if(rounding == 0.0) rounding = 0.5;
		
		return rounding;
	}
	
	public static long getFileCreationEpoch (File file) {
	      try {
	          BasicFileAttributes attr = Files.readAttributes(file.toPath(),
	                  BasicFileAttributes.class);
	          return attr.creationTime()
	                     .toInstant().toEpochMilli();
	      } catch (IOException e) {
	          throw new RuntimeException(file.getAbsolutePath(), e);
	      }
	  }
	
	public static void sortFilesByDateCreated (File[] files) {
	      Arrays.sort(files, new Comparator<File>() {
	          public int compare (File f1, File f2) {
	              long l1 = getFileCreationEpoch(f1);
	              long l2 = getFileCreationEpoch(f2);
	              return Long.valueOf(l1).compareTo(l2);
	          }
	      });
	}
	
	//Reading the Card Kingdom File
	static List<MtgCard> readCKFile(String filename) throws FileNotFoundException, IOException, Exception {
		List<MtgCard> cardList = new ArrayList<>();
		CsvMapper csvMapper = new CsvMapper();
		CsvSchema schema = CsvSchema.builder().addColumn("name").addColumn("set").addColumn("rarity").addColumn("nm").addColumn("ex").addColumn("vg").addColumn("g").build().withQuoteChar('"');
		System.out.println(filename);
		ObjectReader objReader = csvMapper.readerFor(MtgCard.class).with(schema);
		try(Reader reader = new FileReader(filename)){
			MappingIterator<MtgCard> mi = objReader.readValues(reader);
			while(mi.hasNext()) {
				MtgCard current = mi.next();
				if(current.getname().equals("name")) {
					continue;
				}
				else {
					String temp = current.getset().substring(0, current.getset().length() - 1);
					current.setset(temp);
					String temp2 = current.getname().replace("\"", "");
					current.setname(temp2);
					Main.entries.add(current.getname());
					cardList.add(current);
				}
			}
		}catch(Exception e) {
			Main.logger.error("Error Reading: " + filename);
			Main.logger.error("Error : " + e.getStackTrace());
		}
		return cardList;
	}
	
	//Reading the Star City Games File
	static List<ScgMtgCard> readScgFile(String filename) throws FileNotFoundException, IOException, Exception {
		List<ScgMtgCard> cardList = new ArrayList<>();
		CsvMapper csvMapper = new CsvMapper();
		CsvSchema schema = CsvSchema.builder().addColumn("name").addColumn("set").addColumn("color").addColumn("rarity").addColumn("nm").build().withQuoteChar('"');
		System.out.println(filename);
		ObjectReader objReader = csvMapper.readerFor(ScgMtgCard.class).with(schema);
		try(Reader reader = new FileReader(filename)){
			MappingIterator<ScgMtgCard> mi = objReader.readValues(reader);
			while(mi.hasNext()) {
				ScgMtgCard current = mi.next();
				if(current.getname().equals("name")) {
					continue;
				}
				else {
					if(current.getname().contains("Emblem") || current.getname().contains("Token")) {
						continue;
					}else {
						String[] names = current.getname().split("\\[SGL");
						current.setname(names[0].substring(0, names[0].length() - 1));
						String temp2 = current.getname().replace("\"", "");
						current.setname(temp2);
					}
					String temp = current.getset().substring(1);
					current.setset(temp);
					cardList.add(current);
				}
			}
		}catch(Exception e) {
			Main.logger.error("Error Reading: " + filename);
			Main.logger.error("Error : ", e);
		}
		return cardList;
	}
	
	//Read the CardData json file for all the information
	static List<CardData> readCardDataFile(String filename) throws FileNotFoundException, IOException, Exception {
		List<CardData> cardList;
		
		Reader reader = Files.newBufferedReader(Paths.get(filename));
		cardList 	  = new Gson().fromJson(reader, new TypeToken<List<CardData>>() {}.getType());
		
		reader.close();
		
		System.out.println(filename);
		return cardList;
	}
	
	//Get the filenames of card price databases that we are using
	static String getFilenames(String file1, String file2, String file3, String file4) {
		StringBuilder returnStr = new StringBuilder();
		String date1 = file1.substring(35,  file1.length() - 4);
		String date2 = file2.substring(36, file2.length() - 4);
		String date3 = file3.substring(38,  file3.length() - 4);
		String date4 = file4.substring(39, file4.length() - 4);
		
		returnStr.append("Card Kingdom Files: ");
		if(date1.equals(date2)) {
			returnStr.append(date1);
		}
		else {
			returnStr.append("Non-Foil: " + date1 + " Foil: " + date2);
		}
			
		returnStr.append("\t\t\t");
		returnStr.append("Star City Games Files: ");
		if(date3.equals(date4)) {
			returnStr.append(date3);
		}
		else {
			returnStr.append("Non-Foil: " + date3 + " Foil: " + date4);
		}
		return returnStr.toString();
		
	}
	
	//Binary search for the card
	/*static int cardSearch(String card, List<MtgCard> cards) {
		
		int firstIndex = 0;
		int lastIndex = cards.size() - 1;
		int finalResult = 0;
		while(firstIndex <= lastIndex) {
			int middleIndex = (firstIndex + lastIndex) / 2;
			String name = cards.get(middleIndex).getname().toUpperCase();
			int result = card.compareTo(name);
			if(result == 0) {
				finalResult = middleIndex;
				return finalResult;
			}
			else if(result > 0) {
				firstIndex = middleIndex + 1;
			}
			else {
				lastIndex = middleIndex - 1;
			}
		}
		
		return -1;
	}*/
	
	//Binary search for the card
	static int cardSearch(String card, List<CardData> cards) {
		card = card.toUpperCase();
		int firstIndex = 0;
		int lastIndex = cards.size() - 1;
		int finalResult = 0;
		while(firstIndex <= lastIndex) {
			int middleIndex = (firstIndex + lastIndex) / 2;
			String name = cards.get(middleIndex).getName().toUpperCase();
			int result = card.compareTo(name);
			if(result == 0) {
				finalResult = middleIndex;
				return finalResult;
			}
			else if(result > 0) {
				firstIndex = middleIndex + 1;
			}
			else {
				lastIndex = middleIndex - 1;
			}
		}
		
		return -1;
	}
	
	//Get all versions of the card
	static List<Integer> getAllVersions(MtgCard card, List<MtgCard> cards){
    	List<Integer> resulting = new ArrayList<>();
		if(card.getrarity().equals("(L)")) {
    		resulting = IntStream.range(0, cards.size()).boxed()
    				.filter(i -> cards.get(i).getname().contains(card.getname()) && cards.get(i).getrarity().equals(card.getrarity()))
    				.collect(Collectors.toList());
    	}
    	else {
    		resulting = IntStream.range(0, cards.size()).boxed()
    				.filter(i -> cards.get(i).getname().contains(card.getname()))
    				.collect(Collectors.toList());
    	}
    	return resulting;
    }
	
	//Get all versions of the card
	static List<Integer> getAllVersions(CardData card, List<CardData> cards){
	   	List<Integer> resulting = new ArrayList<>();
		if(card.getRarity().equals("(L)")) {
	   		resulting = IntStream.range(0, cards.size()).boxed()
	   				.filter(i -> cards.get(i).getName().contains(card.getName()) && cards.get(i).getRarity().equals(card.getRarity()))
	   				.collect(Collectors.toList());
	   	}
	   	else {
	   		resulting = IntStream.range(0, cards.size()).boxed()
	   				.filter(i -> cards.get(i).getName().contains(card.getName()))
	   				.collect(Collectors.toList());
	   	}
	   	return resulting;
	}
	
	//Create the MTG card tables
	static TreeTableView<CardData> createTable(){
		TreeTableView<CardData> table = new TreeTableView<CardData>();
		
		TreeTableColumn<CardData, String> cardName = new TreeTableColumn<>("Name");
        cardName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        TreeTableColumn<CardData, String> set = new TreeTableColumn<>("Set");
        set.setCellValueFactory(new TreeItemPropertyValueFactory<>("set"));
        TreeTableColumn<CardData, String> rarity = new TreeTableColumn<>("Rarity");
        rarity.setCellValueFactory(new TreeItemPropertyValueFactory<>("rarity"));
        TreeTableColumn<CardData, String> color = new TreeTableColumn<>("Color");
        color.setCellValueFactory(new TreeItemPropertyValueFactory<>("color"));
        TreeTableColumn<CardData, String> nm = new TreeTableColumn<>("CK NM");
        nm.setCellValueFactory(new TreeItemPropertyValueFactory<>("nm"));
        TreeTableColumn<CardData, Number> USDPrices = new TreeTableColumn<>("USD Price");
        USDPrices.setMinWidth(200);
        TreeTableColumn<CardData, Number> SGDPrices = new TreeTableColumn<>("SGD Price");
        SGDPrices.setMinWidth(300);
        TreeTableColumn<CardData, String> scgNM = new TreeTableColumn<>("SCG NM");
        scgNM.setCellValueFactory(new TreeItemPropertyValueFactory<>("scgnm"));
        TreeTableColumn<CardData, Double> sgdCK = new TreeTableColumn<>("CK NM");
        sgdCK.setCellValueFactory(new TreeItemPropertyValueFactory<>("ckNmSGD"));
        TreeTableColumn<CardData, Double> sgdSCG = new TreeTableColumn<>("SCG NM");
        sgdSCG.setCellValueFactory(new TreeItemPropertyValueFactory<>("scgNmSGD"));
        TreeTableColumn<CardData, Double> ckBuyNM = new TreeTableColumn<>("CK Buylist Price");
        sgdSCG.setCellValueFactory(new TreeItemPropertyValueFactory<>("ckBuyNM"));
        
        USDPrices.getColumns().add(nm);
        USDPrices.getColumns().add(scgNM);
        SGDPrices.getColumns().add(sgdCK);
        SGDPrices.getColumns().add(sgdSCG);
        
        table.getColumns().add(cardName);
        table.getColumns().add(set);
        table.getColumns().add(color);
        table.getColumns().add(rarity);
        table.getColumns().add(USDPrices);
        table.getColumns().add(SGDPrices);
        table.getColumns().add(ckBuyNM);
        table.setPlaceholder(new Label("No results to display"));
		
		return table;
	}
	
	//Sees if the current card lengths is longer than the currrent max length 
	static void lengthCheck(CardData answer, int nameLength, int setLength, int rarityLength) {
		int nLength   = answer.getName().length();
		int sLength   = answer.getSet().length();
		int rLength   = answer.getRarity().length();
		
		nameLength 		= Math.max(nameLength	 , nLength);
		setLength		= Math.max(setLength	 , sLength);
		rarityLength	= Math.max(rarityLength	 , rLength);

	}
	
	static List<Integer> searchAndDisplayCard(String theCard, TreeTableView<CardData> resultTable, List<CardData> cardList) {
		//Stores all of the card positions for the result of the binary search
		List<Integer> results = new ArrayList<>();
		
		int nameLength;
		int setLength;
		int rarityLength;
		nameLength = setLength = rarityLength = 0;
		
		//Change the string to all upper case to handle lower case issues
    	String textFieldString = theCard.toUpperCase();
        
    	//Get the Index for the card
    	results.add(cardSearch(textFieldString, cardList));
        
    	//TreeItem to store the result
    	TreeItem<CardData> searchResult = new TreeItem<>(new CardData("Cards"));
    	
    	//If the result cannot be found
        if(results.get(0) == -1) {
        	Alert a = new Alert(AlertType.INFORMATION);
        	a.setHeaderText("Card not found");
        	a.setContentText(textFieldString + " could not be found");
        	a.show();
        }
        //If the result was found, add it to the list
        else {
	        CardData answer = cardList.get(results.get(0));
	        nameLength      = answer.getName().length();
	        setLength 		= answer.getSet().length();
	        rarityLength 	= answer.getRarity().length();
	        
	        TreeItem<CardData> resultItem = new TreeItem<>();
	        List<Integer> allVersions 	  = Helper.getAllVersions(answer,cardList);
	        List<CardData> resultList 	  = new ArrayList<>();
	        
	        for(Integer number: allVersions) {
	        	resultList.add(cardList.get(number));
	        }
	        Collections.sort(resultList, Comparator.comparing(CardData::getCkNmSGD));
	        
	        if(answer.getRarity().contains("(L)")) {
	        	resultItem = new TreeItem<CardData>(resultList.get(0));
		        for(int j = 0; j < allVersions.size(); j++) {	
		        	if(cardList.get(allVersions.get(j)).getRarity().equals(answer.getRarity())) {
		        		resultItem.getChildren().add(new TreeItem<CardData>(resultList.get(j)));
		        		lengthCheck(resultList.get(j),nameLength, setLength, rarityLength);
		        	}
		        	else {
		        		continue;
		        	}
	        	}
	         }
	        else {
	        	resultItem = new TreeItem<CardData>(resultList.get(0));
	        	for(int j = 0; j < allVersions.size(); j++) {
	        		resultItem.getChildren().add(new TreeItem<CardData>(resultList.get(j)));
	        		lengthCheck(resultList.get(j),nameLength, setLength, rarityLength);
	        	}
	        }
	        searchResult.getChildren().add(resultItem);
        }
        searchResult.setExpanded(true);
        resultTable.setRoot(searchResult);
        
        List<Integer> lengths = new ArrayList<>();
        lengths.add( nameLength);
        lengths.add( setLength);
        lengths.add( rarityLength);
        
        return lengths;
	}                  
	
	//Resizing the columns after getting the results
	static void autoResizeColumns(TreeTableView<?> table, List<Integer> lengths) {
		
		table.setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
		table.getVisibleLeafColumns().stream().forEach((column)->{
			//Minimal width = columnheader
	        Text t = new Text( column.getText() );
	        double max = t.getLayoutBounds().getWidth();
	        for ( int i = 0; i < table.getChildrenUnmodifiable().size(); i++ )
	        {
	            //cell must not be empty
	            if ( column.getCellData( i ) != null )
	            {
	                t = new Text( column.getCellData( i ).toString() );
	                double calcwidth = t.getLayoutBounds().getWidth();
	                //remember new max-width
	                if ( calcwidth > max )
	                {
	                    max = calcwidth;
	                }
	            }
	        }
	        //set the new max-width with some extra space
	        column.setPrefWidth( max + 30.0d );
		});
		//Set the first three columns the appropriate aamount of space 
		table.getColumns().get(0).setPrefWidth(lengths.get(0) + 275.d);
		table.getColumns().get(1).setPrefWidth(lengths.get(1) + 120.d);
		table.getColumns().get(2).setPrefWidth(lengths.get(2) + 50.0d);
		
	}
	
	//Write Buylist data to json file
	public static void writeDataToFile(String filename, List<BuyListCards> cardList) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			writer.writeValue(Paths.get(filename).toFile(),cardList);
		}catch(Exception e) {
			
		}
	}
	
	//Read Buylist data from json file
	static List<BuyListCards> readDataFile(String filename) throws FileNotFoundException, IOException, Exception {
		List<BuyListCards> cardList;
		System.out.println(filename);
		ObjectMapper mapper = new ObjectMapper();
		cardList = new ArrayList<>(Arrays.asList(mapper.readValue(Paths.get(filename).toFile(), BuyListCards[].class)));
		return cardList;
	}
	
	//Search and display results for the deck builder scene
	static List<Integer> searchAndDisplayCard(String[] textAreaStrings, TreeTableView<CardData> dbResultsTable, CodeArea dbText, List<CardData> allCards) {
		int nameLength;
		int setLength;
		int rarityLength;
		List<Integer> dbResults   = new ArrayList<>();
		List<String> wrongItems   = new ArrayList<>();
		StringBuilder allText = new StringBuilder();
		
		nameLength = setLength = rarityLength = 0;
		
		dbResultsTable.setRoot(null);
		dbText.setStyleClass(0, dbText.getText().length(),"paragraph-box");
		
		for(int i = 0; i < textAreaStrings.length; i++) {
        	textAreaStrings[i] = textAreaStrings[i].trim();
        	allText.append(textAreaStrings[i] + "\n");
        }
        dbText.replaceText(allText.toString());
		
		//TreeItem to store the result
    	TreeItem<CardData> searchResult = new TreeItem<>(new CardData("Cards"));
    	
        for(int i = 0; i < textAreaStrings.length; i++) {
        	int result = (Helper.cardSearch(textAreaStrings[i],allCards));
        	if(result == -1) {
        		wrongItems.add((textAreaStrings[i]));
        	}
        	else {
        		dbResults.add(result);
        	}
        }
        
        if(!wrongItems.isEmpty())
        {
        	StringBuilder sb = new StringBuilder();
        	for(int i = 0; i < wrongItems.size(); i++) {
        		if(i == 0) {
        			sb.append(wrongItems.get(i) + ",");
        		}
        		else {
        			sb.append(" " + wrongItems.get(i) + ",");
        		}
        			
        	}
        	String wrongStrings = sb.substring(0, sb.length() - 1);
        	Alert a = new Alert(AlertType.INFORMATION);
        	a.setHeaderText("Card not found");
        	a.setContentText(wrongStrings + " could not be found");
        	a.show();
        }
        
        for(int i = 0; i < dbResults.size(); i++) {
        	CardData answer = allCards.get(dbResults.get(i));
	        nameLength = answer.getName().length();
	        setLength = answer.getSet().length();
	        rarityLength = answer.getRarity().length();
	        
        	TreeItem<CardData> resultItem = new TreeItem<>();
        	List<Integer> allVersions = Helper.getAllVersions(answer,allCards);
        	List<CardData> resultList = new ArrayList<>();
        	
    		for(int j = 0; j < allVersions.size();j++) {
    			resultList.add(allCards.get(allVersions.get(j)));
    		}
    		
    		Collections.sort(resultList, Comparator.comparing(CardData::getCkNmSGD));
        	if(answer.getRarity().contains("(L)")) {
        		resultItem = new TreeItem<CardData>(resultList.get(0));
		        for(CardData card: resultList) {	
		        	if(card.getRarity().equals(answer.getRarity())) {
		        		resultItem.getChildren().add(new TreeItem<CardData>(card));
		        		lengthCheck(card, nameLength, setLength, rarityLength);
		        	}
		        	else {
		        		continue;
		        	}
	        	}
	         }
        	else {
        		resultItem = new TreeItem<CardData>(resultList.get(0));
	        	for(CardData card: resultList) {
	        		resultItem.getChildren().add(new TreeItem<CardData>(card));
	        		lengthCheck(card, nameLength, setLength, rarityLength);
	        	}
        	}
        	searchResult.getChildren().add(resultItem);
        }
        searchResult.setExpanded(true);
        dbResultsTable.setRoot(searchResult);
        
        List<IndexRange> errorRanges = Main.getTextErrors(textAreaStrings, wrongItems,dbText);
        for(IndexRange range: errorRanges) {
        	dbText.setStyleClass(range.getStart(), range.getEnd(),"underlined");
        }
        dbResultsTable.autosize();
        
        List<Integer> lengths = new ArrayList<>();
        lengths.add( nameLength);
        lengths.add( setLength);
        lengths.add( rarityLength);
        
        return lengths;
	}
	
	public static void viewCard(CardData row) {
		Stage newWindow   = new Stage();
		GridPane grid   = new GridPane();
		Scene fpScene     = new Scene(grid);
		
		newWindow.setWidth(1950);
		newWindow.setHeight(300);
		
		Label selectedName   = new Label("Name: ");
        Label selectedSet    = new Label("Set: ");
        Label selectedColor  = new Label("Color: ");
        Label selectedRarity = new Label("Rarity: ");
        Label selectedCKNM   = new Label("CK NM: ");
        Label selectedCKEX   = new Label("CK EX: ");
        Label selectedCKVG   = new Label("CK VG: ");
        Label selectedCKG    = new Label("CK G: ");
        Label selectedSCGNM  = new Label("SCG NM: ");
        Label selectedckNM   = new Label("CK SGD NM: ");
        Label selecteckEX    = new Label("CK SGD EX: ");
        Label selectedckVG   = new Label("CK SGD VG: ");
        Label selectedckG    = new Label("CK SGD G: ");
        Label selectedscgNM  = new Label("SCG SGD NM: ");
        Label selectedckBuy  = new Label("CK Buylist Price: ");
        
		
        TextField thisName   = new TextField();
        TextField thisSet 	 = new TextField();
        TextField thisColor  = new TextField();
        TextField thisRarity = new TextField();
        TextField thisCKNM   = new TextField();
        TextField thisCKEX   = new TextField();
        TextField thisCKVG   = new TextField();
        TextField thisCKG 	 = new TextField();
        TextField thisSCGNM  = new TextField();
        TextField thisckNM   = new TextField();
        TextField thisckEX   = new TextField();
        TextField thisckVG   = new TextField();
        TextField thisckG 	 = new TextField();
        TextField thisscgNM  = new TextField();
        TextField thisckBuy  = new TextField();
        
        
        thisName.setText(  row.getName());
        thisSet.setText(   row.getSet());
        thisColor.setText( row.getColor());
        thisRarity.setText(row.getRarity());
        thisCKNM.setText(  row.getNm());
        thisCKEX.setText(  row.getEx());
        thisCKVG.setText(  row.getVg());
        thisCKG.setText(   row.getG());
        thisSCGNM.setText( row.getScgNm());
        thisckNM.setText(  String.valueOf(row.getCkNmSGD()));
        thisckEX.setText(  String.valueOf(row.getCkExSGD()));
        thisckVG.setText(  String.valueOf(row.getCkVgSGD()));
        thisckG.setText(   String.valueOf(row.getCkGSGD()));
        thisscgNM.setText( String.valueOf(row.getScgNmSGD()));
        thisckBuy.setText( String.valueOf(row.getCkBuyNm()));
                
        thisName.setEditable(false);
        thisSet.setEditable(false);
        thisColor.setEditable(false);
        thisRarity.setEditable(false);
        thisCKNM.setEditable(false);
        thisCKEX.setEditable(false);
        thisCKVG.setEditable(false);
        thisCKG.setEditable(false);
        thisSCGNM.setEditable(false);
        thisckNM.setEditable(false);
        thisckEX.setEditable(false);
        thisckVG.setEditable(false);
        thisckG.setEditable(false);
        thisscgNM.setEditable(false);
        thisckBuy.setEditable(false);
        
        grid.add(selectedName,	  0 ,0,1,1);
        grid.add(selectedSet,     1 ,0,1,1);
        grid.add(selectedColor,   2 ,0,1,1);
        grid.add(selectedRarity,  3 ,0,1,1);
        grid.add(selectedCKNM,    4 ,0,1,1);
        grid.add(selectedCKEX,    5 ,0,1,1);
        grid.add(selectedCKVG,    6 ,0,1,1);
        grid.add(selectedCKG,     7 ,0,1,1);
        grid.add(selectedSCGNM,   8 ,0,1,1);
        grid.add(selectedckNM,    9 ,0,1,1);
        grid.add(selecteckEX,     10,0,1,1);
        grid.add(selectedckVG,    11,0,1,1);
        grid.add(selectedckG,     12,0,1,1);
        grid.add(selectedscgNM,   13,0,1,1);
        grid.add(selectedckBuy,   14,0,1,1);
        grid.add(thisName,        0 ,1,1,1);
        grid.add(thisSet, 	      1 ,1,1,1);
        grid.add(thisColor,       2 ,1,1,1);
        grid.add(thisRarity,      3 ,1,1,1);
        grid.add(thisCKNM,        4 ,1,1,1);
        grid.add(thisCKEX,        5 ,1,1,1);
        grid.add(thisCKVG,        6 ,1,1,1);
        grid.add(thisCKG, 	      7 ,1,1,1);
        grid.add(thisSCGNM,       8 ,1,1,1);
        grid.add(thisckNM,        9 ,1,1,1);
        grid.add(thisckEX,        10,1,1,1);
        grid.add(thisckVG,        11,1,1,1);
        grid.add(thisckG, 	      12,1,1,1);
        grid.add(thisscgNM,       13,1,1,1);
        grid.add(thisckBuy,       14,1,1,1);
   
        grid.setHgap(5);
        grid.setVgap(10);
		newWindow.setScene(fpScene);
		newWindow.show();
		
	}
}
