/**
  Implements the linked list for storing cards
  */
public class CardLinkedList {
   private Node head = null; 

    /**
      Checks if list is empty
      @return true iff the head is null
      */
    public boolean isEmpty(){
      if(head == null){
	  return true;
      }
      return false;
    }
    
    /**
      @return the length of the list
      */
    public int getLength(){
	int count = 0;
	Node current = head;
	while(current != null){
	    count++;
	    current = current.next;
	}
	return count;
    }
    
    /**
      Checks if the list contains the given card
      @param Card c, the card to search for.
      @return true iff the passed card is in the list
      */
    public boolean hasCard(Card c){
	Node current = head;
	while(current != null){
	    if(current.card == c){
		return true;
	    }
	    current = current.next;
	}
	return false;
    }	

    /**
      Gets the card at the given position, starting at 0
      @param int i, the index of the card
      @return the `Card` object at the passed index
      @exception IndexOutOfBoundsExcpetion if index is out of bounds
      */
    public Card getCardAt(int i){
	if(i >= this.getLength()){
	    throw new IndexOutOfBoundsException("Error: Tried to access index " + Integer.toString(i) + " when list length was " + Integer.toString(this.getLength()) + ".");
	}
	if(i < 0){
	    throw new IndexOutOfBoundsException("Error, index passed in is below 0");
	}
	
	int count = 0;
	Node current = head;
	
	while(count < i){
	    count++;
	    current = current.next;
	}
	return current.card;
    }

    /**
      Adds a card to the linked list
      @param Card c, the card to add
      */
    public void add(Card c){
	Node newHead = new Node(c);
	newHead.next = head;
	head = newHead;
    }
    
    /**
      Removes the first instance of the card from the linked list if it is present
      @param Card c, the card to remove
      */
    public void remove(Card c){
        Node previous = null;
	Node current = head;

	while(current != null){
	    if(current.card == c){
		if(previous == null){
		    head = current.next;
		}else{
		    previous.next = current.next;
		}
		return;
	    }
	    previous = current;
	    current = current.next;
	}
    }

    /**
      Prints the `Card`s in the list to the console, with each card seperated by a comma.
      */
    public void print(){
	if(this.isEmpty()){
	    System.out.print("No cards.");
	}else{
	    Node current = head;

	    while(current != null){
		current.card.print();
		current = current.next;
	    }
	}
    }

    /**
      Attempts to play a card from the deck
      @param CardLinkedList pile, the list to which to add the card when it is played
      @return true iff a card is successfully played
      */
    public boolean play(CardLinkedList pile){
	for(int i = 0; i < this.getLength(); i++){
	    Card currentCard = this.getCardAt(i);

	    //Check the current card in the list against the card on top of the pile.
	    if(currentCard.isValidPlay(pile.getCardAt(0))){
		pile.add(currentCard);
		this.remove(currentCard);
		return true;
	    }
	}
	return false;
    }

    private class Node{
		public Card card;
		public Node next;

		public Node(Card c){
		    card = c;
		}
    }
}
