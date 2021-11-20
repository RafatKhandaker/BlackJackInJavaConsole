
/*
 * 
 * Mini-Project #2 : Black Jack Card Game
 * 
 * Program Specification:This project involves writing a program to simulate a blackjack card game.
 * You will use a simple console-based user interface to implement this game.A simple blackjack card  
 * game consists of a player and a dealer. A player is provided with a sum of money with which to play. 
 * A player can place a bet between $0 and the amount of money the player has. A player is dealt cards, 
 * called a hand. Each card in the hand has a point value. The objective of the game is to get as close 
 * to 21 points as possible without exceeding 21 points. A player that goes over is out of the game. 
 * The dealer deals cards to itself and a player. The dealer must play by slightly different rules 
 * than a player, and the dealer does not place bets. A game proceeds as follows: A player is dealt 
 * two cards face up. If the point total is exactly 21 the player wins immediately. If the total is not 21, 
 * the dealer is dealt two cards, one face up and one face down. A player then determines whether to ask 
 * the dealer for another card (called a hit) or to stay with his/her current hand. A player may ask 
 * for several hits. When a player decides to stay the dealer begins to play. If the dealer has 21 it 
 * immediately wins the game. Otherwise, the dealer must take hits until the total points in its hand is 
 * 17 or over, at which point the dealer must stay. If the dealer goes over 21 while taking hits the 
 * game is over and the player wins. If the dealers points total exactly 21, the dealer wins immediately. 
 * If the dealers points exactly match the players points, the game is a tie and the player takes back 
 * his bet. When the dealer and player have finished playing their hands, the one with the highest point 
 * total is the winner. Play is repeated until the player decides to quit or runs out of money to bet.
 * 
 * You must use an object-oriented solution for implementing this game.Submit the following items for this project in a single zip file:
 * 
 * 		1.A Java source code file containing the code for all classes used in your solution
 * 		2.A screen capture demonstrating the program executing
 * 		3.A UML diagram illustrating the relationship between all classes used in your solution
 * 
 * 	By: Rafat Khandaker
 * 	Date: 08/07/2021
 *                                                                                                                                                                                                    
 * */

// Add Imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class BlackJackGame {
	

	private static String[] DistinctCards = new String[]{ "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
	private static String[] DistinctGroups = new String[] {"Hearts", "Clubs", "Diamonds","Spades"};
	
	public static void main(String[] args) {
		List<Card> deckOfCards = addCards2Deck(DistinctCards, DistinctGroups);			
		Collections.shuffle(deckOfCards);
		
		Stack<Card> stackedDeckOfCards = stackCards(deckOfCards);
		
		Scanner scanner = new Scanner(System.in);
		
		// Start Game
		System.out.println("Welcome To BlackJack !");
		System.out.println("You will be playing 1 x 1 BlackJack against the Dealer...");
		System.out.println("Please Enter Player Name: ");
		String playerName = scanner.nextLine();
		int playerPurse = 0;
		
		do {
			try {
				System.out.println("Please Enter Player Purse (Total Money) ex: (100, 1000, 10000) :");
				playerPurse = scanner.nextInt();

			}catch(Exception e) {
				System.out.println("Error in entering purse: "+e.toString());
				continue;
			}
			
		
		}while( !(playerPurse > 0) );
		
		Player player = new Player( playerName , playerPurse);
		Dealer dealer = new Dealer();
		
		boolean continueGame = true;
		int turn = 1;
		
		while(continueGame) {

			String turnWinner;
			try {
				int bet = 0;
				
				do {
					System.out.println("Place Bets: (Do Not Exceed Player Purse) ex: (5, 10, 100) ");
					bet = scanner.nextInt();
					
				}while( !((bet > 0) && (bet <= player.getTotalMoney())) );
				
				player.setCurrentBet( bet );
				
				if(turn == 1) {
					player.addCard( stackedDeckOfCards.pop(), true );
					player.addCard( stackedDeckOfCards.pop(), true );
					
					player.setPoints( calculatePoints(player.getCardsInHand()) );
					
					player.printData();

					turnWinner = checkWinner(player.getPoints(), 0 , true);

					switch(turnWinner) 
					{
						case "Player":
							player.exchangeMoney( +bet );
							
							System.out.println("Continue Playing? (Y) or (N):");
							if(scanner.nextLine().toLowerCase() == "y") 
							{ 
								continueGame = true; 
								player.resetPoints();
								player.dropHand();

								dealer.resetPoints();
								dealer.dropHand();
							}
							else 
							{ 
								continueGame = false; 
								player.resetPoints();
								player.dropHand();

								dealer.resetPoints();
								dealer.dropHand();
								continue; 
							}
							
						    break;
							
							
						case "Dealer":
							player.exchangeMoney( -bet );
							player.resetPoints();
							player.dropHand();

							dealer.resetPoints();
							dealer.dropHand();
							break;
						default:
							turn++;
							continue;
					}
					
					dealer.addCard(stackedDeckOfCards.pop(), true);
					dealer.addCard( stackedDeckOfCards.pop(), false );
					
					dealer.setPoints(calculatePoints(dealer.getCardsInHand()) );
					
					dealer.printData();
					turn++;			
					continue;
				}
				
				else 
				{
					player.printData();
					dealer.printData();
					String turnInput;

					do{
						System.out.println("Hit or Stay? : type('hit' or 'stay')");
					    turnInput = scanner.nextLine();

						if(turnInput.toLowerCase() == "hit")
						{
							player.addCard(stackedDeckOfCards.pop(), true);
							player.setPoints( calculatePoints(player.getCardsInHand()) );

							player.printData();
							continue;
						}	


					}while( (turnInput.toLowerCase() == "stay") );

					if( !(player.getPoints() > 21) ){ 
						System.out.println("Dealer will continue to hit until 17 or higher..");
						dealer.flipAllCardsFaceUp();
						dealer.setPoints( calculatePoints(dealer.getCardsInHand()) );
						dealer.printData();

						do{
							dealer.addCard(stackedDeckOfCards.pop(), true);

						}while( !(dealer.getPoints() > 16) );

					}
					
					System.out.println("Final Hand...");
					player.printData();
					dealer.printData();
					
					turnWinner = checkWinner(player.getPoints(), dealer.getPoints() , false);

					switch(turnWinner) 
					{
						case "Player":
							player.exchangeMoney( +bet );
							
							System.out.println("Continue Playing? (Y) or (N):");
							if(scanner.nextLine().toLowerCase() == "y") 
							{ 
								continueGame = true; 
								player.resetPoints();
								player.dropHand();

								dealer.resetPoints();
								dealer.dropHand();
							}
							else 
							{ 
								continueGame = false; 
								player.resetPoints();
								player.dropHand();

								dealer.resetPoints();
								dealer.dropHand();
								continue; 
							}
							
						    break;
							
							
						case "Dealer":
							player.exchangeMoney( -bet );
							player.resetPoints();
							player.dropHand();

							dealer.resetPoints();
							dealer.dropHand();
							break;
						default:
							turn++;
							continue;
					}
			
				}
			
			if( player.getTotalMoney() <= 0 ){
				System.out.println("Player is out of money! Game is over!");
                continueGame = false;
				break;
				
			}
			else{
				System.out.println("Continue Playing? (Y) or (N):");

				if(scanner.nextLine().toLowerCase() == "y") 
				{ 
					continueGame = true; 
					player.resetPoints();
					player.dropHand();

					dealer.resetPoints();
					dealer.dropHand();
				} 
				else 
				{ 
					continueGame = false; 
					player.resetPoints();
					player.dropHand();

					dealer.resetPoints();
					dealer.dropHand();
					continue; 
				}

			}

	
			}catch(Exception e) {
				System.out.println("Error Caught: "+e);
				System.out.println("please re-enter turn values");

			}
				
		}

		System.out.println(" Thank You For Playing Black Jack!");


        
	}
	
	
	private static List<Card> addCards2Deck(String[] cards, String[] groups){
		List<Card> deck = new ArrayList<Card>();
		
		for(String distinctCard : cards){
			for(String distinctGroup : groups)
				deck.add(new Card(distinctCard, distinctGroup));
		}
		
		return deck;
	}
	
	private static Stack<Card> stackCards(List<Card> deck){
		Stack<Card> stack = new Stack<Card>();
		
		for(Card card : deck)
			stack.push(card);
		
		return stack;
	}
	
	private static String checkWinner(int pointsPlayer, int pointsDealer, boolean hit ) {
		if( pointsPlayer == 21) { System.out.println("Player Wins!"); return "Player"; }
		if( pointsDealer == 21) { System.out.println("Dealer Wins!");  return "Dealer"; }
		
		if(pointsPlayer > 21 ) { System.out.println("Player Lose!"); return "Dealer";  }
		if( pointsDealer > 21 ) { System.out.println("Dealer Lose!"); return "Player";  }

		if( pointsPlayer > pointsDealer && !hit ) { System.out.println("Player Wins!"); return "Player"; }
		if( pointsPlayer > pointsDealer && !hit) { System.out.println("Dealer Wins!"); return "Dealer"; }
		
		return "No Winner";
	}
	
	private static int calculatePoints(List<Card> hand){
		int points = 0;
		
		for(Card card : hand) 
		{
			if(card.isCurrentlyFaceUp())
			{
				switch(card.flipCard()) 
				{
			 		case "A":
			 			if(points >= 11) { points += 1;}
			 			if(points < 11) { points += 11;}
			 			continue;
			 		case "2":
			 			points += 2;
			 			continue;
			 		case "3":
			 			points += 3;
			 			continue;
			 		case "4":
			 			points += 4;
			 			continue;
			 		case "5":
			 			points += 5;
			 			continue;
			 		case "6":
			 			points += 6;
			 			continue;
			 		case "7":
			 			points += 7;
			 			continue;
			 		case "8":
			 			points += 8;
			 			continue;
			 		case "9":
			 			points += 9;
			 			continue;
			 		case "10":
			 			points += 10;
			 			continue;
			 		case "J":
			 			points += 10;
			 			continue;
			 		case "Q":
			 			points += 10;
			 			continue;
			 		case "K":
			 			points += 10;
			 			continue;
			
				}
			}
			
		}
		
		return points;
	}



}


class Card{
	
	private String Flip;
	private String Group;
	private boolean IsFaceUp;

	
	public Card(String flip, String group) {
		this.Flip = flip;		
		this.Group = group;
	}
	
	public void setFaceUp(boolean faceUp) { this.IsFaceUp = faceUp; }
    public boolean isCurrentlyFaceUp() { return this.IsFaceUp; }
    
	public String flipCard() { return this.Flip; }
				
	public String getGroup() { return this.Group; }
}

class Player{
	private String Name;
	private int TotalPoints;
	private List<Card> CardsInHand;
	private int TotalMoney;
	private int CurrentBet;
	
	public Player(String name, int totalMoney) {
		this.Name = name;
		this.TotalPoints = 0;
		CardsInHand = new ArrayList<Card>();
		this.TotalMoney = totalMoney;
	}
	
	public void setCurrentBet( int bet ) { this.CurrentBet = bet; }
	public void resetPoints() { this.TotalPoints = 0; }
	public void setPoints( int points ) { this.TotalPoints = points; }
	
	public void addCard( Card newCard, boolean faceUp ) { 
		newCard.setFaceUp(faceUp);
		this.CardsInHand.add(newCard); 
	}
	
	public void dropHand() { this.CardsInHand = new ArrayList<Card>(); }
	public void exchangeMoney(int moneyChange ) { this.TotalMoney += moneyChange; }
	
	public List<Card> getCardsInHand(){ return this.CardsInHand; }
	public int getCurrentBet() { return this.CurrentBet; }
	public int getPoints() { return this.TotalPoints; }
	public int getTotalMoney(){ return this.TotalMoney; }
	
	public void printData() {
		System.out.println("----------------------------------------------------------------------");
		System.out.println("Player: " +this.Name);
		System.out.println("Total Money: " +this.TotalMoney);
		System.out.println("Current Bet: " + this.CurrentBet);
		
		for(Card card : this.CardsInHand) {
			
			if(card.isCurrentlyFaceUp()) {
				System.out.print("    "+card.flipCard());
				
				switch(card.getGroup()) {
					case "Hearts":
						System.out.print("\u2665");
						break;
					case "Diamonds":
						System.out.print("\u2666");
						break;
					case "Spades":
						System.out.print("\u2660");
						break;
					case "Clubs":
						System.out.print("\u2663");
						break;
				}
				
				System.out.println();
			}else {
				System.out.println("     **Face-Down Card** ");

			}

		}
		System.out.println("Total Points: " + this.TotalPoints);
		System.out.println("----------------------------------------------------------------------");
	}
	
}

class Dealer{
	private int TotalPoints;
	private List<Card> CardsInHand;
	
	public Dealer() {
		this.TotalPoints = 0;
		this.CardsInHand = new ArrayList<Card>();
	}
	
	public void setPoints( int points ) { this.TotalPoints = points; }
	
	public void addCard( Card newCard, boolean faceUp ) { 
		newCard.setFaceUp(faceUp);
		this.CardsInHand.add(newCard); 
	}

	public void flipAllCardsFaceUp(){ 
		for(int i = 0; i < this.CardsInHand.size(); i++){
			this.CardsInHand.get(i).setFaceUp(true);
		}
	}

	public void dropHand() { this.CardsInHand = new ArrayList<Card>(); }
	public void resetPoints() { this.TotalPoints = 0; }
	
	public int getPoints() { return this.TotalPoints; }
	public List<Card> getCardsInHand(){ return this.CardsInHand; }

	public void printData() {
		System.out.println("----------------------------------------------------------------------");
		System.out.println("Dealer! " );
		
		for(Card card : this.CardsInHand) {
			
			if(card.isCurrentlyFaceUp()) {
				System.out.print("    "+card.flipCard());
				
				switch(card.getGroup()) {
					case "Hearts":
						System.out.print("\u2665");
						break;
					case "Diamonds":
						System.out.print("\u2666");
						break;
					case "Spades":
						System.out.print("\u2660");
						break;
					case "Clubs":
						System.out.print("\u2663b");
						break;
				}
				
				System.out.println();
			}else {
				System.out.println("     **Face-Down Card** ");

			}

		}
		System.out.println("Total Points: " + this.TotalPoints);
		System.out.println("----------------------------------------------------------------------");
	}
	
}
