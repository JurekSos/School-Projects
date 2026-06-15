import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Test suite for the RecordHashTable class.
 * 
 * These tests aim to provide broad coverage of:
 * - insertion
 * - removal
 * - searching
 * - collision handling
 * - rehashing
 * - edge cases
 * - size tracking
 * - empty table behavior
 * 
 * NOTE:
 * The Record class itself is NOT tested directly as requested.
 */
public class HashTableAITest {
	private final PrintStream standardOut = System.out;
	private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    /**
     * Helper method to create records quickly for tests.
     */
    private Record createRecord(String title) {
        return new Record("Rock", 2000, "Artist", title);
    }

    @Test
    @DisplayName("New hash table should be empty")
    void testNewTableIsEmpty() {
        // Create a new empty table
        RecordHashTable table = new RecordHashTable(10);

        // Verify empty state
        assertTrue(table.isEmpty());
        assertEquals(0, table.size());
    }

    @Test
    @DisplayName("Putting a single record should increase size")
    void testPutSingleRecord() {
        RecordHashTable table = new RecordHashTable(10);
        Record record = createRecord("Album1");

        // Insert one record
        table.put(record);

        // Verify insertion
        assertFalse(table.isEmpty());
        assertEquals(1, table.size());
        assertTrue(table.contains(record));
    }

    @Test
    @DisplayName("Putting null should not modify the table")
    void testPutNull() {
        RecordHashTable table = new RecordHashTable(10);

        // Attempt to insert null
        table.put(null);

        // Verify table unchanged
        assertTrue(table.isEmpty());
        assertEquals(0, table.size());
    }

    @Test
    @DisplayName("Removing null should not modify the table")
    void testRemoveNull() {
        RecordHashTable table = new RecordHashTable(10);

        // Attempt to remove null
        table.remove(null);

        // Verify table unchanged
        assertTrue(table.isEmpty());
        assertEquals(0, table.size());
    }

    @Test
    @DisplayName("Contains should return false for null")
    void testContainsNull() {
        RecordHashTable table = new RecordHashTable(10);

        // Verify null is never contained
        assertFalse(table.contains(null));
    }

    @Test
    @DisplayName("Contains should return true for inserted record")
    void testContainsInsertedRecord() {
        RecordHashTable table = new RecordHashTable(10);
        Record record = createRecord("Album2");

        table.put(record);

        // Verify record exists
        assertTrue(table.contains(record));
    }

    @Test
    @DisplayName("Contains should return false for non-existent record")
    void testContainsNonExistentRecord() {
        RecordHashTable table = new RecordHashTable(10);

        Record inserted = createRecord("Album3");
        Record missing = createRecord("Album4");

        table.put(inserted);

        // Verify missing record is not found
        assertFalse(table.contains(missing));
    }

    @Test
    @DisplayName("Removing existing record should decrease size")
    void testRemoveExistingRecord() {
        RecordHashTable table = new RecordHashTable(10);
        Record record = createRecord("Album5");

        table.put(record);

        // Remove inserted record
        table.remove(record);

        // Verify removal
        assertFalse(table.contains(record));
        assertEquals(0, table.size());
        assertTrue(table.isEmpty());
    }

    @Test
    @DisplayName("Removing non-existent record should not affect size")
    void testRemoveNonExistentRecord() {
        RecordHashTable table = new RecordHashTable(10);

        Record inserted = createRecord("Album6");
        Record missing = createRecord("Album7");

        table.put(inserted);

        // Attempt to remove missing record
        table.remove(missing);

        // Verify table unchanged
        assertEquals(1, table.size());
        assertTrue(table.contains(inserted));
    }

    @Test
    @DisplayName("Multiple records should all be stored correctly")
    void testMultipleInsertions() {
        RecordHashTable table = new RecordHashTable(10);

        Record r1 = createRecord("A");
        Record r2 = createRecord("B");
        Record r3 = createRecord("C");

        table.put(r1);
        table.put(r2);
        table.put(r3);

        // Verify all records exist
        assertEquals(3, table.size());

        assertTrue(table.contains(r1));
        assertTrue(table.contains(r2));
        assertTrue(table.contains(r3));
    }

    @Test
    @DisplayName("Hash table should handle collisions correctly")
    void testCollisionHandling() {
        // Small table increases likelihood of collisions
        RecordHashTable table = new RecordHashTable(10);

        Record r1 = createRecord("AAA");
        Record r2 = createRecord("AAK");
        Record r3 = createRecord("AKK");

        table.put(r1);
        table.put(r2);
        table.put(r3);

        // Verify all records remain accessible
        assertTrue(table.contains(r1));
        assertTrue(table.contains(r2));
        assertTrue(table.contains(r3));

        assertEquals(3, table.size());
    }

    @Test
    @DisplayName("Rehashing should preserve all inserted records")
    void testRehashPreservesRecords() {
        // Small initial size to trigger rehash quickly
        RecordHashTable table = new RecordHashTable(5);

        Record r1 = createRecord("AlbumA");
        Record r2 = createRecord("AlbumB");
        Record r3 = createRecord("AlbumC");
        Record r4 = createRecord("AlbumD");
        Record r5 = createRecord("AlbumE");

        // Insert enough records to trigger rehash
        table.put(r1);
        table.put(r2);
        table.put(r3);
        table.put(r4);
        table.put(r5);

        // Verify all records survived rehash
        assertTrue(table.contains(r1));
        assertTrue(table.contains(r2));
        assertTrue(table.contains(r3));
        assertTrue(table.contains(r4));
        assertTrue(table.contains(r5));

        assertEquals(5, table.size());
    }

    @Test
    @DisplayName("Get should return matching record when present")
    void testGetExistingRecord() {
        RecordHashTable table = new RecordHashTable(10);

        String title = "TargetAlbum";
        Record record = createRecord(title);

        table.put(record);

        // Verify get returns the same record
        assertEquals(record, table.get(title));
    }

    @Test
    @DisplayName("Get should return null for missing title")
    void testGetMissingRecord() {
        RecordHashTable table = new RecordHashTable(10);

        // Verify missing title returns null
        assertNull(table.get("DoesNotExist"));
    }

    @Test
    @DisplayName("Removing one record should not affect other records")
    void testRemoveDoesNotAffectOthers() {
        RecordHashTable table = new RecordHashTable(10);

        Record r1 = createRecord("KeepMe");
        Record r2 = createRecord("RemoveMe");

        table.put(r1);
        table.put(r2);

        // Remove only one record
        table.remove(r2);

        // Verify remaining record still exists
        assertTrue(table.contains(r1));
        assertFalse(table.contains(r2));

        assertEquals(1, table.size());
    }

    @Test
    @DisplayName("Table should support repeated insertions and removals")
    void testRepeatedInsertionsAndRemovals() {
        RecordHashTable table = new RecordHashTable(10);

        Record r1 = createRecord("R1");
        Record r2 = createRecord("R2");

        // Insert records
        table.put(r1);
        table.put(r2);

        // Remove one
        table.remove(r1);

        // Reinsert another
        Record r3 = createRecord("R3");
        table.put(r3);

        // Verify final state
        assertFalse(table.contains(r1));
        assertTrue(table.contains(r2));
        assertTrue(table.contains(r3));

        assertEquals(2, table.size());
    }

    @Test
    @DisplayName("Removing a collision-chain record should maintain accessibility")
    void testRemoveFromCollisionChain() {
        RecordHashTable table = new RecordHashTable(10);

        Record r1 = createRecord("AAA");
        Record r2 = createRecord("AAK");
        Record r3 = createRecord("AKK");

        table.put(r1);
        table.put(r2);
        table.put(r3);

        // Remove middle record in collision scenario
        table.remove(r2);

        // Verify other records still accessible
        assertTrue(table.contains(r1));
        assertTrue(table.contains(r3));
        assertFalse(table.contains(r2));

        assertEquals(2, table.size());
    }

    @Test
    @DisplayName("Size should track number of stored records accurately")
    void testSizeTracking() {
        RecordHashTable table = new RecordHashTable(10);

        Record r1 = createRecord("One");
        Record r2 = createRecord("Two");

        // Initial size
        assertEquals(0, table.size());

        // Insert first record
        table.put(r1);
        assertEquals(1, table.size());

        // Insert second record
        table.put(r2);
        assertEquals(2, table.size());

        // Remove one record
        table.remove(r1);
        assertEquals(1, table.size());
    }

	@Test
	@DisplayName("Put should correctly handle insertion into previously probed locations")
	void testPutIntoPreviouslyRemovedProbeLocation() {
		RecordHashTable table = new RecordHashTable(10);

		Record r1 = createRecord("AAA");
		Record r2 = createRecord("AAK");
		Record r3 = createRecord("AKK");

		// Create a probing situation
		table.put(r1);
		table.put(r2);

		// Remove one probed record
		table.remove(r2);

		// Insert another record
		table.put(r3);

		// Verify table integrity
		assertTrue(table.contains(r1));
		assertTrue(table.contains(r3));
		assertFalse(table.contains(r2));

		assertEquals(2, table.size());
	}

	@Test
	@DisplayName("Put should preserve records with similar titles")
	void testPutSimilarTitles() {
		RecordHashTable table = new RecordHashTable(10);

		Record r1 = createRecord("Test");
		Record r2 = createRecord("Test1");
		Record r3 = createRecord("Test2");

		// Insert similar titles
		table.put(r1);
		table.put(r2);
		table.put(r3);

		// Verify all are uniquely stored
		assertTrue(table.contains(r1));
		assertTrue(table.contains(r2));
		assertTrue(table.contains(r3));

		assertEquals(3, table.size());
	}

	@Test
	@DisplayName("Put should not allow insertion of multiple records with the same title")
	void testPutMatchingKeys() {
		RecordHashTable table = new RecordHashTable(10);

		Record record = createRecord("DuplicateAlbum");

		// Insert the same record twice
		table.put(record);
		table.put(record);

		// Only one insertion increases the size because duplicates are not allowed
		assertEquals(1, table.size());

		// Record should still be contained
		assertTrue(table.contains(record));
	}

	@Test
	@DisplayName("Remove should not remove partially matching records")
	void testRemoveOnlyExactMatch() {
		RecordHashTable table = new RecordHashTable(10);

		Record r1 = new Record("Rock", 2000, "Artist", "Album");
		Record r2 = new Record("Rock", 2001, "Artist", "Album");

		// Insert similar but distinct records
		table.put(r1);

		// Do not remove the similar record
		table.remove(r2);

		// Verify only exact match removed
		assertTrue(table.contains(r1));

		assertEquals(1, table.size());
	}

	@Test
	@DisplayName("Dump should print null entries for an empty table")
	void testDumpEmptyTable() {
		RecordHashTable table = new RecordHashTable(3);

		// Capture console output
		System.setOut(new PrintStream(outputStreamCaptor));

		try {
			// Call dump method
			table.dump();
		} finally {
			// Restore original output stream
			System.setOut(standardOut);
		}

		// Normalize line separators for portability
		String output = outputStreamCaptor.toString().replace("\r\n", "\n");

		// Verify expected dump output
		String expected =
				"0:    null\n" +
				"1:    null\n" +
				"2:    null\n";

		assertEquals(expected, output);
	}

	@Test
	@DisplayName("Dump should print inserted record information")
	void testDumpSingleRecord() {
		RecordHashTable table = new RecordHashTable(5);

		Record record = new Record("Rock", 1999, "Artist", "Album");
		table.put(record);

		// Capture console output
		System.setOut(new PrintStream(outputStreamCaptor));

		try {
			table.dump();
		} finally {
			System.setOut(standardOut);
		}

		String output = outputStreamCaptor.toString().replace("\r\n", "\n");

		// Verify inserted record appears in output
		assertTrue(output.contains("Album, Rock | 1999 | Artist"));

		// Verify every table index is printed
		assertTrue(output.contains("0:"));
		assertTrue(output.contains("1:"));
		assertTrue(output.contains("2:"));
		assertTrue(output.contains("3:"));
		assertTrue(output.contains("4:"));
	}

	@Test
	@DisplayName("Dump should print multiple inserted records")
	void testDumpMultipleRecords() {
		RecordHashTable table = new RecordHashTable(10);

		Record r1 = new Record("Jazz", 1980, "Miles Davis", "KindOfBlue");
		Record r2 = new Record("Rock", 1975, "Queen", "BohemianRhapsody");

		table.put(r1);
		table.put(r2);

		// Capture console output
		System.setOut(new PrintStream(outputStreamCaptor));

		try {
			table.dump();
		} finally {
			System.setOut(standardOut);
		}

		String output = outputStreamCaptor.toString().replace("\r\n", "\n");

		// Verify both records appear
		assertTrue(output.contains("KindOfBlue, Jazz | 1980 | Miles Davis"));
		assertTrue(output.contains("BohemianRhapsody, Rock | 1975 | Queen"));
	}

	@Test
	@DisplayName("Dump should correctly reflect removals")
	void testDumpAfterRemoval() {
		RecordHashTable table = new RecordHashTable(5);

		Record r1 = new Record("Pop", 2005, "Artist1", "Album1");
		Record r2 = new Record("Rock", 2010, "Artist2", "Album2");

		table.put(r1);
		table.put(r2);

		// Remove one record
		table.remove(r1);

		// Capture console output
		System.setOut(new PrintStream(outputStreamCaptor));

		try {
			table.dump();
		} finally {
			System.setOut(standardOut);
		}

		String output = outputStreamCaptor.toString().replace("\r\n", "\n");

		// Removed record should not appear
		assertFalse(output.contains("Album1, Pop | 2005 | Artist1"));

		// Remaining record should still appear
		assertTrue(output.contains("Album2, Rock | 2010 | Artist2"));
	}

	@Test
	@DisplayName("Dump should correctly print table after rehashing")
	void testDumpAfterRehash() {
		RecordHashTable table = new RecordHashTable(3);

		// Insert enough records to trigger rehash
		Record r1 = new Record("Genre1", 2001, "Artist1", "A");
		Record r2 = new Record("Genre2", 2002, "Artist2", "B");
		Record r3 = new Record("Genre3", 2003, "Artist3", "C");

		table.put(r1);
		table.put(r2);
		table.put(r3);

		// Capture console output
		System.setOut(new PrintStream(outputStreamCaptor));

		try {
			table.dump();
		} finally {
			System.setOut(standardOut);
		}

		String output = outputStreamCaptor.toString().replace("\r\n", "\n");

		// Verify all records remain visible after rehash
		assertTrue(output.contains("A, Genre1 | 2001 | Artist1"));
		assertTrue(output.contains("B, Genre2 | 2002 | Artist2"));
		assertTrue(output.contains("C, Genre3 | 2003 | Artist3"));
	}

	@Test
	@DisplayName("Dump should print correct number of table rows")
	void testDumpLineCount() {
		RecordHashTable table = new RecordHashTable(7);

		// Capture console output
		System.setOut(new PrintStream(outputStreamCaptor));

		try {
			table.dump();
		} finally {
			System.setOut(standardOut);
		}

		String output = outputStreamCaptor.toString().replace("\r\n", "\n");

		// Split into lines
		String[] lines = output.split("\n");

		// Verify one line per table slot
		assertEquals(7, lines.length);
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
		assertEquals("0:    null\n1:    Title, Genre | 1 | Artist", actual);
		//Cleanup
		System.setOut(standardOut);
	}

	@Test
	@DisplayName("Contains should correctly find records involved in hash collisions")
	void testContainsWithKeyCollisions() {
		RecordHashTable table = new RecordHashTable(10);

		/*
		 * These titles produce collisions in a table of size 10 because the
		 * hash function ultimately mods by table length and K has a value 10 higher than A.
		 */
		Record r1 = new Record("Rock", 2000, "Artist1", "AAA");
		Record r2 = new Record("Jazz", 2001, "Artist2", "AAK");

		// Insert both colliding records
		table.put(r1);
		table.put(r2);

		// Verify both records are still accessible
		assertTrue(table.contains(r1));
		assertTrue(table.contains(r2));

		// Verify size is correct
		assertEquals(2, table.size());
	}

	@Test
	@DisplayName("Get should correctly retrieve records involved in hash collisions")
	void testGetWithKeyCollisions() {
		/*
		 * "AAA" and "AAK" both hash to the same index in a table of size 10,
		 * forcing linear probing to occur.
		 */
		RecordHashTable table = new RecordHashTable(10);

		Record r1 = new Record("Rock", 2000, "Artist1", "AAA");
		Record r2 = new Record("Jazz", 2001, "Artist2", "AAK");

		// Insert colliding records
		table.put(r1);
		table.put(r2);

		/*
		 * Use the exact same String references used during insertion,
		 * since get() compares strings using == rather than equals().
		 */
		String key1 = r1.getTitle();
		String key2 = r2.getTitle();

		// Verify both colliding records can still be retrieved
		assertEquals(r1, table.get(key1));
		assertEquals(r2, table.get(key2));
	}

	@Test
    @DisplayName("Put should correctly handle wrap-around collision insertion")
    void testPutWrapAroundCollision() {
        RecordHashTable table = new RecordHashTable(6);

        Record r1 = createRecord("AAC");
        Record r2 = createRecord("AAI");

        table.put(r1);
        table.put(r2);

        assertTrue(table.contains(r1));
        assertTrue(table.contains(r2));
        assertEquals(2, table.size());
    }

	@Test
    @DisplayName("Get should retrieve records stored via wrap-around probing")
    void testGetWrapAroundCollision() {
        RecordHashTable table = new RecordHashTable(6);

        Record r1 = createRecord("AAC");
        Record r2 = createRecord("AAI");

        table.put(r1);
        table.put(r2);

        assertEquals(r1, table.get("AAC"));
        assertEquals(r2, table.get("AAI"));
    }

	@Test
    @DisplayName("Remove should correctly delete wrap-around placed records")
    void testRemoveWrapAroundCollision() {
        RecordHashTable table = new RecordHashTable(6);

        Record r1 = createRecord("AAC");
        Record r2 = createRecord("AAI");

        table.put(r1);
        table.put(r2);

        table.remove(r2);

        assertFalse(table.contains(r2));
        assertTrue(table.contains(r1));
        assertEquals(1, table.size());
    }

	@Test
    @DisplayName("Dump should correctly print table after wrap-around insertion")
    void testDumpWrapAroundCollision() {
        RecordHashTable table = new RecordHashTable(6);

        Record r1 = createRecord("AAC");
        Record r2 = createRecord("AAI");

        table.put(r1);
        table.put(r2);

        System.setOut(new PrintStream(outputStreamCaptor));

        try {
            table.dump();
        } finally {
            System.setOut(standardOut);
        }

        String output = outputStreamCaptor.toString().replace("\r\n", "\n");

        // Both records must appear somewhere in output
		assertTrue(output.contains("5:    AAC"));
        assertTrue(output.contains("0:    AAI"));

        // Must print exactly 6 slots (table size)
        assertEquals(6, output.split("\n").length);
    }
}
