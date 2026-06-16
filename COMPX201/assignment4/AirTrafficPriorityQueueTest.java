import com.sun.source.tree.AssertTree;
import org.junit.Assert;
import org.	junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions.*;
import java.io.*;

public class AirTrafficPriorityQueueTest {
	private final PrintStream standardOut = System.out;
	private final ByteArrayOutputStream outputCaptor = new ByteArrayOutputStream();

	//enqueue
	@Test
	@DisplayName("Enqueueing a plane correctly increases length by 1")
	public void enqueueTest1() {
		Plane[] p = {new Plane("1")};
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(p);
		int initial = q.length();

		q.enqueue(new Plane("2"));
		int actual = q.length() - initial;

		Assertions.assertEquals(1, actual);
	}

	@Test
	@DisplayName("Enqueueing maintains heap priority when higher priority plane added")
	public void enqueueTest2() {
		Plane[] p = {new Plane("1", Usage.TAKEOFF, 100.f, false, -6)};
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(p);

		Plane expected = new Plane("2", Usage.LANDING, 100.f, true, 4);
		q.enqueue(expected);
		Plane actual = q.peek();

		Assertions.assertEquals(expected, actual);
	}

	@Test
	@DisplayName("Enqueueing maintains heap priority when lower priority plane added")
	public void enqueueTest3() {
		Plane[] p = {new Plane("1", Usage.LANDING, 100.f, true, 4)};
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(p);

		q.enqueue(new Plane("2", Usage.TAKEOFF, 100.f, false, -6));
		Plane actual = q.peek();

		Assertions.assertEquals(p[0], actual);
	}

	@Test
	@DisplayName("Large amount of enqueued planes are all stored")
	public void enqueueTest4() {
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(new Plane[0]);

		for (int i = 0; i < 512; i++) {
			q.enqueue(new Plane(Integer.toString(i)));
		}
		int actual = q.length();

		Assertions.assertEquals(512, actual);
	}

	//dequeue
	@Test
	@DisplayName("Dequeueing from an empty queue returns null")
	public void dequeueTest1() {
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(new Plane[0]);

		Object actual = q.dequeue();

		Assertions.assertNull(actual);
	}

	@Test
	@DisplayName("Dequeueing from a queue with one plane returns that plane")
	public void DequeueTest2() {
		Plane expected = new Plane("1");
		Plane[] p = {expected};
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(p);

		Plane actual = q.dequeue();
		int actualLength = q.length();

		Assertions.assertEquals(expected, actual);
	}

	@Test
	@DisplayName("Dequeueing retains heap order")
	public void DequeueTest3() {
		Plane expected = new Plane("5", Usage.TAKEOFF, 100.f, false, 4);
		Plane[] p = new Plane[6];
		for (int i = 0; i < 4; i++) {
			p[i] = new Plane(Integer.toString(i), Usage.TAKEOFF, 100.f, false, 0);
		}
		p[4] = expected;
		p[5] = new Plane("6", Usage.LANDING, 5.f, true, 6);
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(p);

		q.dequeue();
		Plane actual = q.peek();

		Assertions.assertEquals(expected, actual);
	}

	@Test
	@DisplayName("Dequeueing removes the highest priority plane")
	public void dequeueTest4() {
		Plane expected = new Plane("6", Usage.LANDING, 5.f, true, 6);
		Plane[] p = new Plane[6];
		for (int i = 0; i < 5; i++) {
			p[i] = new Plane(Integer.toString(i), Usage.TAKEOFF, 100.f, false, 0);
		}
		p[5] = expected;
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(p);

		Plane actual = q.dequeue();

		Assertions.assertEquals(expected, actual);
	}

	//peek
	@Test
	@DisplayName("Peeking an empty queue returns null")
	public void peekTest1() {
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(new Plane[0]);

		Object actual = q.peek();

		Assertions.assertNull(actual);
	}

	@Test
	@DisplayName("Peeking returns the same plane as dequeue")
	public void peekTest2() {
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(new Plane[0]);
		for (int i = 0; i < 8; i++) {
			q.enqueue(new Plane(Integer.toString(i)));
		}

		Plane actual = q.peek();
		Plane expected = q.dequeue();

		Assertions.assertNotNull(actual);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	@DisplayName("Peeking a queue with one plane returns that plane")
	public void peekTest3() {
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(new Plane[0]);
		Plane expected = new Plane("1");
		q.enqueue(expected);

		Plane actual = q.peek();

		Assertions.assertEquals(expected, actual);
	}

	@Test
	@DisplayName("Peeking returns null after all planes have been dequeued")
	public void peekTest4() {
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(new Plane[0]);
		for (int i = 0; i < 8; i++) {
			q.enqueue(new Plane(Integer.toString(i)));
		}
		for (int i = 0; i < 8; i++) {
			q.dequeue();
		}

		Object actual = q.peek();

		Assertions.assertNull(actual);
	}

	//length
	@Test
	@DisplayName("Empty queue has length 0")
	public void lengthTest1() {
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(new Plane[0]);

		int actual = q.length();

		Assertions.assertEquals(0, actual);
	}

	@Test
	@DisplayName("Freshly constructed queue has correct length")
	public void lengthTest2() {
		Plane[] p = new Plane[8];
		for (int i = 0; i < 8; i++) {
			p[i] = new Plane(Integer.toString(i));
		}
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(p);

		int actual = q.length();

		Assertions.assertEquals(8, actual);
	}

	@Test
	@DisplayName("Enqueueing and then dequeueing a plane maintains length")
	public void lengthTest3() {
		Plane[] p = new Plane[8];
		for (int i = 0; i < 8; i++) {
			p[i] = new Plane(Integer.toString(i));
		}
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(p);

		q.enqueue(new Plane("9"));
		q.dequeue();
		int actual = q.length();

		Assertions.assertEquals(8, actual);
	}

	@Test
	@DisplayName("Length is correct after resizing")
	public void lengthTest4() {
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(new Plane[0]);

		for (int i = 0; i < 2; i++) {
			q.enqueue(new Plane(Integer.toString(i)));
		}
		int actual = q.length();

		Assertions.assertEquals(2, actual);
	}

	//isempty
	@Test
	@DisplayName("Empty queue is empty")
	public void isEmptyTest1() {
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(new Plane[0]);

		boolean actual = q.isEmpty();

		Assertions.assertTrue(actual);
	}

	@Test
	@DisplayName("Enqueueing a plane makes the queue not empty")
	public void isEmptyTest2() {
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(new Plane[0]);

		q.enqueue(new Plane("1"));
		boolean actual = q.isEmpty();

		Assertions.assertFalse(actual);
	}

	@Test
	@DisplayName("Removing the last plane from the queue makes it empty")
	public void isEmptyTest3() {
		Plane[] p = {new Plane("1")};
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(p);

		q.dequeue();
		boolean actual = q.isEmpty();

		Assertions.assertTrue(actual);
	}

	@Test
	@DisplayName("A freshly constructed queue with planes is not empty")
	public void isEmptyTest4(){
		Plane[] p = {new Plane("1")};
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(p);

		boolean actual = q.isEmpty();

		Assertions.assertFalse(actual);
	}

	//print
	@Test
	@DisplayName("Empty queue prints just square brackets")
	public void printTest1(){
		System.setOut(new PrintStream(outputCaptor));
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(new Plane[0]);

		q.print();
		String actual = outputCaptor.toString().trim();

		Assertions.assertEquals("[]", actual);

		System.setOut(standardOut);
	}

	@Test
	@DisplayName("Correct print from freshly constructed queue")
	public void printTest2(){
		System.setOut(new PrintStream(outputCaptor));
		Plane[] p = {new Plane("1"), new Plane("2")};
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(p);

		q.print();
		String actual = outputCaptor.toString().trim();
		boolean matches = actual.compareTo("[1, 2]") == 0 || actual.compareTo("[2, 1]") == 0;

		Assertions.assertTrue(matches);

		System.setOut(standardOut);
	}

	@Test
	@DisplayName("Correct print after enqueueing")
	public void printTest3(){
		System.setOut(new PrintStream(outputCaptor));
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(new Plane[0]);

		q.enqueue(new Plane("1"));
		q.enqueue(new Plane("2"));
		q.print();
		String actual = outputCaptor.toString().trim();
		boolean matches = actual.compareTo("[1, 2]") == 0 || actual.compareTo("[2, 1]") == 0;

		Assertions.assertTrue(matches);

		System.setOut(standardOut);
	}

	@Test
	@DisplayName("Correct print after dequeueing")
	public void printTest4(){
		System.setOut(new PrintStream(outputCaptor));
		Plane[] p = {new Plane("1"), new Plane("2")};
		AirTrafficPriorityQueue q = new AirTrafficPriorityQueue(p);

		q.dequeue();
		q.print();
		String actual = outputCaptor.toString().trim();
		boolean matches = actual.compareTo("[1]") == 0 || actual.compareTo("[2]") == 0;

		Assertions.assertTrue(matches);

		System.setOut(standardOut);
	}
}
