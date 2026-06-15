import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions.*;
import java.io.*;

class HashTableTest{
	private final PrintStream standardOut = System.out;
	private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
	/*
	@Test
	@DisplayName("name")
	public void test(){
		//Arrange

		//Act

		//Assert
		Assertions.assert
	}
	*/

	//Most tests here have similar reasoning, which is that I believe it is most important to initailly verify that the program works as expected when used as intended

	// put
	@Test
	@DisplayName("Putting into an empty table without rehashing")
	public void emptyPutTest(){
		//Arrange
		RecordHashTable test = new RecordHashTable(10);
		//Act
		test.put(new Record("A", 1, "A", "A"));
		//Assert
		int actual = test.size();
		Assertions.assertEquals(1, actual);
	}

	@Test
	@DisplayName("Putting a second record with the same hash")
	public void sameHashPutTest(){
		//Arrange
		RecordHashTable test = new RecordHashTable(10);
		//Act
		test.put(new Record("A", 1, "A", "A"));
		test.put(new Record("K", 11, "K", "K"));
		//Assert
		int actual = test.size();
		Assertions.assertEquals(2, actual);
	}

	// remove
	@Test
	@DisplayName("Removing a record in the table")
	public void validRemoveTest(){
		//Arange
		RecordHashTable test = new RecordHashTable(10);
		//Act
		test.put(new Record("A", 1, "A", "A"));
		test.remove(new Record("A", 1, "A", "A"));
		//Assert
		int actual = test.size();
		Assertions.assertEquals(0, actual);
	}

	@Test
	@DisplayName("Trying to remove a nonexistant record")
	public void invalidRemoveTest(){
		//Arange
		RecordHashTable test = new RecordHashTable(10);
		//Act
		test.put(new Record("A", 1, "A", "A"));
		test.remove(new Record("G", 5, "C", "B"));
		//Assert
		int actual = test.size();
		Assertions.assertEquals(1, actual);
	}

	// contains
	@Test
	@DisplayName("Checking for an existing record")
	public void validContainsTest(){
		//Arrange
		RecordHashTable test = new RecordHashTable(10);
		//Act
		test.put(new Record("A", 1, "A", "A"));
		boolean actual = test.contains(new Record("A", 1, "A", "A"));
		//Assert
		Assertions.assertTrue(actual);
	}

	@Test
	@DisplayName("Checking for a nonexistant record")
	public void invalidContainsTest(){
		//Arrange
		RecordHashTable test = new RecordHashTable(10);
		//Act
		boolean actual = test.contains(new Record("A", 1, "A", "A"));
		//Assert
		Assertions.assertFalse(actual);
	}

	// get	
	@Test
	@DisplayName("Get an existing record")
	public void validGetTest(){
		//Arrange
		RecordHashTable test = new RecordHashTable(10);
		Record expected = new Record("A", 1, "A", "B");
		test.put(expected);
		//Act
		Record actual = test.get("B");
		//Assert
		Assertions.assertEquals(0, expected.compareTo(actual));
	}

	@Test
	@DisplayName("Get a nonexistant record")
	public void invalidGetTest(){
		//Arrange
		RecordHashTable test = new RecordHashTable(10);
		//Act
		Record actual = test.get("B");
		//Assert
		Assertions.assertNull(actual);
	}

	//isEmpty
	@Test
	@DisplayName("isEmpty when empty")
	public void emptyTest(){
		//Arrange
		RecordHashTable test = new RecordHashTable(10);
		//Act
		boolean actual = test.isEmpty();
		//Assert
		Assertions.assertTrue(actual);
	}

	@Test
	@DisplayName("isEmpty when not empty")
	public void notEmptyTest(){
		//Arrange
		RecordHashTable test = new RecordHashTable(10);
		test.put(new Record("A", 1, "A", "A"));
		//Act
		boolean actual = test.isEmpty();
		//Assert
		Assertions.assertFalse(actual);
	}

	//size
	@Test
	@DisplayName("Size without rehashing")
	public void basicSizeTest(){
		//Arrange
		RecordHashTable test = new RecordHashTable(10);
		for(int i = 0; i < 5; i++){
			test.put(new Record("A", i, "A", "A"));
		}
		//Act
		int actual = test.size();
		//Assert
		Assertions.assertEquals(5, actual);		
	}

	@Test
	@DisplayName("Size with rehashing")
	public void rehashSizeTest(){
		//Arrange
		RecordHashTable test = new RecordHashTable(10);
		for(int i = 0; i < 20; i++){
			test.put(new Record("A", i, "A", "A"));
		}
		//Act
		int actual = test.size();
		//Assert
		Assertions.assertEquals(20, actual);		
	}

	// dump
	@Test
	@DisplayName("Empty single row dump")
	public void singleNullDumpTest(){
		//Arrange
		System.setOut(new PrintStream(outputStreamCaptor));
		RecordHashTable test = new RecordHashTable(1);
		//Act
		test.dump();
		String actual = outputStreamCaptor.toString().trim();
		//Assert
		Assertions.assertEquals("0:    null", actual);
		//Cleanup
		System.setOut(standardOut);
	}

	@Test
	@DisplayName("Two row dump with a valid record stored")
	public void storedRecordDumpTest(){
		//Arrange
		System.setOut(new PrintStream(outputStreamCaptor));
		RecordHashTable test = new RecordHashTable(2);
		test.put(new Record("Genre", 1, "Artist", "Title"));
		//Act
		test.dump();
		String actual = outputStreamCaptor.toString().trim();
		//Assert
		Assertions.assertEquals("0:    null\n1:    Title, Genre | 1 | Artist", actual);
		//Cleanup
		System.setOut(standardOut);
	}
}
