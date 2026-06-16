/**
 * Implements a priority queue for planes to be serviced by an airport
 */
public class AirTrafficPriorityQueue {
    private Plane[] priorityQueue;
    private int next = 1;

    /**
     * Creates a priority queue from an array of initial planes
     * @param planes, the initial array of planes to store in the queue
     */
    public AirTrafficPriorityQueue(Plane[] planes) {
		for(int i = 0; i < planes.length; i++){
			if(planes[i] == null){
				throw new NullPointerException("Initialiser array contains null values");
			}
		}

		//Find necessary size of data array
        int sz = 1;
        next = planes.length + 1;
        while (sz < next) {
            sz *= 2;
        }

        priorityQueue = new Plane[sz];
        java.lang.System.arraycopy(planes, 0, priorityQueue, 1, planes.length);
        heapify();
    }

    /**
     * Inserts a plane into the priority queue
     * @param p, the plane to enqueue
     */
    public void enqueue(Plane p) {
		if(p == null){
			return;
		}

        if (next >= priorityQueue.length) {
            resize();
        }
        priorityQueue[next] = p;
        upheap(next);
        next++;
    }

    /**
     * Removes the first plane in the priority queue
     * @return the first plane in the priority queue
     */
    public Plane dequeue() {
        if (length() == 0) {
            return null;
        }

        Plane ret = priorityQueue[1];
        swap(1, length());
        next--;
        downheap(1);

        return ret;
    }

    /**
     * Looks at the first plane in the priority queue without removing it
     * @return the first plane in the priority queue
     */
    public Plane peek() {
		if(length() == 0){
			return null;
		}

        return priorityQueue[1];
    }

    /**
     * @return the number of planes stored
     */
    public int length() {
        return next - 1;
    }

    /**
     * @return whether the queue is empty
     */
    public boolean isEmpty() {
        return length() == 0;
    }

    /**
     * Prints the flight IDs of all flights stored
     */
    public void print() {
        String print = "[";
        int sz = length();
        for (int i = 0; i < sz; i++) {
            print += priorityQueue[i + 1].getId();
            if (i < sz - 1) {
                print += ", ";
            }
        }
        print += "]";

        System.out.println(print);
    }

    private void upheap(int index) {
        if (index <= 1) {
            return;
        }

        int parentIndex = index / 2;

        Plane child = priorityQueue[index];
        Plane parent = priorityQueue[parentIndex];

        if (child.getPriority() > parent.getPriority()) {
            swap(index, parentIndex);
            upheap(parentIndex);
        }
    }

    private void downheap(int index) {
        int leftChildIndex = index * 2;
        int rightChildIndex = leftChildIndex + 1;

		//Check if index is at a leaf node
        if (leftChildIndex > length()) {
            return;
        }

        Plane parent = priorityQueue[index];
        int parentPriority = parent.getPriority();

        Plane leftChild = priorityQueue[leftChildIndex];
        int leftChildPriority = leftChild.getPriority();

        Plane rightChild;
        int rightChildPriority = 0;

		//Check if right child exists
        if (rightChildIndex <= length()) {
            rightChild = priorityQueue[rightChildIndex];
            rightChildPriority = rightChild.getPriority();
        }

        if (leftChildPriority >= rightChildPriority) {
            if (leftChildPriority > parentPriority) {
                swap(index, leftChildIndex);
                downheap(leftChildIndex);
            }
            return;
        } else {
            if (rightChildPriority > parentPriority) {
                swap(index, rightChildIndex);
                downheap(rightChildIndex);
            }
            return;
        }
    }

    private void heapify() {
        int firstNonLeaf = length() / 2;

        for (int i = firstNonLeaf; i > 0; i--) {
            downheap(i);
        }
    }

    private void swap(int i1, int i2) {
        Plane temp = priorityQueue[i1];
        priorityQueue[i1] = priorityQueue[i2];
        priorityQueue[i2] = temp;
    }

    private void resize() {
        Plane[] newQueue = new Plane[priorityQueue.length * 2];
        java.lang.System.arraycopy(priorityQueue, 0, newQueue, 0, priorityQueue.length);
        priorityQueue = newQueue;
    }

    /**
     * Updates the attributes of all planes stored, simulating a one hour time step
     */
    public void updateValues() {
        int sz = length();
        for (int i = 0; i < sz; i++) {
            priorityQueue[i + 1].updateValues();
        }
        heapify();
    }
}
