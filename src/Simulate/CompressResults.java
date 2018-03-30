package Simulate;

import Game.*;
import java.io.*;
import java.util.*;

// Converts descriptive text produced by BestHands and RealPlayHands into a hashmap containing
// the win percentage of each hand.
public class CompressResults {
	CompressResults(String filename) {
		Euchre game = new Euchre();
		Deck deck = game.getDeck();

		HashMap<String, SimCard> simDeck = new HashMap<String, SimCard>();
		while ( deck.cardsLeft() > 0 ) {
			Card card = deck.dealCard();
			SimCard simCard = new SimCard(card, Card.CLUBS);

			simDeck.put(simCard.toString(), simCard);
		}

		// Read in contents of file and store results
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line = null;
			while ( (line=in.readLine()) != null) {
				String data[] = line.split("/");
				if ( data.length != 2 ) {
					System.out.print("Error in line: "+line);
					System.exit(0);
				}

				String handText[] = data[0].split(" ");
				if ( handText.length != 5 ) {
					System.out.print("Error in hand: "+data[0]);
					System.exit(0);
				}
				SimHand hand = new SimHand();
				for (int index=0; index<handText.length; index++) {
					if ( ! simDeck.containsKey( handText[index] ) ) {
						System.out.println("Error: no card in simDeck: "+handText[index]);
						System.exit(0);
					}
					hand.addCard( (Card)simDeck.get( handText[index] ) );
				}

				Scanner sc = new Scanner(data[1]);
				int pctWin = sc.nextInt();

				hand.sortByValue();
				System.out.println(hand.hashCode()+" "+pctWin);
			}
			in.close();
		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
	}

	public static void main(String args[]) {
		if ( args.length == 1 ) {
			new CompressResults(args[0]);
		} else {
			System.err.println("Usage: CompressResults <filename>");
			System.exit(1);
		}
	}
}
