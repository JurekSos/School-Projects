/**
  Implements a hash table for storing objects of the `Record` class
  */
class RecordHashTable{
	//Stores the values in the hash table
	private Record[] valueArray;
	//Stores the maximum distance for linear probing from a given index - used to deal with removed record creating a gap
	private int[] maxProbeDistance;
	//The number of records stored in the table
	private int storedRecords;


	public RecordHashTable(int sz){
		valueArray = new Record[sz];
		maxProbeDistance = new int[sz];
		storedRecords = 0;
	}

	/**
	  Puts a record into the tree, using the record's title as the key
	  Duplicate keys are not permitted
	  @param Record r, the record to store in the tree
	  */
	public void put(Record r){
		//no storing null record
		if(r == null){
			return;
		}
		
		int hash = hashFunction(r);

		int offset = 0;
		//move through the table until an empty space is found
		while(offset < valueArray.length){
			int index = (hash + offset) % valueArray.length;
			if(valueArray[index] == null){
				//store the record in an empty space
				valueArray[index] = r;
				storedRecords++;
				if(maxProbeDistance[hash] < offset){
					maxProbeDistance[hash] = offset;
				}
				break;
			}else if(r.getTitle() == valueArray[index].getTitle()){
				//Duplicate keys are not allowed
				return;
			}
			offset++;
		}

		//check that load factor has not exceeded 80%
		//rehash the table if it has
		float loadFactor = (float)storedRecords/(float)valueArray.length;
		if(loadFactor >= 0.8){
			rehash();
		}
	}

	/**
	  Removes a record from the table if it is stored in the table
	  @param Record r, the record the remove
	  */
	public void remove(Record r){
		//Don't try to remove a null record
		if(r == null){
			return;
		}

		int hash = hashFunction(r);
		int offset = 0;
		//search through the table for the record to remove until the known maximum probing distance has been reached
		while(offset <= maxProbeDistance[hash]){
			int i = (hash + offset) % valueArray.length;
			if(valueArray[i] != null && valueArray[i].compareTo(r) == 0){
				valueArray[i] = null;
				storedRecords--;
				if(offset == maxProbeDistance[hash]){
					updateMaxProbeDistance(hash);
				}
				break;
			}
			offset++;
		}
	}

	/**
	  Calculates the hash of the string via a folding operation and modulo division by the table size
	  @param String key, the key to hash
	  @return the hash
	  */
	private int hashFunction(String key){
		final int SEGMENT_LENGTH = 3;

		int segmentCount = (key.length()+2)/SEGMENT_LENGTH;
		String[] keySegments = new String[segmentCount];

		for(int i = 0; i < segmentCount - 1; i++){
			keySegments[i] = key.substring(i*SEGMENT_LENGTH, (i+1)*SEGMENT_LENGTH);
		}
		keySegments[segmentCount-1] = key.substring((segmentCount-1)*SEGMENT_LENGTH);

		int[] concatenatedCharacterValues = new int[segmentCount];
		for(int i = 0; i < segmentCount; i++){
			concatenatedCharacterValues[i] = concatenateCharacterValues(keySegments[i]);
		}

		int hash = 0;
		for(int i : concatenatedCharacterValues){
			hash += i;
		}

		return hash%valueArray.length;
	}

	/**
	  Calculates the hash of the record's title via a folding operation and modulo division by the table size
	  @param Record r, the record for which to calculate the hash for
	  @return the hash
	  */
	private int hashFunction(Record r){
		String key = r.getTitle();

		int hash = hashFunction(key);

		return hash;
	}

	/**
	  Expands the capacity of the hash table
	  */
	private void rehash(){
		storedRecords = 0;
		int newSize = valueArray.length * 2;

		Record[] oldValueArray = valueArray;

		valueArray = new Record[newSize];
		maxProbeDistance = new int[newSize];

		for(Record r : oldValueArray){
			put(r);
		}
	}

	/**
	  Checks whether a record is contained in the table
	  @param Record r, the record for which to check
	  @return whether the record is stored in the table
	  */
	public boolean contains(Record r){
		if(r == null){
			return false;
		}

		int hash = hashFunction(r);
		int offset = 0;

		//search through the table for the record to remove until the known maximum probing distance has been reached
		while(offset <= maxProbeDistance[hash]){
			int i = (hash + offset) % valueArray.length;

			if(valueArray[i] != null && valueArray[i].compareTo(r) == 0){
				return true;
			}

			offset++;
		}

		return false;
	}

	/**
	  Searches for a record matching the title and returns it if found
	  @param String t, the title of the record to get
	  @return the record matching the title, if found
	  */
	public Record get(String t){
		int hash = hashFunction(t);
		int offset = 0;

		//search through the table for the record to remove until the known maximum probing distance has been reached
		while(offset <= maxProbeDistance[hash]){
			int i = (hash + offset) % valueArray.length;

			if(valueArray[i] != null && valueArray[i].getTitle().compareTo(t) == 0){
				return valueArray[i];
			}

			offset++;
		}

		return null;
	}

	/**
	  Checks whether there are any records stored in the table, and returns false iff there are
	  @return whether the table is empty
	  */
	public boolean isEmpty(){
		return storedRecords == 0;
	}

	/**
	  Gives the number of records stored in the table
	  @return the number of records stored in the table
	  */
	public int size(){
		return storedRecords;
	}

	/**
	  Prints out the contents of the table
	  */
	public void dump(){
		for(int i = 0; i < valueArray.length; i++){
			String recordValue = valueArray[i] == null ? "null" : valueArray[i].getTitle() + ", " + valueArray[i].toString();
			System.out.println(Integer.toString(i) + ":    " + recordValue);
		}
	}

	/**
	  Takes a string and concatenated the ASCII values of the characters in the string
	  @param String s, the string for which to concatenate the character values
	  @return the int obtained from concatenated the character values
	  */
	private int concatenateCharacterValues(String s){
		String concatenated = "0";
		for(char c : s.toCharArray()){
			concatenated += Integer.toString((int)c);
		}
		return Integer.parseInt(concatenated);
	}

	/**
	  Checks whether the max probe distance for a specific hash can be decreased, and updates it if so
	  @param int hash, the hash to check for
	  */
	private void updateMaxProbeDistance(int hash){
		int offset = 0;
		int newMax = 0;
		while(offset <= maxProbeDistance[hash]){
			int i = (hash + offset) % valueArray.length;
			Record r = valueArray[i];
			if(r != null){
				if(hashFunction(r) == hash){
					newMax = offset;
				}
			}
			offset++;
		}
		maxProbeDistance[hash] = newMax;
	}
}
