package application;
	
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

public class Main extends Application {
	String split[], loginFile = "loginInfo.txt", orderFile = "orderFile.txt", inventoryFile = "inventory.txt";
	double total;
	int numberOfItems = 5;
	
	ArrayList<Customer> customers;
	ArrayList<Item> inventoryList;
	ArrayList<String> orders = new ArrayList<String>();
	
	TextField ID, password, address, phone, card, testField;
	TextField createID, createPassword;
	Label display, passedLogin = new Label();
	Label promptLogin = new Label("Enter username: ");
	Label promptPass = new Label("Enter password: ");
	
	Label item[] = new Label[numberOfItems];
	Label describe[] = new Label[numberOfItems];
	Label price_reg[] = new Label[numberOfItems];
	Label price_prem[] = new Label[numberOfItems];
	Label quanitites[] = new Label[numberOfItems];
	TextField quantity[] = new TextField[numberOfItems];
	Label temp1[] = new Label[numberOfItems];
	Label temp2[] = new Label[numberOfItems];
	
	Button createAcc = new Button("Create Account");
	Label c_promptID = new Label("Enter ID: "); 
	Label c_promptPass = new Label("Enter Password: ");
	Label promptAddress = new Label("Enter address: ");
	Label promptPhone = new Label("Enter Phone Number: ");
	Label promptCard = new Label("Enter Credit Card Number: ");
	Label makeAccountDisplay = new Label();
	
	Scene scene;
	
	//Item layout information for catalog:
	Label promptCataglog = new Label("Choose items and quantity from list: ");
	Label currentOrder = new Label("Current order: ");
	Label itemTypePrompt = new Label("Prices for reg and premium users");
	
	//Labels to display Order Info(Array of display labels)
	Label orderView[] = new Label[numberOfItems+1];
	Label priceView[] = new Label[orderView.length];
	
	RadioButton accountType_prem, accountType_reg;
	ToggleGroup radioButtons = new ToggleGroup();
	Button loginButton = new Button("Login"); 
	
	BufferConnector con;
	
	public void start(Stage primaryStage) {
		makeAccountDisplay.setWrapText(true);
		makeAccountDisplay.setMaxWidth(150);
		makeAccountDisplay.setTextAlignment(TextAlignment.CENTER);
		passedLogin.setWrapText(true);
		passedLogin.setMaxWidth(160);
		passedLogin.setTextAlignment(TextAlignment.CENTER);
		
		try{
			customers = readLoginFile(); //Read in file login stuff.
			inventoryList = readStockFile();//Item reading method
		}
		catch(IOException e){
			e.printStackTrace();
		}
		System.out.println("Program running");
		/*
		for(Customer x:customers) {//Parse ID/Pass into seperate string arrays
			System.out.println(String.format("ID: %s\nPass: %s\nCard: %s\nAddress: %s\nPhone#: %s\nAccount: %s\n",
								x.ID,x.password,x.card,x.address,x.phone,x.accountType));
		}*/
		
		for(int y = 0;y < numberOfItems;y++) {//loop to initialize Label Arrays
			item[y] = new Label(inventoryList.get(y).name);
			price_reg[y] = new Label(String.valueOf(inventoryList.get(y).price_reg));
			price_prem[y] = new Label(String.valueOf(inventoryList.get(y).price_prem));
			describe[y] = new Label(inventoryList.get(y).description);
			temp1[y] = new Label();
			temp2[y] = new Label();
			quantity[y] = new TextField();
			orderView[y] = new Label();
			priceView[y] = new Label();
		}
		orderView[numberOfItems] = new Label();
		priceView[numberOfItems] = new Label();
		ID = new TextField();
		password = new TextField();
		
		createAcc.setOnAction(new CreateAccountClass());
		loginButton.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event) {
				String userName = ID.getText();
				String Password = password.getText();
				
				for(Customer x:customers) { //Check if login works. Open new window for items...------------
					if(x.ID.equals("Lisa") && x.password.equals("Gordon")) {
						Button viewStock = new Button("View Stock");
						Button logout = new Button("Logout");
						Button processOrder = new Button("Process Order");
						Button shipOrder = new Button("Ship Order");
						
						Label orderStatus = new Label();
						Label listPrompt = new Label();
						ListView<String> ordersList = new ListView<String>();//List of names for list, to process Orders
						ordersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
						
						for(int y = 0;y < numberOfItems;y++){
							temp1[y] = new Label("");
							temp2[y] = new Label("");
						}
						try{
							Scanner person = new Scanner(new File(orderFile));
							orders.clear();
							
							//Looks through each line of itemFile
							while(person.hasNextLine()){
								//Adds each line to an ArrayList
								orders.add(person.nextLine());
							}
							person.close();
						}catch(IOException e){
							System.out.println("File not found");
						}
						
						viewStock.setOnAction(new EventHandler<ActionEvent>(){
							public void handle(ActionEvent event) {
								for(int y = 0;y < numberOfItems;y++){
									temp1[y].setText(item[y].getText());
									temp2[y].setText(String.valueOf(inventoryList.get(y).stock));
								}
						}});
						logout.setOnAction(new EventHandler<ActionEvent>(){//Operation to Log out
							
							public void handle(ActionEvent event) {
								passedLogin.setText("Logout successful");
								ID.setText("");
								password.setText("");
								primaryStage.setScene(scene);
							}
						});
						processOrder.setOnAction(new EventHandler<ActionEvent>(){
							public void handle(ActionEvent event) {
								listPrompt.setText("\t\t\tSelect an order from list to view: ");
								try{
									Scanner person = new Scanner(new File(orderFile));
									orders.clear();
									
									//Looks through each line of orderFile
									while(person.hasNextLine()){
										//Adds each line to an ArrayList
										orders.add(person.nextLine());
									}
									person.close();
								}
								catch(FileNotFoundException e){
									System.out.println("File not found");
									e.printStackTrace();
								}
								
								for(int y = 0;y < orders.size();y += 6){
									if(ordersList.getItems().contains(orders.get(y)) == false){
										ordersList.getItems().add(orders.get(y));
									}
								}
								ordersList.setOnMouseClicked(new EventHandler<MouseEvent>(){
									String temp;
									public void handle(MouseEvent event) {
										String selectedOrder = ordersList.getSelectionModel().getSelectedItem();//Name of selected order
										temp = "";
										
										for(int y = 0;y < numberOfItems;y++){
											if(inventoryList.get(y).stock < 
														Integer.parseInt(orders.get(orders.indexOf(selectedOrder) + y + 1).split(",")[2])){
												temp += (inventoryList.get(y).name+" needs to be refilled\n");
											}
										}
										if(temp.equals("")){
											temp = "Order Ready";
										}
										orderStatus.setText(temp);
										
								}});
								shipOrder.setOnAction(new EventHandler<ActionEvent>(){
									public void handle(ActionEvent event) {
										String selectedOrder = ordersList.getSelectionModel().getSelectedItem();
										if(orderStatus.getText().equals("Order Ready")) {
											for(int y = 0;y < numberOfItems;y++){
												inventoryList.get(y).stock -= Integer.parseInt(orders.get(orders.indexOf(selectedOrder) + y + 1).split(",")[2]);
											}
											try{
												FileWriter fr = new FileWriter(new File(inventoryFile));
												//Loops through the ArrayList and adds each entry to the file
												for(Item x:inventoryList){
													fr.write(String.format("%s,%f,%f,%s,%d\n",x.name,x.price_reg,x.price_prem,x.description,x.stock));
													orderStatus.setText("Order Shipped");
												}
												fr.close();
											}catch(IOException e){
												System.out.println("File not found");
											}
										}
								}});
						}});
						VBox items = new VBox(20,temp1[0],temp1[1],temp1[2],temp1[3],temp1[4]);
						VBox stock = new VBox(20,temp2[0],temp2[1],temp2[2],temp2[3],temp2[4]);
						HBox stockDisplay = new HBox(50,items,stock);
						
						VBox buttons = new VBox(20,viewStock,processOrder,shipOrder,logout);
						buttons.setAlignment(Pos.CENTER);
						HBox buttonLayout = new HBox(20,stockDisplay,buttons);
						buttonLayout.setAlignment(Pos.CENTER);
						HBox orderBox = new HBox(30,ordersList,orderStatus);
						orderBox.setAlignment(Pos.CENTER);
						orderBox.setPrefWidth(700);					//Fix the sizing sadness
						VBox orderDisplay = new VBox(20,buttonLayout,listPrompt,orderBox);
						orderDisplay.setAlignment(Pos.CENTER);
						Scene scene3 = new Scene(orderDisplay,1280, 720);
		                primaryStage.setScene(scene3);
		                primaryStage.setTitle("Supplier Menu");
					}
					//Where customers do stuff
					else if(userName.equals(x.ID) && Password.equals(x.password)){
						Button logout = new Button("Logout");
						Button addCart = new Button("Add to cart");
						Button viewOrder = new Button("View Order");
						Button makeOrder = new Button("Make Order");
						
						logout.setOnAction(new EventHandler<ActionEvent>(){//Operation to Log out
							public void handle(ActionEvent event) {
								passedLogin.setText("Logout successful");
								ID.setText("");
								password.setText("");
								primaryStage.setScene(scene);
							}});
						makeOrder.setOnAction(new EventHandler<ActionEvent>(){//addToCart action event--(Add to a file)
							public void handle(ActionEvent event) {
								total = 0;
 								try {
									Scanner person = new Scanner(new File(orderFile));
									orders.clear();
									
									//Looks through each line of itemFile
									while(person.hasNextLine()){
										//Adds each line to an ArrayList
										orders.add(person.nextLine());
									}
									person.close();
								}catch(IOException e) {
									System.out.println("File not found");
								}
 								for(int y = 0;y < quantity.length;y++){//if blank entry, set quantity to 0
 									orderView[y].setText("");
 									priceView[y].setText("");
								}
 								orderView[numberOfItems].setText("");
 								
 								if(orders.indexOf(x.ID) == -1) {
 									orderView[2].setText("No orders found");
 									return;
 								}
 								for(int y = orders.indexOf(x.ID); y < orders.indexOf(x.ID) + 5;y++) {
 									if(Integer.parseInt(orders.get(y+1).split(",")[2]) == 0) {
 										total += 1;
 									}
 								}
 								if(total == 5) {
 									orderView[2].setText("Order is empty, please add to order");
 									return;
 								}
 								total = 0;
								for(int y = orders.indexOf(x.ID); y < orders.indexOf(x.ID) + 5;y++) {
									total += (Double.parseDouble(orders.get(y+1).split(",")[1]) * 
												Integer.parseInt(orders.get(y+1).split(",")[2]));
								}
								
								con = new BufferConnector();
								new Bank(con);
								new OSS(con, x.card, total);
								
								if(con.getAuth() == -1) {
									orderView[2].setText("Order could not go through");
									orderView[3].setText("please check account balance");
								}
								else {
									orderView[2].setText("Order has been processed");
									/*
									int spot = orders.indexOf(x.ID);
									for(int y = spot; y <= spot + 5;y++) {
										orders.remove(spot);
									}*/
								}
								try {
									FileWriter fr = new FileWriter(new File(orderFile));
									//Loops through the ArrayList and adds each entry to the file
									for(String x:orders){
										fr.write(x + "\n");
									}
									fr.close();
								}catch(IOException e) {
									System.out.println("File not found");
								}
								
							}});
						addCart.setOnAction(new EventHandler<ActionEvent>(){//addToCart action event--(Add to a file)
							public void handle(ActionEvent event) {
								try {
									Scanner person = new Scanner(new File(orderFile));
									orders.clear();
									
									//Looks through each line of itemFile
									while(person.hasNextLine()){
										//Adds each line to an ArrayList
										orders.add(person.nextLine());
									}
									person.close();
									
									for(int y = 0;y < quantity.length;y++){//if blank entry, set quantity to 0
										if(quantity[y].getText().equals("")){
											quantity[y].setText("0");
										}
									}
									
									//If the ArrayList contains the user, edit their order
									if(orders.contains(x.ID)){
										//Checks if the user is premium or not
										if(x.accountType == "True"){
											for(int z = 0; z < numberOfItems;z++){
												//Adds the 1 to the index to get off the name of the user, then adds z to increment through items
												orders.set(orders.indexOf(x.ID) + z + 1,String.format("%s,%s,%s",item[z].getText(), price_prem[z].getText(),
													quantity[z].getText()));
											}
										}
										else{
											for(int z = 0; z < numberOfItems;z++){
												orders.set(orders.indexOf(x.ID) + z + 1,String.format("%s,%s,%s",item[z].getText(), price_reg[z].getText(),
													quantity[z].getText()));
											}
										}
									}
									//If the user is not in the ArrayList, add them
									else{
										orders.add(x.ID);
										if(x.accountType == "True"){
											for(int z = 0; z < numberOfItems;z++){
												orders.add(String.format("%s,%s,%s",item[z].getText(), price_prem[z].getText(),
													quantity[z].getText()));
											}
										}
										else{
											for(int z = 0; z < numberOfItems;z++){
												orders.add(String.format("%s,%s,%s",item[z].getText(), price_reg[z].getText(),
													quantity[z].getText()));
											}
										}
									}
									
									FileWriter fr = new FileWriter(new File(orderFile));
									//Loops through the ArrayList and adds each entry to the file
									for(String x:orders){
										fr.write(x + "\n");
									}
									fr.close();
									
									if(x.accountType.equals("True")){
										for(int y = 0;y < numberOfItems;y++){
											total += (Double.parseDouble(price_prem[y].getText()) * 
												Double.parseDouble(quantity[y].getText()));
										}
									}
									else if(x.accountType.equals("False")){
										for(int y = 0;y < numberOfItems;y++){
											total += (Double.parseDouble(price_reg[y].getText()) *
												Double.parseDouble(quantity[y].getText()));
										}
									}
								} 
								catch (IOException e) {
									e.printStackTrace();
								}
							//System.out.println("total: "+total);
							}
						});
						viewOrder.setOnAction(new EventHandler<ActionEvent>(){
							//Take Array list order, (matching login info). Display order info for that user below in GUI
							public void handle(ActionEvent event) {
								try {
									String line;
									Scanner personFind = new Scanner(new File(orderFile));
									double calced;
									total = 0;
									while(personFind.hasNextLine()){
										line = personFind.nextLine();
										if(line.equals(x.ID)){
											for(int z = 0;z < numberOfItems;z++){
												//Read in from txt file
												//Display to labels in GUI
												split = personFind.nextLine().split(",");
												calced = (Double.parseDouble(split[1]) * Double.parseDouble(split[2]));
												orderView[z].setText(split[0] + ": " + split[2]);
												priceView[z].setText("price: $"+ calced);
												total += calced;
											}
											orderView[numberOfItems].setText("Order total: $"+total/2);
											return;
										}
										orderView[2].setText("No Order Found");
									}
									
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
							}
							
						});
						passedLogin.setText("Login Info Sucessful");
						
						VBox orderInfo = new VBox(20, orderView[0],orderView[1],orderView[2],orderView[3],orderView[4],orderView[5]);//Order Info Display Labels
						VBox priceInfo = new VBox(20, priceView[0],priceView[1],priceView[2],priceView[3],priceView[4],priceView[5]);
						HBox priceStuff = new HBox(20, orderInfo,priceInfo);
						VBox generalInfo = new VBox(60,promptCataglog,itemTypePrompt,currentOrder,priceStuff);
						VBox itemList_v = new VBox(20,item[0],describe[0],item[1],describe[1],item[2],describe[2],item[3],
													describe[3],item[4],describe[4]);
													
						VBox priceVbox_reg = new VBox(60,price_reg[0],price_reg[1],price_reg[2],price_reg[3],price_reg[4]);
						VBox priceVbox_prem = new VBox(60,price_prem[0],price_prem[1],price_prem[2],price_prem[3],price_prem[4]);
						VBox quantityVbox = new VBox(60,quantity[0],quantity[1],quantity[2],quantity[3],quantity[4]);
						VBox buttonBoxes = new VBox(20,addCart,viewOrder,makeOrder,logout);
						buttonBoxes.setAlignment(Pos.CENTER);
						VBox rightSide = new VBox(40,quantityVbox,buttonBoxes);
						HBox itemList_h = new HBox(130,generalInfo,itemList_v,priceVbox_reg,priceVbox_prem,rightSide);
						itemList_h.setAlignment(Pos.CENTER);
						itemList_h.setPadding(new Insets(40));
						
		                Scene scene2 = new Scene(itemList_h,1280, 720);
		                
		                primaryStage.setScene(scene2);
		                primaryStage.setTitle("ItemList");
						return;
					}
				}
				passedLogin.setText("Error: Wrong login info. Either retype or create an account");
			}});
		
		createID = new TextField();
		createPassword = new TextField();
		address = new TextField();
		phone = new TextField();
		card = new TextField();
		
		accountType_prem = new RadioButton("Preminum");
		accountType_prem.setToggleGroup(radioButtons);
		accountType_reg = new RadioButton("Regular");
		accountType_reg.setToggleGroup(radioButtons);
		
		VBox v_createAccount = new VBox(5,c_promptID,createID,c_promptPass,createPassword,promptAddress,address,promptPhone,
				phone,promptCard,card,accountType_prem,accountType_reg,createAcc, makeAccountDisplay);
		
		VBox vbox1 = new VBox(30, promptLogin,ID,promptPass,password,loginButton,passedLogin);
		HBox hbox = new HBox(300,vbox1,v_createAccount);
		hbox.setAlignment(Pos.CENTER);
		hbox.setPadding(new Insets(40));
		scene = new Scene(hbox,1280, 720);
		primaryStage.setScene(scene);
		primaryStage.setTitle("LoginPage");
		primaryStage.show();
	}
	
	public class Item{
		String name,description;
		double price_reg,price_prem;
		int stock;
		Item(String name,String price_reg, String price_prem, String description, String stock){
			this.name = name;
			this.price_reg = Double.parseDouble(price_reg);
			this.price_prem = Double.parseDouble(price_prem);
			this.description = description;
			this.stock = Integer.parseInt(stock);
		}
	}
	public class Customer{
		String ID,password,card,address,phone,accountType;
		Customer(String ID, String password, String card, String address, String phone,String accountType){
			this.ID = ID;
			this.password = password;
			this.card = card;
			this.address = address;
			this.phone = phone;
			this.accountType = accountType;
		}
	}
	
	//Reads the file and makes an array of objects containing the customers
	ArrayList<Customer> readLoginFile() throws FileNotFoundException {
		Scanner fileScanner = new Scanner(new File(loginFile));
		ArrayList<Customer> temp = new ArrayList<Customer>();
		
		while(fileScanner.hasNextLine()) {
			split = fileScanner.nextLine().split(",");
			
			temp.add(new Customer(split[0],split[1],split[2],split[3],split[4],split[5]));
		}
		fileScanner.close();
		return(temp);	
	}
	
	//Reads the stock file and prints it out to display
	ArrayList<Item> readStockFile() throws FileNotFoundException {
		Scanner fileScanner = new Scanner(new File(inventoryFile));
		ArrayList<Item> temp = new ArrayList<Item>();
		
		while(fileScanner.hasNextLine()) {
			split = fileScanner.nextLine().split(",");
			
			temp.add(new Item(split[0],split[1],split[2],split[3],split[4]));
		}
		fileScanner.close();
		return(temp);	
	}
	
	void appendfile(String ID, String password, String card, String address, String phone, String accountType) throws IOException {
		FileWriter fr = new FileWriter(new File(loginFile), true);
		fr.write(String.format("%s,%s,%s,%s,%s,%s\n",ID,password,card,address,phone,accountType));
		fr.close();
	}
	class CreateAccountClass implements EventHandler<ActionEvent>{//Create Account action event
		public void handle(ActionEvent event) {
			String userID = createID.getText();
			String passwordData = createPassword.getText();
			String ccInfo = card.getText();
			String str_address = address.getText();
			String phoneNumber = phone.getText();
			
			if(ccInfo.equals("") && str_address.equals("") && phoneNumber.equals("")) {//If only enter ID/Pass-Supplier
				if(userID.equals("") || passwordData.equals("")){
					makeAccountDisplay.setText("Error: information not complete, please enter in all data");
					return;
				}
				else{
					for(Customer x:customers) {
						if(userID.equals(x.ID)) {
							makeAccountDisplay.setText("Error: ID already in system. Try different credentials");
							return;
						}
					}
				}
			}
			else if(ccInfo.equals("") || str_address.equals("") || phoneNumber.equals("") || userID.equals("") || passwordData.equals("")){
				makeAccountDisplay.setText("Error: information not complete, please enter in all data");
				return;
			}
			try {
				if(accountType_prem.isSelected()) {//if account is premuim
					appendfile(userID,passwordData,ccInfo,str_address,phoneNumber,"True");
				}
				else {
					appendfile(userID,passwordData,ccInfo,str_address,phoneNumber,"False");//Add new user data to file.
				}
				readLoginFile();//Update str array info
			} catch (IOException e) {
					e.printStackTrace();
			}
		}
		
	}
	public static void main(String[] args) {
		launch(args);
	}
}

class OSS implements Runnable {
	BufferConnector con;
	double charge;
	String account;
	
	OSS(BufferConnector con,String account,double charge) {
		this.con = con;
		this.charge = charge;
		this.account = account;
		new Thread(this,"OSS").start();
	}
	
	public void run() {
		con.send(account,charge);
	}
}

class Bank implements Runnable {
	BufferConnector con;
	String cardNumber,charge;
	ArrayList<String[]> cards = new ArrayList<String[]>();
	
	Bank(BufferConnector con) {
		this.con = con;
		new Thread(this,"Bank").start();
	}
	
	public void run() {
		//Gets charge and does checking
		charge = con.recieve();
		chargeAmount(charge.split(",")[0],Double.parseDouble(charge.split(",")[1]));
	}
	void chargeAmount(String cardNumber,double charge){
		Boolean reply = false;
		Scanner people;
		cards.clear();
		try {
			people = new Scanner(new File("customerCard.txt"));
			//Looks through each line of creditCard file
			while(people.hasNextLine()){
				//Adds each line to an ArrayList
				cards.add(people.nextLine().split(","));
			}
			people.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		for(String[] x:cards){
			if(x[0].equals(cardNumber)){
				if(Double.parseDouble(x[1]) >= charge){
					x[1] = String.valueOf(Double.parseDouble(x[1]) - charge);
					reply = true;
					break;
				}
			}
		}
		try{
			FileWriter fr = new FileWriter(new File("customerCard.txt"));
			//Loops through the ArrayList and adds each entry to the file
			for(String[] y:cards){
				fr.write(y[0] + "," + y[1] + "\n");
			}
			fr.close();
		}catch(IOException e){
			System.out.println("File not found");
		}
		con.reply(reply);
	}
}

class BufferConnector{
	String account;
	double charge;
	int authCode = 0;
	boolean valueSet = false;
	
	synchronized String recieve() {
		while(!valueSet) {
			try {
				wait();
			}catch(InterruptedException e) {
				System.out.println("InterruptedException caught");
			}
		}
		valueSet = false;
		return(account +","+charge);
	}
	
	synchronized void send(String account,double charge) {
		while(valueSet) {
			try {
				wait();
			}catch(InterruptedException e) {
				System.out.println("InterruptedException caught");
			}
		}
		this.account = account;
		this.charge = charge;
		valueSet = true;
		notify();
	}
	
	synchronized void reply(boolean result) {
		Random auth = new Random();
		notify();
		
		if(result == true){
			authCode = auth.nextInt(1000)+1000;
		}
		else{
			authCode = -1;
		}
	}
	synchronized int getAuth() {
		if(authCode == 0) {
			try {
				wait();
			}catch(InterruptedException e) {
				System.out.println("InterruptedException caught");
			}
		}
		//System.out.println("code is: "+authCode);
		return authCode;
	}
}