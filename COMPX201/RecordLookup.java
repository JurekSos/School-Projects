import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class RecordLookup{
	private static String treeFileName = "records.csv"; 
	private static RecordBST tree = new RecordBST();

	public static void main(String[] args) throws IOException {
		importTreeFromFile(treeFileName);
		tree.print();
		
		while(true){
			showOptions();
			int choice = getChoice();
			processChoice(choice);
		}
	}

	/**
	  Imports the given file and attempts to store it in a binary search tree
	  @param String filename, the name of the file to open
	  */
	private static void importTreeFromFile(String filename){
		try(FileReader fr = new FileReader(filename)){
			boolean firstLineRead = false;
			int i;
			String line = "";
			while((i = fr.read()) != -1){
				char currChar = (char)i;
				if(currChar == '\n'){
					//Process the line and add a new Record to the BST, then clear the line

					//Skip trying to add the first line of the file to the tree
					if(firstLineRead){
						addCSVLineToTree(line);
					}else{
						firstLineRead = true;
					}

					line = "";
				}else{
					line = line.concat(Character.toString(currChar));
				}
			}
			//Make sure there isn't an unprocesed line after reaching EOF
			if(line != ""){
				addCSVLineToTree(line);
			}
			System.out.println("File read: " + treeFileName);
		}catch(Exception ex){
			System.out.println(ex);
			System.out.println("Warning: Aborting file read");
		}
	}

	/**
	  Takes a line of comma seperated values and attempts to add it to the tree as a record
	  @param String line, the line of comma seperated values
	  @throws Exception when `line` does not contain 4 values
	  */
	private static void addCSVLineToTree(String line) throws Exception {
		line = line.replaceAll("\\r", "");
		String[] vals = line.split(",");
		if(vals.length == 4){
			String genre = vals[0];
			int year = Integer.parseInt(vals[1]);
			String artist = vals[2];
			String title = vals[3];

			tree.insert(new Record(genre, year, artist, title));
		}else{
			throw new Exception("Error: incorrect number of values in line. Line was: \"" + line + "\"");
		}
	}

	/**
	  Displays the options the user can choose from
	  */
	private static void showOptions(){
		System.out.println("\n\nMENU:\n");
		System.out.println("1 - Search for a record");
		System.out.println("2 - Add a new record");
		System.out.println("3 - Remove a record");
		System.out.println("4 - Print all records within a genre");
		System.out.println("5 - Print all records within a genre and year range");
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
			case 1: searchForRecord();
					break;
			case 2: addNewRecord();
					break;
			case 3: removeRecord();
					break;
			case 4: printRecordsWithinGenre();
					break;
			case 5: printRecordsWithinGenreAndYear();
					break;
		}
	}

	/**
	  Gets a record to search for from the user and then prints if it was found or not
	  @throws IOException
	  */
	private static void searchForRecord() throws IOException {
		Record s = getRecordFromUser();
		System.out.println(s.toString());
		System.out.println(tree.search(s));
		if(tree.search(s)){
			System.out.println("Found");
		}else{
			System.out.println("Not found");
		}
	}

	/**
	  Gets a record to add to the tree from the user and inserts it
	  @throws IOException
	  */
	private static void addNewRecord() throws IOException {
		Record a = getRecordFromUser();
		if(tree.search(a)){
			System.out.println("Record is already in tree");
		}else{
			tree.insert(a);
			System.out.println("Record added");
		}
	}

	/**
	  Gets a record to remove from the tree from the user and removes it
	  @throws IOException
	  */
	private static void removeRecord() throws IOException {
		Record r = getRecordFromUser();
		tree.remove(r);
		System.out.println("Removed");
	}

	/**
	  Gets a genre from the user and then prints every `Record` which matches the genre
	  @throws IOException
	  */
	private static void printRecordsWithinGenre() throws IOException {
		String g = getStringFromUser("genre");
		tree.printGenre(g);
	}

	/**
	  Gets a genre and year range from the user and then prints every `Record` which matches the genre and year range
	  @throws IOException
	  */
	private static void printRecordsWithinGenreAndYear() throws IOException {
		String g = getStringFromUser("genre");
		int earliest;
		int latest;
		String input;

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		System.out.print("Please enter the earliest year for the records: ");
		while(true){
			try{
				input = in.readLine();
				earliest = Integer.parseInt(input);
				break;
			}catch(Exception ex){
				System.out.print("Please enter a number: ");
			}
		}
		System.out.print("Please enter the latest year for the records: ");
		while(true){
			try{
				input = in.readLine();
				latest = Integer.parseInt(input);
				break;
			}catch(Exception ex){
				System.out.print("Please enter a number: ");
			}
		}

		tree.printGenreWithYearRange(g, earliest, latest);
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

		System.out.print("Enter the record's release year: ");
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
		return in.readLine();
	}
}
