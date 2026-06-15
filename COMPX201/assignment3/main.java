public class main{
	public static void main(String[] args){
		RecordHashTable test = new RecordHashTable(1);
		test.dump();
		System.out.println();

		test.put(new Record("A", 1, "A", "A"));

		test.dump();

		test.remove(new Record("A", 1, "A", "A"));

		test.dump();
	}
}
