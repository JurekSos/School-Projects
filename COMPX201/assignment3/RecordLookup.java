import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class RecordLookup{
	private static String tableFileName = "records.csv"; 
	private static RecordHashTable table = new RecordHashTable(16);

	public static void main(String[] args) throws IOException {
		importTableFromFile(tableFileName);
		
		while(true){
			showOptions();
			int choice = getChoice();
			processChoice(choice);
		}
	}

	/**
	  Imports the given file and attempts to store it in a hash table
	  @param String filename, the name of the file to open
	  */
	private static void importTableFromFile(String filename){
		try(FileReader fr = new FileReader(filename)){
			boolean firstLineRead = false;
			int i;
			String line = "";
			while((i = fr.read()) != -1){
				char currChar = (char)i;
				if(currChar == '\n'){

					//Skip trying to add the first line of the file to the table
					if(firstLineRead){
						addCSVLineToTable(line);
					}else{
						//Process the line and add a new Record to the table, then clear the line
						firstLineRead = true;
					}

					line = "";
				}else{
					line = line.concat(Character.toString(currChar));
				}
			}
			//Make sure there isn't an unprocesed line after reaching EOF
			if(line != ""){
				addCSVLineToTable(line);
			}
			System.out.println("File read: " + filename);
		}catch(Exception ex){
			System.out.println(ex);
			System.out.println("Warning: Aborting file read");
		}
	}

	/**
	  Takes a line of comma seperated values and attempts to add it to the table as a record
	  @param String line, the line of comma seperated values
	  @throws Exception when `line` does not contain 4 values
	  */
	private static void addCSVLineToTable(String line) throws Exception {
		line = line.replaceAll("\\r", "");
		String[] vals = line.split(",");
		if(vals.length == 4){
			String genre = vals[0];
			int year = Integer.parseInt(vals[1]);
			String artist = vals[2];
			String title = vals[3];

			table.put(new Record(genre, year, artist, title));
		}else{
			throw new Exception("Error: incorrect number of values in line. Line was: \"" + line + "\"");
		}
	}

	/**
	  Displays the options the user can choose from
	  */
	private static void showOptions(){
		System.out.println("\n\nMENU:\n");
		System.out.println("1 - Display the number of records stored");
		System.out.println("2 - Print out the contents of the table");
		System.out.println("3 - Search for a record");
		System.out.println("4 - Add a new record");
		System.out.println("5 - Remove a record");
		System.out.print("Enter selection: ");
	}

	/**
	  Gets an input from user until they choose a valid option
	  @throws IOException
	  */
	private static int getChoice() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		int choice = 0;
		String input;

		while(choice == 0){
			try{
				input = in.readLine();
				choice = Integer.parseInt(input);
				if(choice < 1 || choice > 5){
					System.out.print("Invalid option, please enter a number between 1 and 5: ");
					choice = 0;
				}
			}catch(Exception ex){
				System.out.print("Invalid option, please enter a number between 1 and 5: ");
			}
		}

		return choice;
	}

	/**
	  Performs an action based on the choice
	  @param int choice, the user's earlier choice
	  */
	private static void processChoice(int choice) throws IOException {
		System.out.println();
		switch (choice){
			case 1: printStoredRecords();
					break;
			case 2: printTable();
					break;
			case 3: searchForRecord();
					break;
			case 4: addNewRecord();
					break;
			case 5: removeRecord();
					break;
		}
	}

	private static void printStoredRecords(){
		System.out.println(table.size());
	}

	private static void printTable(){
		table.dump();
	}

	/**
	  Gets a record to search for from the user and then prints if it was found or not
	  @throws IOException
	  */
	private static void searchForRecord() throws IOException {
	   String t = getStringFromUser("title");
	   Record r = table.get(t);
		if(r != null){
			System.out.println("Found: " + t + ", " + r.toString());
		}else{
			System.out.println("Not found");
		}
	}

	/**
	  Gets a record to add to the table from the user and inserts it
	  @throws IOException
	  */
	private static void addNewRecord() throws IOException {
		Record a = getRecordFromUser();
		if(table.contains(a)){
			System.out.println("Record is already in table");
		}else{
			table.put(a);
			System.out.println("Record added");
		}
	}

	/**
	  Gets a record to remove from the table from the user and removes it
	  @throws IOException
	  */
	private static void removeRecord() throws IOException {
		Record r = getRecordFromUser();
		table.remove(r);
		System.out.println("Removed");
	}

	/**
	  Generates a `Record` from user input
	  @return the record given by the user
	  @throws IOException
	  */
	private static Record getRecordFromUser() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		String g;
		int y;
		String a;
		String t;

		String input;

		g = getStringFromUser("genre");

		System.out.print("Please enter the record's release year: ");
		while(true){
			try{
				input = in.readLine();
				y = Integer.parseInt(input);
				break;
			}catch(Exception ex){
				System.out.print("Please enter a number: ");
			}
		}

		a = getStringFromUser("artist");
		t = getStringFromUser("title");

		return new Record(g,y,a,t);
	}

	/**
	  Gets one of the string fields for the `Record` from the user
	  @param String field, the field of the record to get an input for
	  @throws IOException
	  */
	private static String getStringFromUser(String field) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Please enter the record's " + field + ": ");
		return in.readLine().trim();
	}
}
