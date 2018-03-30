package Simulate;

import Game.*;

// Card used for sumulated RealPlay results.  
// This turns a real card description into a relative-to-trump description.
//
// For example, if clubs where trump, Ace-Clubs would be Ace-TRUMP.
// Suite values are TRUMP, COLOR, OFFCOLOR1, OFFCOLOR2
//
public class SimCard extends Card {
	private int trump;
	private int originalValue;

	public SimCard(Card card, int initTrump) {
		super(card.getValue(), card.getSuit());

		trump = initTrump;
		originalValue = card.getValue();
	}
	public int getColor(int cardSuit) {
		if ( cardSuit == CLUBS || cardSuit == SPADES ) {
			return BLACK;
		}
		return RED;
	}

	@Override
	public int hashCode() {
		return (toString().hashCode() * getValue());
	}

	// getValue() spreads out each card value so that when hashCode() is called for SimHand() objects, each
	// value is unique
	@Override
	public int getValue() {
		int value = originalValue*100;

		if ( value == 100 ) { value = 140; }

		if ( value == Card.JACK && getColor() == getColor(trump) ) {
			value = 150;
			if ( getSuit() == trump ) {
				value = 160;
			}
		}

		if ( getSuit() == trump ) {
			value += 750;
		} else if (getColor() == getColor(trump)) {
			value += 500;
		} else if ( getSuit() < 2 ) {
			value += 250;
		}

		return value;
	}

	@Override
	public String getSuitAsString() {
		if ( getSuit() == trump ) {
			return "TRUMP";
		}

		if ( getColor() == getColor(trump) ) {
			return "COLOR";
		}

		if ( getSuit() < 2 ) {
			return "OFFCOLOR1";
		} else {
			return "OFFCOLOR2";
		}
	}
	@Override
	public String toString() {
		return getValueAsString() + "-" + getSuitAsString();
	}
}

