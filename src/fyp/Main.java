package fyp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.NavigationActions.SelectionPolicy;
import org.reactfx.value.*;

public class Main extends Application{
	static boolean cknf;
	static boolean ckf;
	static boolean scgnf;
	static boolean scgf;
	static boolean ckBuylist;
	static boolean updateData;
	static boolean updateFoilData;
	static boolean updateCKing;
	static boolean updateFoilCKing;
	static boolean updateSCGames;
	static boolean updateFoilSCGames;
	static boolean updateCKBuylist;
	static boolean fillCKing;
	static boolean fillFoilCKing;
	static boolean fillCKBuylist;
	static boolean fillSCGames;
	static boolean fillFoilSCGames;
	static boolean correctData;
	static boolean writeFile;
	static boolean writeFoilFile;
	
	static String buylistFilename;
	static String buyCardsFilename;
	static String buylistUrlFilename;
	static String cardDataFilename;
	static String foilDataFilename;
	static String cknfFilename;
	static String cknfCardsFilename;
	static String cknfUrlFilename;
	static String ckfFilename;
	static String ckfCardsFilename;
	static String ckfUrlFilename;
	static String scgnfFilename;
	static String scgnfCardsFilename;
	static String scgnfUrlFilename; 
	static String scgfFilename;
	static String scgfCardsFilename;
	static String scgfUrlFilename; 
	
	
	CKWebScraper ckWebScraper;
	SCGWebScraper scgWebScraper;
	TransformData transformCD;
	
	TreeTableView<CardData> csResultsTable;
	TreeTableView<CardData> dbResultsTable;
	
	public static List<CardData> allCards;
	public static List<CardData> allFoilCards;
	static List<BuyListCards> 	 CKBuylistCards;
	static List<MtgCard> 	  	 CKCards;
	static List<MtgCard> 	  	 CKFoilCards;
	static List<ScgMtgCard>   	 SCGCards;
	static List<ScgMtgCard>      SCGFoilCards;
	
	ContextMenu entryPopup 					   = new ContextMenu();
	ListView<String> suggestions 			   = new ListView<>();
	public static SortedSet<String> entries    = new TreeSet<>();
	public static Logger logger				   = LogManager.getLogger(Main.class);
	public LoggerContext context 			   = (org.apache.logging.log4j.core.LoggerContext)LogManager.getContext(false);
	final KeyCodeCombination keyCodeCopy 	   = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
	final KeyCodeCombination keyCodePaste 	   = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY);
	final KeyCodeCombination keyCodeUndo 	   = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_ANY);
	final KeyCodeCombination keyCodeRedo 	   = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_ANY);
	final KeyCodeCombination keyCodeSelectAll  = new KeyCodeCombination(KeyCode.A, KeyCombination.SHIFT_DOWN);
	final KeyCodeCombination keyCodeSelectNext = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.SHIFT_DOWN);
	final KeyCodeCombination keyCodeSelectBack = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.SHIFT_DOWN);
	final KeyCodeCombination keyCodeSelectHome = new KeyCodeCombination(KeyCode.HOME, KeyCombination.SHIFT_DOWN);
	final KeyCodeCombination keyCodeSelectEnd = new KeyCodeCombination(KeyCode.END, KeyCombination.SHIFT_DOWN);
	
	@SuppressWarnings("rawtypes")
	private void copySelectionToClipboard(TreeTableView<CardData>table) {
		StringBuilder clipboardString = new StringBuilder();
		
		logger.info("Copied to clipboard");
		
		//Get the data from all selected table cells
		ObservableList<TreeTablePosition<CardData, ?>> positionList = table.getSelectionModel().getSelectedCells();
		
		int pRow = -1;
		
		for(TreeTablePosition position: positionList) {
			int row = position.getRow();
			int col = position.getColumn();
			
			if(pRow == row) {
				clipboardString.append('\t');
			}
			else if(pRow != -1) {
				clipboardString.append('\n');
			}
			
			String text = "";
			
			Object observableValue = (Object) table.getVisibleLeafColumn(col).getCellObservableValue(row);
			
			if(observableValue == null) {
				text = "";
			}
			else if(observableValue instanceof DoubleProperty) {
				text = NumberFormat.getNumberInstance().format(((DoubleProperty) observableValue).get());
			}
			else if( observableValue instanceof IntegerProperty) { 

	    		text = NumberFormat.getNumberInstance().format( ((IntegerProperty) observableValue).get());
	    		
	    	}			    	
	    	else if( observableValue instanceof StringProperty) { 
	    		
	    		text = ((StringProperty) observableValue).get();
	    		
	    	}
	    	else {
	    		logger.error("copySelectionToClipboard : Unsupported observable value" + observableValue);
	    		System.out.println("Unsupported observable value: " + observableValue);
	    	}
			
			clipboardString.append(text);
			pRow = row;
		}
		// create clipboard content
		final ClipboardContent clipboardContent = new ClipboardContent();
		clipboardContent.putString(clipboardString.toString());
		
		logger.info("Copied " + clipboardString.toString() + " to the Clipboard");		
		
		// set clipboard content
		Clipboard.getSystemClipboard().setContent(clipboardContent);
	}
	
	private static TextFlow buildTextFlow(String text, String filter) {
		int filterIndex = text.toLowerCase().indexOf(filter.toLowerCase());
		Text textBefore = new Text(text.substring(0,filterIndex));
		Text textAfter  = new Text(text.substring(filterIndex + filter.length()));
		Text textFilter = new Text(text.substring(filterIndex, filterIndex + filter.length()));
		
		textFilter.setFill(Color.ORANGE);
		textFilter.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		return new TextFlow(textBefore, textFilter, textAfter);
	}
	
	private void populatePopup(List<String> searchResult, String enteredText, TextField searchField) {
		List<CustomMenuItem> menuItems = new LinkedList<>();
		
		int maxEntries = 10;
		int count = Math.min(searchResult.size(), maxEntries);
		
		for(int i = 0; i < count; i++) {
			final String result = searchResult.get(i);
			
			Label entryLabel = new Label();
			entryLabel.setGraphic(buildTextFlow(result, enteredText));
			entryLabel.setPrefHeight(10);
			CustomMenuItem item = new CustomMenuItem(entryLabel, true);
			menuItems.add(item);
			
			item.setOnAction(actionEvent ->{
				searchField.setText(result);
				searchField.positionCaret(result.length());
				entryPopup.hide();
			});
		}
		
		entryPopup.getItems().clear();
		entryPopup.getItems().addAll(menuItems);
	}

	public static List<IndexRange> getTextErrors(String[] inputStrings, List<String> wrongNames, CodeArea text){
		List<IndexRange> errorRanges = new ArrayList<>();
		int start = 0;
		int end = 0;
		
		for(int i = 0; i < inputStrings.length; i++) {
			for(int j = 0; j < wrongNames.size(); j++) {
				if(inputStrings[i].equals(wrongNames.get(j))) {
					System.out.println(i);
					if(i == 0) {
						end = inputStrings[i].length();
						errorRanges.add(new IndexRange(start,end));
					}
					else {
						end	  = start + inputStrings[i].length() + 1;
						errorRanges.add(new IndexRange(start, end));
					}
				}
			}
			start += inputStrings[i].length() + 1;
			
		}
		return errorRanges;
	}
	
	@Override
	public void init() throws FileNotFoundException, IOException, Exception{
		LocalDate localDate = LocalDate.now();
		String date = DateTimeFormatter.ofPattern("ddMMyyyy").format(localDate);
		File logFile = new File("log4j2.xml");
		context.setConfigLocation(logFile.toURI());
		
		
		System.out.println("Starting");
		logger.info("Loading Application");
		
		buylistFilename    = "CardKingdom/Buylist/Buys/CKBuys"			  + date + ".json";
		buyCardsFilename   = "CardKingdom/Buylist/Cards/CKBuysCardNames"	  + date + ".txt";
		buylistUrlFilename = "CardKingdom/Buylist/UrlList/CKBuysUrlList"  + date + ".txt" ;
		cardDataFilename   = "AllCards/AllCard.json"							    	  ;
		foilDataFilename   = "AllCards/AllFoilCards.json"							 	  ;
		cknfFilename   	   = "CardKingdom/NonFoil/Prices/CKPrices" 		  + date + ".csv" ;
		cknfCardsFilename  = "CardKingdom/NonFoil/Cards/CKCards" 		  + date + ".txt" ;
		cknfUrlFilename    = "CardKingdom/NonFoil/UrlList/CKUrlList" 	  + date + ".txt" ;
		ckfFilename    	   = "CardKingdom/Foil/Prices/CKFoilPrices" 	  + date + ".csv" ;
		ckfCardsFilename   = "CardKingdom/Foil/Cards/CKFoilCards" 	  	  + date + ".txt" ;
		ckfUrlFilename     = "CardKingdom/Foil/UrlList/CKFoilUrlList" 	  + date + ".txt" ;
		scgnfFilename  	   = "StarCityGames/NonFoil/Prices/SCGPrices" 	  + date + ".csv" ;
		scgnfCardsFilename = "StarCityGames/NonFoil/Cards/SCGCards" 	  + date + ".txt" ;
		scgnfUrlFilename   = "StarCityGames/NonFoil/UrlList/SCGUrlList"   + date + ".txt" ;
		scgfFilename  	   = "StarCityGames/Foil/Prices/SCGFoilPrices"    + date + ".csv" ;
		scgfCardsFilename  = "StarCityGames/Foil/Cards/SCGFoilCards"     + date + ".txt" ;
		scgfUrlFilename    = "StarCityGames/Foil/UrlList/SCGFoilUrlList"  + date + ".txt" ;
		
		
		File cknfFile  = new File(cknfFilename);
		if(cknfFile.exists()) {
			CKCards    = Helper.readCKFile(cknfFilename);
			logger.info("Non-Foil Card Kingdom File: " + cknfFilename  + " loaded.");
		}
		else {
			File directory 		 = new File("CardKingdom/NonFoil/Prices");
			File[] filenames   = directory.listFiles();
			Helper.sortFilesByDateCreated(filenames);
			cknfFilename = filenames[filenames.length - 1].toString();
			CKCards = Helper.readCKFile(cknfFilename);
			logger.info("Non-Foil Card Kingdom File: " + cknfFilename  + " loaded.");
		}
		File ckfFile  = new File(ckfFilename);
		if(ckfFile.exists()) {
			CKFoilCards    = Helper.readCKFile(ckfFilename);
			logger.info("Foil Card Kingdom File: " + ckfFilename  + " loaded.");
		}
		else {
			File directory 		 = new File("CardKingdom/Foil/Prices");
			File[] filenames   = directory.listFiles();
			Helper.sortFilesByDateCreated(filenames);
			ckfFilename = filenames[filenames.length - 1].toString();
			CKFoilCards = Helper.readCKFile(ckfFilename);
			logger.info("Foil Card Kingdom File: " + ckfFilename  + " loaded.");
		}
		
		
		File scgnfFile = new File(scgnfFilename);
		if(scgnfFile.exists()) {
			SCGCards = Helper.readScgFile(scgnfFilename);
			logger.info("Non-Foil Star City Games File: " + scgnfFilename  + " loaded.");
		}
		else {
			File directory 		 = new File("StarCityGames/NonFoil/Prices");
			File[] filenames   = directory.listFiles();
			Helper.sortFilesByDateCreated(filenames);
			scgnfFilename = filenames[filenames.length - 1].toString();
			SCGCards = Helper.readScgFile(scgnfFilename);
			logger.info("Non-Foil Star City Games File: " + scgnfFilename  + " loaded.");
		}
		File scgfFile = new File(scgfFilename);
		if(scgfFile.exists()) {
			SCGFoilCards = Helper.readScgFile(scgfFilename);
			logger.info("Foil Star City Games File: " + scgfFilename  + " loaded.");
		}
		else {
			File directory 		 = new File("StarCityGames/Foil/Prices");
			File[] filenames   = directory.listFiles();
			Helper.sortFilesByDateCreated(filenames);
			scgfFilename = filenames[filenames.length - 1].toString();
			SCGFoilCards = Helper.readScgFile(scgfFilename);
			logger.info("Foil Star City Games File: " + scgfFilename  + " loaded.");
		}
		
		
		File ckBuyFile  = new File(buylistFilename);
		if(ckBuyFile.exists()) {
			CKBuylistCards    = Helper.readDataFile(buylistFilename);
			logger.info("Card Kingdom Buylist File: " + buylistFilename  + " loaded.");
		}
		else {
			File directory 		 = new File("CardKingdom/Buylist/Buys");
			File[] filenames   = directory.listFiles();
			Helper.sortFilesByDateCreated(filenames);
			buylistFilename = filenames[filenames.length - 1].toString();
			CKBuylistCards = Helper.readDataFile(buylistFilename);
			logger.info("Card Kingdom Buylist File: " + buylistFilename  + " loaded.");
		}
		Collections.sort(SCGCards, Comparator.comparing(ScgMtgCard::getname));
		Collections.sort(SCGFoilCards, Comparator.comparing(ScgMtgCard::getname));
		
		File nonFoilFile = new File(cardDataFilename);
		if(nonFoilFile.exists()) {
			allCards 	 = Helper.readCardDataFile(cardDataFilename);
			logger.info("All Non-Foil Cards File: " + cardDataFilename  + " loaded.");
		}
		else {
			allCards 	 = new ArrayList<CardData>();
			logger.info("No All Non-Foil Cards File detected");
		}
		
		File foilFile = new File(foilDataFilename);
		if(foilFile.exists()) {
			allFoilCards = Helper.readCardDataFile(foilDataFilename);
			logger.info("All Foil Cards File: " + foilDataFilename  + " loaded.");
		}
		else {
			allFoilCards = new ArrayList<CardData>();
			logger.info("No All Foil Cards File detected");
		}
		
		csResultsTable = Helper.createTable();
		dbResultsTable = Helper.createTable();
		logger.info("Created Card Search and Deck Builder Results table");
		System.out.println(CKFoilCards.size());
	}
	
	@Override
	public void start(Stage stage) {
		
		logger.info("Application Starting");
		
		//Set the title and size of the base window
		stage.setTitle("Grey Ogre Games Price Scraper & Card Search");
		stage.setWidth(1600);
        stage.setHeight(900);
		
        //Set up and create the initial screen and buttons
		BorderPane root = new BorderPane();
		Scene base 		= new Scene(root);
		HBox top 		= new HBox();
		HBox bottom		= new HBox();
		VBox center 	= new VBox();
		Insets insets 	= new Insets(20, 20, 20, 20);
		
		//Instantiate and define all of the necessary Labels and Buttons
		Label versionNum		= new Label("v1.0.2.1");
		Label title 			= new Label("Grey Ogre Games Price Scraper & Card Search");
		Button cardDataPage 	= new Button("Update & Create Card Data ");
		Button priceScraper 	= new Button("Price Scraping");
		Button cardSearch 		= new Button("Search For a Card");
		Button deckBuilder 		= new Button("Deck Builder");
		
		title.setFont(new Font("Arial", 50));
		cardDataPage.setFont(new Font("Arial", 45));
		priceScraper.setFont(new Font("Arial", 45));
		cardSearch.setFont(new Font("Arial"  , 45));
		deckBuilder.setFont(new Font("Arial" , 45));
		
		//Set the title to the top the app
		top.setAlignment(Pos.BASELINE_CENTER);
		top.getChildren().add(title);
		top.setPadding(insets);
		root.setTop(top);
		
		//Set the buttons to the center of the app
		center.setAlignment(Pos.BASELINE_CENTER);
		center.setSpacing(100);
		center.getChildren().addAll(priceScraper, cardDataPage, cardSearch, deckBuilder);
		root.setCenter(center);
		
		bottom.setAlignment(Pos.BOTTOM_RIGHT);
		bottom.getChildren().add(versionNum);
		root.setBottom(bottom);
		
		//Create the Card Data Update and Creation Scene
		BorderPane cdRoot = new BorderPane();
		Scene cdScene 	  = new Scene(cdRoot);
		
		//Instantiating and creating the necessary parts of the Card Data Page Scene
		Button cdBack 		 		= new Button("Back");
		Button cdStart		 		= new Button("Start");
		Button cdCancel		 		= new Button("Cancel");
		CheckBox correctCD			= new CheckBox("Correct Foil & Non-Foil Data");
		CheckBox fillCK 	 		= new CheckBox("Fill Card Data List (Card Kingdom Information)");
		CheckBox fillSCG 	 		= new CheckBox("Fill Card Data List (Star City Games Information)");
		CheckBox fillCKBuy			= new CheckBox("Fill Card Data List with Card Kingdom Buylist Information");
		CheckBox fillFoilCK			= new CheckBox("Fill Foil Card Data List (Card Kingdom Information)");
		CheckBox fillFoilSCG		= new CheckBox("Fill Foil Card Data List (Star City Games Information)");
		CheckBox updateCD 	 		= new CheckBox("Update List with New Cards");
		CheckBox updateFoilCD		= new CheckBox("Update Foil List with New Cards");
		CheckBox updateCK 	 		= new CheckBox("Update Card Kingdom Information");
		CheckBox updateFoilCK		= new CheckBox("Updare Foil Card Kingdom Information");
		CheckBox updateCKBuy		= new CheckBox("Update Card Kingdom Buylist Information");
		CheckBox updateSCG 	 		= new CheckBox("Update Star City Games Information");
		CheckBox updateFoilSCG		= new CheckBox("Update Foil Star City Games Information");
		CheckBox writeToFile 		= new CheckBox("Write Card Data File");
		CheckBox writeToFoilFile  	= new CheckBox("Write Foil Card Data File");
		HBox cdTop 			 		= new HBox();
		Label cdTitle 		 		= new Label("Card Data Update & Creation");
		Label cdSub					= new Label("What to do with the list");
		ProgressBar cdProgress  	= new ProgressBar();
		VBox cdLeft 		 		= new VBox();
		VBox cdRight 		 		= new VBox();
		
		//Set the font and size of the buttons and title
		cdTitle.setFont(new Font("Arial", 50));
		cdBack.setFont(new Font("Arial", 36));
		
		//Set the top of the scene
		cdTitle.setAlignment(Pos.TOP_CENTER);
		cdBack.setAlignment(Pos.TOP_RIGHT);
		cdTop.setSpacing(762);
		cdTop.getChildren().addAll(cdTitle, cdBack);
		cdTop.setPadding(insets);
		cdRoot.setTop(cdTop);
		
		//Set the left side of the scene
		cdSub.setAlignment(Pos.TOP_LEFT);
		cdCancel.setDisable(true);
		cdLeft.setSpacing(25);
		cdLeft.getChildren().addAll(correctCD, fillCK, fillFoilCK, fillCKBuy, fillSCG, 
									fillFoilSCG, updateCD, updateFoilCD, updateCK, 
									updateFoilCK, updateCKBuy, updateSCG, updateFoilSCG, 
									writeToFile, writeToFoilFile, cdStart, cdCancel);
		cdRoot.setLeft(cdLeft);
		
		//Set the right side of the scene
		cdRight.getChildren().add(cdProgress);
		cdRoot.setRight(cdRight);
		
		//Tells the application what to do given the selected options
		cdStart.setOnAction(action ->{
			correctData			= correctCD.isSelected();
			updateData 	    	= updateCD.isSelected();
			updateFoilData  	= updateFoilCD.isSelected();
			updateCKing     	= updateCK.isSelected();
			updateFoilCKing     = updateFoilCK.isSelected();
			updateSCGames   	= updateSCG.isSelected();
			updateFoilSCGames	= updateFoilSCG.isSelected();
			updateCKBuylist 	= updateCKBuy.isSelected();
			fillCKing 	    	= fillCK.isSelected();
			fillFoilCKing		= fillFoilCK.isSelected();
			fillCKBuylist   	= fillCKBuy.isSelected();
			fillSCGames     	= fillSCG.isSelected();
			fillFoilSCGames		= fillFoilSCG.isSelected();
			writeFile 	    	= writeToFile.isSelected();
			writeFoilFile		= writeToFoilFile.isSelected();
			
			logger.info("Starting Data Transformation");
			StringBuilder sb = new StringBuilder();
			sb.append(correctCD.getText()     	  + ": " + correctData       +  "\n");
			sb.append(updateCD.getText()      	  + ": " + updateData        +  "\n");
			sb.append(updateFoilCD.getText()  	  + ": " + updateFoilData    +  "\n");
			sb.append(updateSCG.getText()     	  + ": " + updateSCGames     +  "\n");
			sb.append(updateFoilSCG.getText() 	  + ": " + updateFoilSCGames +  "\n");
			sb.append(updateCKBuy.getText()   	  + ": " + updateCKBuylist   +  "\n");
			sb.append(fillCK.getText() 		  	  + ": " + fillCKing         +  "\n");
			sb.append(fillFoilCK.getText() 	  	  + ": " + fillFoilCKing     +  "\n");
			sb.append(fillCKBuy.getText() 	  	  + ": " + fillCKBuylist     +  "\n");
			sb.append(fillSCG.getText() 	  	  + ": " + fillSCGames       +  "\n");
			sb.append(fillFoilSCG.getText() 	  + ": " + fillFoilSCGames   +  "\n");
			sb.append(writeToFile.getText() 	  + ": " + writeFile         +  "\n");
			sb.append(writeToFoilFile.getText()   + ": " + writeFoilFile     +  "\n");
			logger.info("Data Transformation functions: " + sb.toString());
			
			transformCD = new TransformData();
			cdStart.setDisable(true);
			cdCancel.setDisable(false);
			cdProgress.progressProperty().unbind();
			cdProgress.progressProperty().bind(transformCD.progressProperty());
			
			
			new Thread(transformCD).start();
			transformCD.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
					  new EventHandler<WorkerStateEvent>() {
					  @Override
					  public void handle(WorkerStateEvent e) {
						  cdStart.setDisable(false);
						  cdCancel.setDisable(true);
						  if(correctData) {
							  System.out.println("Corrected Card Data");
						  }
						  if(updateData) {
							  System.out.println("Updated Card Data");
						  }
						  if(updateFoilData) {
							  System.out.println("Updated Foil Card Data");
						  }
						  if(updateCKing) {
							  System.out.println("Updated Card Kingdom Prices");
						  }
						  if(updateFoilCKing) {
							  System.out.println("Updated Foil Card Kingdom Prices");
						  }
						  if(updateSCGames) {
							  System.out.println("Updated Star City Games Prices");
						  }
						  if(updateFoilSCGames) {
							  System.out.println("Updated Foil Star City Games Prices");
						  }
						  if(updateCKBuylist) {
							  System.out.println("Updated Card Kingdom Buylist Price");
						  }
						  if(fillCKing){
							  System.out.println("Filled in Card Kingdom Information");
						  }
						  if(fillFoilCKing){
							  System.out.println("Filled in Foil Card Kingdom Information");
						  }
						  if(fillCKBuylist) {
							  System.out.println("Filled in Card Kingdom Buylist Information");
						  }
						  if(fillSCGames) {
							  System.out.println("Filled in Star City Games Information");
						  }
						  if(fillFoilSCGames) {
							  System.out.println("Filled in Foil Star City Games Information");
						  }
						  if(writeFile) {
							  System.out.println("Saved the File");
						  }
						  if(writeFoilFile) {
							  System.out.println("Saved the Foil File");
						  }
					  }
			});

		});
		
		//Stop the task
		cdCancel.setOnAction(action -> {
			logger.info("Data Transformation cancelled");
			cdProgress.progressProperty().unbind();
			cdStart.setDisable(false);
			cdCancel.setDisable(true);
			cdProgress.setProgress(0);
		});
		
		//Go back to the title screen
		cdBack.setOnAction(action -> {
			logger.info("Back to Main Menu");
			stage.setScene(base);
		});
		
		//Create the Card Search Scene
		BorderPane csRoot = new BorderPane();
		Scene csScene 	  = new Scene(csRoot);
		
		//Instantiating and creating the necessary parts of the Card Search Scene
		CheckBox csFoil 	  = new CheckBox("Foil");
		CheckBox showSGD	  = new CheckBox("Show Buylist Prices");
		HBox csTop  		  = new HBox();
		HBox csCenter	  	  = new HBox();
		VBox csBottom		  = new VBox();
		Label csTitle 		  = new Label("Card Search");
		Label csFileLabel 	  = new Label();
		TextField searchField = new TextField();
		Button csSearch 	  = new Button("Search");
		Button csBack		  = new Button("Back");
		
		//Set the font and size of the buttons and title
		csTitle.setFont(new Font( "Arial", 50));
		csBack.setFont(new Font(  "Arial", 36));
		csSearch.setFont(new Font("Arial", 20));
		
		//Arrange the top of the Card Search Scene
		csTitle.setAlignment(Pos.TOP_CENTER);
		csBack.setAlignment(Pos.TOP_RIGHT);
		csTop.setSpacing(1135);
		csTop.getChildren().addAll(csTitle, csBack);
		csTop.setPadding(insets);
		csRoot.setTop(csTop);
		
		//Arrange the center of the Card Search Scene
		searchField.setPrefWidth(300);
		searchField.setMaxWidth(300);
		searchField.setPrefHeight(20);
		searchField.setMaxHeight(20);
		csSearch.disableProperty().bind(searchField.textProperty().isEmpty());
		csCenter.getChildren().addAll(searchField, showSGD, csFoil, csSearch);
		csCenter.setSpacing(325);
		csRoot.setCenter(csCenter);
		
		showSGD.selectedProperty().addListener((observable,oldValue,newValue) ->{
				csResultsTable.getColumns().get(5).setVisible(!newValue);
				logger.info("Show Buylist: " + showSGD.isSelected());
		});
		
		//SearchField autocomplete
		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			String enteredText = searchField.getText();
			
			if(enteredText == null || enteredText.isEmpty()) {
				entryPopup.hide();
			}
			else {
				List<String> filteredEntries = entries.stream()
						.filter(e -> e.toLowerCase().contains(enteredText.toLowerCase()))
						.collect(Collectors.toList());
				if(!filteredEntries.isEmpty()) {
					populatePopup(filteredEntries,enteredText, searchField);
					if(!entryPopup.isShowing()) {
						entryPopup.show(searchField, Side.BOTTOM, 0, 0);
					}
				}
				else {
					entryPopup.hide();
				}
			}
		});
		searchField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
				entryPopup.hide();
			}
		});
		
		//Arrange the bottom of the Card Search Scene
		csFileLabel.setText(Helper.getFilenames(cknfFilename,ckfFilename, scgnfFilename, scgfFilename));
		csResultsTable.getSelectionModel().setCellSelectionEnabled(true);
		csResultsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		csBottom.getChildren().addAll(csFileLabel, csResultsTable);
		csRoot.setBottom(csBottom);
		
		csResultsTable.setRowFactory( tv -> {
			TreeTableRow<CardData> row = new TreeTableRow<>();
			row.setOnMouseClicked(event ->{
				if(event.getClickCount() == 2 && (!row.isEmpty())) {
					CardData rowData = row.getItem();
					logger.info("Viewing: " + rowData.getName() + "," + rowData.getSet());
					Helper.viewCard(rowData);
				}
			});
			return row;
		});
		
		//Go back to the homepage
		csBack.setOnAction(action ->{
			logger.info("Back to Main Menu");
			searchField.clear();
			csResultsTable.setRoot(null);
			stage.setScene(base);
		});
		
		//Search the csv for the card
		csSearch.setOnAction(action ->{
			//clear the list and table of any previous search results
			List<Integer> lengths = new ArrayList<>();
			csResultsTable.setRoot(null);
			
			logger.info("Card Search for " + searchField.getText() + ", is foil: " + csFoil.isSelected());
			
			if(csFoil.isSelected()) {
				lengths = Helper.searchAndDisplayCard(searchField.getText(), csResultsTable, allFoilCards);
			}
			else {
				lengths = Helper.searchAndDisplayCard(searchField.getText(), csResultsTable, allCards);
			}
			
			Helper.autoResizeColumns(csResultsTable, lengths);
		});
		
		/*Add an event filter that allows that allows the enter key to act as the search button and
		 * for other custom functionality  
		 */
		csScene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
			if(event.getCode() == KeyCode.LEFT) {
				logger.info("Move caret to the left");
				searchField.backward();
			}
			if(event.getCode() == KeyCode.RIGHT) {
				logger.info("Move caret to the right");
				searchField.forward();
			}
            if(event.getCode() == KeyCode.ENTER) {
            	logger.info("Search for a card from the enter button");
                csSearch.fire();
            }
			if(event.getCode() == KeyCode.BACK_SPACE)
	        {
				logger.info("Delete the previous char in searchfield");
				searchField.deletePreviousChar();
	        }
			if(event.getCode() == KeyCode.DELETE)
	        {
				logger.info("Delete the next char in searchfield");
				searchField.deleteNextChar();
	        }
            if(keyCodeCopy.match(event)) {
            	logger.info("Copy from the csResultsTable");
            	copySelectionToClipboard(csResultsTable);
	        }
            if(keyCodePaste.match(event)) {
            	logger.info("Paste to the searchfield");
            	searchField.paste();
            }
            if(keyCodeUndo.match(event)) {
            	logger.info("Undo");
            	searchField.undo();
            }
            if(keyCodeRedo.match(event)) {
            	logger.info("Redo");
            	searchField.redo();
            }
            if(keyCodeSelectAll.match(event)) {
            	logger.info("Select the entire searchfield");
				searchField.selectAll();
			}
			if(keyCodeSelectNext.match(event)) {
				logger.info("Select the next character");
				searchField.selectForward();
			}
			if(keyCodeSelectBack.match(event)) {
				logger.info("Select the previous character");
				searchField.selectBackward();
			}
			if(keyCodeSelectHome.match(event)) {
				logger.info("Select till the start of the field");
				searchField.selectHome();
			}
			if(keyCodeSelectEnd.match(event)) {
				logger.info("Select till the end of the field");
				searchField.selectEnd();
			}
            event.consume();
        });
		
		searchField.selectForward();
		
		//Create the Deck Builder Scene
		BorderPane dbRoot = new BorderPane();
		Scene dbScene	  = new Scene(dbRoot);
		
		//Instantiating and creating the necessary parts of the Card Search Scene
		Button dbBack 				= new Button("Back");
		Button dbSearch 			= new Button("Search");
		CodeArea dbText 			= new CodeArea();
		File spellCheck 			= new File("deckBuilderStyle.css");
		HBox dbTop 	 				= new HBox();
		HBox dbCenter				= new HBox();
		HBox dbLeft								;
		Label dbTitle				= new Label("Deck Builder");
		Label dbFile				= new Label();
		VBox dbBottom				= new VBox();
		
		//Set the fonts and sizes
		dbTitle.setFont(new Font( "Arial", 50));
		dbBack.setFont(new Font(  "Arial", 36));
		dbSearch.setFont(new Font("Arial", 20));
		
		//Set the top of the Deck Builder Scene
		dbTitle.setAlignment(Pos.TOP_CENTER);
		dbBack.setAlignment(Pos.TOP_RIGHT);
		dbTop.setSpacing(1100);
		dbTop.getChildren().addAll(dbTitle, dbBack);
		dbTop.setPadding(insets);
		dbRoot.setTop(dbTop);
		
		//Set the left side of the Deck Builder Scene
		dbLeft = new HBox(new VirtualizedScrollPane<>(dbText));
		dbText.setPrefWidth(800);
		dbText.setMaxWidth(800);
		dbLeft.setPadding(new Insets(0,105,0,0));
		dbRoot.setLeft(dbLeft);
		
		//Arrange the center of the Card Search Scene
		dbSearch.disableProperty().bind(Val.map(dbText.lengthProperty(), n-> n == 0));
		suggestions.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		suggestions.setPrefWidth(400);
		suggestions.setMaxWidth(400);
		dbCenter.getChildren().addAll(suggestions, dbSearch);
		dbCenter.setSpacing(100);
		dbRoot.setCenter(dbCenter);
		
		//Arrange the bottom of the Card Search Scene
		dbFile.setText(Helper.getFilenames(cknfFilename, ckfFilename, scgnfFilename, scgfFilename));
		dbResultsTable.getSelectionModel().setCellSelectionEnabled(true);
		dbResultsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		dbBottom.getChildren().addAll(dbFile, dbResultsTable);
		dbBottom.setPadding(new Insets(20, 0, 0, 0));
		dbRoot.setBottom(dbBottom);
		
		//add the style sheet
		try {
			dbScene.getStylesheets().add(spellCheck.toURI().toURL().toString());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		dbText.setStyle("-fx-font-family: Arial; -fx-font-size: 12pt;");
		
		dbResultsTable.setRowFactory( tv -> {
			TreeTableRow<CardData> row = new TreeTableRow<>();
			row.setOnMouseClicked(event ->{
				if(event.getClickCount() == 2 && (!row.isEmpty())) {
					CardData rowData = row.getItem();
					logger.info("Selected: " + rowData.getName() + ", set: " + rowData.getSet());
					Helper.viewCard(rowData);
				}
			});
			return row;
		});
		
		try {
			//Get suggestions based on what the user has inputted so far
			dbText.caretPositionProperty().addListener((obs, oldPosition, newPosition) -> {
				String text = dbText.getText().substring(0, newPosition.intValue());
				int index;

				for (index = text.length() - 1; index >= 0 && !(text.charAt(index) == ('\n')); index--)
					;
				String prefix = text.substring(index + 1, text.length());

				for (index = newPosition.intValue(); index < dbText.getLength()
						&& !(dbText.getText().charAt(index) == '\n'); index++)
					;
				String suffix = dbText.getText().substring(newPosition.intValue(), index);

				prefix = prefix.replaceAll("\\.", "\\.");
				suffix = suffix.replaceAll("\\.", "\\.");

				Pattern pattern = Pattern.compile(prefix + ".*" + suffix, Pattern.CASE_INSENSITIVE);

				suggestions.getItems().setAll(entries.stream().filter(word -> pattern.matcher(word).matches())
						.sorted(Comparator.comparing(String::length)).limit(100).collect(Collectors.toList()));
			});
		} catch (PatternSyntaxException e) {
		}
		//When clicking on a suggestion add it to the input area in the correct position
		suggestions.setOnMouseClicked(event ->{
			if(event.getClickCount() == 2) {
				List<String> wrongItems  = new ArrayList<>();
				StringBuilder output 	 = new StringBuilder();
				String obValue 			 = suggestions.getSelectionModel().getSelectedItem();
				String[] textAreaStrings = dbText.getText().split("\n");
				
				for(int i = 0; i < textAreaStrings.length; i++) {
		        	int result = (Helper.cardSearch(textAreaStrings[i],allCards));
		        	if(result == -1) {
		        		wrongItems.add((textAreaStrings[i]));
		        	}
		        }
				
				textAreaStrings[dbText.getCurrentParagraph()] = obValue;
				if(textAreaStrings.length > 1) {
					for(int i = 0; i < textAreaStrings.length; i++) {
						output.append(textAreaStrings[i] + '\n');
					}
					output.deleteCharAt(output.length() - 1);
					dbText.replaceText(output.toString());
				}
				else {
					dbText.replaceText(obValue);
					dbText.moveTo(obValue.length());
				}
				List<IndexRange> errorRanges = Main.getTextErrors(textAreaStrings, wrongItems,dbText);
		        for(IndexRange range: errorRanges) {
		        	dbText.setStyleClass(range.getStart(), range.getEnd(),"underlined");
		        }
		        logger.info("Replaced using " + obValue);
				dbText.requestFocus();
				dbText.moveTo(output.toString().length());
			}
		});
		
		//Search through the card database for all of the inputs in dbText
		dbSearch.setOnAction(action -> {
			logger.info("Search: " + dbText.getText());
			String[] textAreaStrings = dbText.getText().split("\n");
			List<Integer> lengths = Helper.searchAndDisplayCard(textAreaStrings, dbResultsTable, dbText, allCards);
			Helper.autoResizeColumns(dbResultsTable,lengths);
	    });
		
		//Go back to the homepage
		dbBack.setOnAction(action ->{
			logger.info("Back to Main Menu");
			dbText.clear();
			dbResultsTable.setRoot(null);
			stage.setScene(base);
		});
		
		
		//Scene tells what each button is supposed to do
		dbScene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
			if (keyCodeCopy.match(event)) {
				logger.info("Copy from the dbResultsTable");
	            copySelectionToClipboard(dbResultsTable);
	        }
			if(event.getCode() == KeyCode.ENTER){
				logger.info("Go to the next line");
				dbText.appendText("\n");
	        }
			if(event.getCode() == KeyCode.BACK_SPACE){
				logger.info("Backspace");
				if(dbText.getSelectedText().length() == 0) {
					dbText.deletePreviousChar();
				}
				else {
					dbText.deleteText(dbText.getSelection());
				}
	        }
			if(event.getCode() == KeyCode.DELETE) {
				logger.info("Delete");
				if(dbText.getSelectedText().length() == 0) {
					dbText.deleteNextChar();
				}
				else {
					dbText.deleteText(dbText.getSelection());
				}
			}
			if(event.getCode() == KeyCode.UP) {
				String[] textAreaStrings = dbText.getText().split("\n");
				int number = dbText.getParagraphs().size();
				int lineCountTotal = 0;
				if(textAreaStrings.length > 1) {
					int lineNumber = dbText.getCurrentParagraph() - 2;
					int charCount = 0;
					for(int i = 0; i < number; i++) {
						charCount += dbText.getParagraphLength(i);
						charCount += 1;
						if(i == lineNumber) {
							lineCountTotal = charCount;
							break;
						}
					}
			    	dbText.moveTo(lineCountTotal);
			    	logger.info("Go up to line " + lineNumber);
				}
			}
			if(event.getCode() == KeyCode.DOWN) {
				String[] textAreaStrings = dbText.getText().split("\n");
				int number = dbText.getParagraphs().size();
				int lineCountTotal = 0;
				if(textAreaStrings.length > 1) {
					int lineNumber = dbText.getCurrentParagraph();
					int charCount = 0;
					for(int i = 0; i < number; i++) {
						charCount += dbText.getParagraphLength(i);
						charCount += 1;
						if(i == lineNumber) {
							lineCountTotal = charCount;
							break;
						}
					}
			    	dbText.moveTo(lineCountTotal);
			    	logger.info("Go down to line " + lineNumber);
				}
			}
			if(event.getCode() == KeyCode.LEFT) {
				dbText.previousChar(SelectionPolicy.CLEAR);
				logger.info("Move the caret to the left");
			}if(event.getCode() == KeyCode.RIGHT) {
		    	dbText.nextChar(SelectionPolicy.CLEAR);
		    	logger.info("Move the caret to the right");
			}
			if(keyCodePaste.match(event)) {
				logger.info("Paste text into the search area");
				dbText.paste();
			}
			if(keyCodeUndo.match(event)) {
				logger.info("Undo input into the search area");
				dbText.undo();
			}
			if(keyCodeRedo.match(event)) {
				logger.info("Redo input into the search area");
				dbText.redo();
			}
			if(keyCodeSelectAll.match(event)) {
				logger.info("Select all text in a search area");
				dbText.selectAll();
			}
			/*if(keyCodeSelectNext.match(event)) {
				dbText.nextChar(SelectionPolicy.ADJUST);
			}
			if(keyCodeSelectBack.match(event)) {
				dbText.previousChar(SelectionPolicy.ADJUST);
			}*/
			if(keyCodeSelectHome.match(event)) {
				logger.info("Select till the start of the line");
				dbText.paragraphStart(SelectionPolicy.ADJUST);
			}
			if(keyCodeSelectEnd.match(event)) {
				logger.info("Select till the end of the line");
				dbText.paragraphEnd(SelectionPolicy.ADJUST);
			}
            event.consume();
		});
		
		
		//Create the scene for the Price Scraper
		BorderPane psRoot = new BorderPane();
		Scene psScene	  = new Scene(psRoot);
		
		Button psBack 		= new Button("Back");
		Button psStart 		= new Button("Start");
		Button psCancel		= new Button("Cancel");
		CheckBox ckNonFoil  = new CheckBox("Card Kingdom Non-Foil");
		CheckBox ckFoil 	= new CheckBox("Card Kingdom Foil");
		CheckBox ckBuy		= new CheckBox("Card Kingdom Buylist");
		CheckBox scgNonFoil = new CheckBox("Star City Games Non-Foil");
		CheckBox scgFoil 	= new CheckBox("Star City Games Foil");
		HBox psTop  		= new HBox();
		Label psTitle		= new Label("Price Scraper");
		Label psScrape      = new Label("Which Websites and sections to scrape");
		Label psProgress	= new Label("Scrape Progress");
		ProgressBar psBar	= new ProgressBar(0);
		VBox psLeft 		= new VBox();
		VBox psRight    	= new VBox();
		
		//Set the fonts
		psTitle.setFont(new Font( "Arial", 50));
		psBack.setFont(new Font(  "Arial", 36));
		psScrape.setFont(new Font("Arial", 20));
		
		//Set the top of the Price Scraper Scene
		psTitle.setAlignment(Pos.TOP_CENTER);
		psBack.setAlignment(Pos.TOP_RIGHT);
		psTop.setSpacing(1110);
		psTop.getChildren().addAll(psTitle, psBack);
		psTop.setPadding(insets);
		psRoot.setTop(psTop);
		
		//Set the left of the Deck Builder Scene
		psScrape.setAlignment(Pos.TOP_LEFT);
		psCancel.setDisable(true);
		psLeft.setSpacing(50);
		psLeft.getChildren().addAll(psScrape, ckNonFoil, ckFoil, ckBuy, scgNonFoil, scgFoil, psStart, psCancel);
		psRoot.setLeft(psLeft);
		
		//Set the left of the Deck Builder Scene
		psBar.setPrefWidth(300);
		psBar.setMinWidth(300);
		psRight.setSpacing(15);
		psRight.getChildren().addAll(psProgress,psBar);
		psRoot.setRight(psRight);
		
		//Start the scraping process on the selected sites and sections
		psStart.setOnAction(action ->{
			LocalDate localDate = LocalDate.now();
			String date = DateTimeFormatter.ofPattern("ddMMyyyy").format(localDate);

			cknf  	  = ckNonFoil.isSelected();
			ckf   	  = ckFoil.isSelected();
			scgnf 	  = scgNonFoil.isSelected();
			scgf  	  = scgFoil.isSelected();
			ckBuylist = ckBuy.isSelected();
			
			//Scrape Card Kingdom for non foil, foil and/or the buylist prices
			if(cknf || ckf || ckBuylist) {
				psStart.setDisable(true);
				psCancel.setDisable(false);
				psBar.setProgress(0);
				
				StringBuilder sb = new StringBuilder();
				sb.append(ckNonFoil.getText() + " : " + cknf  	 + ", ");
				sb.append(ckFoil.getText() 	  + " : " + ckf   	 + ", ");
				sb.append(ckBuy.getText() 	  + " : " + ckBuylist + " .");
				
				logger.info("Card Kingdom Scraping: " + sb.toString());
				
				buylistFilename    = "CardKingdom/Buylist/Buys/CKBuys"			  + date + ".json";
				buylistUrlFilename = "CardKingdom/Buylist/UrlList/CKBuysUrlList"  + date + ".txt" ;
				cknfFilename   	   = "CardKingdom/NonFoil/Prices/CKPrices" 		  + date + ".csv" ;
				cknfUrlFilename    = "CardKingdom/NonFoil/UrlList/CKUrlList" 	  + date + ".txt" ;
				ckfFilename    	   = "CardKingdom/Foil/Prices/CKFoilPrices" 	  + date + ".csv" ;
				ckfUrlFilename     = "CardKingdom/Foil/UrlList/CKFoilUrlList" 	  + date + ".txt" ;

				ckWebScraper 	 = new CKWebScraper();
				
				psBar.progressProperty().unbind();
				psBar.progressProperty().bind(ckWebScraper.progressProperty());
				
				psProgress.textProperty().unbind();
				psProgress.textProperty().bind(ckWebScraper.messageProperty());
				
				new Thread(ckWebScraper).start();
				ckWebScraper.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, 
							new EventHandler<WorkerStateEvent>() {
	 
	                           @Override
	                           public void handle(WorkerStateEvent t) {
	                        	   psProgress.textProperty().unbind();
	                        	   psProgress.setText("Scrape Complete");
	                        	   if(cknf) {
	                        		   try {
	   	               						CKCards = Helper.readCKFile(cknfFilename);
	   	               					} catch (Exception e) {
	   	               						System.out.println(cknfFilename + " failed to be read");
	   	               					}
	                        	   }
	                        	   if(ckf) {
	                        		   try {
	   	               						CKFoilCards = Helper.readCKFile(ckfFilename);
	   	               					} catch (Exception e) {
	   	               						System.out.println(ckfFilename + " failed to be read");
	   	               					}
	                        	   }
	                        	   if(ckBuylist) {
	                        		   try {
	                        			   CKBuylistCards = Helper.readDataFile(buylistFilename);
	   	               					} catch (Exception e) {
	   	               						System.out.println(ckfFilename + " failed to be read");
	   	               					}
	                        	   }
	                        	   psStart.setDisable(false);
	                        	   psCancel.setDisable(true);;
	                           }
	                       });
			}
			//Scrape the Star City Games website for non foil and/or foil cards
			if(scgnf || scgf) {
				psStart.setDisable(true);
				psCancel.setDisable(false);
				psBar.setProgress(0);
				
				StringBuilder sb = new StringBuilder();
				sb.append(scgNonFoil.getText() + ": " + scgnf  + ", ");
				sb.append(scgFoil.getText()    + ": " + scgf   + ".");
				
				logger.info("Star City Games Scraping: " + sb.toString());
				
				scgnfFilename  	   = "StarCityGames/NonFoil/Prices/SCGPrices" 	  + date + ".csv" ;
				scgnfUrlFilename   = "StarCityGames/NonFoil/UrlList/SCGUrlList"   + date + ".txt" ;
				scgfFilename  	   = "StarCityGames/Foil/Prices/SCGFoilPrices"    + date + ".csv" ;
				scgfUrlFilename    = "StarCityGames/Foil/UrlList/SCGFoilUrlList"  + date + ".txt" ;
				scgWebScraper 	 = new SCGWebScraper();
				
				psBar.progressProperty().unbind();
				psBar.progressProperty().bind(scgWebScraper.progressProperty());
				
				psProgress.textProperty().unbind();
				psProgress.textProperty().bind(scgWebScraper.messageProperty());
				
				new Thread(scgWebScraper).start();
				scgWebScraper.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //
							new EventHandler<WorkerStateEvent>() {
	 
	                           @Override
	                           public void handle(WorkerStateEvent t) {
	                        	   psProgress.textProperty().unbind();
	                        	   psProgress.setText("Scrape Complete");
	                        	   if(scgnf) {
	                        		   try {
	                        			   SCGCards = Helper.readScgFile(scgnfFilename);
	                        		   }catch( Exception e) {
	                        			   System.out.println(scgnfFilename + " could not be read");
	                        		   }
	                        		   
	                        	   }
	                        	   if(scgf) {
	                        		   try {
	                        			   SCGFoilCards = Helper.readScgFile(scgfFilename);
	                        		   }catch( Exception e) {
	                        			   System.out.println(scgfFilename + " could not be read");
	                        		   }
	                        		   
	                        	   }
	                        	   psStart.setDisable(false);
	                        	   psCancel.setDisable(true);
	                           }
	                       });
			}
		});
		
		//Stop the current task
		psCancel.setOnAction(action -> {
			
				psStart.setDisable(false);
				psCancel.setDisable(true);
				
				if(cknf || ckf) {
					logger.info("Canceling Card Kingdom Web Scraper");
					ckWebScraper.cancel(true);
					psBar.progressProperty().unbind();
					psProgress.textProperty().unbind();
					psBar.setProgress(0);
					psProgress.setText("Scrape Progress");
				}
				if(scgnf || scgf) {
					logger.info("Canceling Star City Games Web Scraper");
					scgWebScraper.cancel(true);
					psBar.progressProperty().unbind();
					psProgress.textProperty().unbind();
					psBar.setProgress(0);
					psProgress.setText("Scrape Progress");
				}
				
		});
		
		//Go back to the homepage
		psBack.setOnAction(action ->{
			logger.info("Back to the Main Menu");
			ckNonFoil.setSelected(false);
			ckFoil.setSelected(false);
			scgNonFoil.setSelected(false);
			scgFoil.setSelected(false);
			stage.setScene(base);
		});
		
		//Change to the Card Data scene
		cardDataPage.setOnAction(action ->{
			logger.info("Go to the Data Transformation Page");
			stage.setScene(cdScene);
		});
		
		//Change to the Price Scraper scene
		priceScraper.setOnAction(action ->{
			logger.info("Go to the Price Scraper Page");
			stage.setScene(psScene);
		});
		
		//Change to the Card Search Scene
		cardSearch.setOnAction(action ->{
			logger.info("Go to the Card Search Page");
			stage.setScene(csScene);
		});
		
		//Change to the Deck Builder Scene
		deckBuilder.setOnAction(action ->{
			logger.info("Go to the Deck Builder Page");
			stage.setScene(dbScene);
		});
		
		//Set to the Base scene and make the stage visible
		stage.setScene(base);
		stage.show();
		
	}
	
	@Override
	public void stop() {
		System.out.println("Closing");
		logger.info("Closing the application");
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
