import java.util.Random;

/**
  Class that contains the programs main() method and other methods to handle playing the game.
  */
public class CardPlay {
    public static final int MAXCARDVALUE = 13;
    private static final String[] SUITS = {"Hearts", "Diamonds", "Clubs", "Spades"};

    private static CardLinkedList deck = new CardLinkedList();
    private static CardLinkedList playedCards = new CardLinkedList();
    private static CardLinkedList[] players = new CardLinkedList[4];

    private static boolean[] winners = new boolean[4]; //default all false

    private static Random RNG = new Random();

    public static void main(String[] args){
	initPlayers();
	
	//Generate the deck of cards
	for(int val = 1; val <= MAXCARDVALUE; val++){
	    for(String suit : SUITS){
		deck.add(new Card(val, suit));		
	    }
	}

	//Give each player their starting cards
	for(int i = 0; i < 5; i++){
	    for(CardLinkedList p : players){
		p.add(draw());
	    }
	}
	
	//Place the first card
	playedCards.add(draw());

	int playerIndex = 0;
	CardLinkedList currentPlayer = players[playerIndex];
	while(true){
	    boolean playedACard = currentPlayer.play(playedCards);
	    
	    //Draw a card if no card was played
	    if(!playedACard){
		currentPlayer.add(draw());
	    }

	    if(checkGameEnd(playerIndex)){
		break;
	    }

	    //change players
	    playerIndex++;
	    playerIndex %= 4;
	    currentPlayer = players[playerIndex];
	}

	System.out.println("Final Pile:\n");
	playedCards.print();
	System.out.println("\n\nFinal Hands:\n");
	for(int i = 0; i < 4; i++){
	    if(winners[i]){
		System.out.print("WINNER! ");
	    }
    	System.out.print("Player " + Integer.toString(i+1) + "    ");
	players[i].print();
	System.out.print("\n\n");
	}
    }
    
    /**
      Checks if the game has finished
      The game has finished if either the current player has run out of cards or the deck has run out of cards.
      @param int currentPlayerIndex the index of the player whose turn it is
      @return boolean is the game over?
      */
    private static boolean checkGameEnd(int currentPlayerIndex){
	if(players[currentPlayerIndex].getLength() == 0){
	    winners[currentPlayerIndex] = true;
	    return true;
	}
	if(deck.getLength() == 0){
	    //Find the smallest size hand (There may be multiple)
	    int min = MAXCARDVALUE * SUITS.length;
	    for(CardLinkedList p : players){
		if(p.getLength() < min){
		    min = p.getLength();
		}
	    }

	    //Set the players with the smallest hands as winners
	    for(int i = 0; i < 4; i++){
		if(players[i].getLength() == min){
		    winners[i] = true;
		}
	    }

	    return true;
	}
	return false;
    }

    /**
      Creates linked lists representing the players' hands and adds them to the `players` array
      */
    private static void initPlayers(){
	for(int i = 0; i < 4; i++){
	    players[i] = new CardLinkedList();
	}
    }

    /**
      returns a random card from the deck and also removes it from the deck.
      */
    private static Card draw(){
	//Selects a random card from the deck
	int index = RNG.nextInt(deck.getLength());
	Card drawn = deck.getCardAt(index);
	deck.remove(drawn);
	return drawn;
    }
}
