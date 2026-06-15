/**
  Implements the playing cards to be used
  number stores the value of the card
  suit stores the suit of the card
  */
public class Card {
    private int number;
    private String suit;

    /**
      Creates a new instance of the card class
      @param int n, the number value of the card
      @param String s, the suit of the card
      */
    public Card(int n, String s){
	number = n;
	suit = s;
    }

    /**
      Getter for the number field
      */
    public int getNumber(){
	return number;
    }

    /**
      Getter for the suit field 
      */
    public String getSuit(){
	return suit;
    }
    
    /**
      Prints out the value and suit of the card to the console
      */
    public void print(){
	System.out.print(number);
	System.out.print(" of " + suit + ", ");
    }

    /**
      Overload of the equals method
      @return true iff the value and suit match
      */
    public boolean equals(Object o){
	if(o instanceof Card){
	    Card c = (Card)o;
	    if(this.number == c.getNumber()){
		if(this.suit == c.getSuit()){
		    return true;
		}else{
		    return false;
		}
	    }else{
		return false;
	    }
	}
	return false;
    }

    /**
      Checks if a card can be legally played on another
      @return true iff the suit matches or the value is one above or below
      */
    public boolean isValidPlay(Card c){
	int val1 = this.getNumber();
	int val2 = c.getNumber();

	if(val1 == val2 + 1){
	    return true;
	}
	if(val1 == val2 - 1){
	    return true;
	}
	if(this.getSuit() == c.getSuit()){
        	return true;
	}

	//Next 2 if statements allow for looping (in a standard deck this would mean a 1 can be played on a 13, and vice-versa)
	if(val1%CardPlay.MAXCARDVALUE == (val2+1)%CardPlay.MAXCARDVALUE){
	    return true;
	}
	if(val1%CardPlay.MAXCARDVALUE == (val2-1)%CardPlay.MAXCARDVALUE){
	    return true;
	}

	return false;
    }
}
