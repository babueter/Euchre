/*  This file is part of Euchre App.
 *
 *  Copyright 2012 Bryan Bueter
 *
 *  Euchre App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Euchre App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Euchre App.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package Game;

/*
   An object of class card represents one of the 52 cards in a
   standard deck of playing cards.  Each card has a suit and
   a value.
 */
public class Card {
    public final static int CLUBS = 0,      // Codes for the 4 suits.
                            HEARTS = 1,
                            SPADES = 2,
                            DIAMONDS = 3;

    public final static int BLACK = 0,      // Color codes for the cards
                            RED = 1;

    public final static int ACE = 1,        // Codes for the non-numeric cards.
                            JACK = 11,      //   Cards 2 through 10 have their
                            QUEEN = 12,     //   numerical values for their codes.
                            KING = 13;

    private final int suit;   // The suit of this card, one of the constants
                              //    SPADES, HEARTS, DIAMONDS, CLUBS.

    private final int value;  // The value of this card, from 1 to 11.
    private final int color;  // The color of the card

    public Card(int theValue, int theSuit) {
            // Construct a card with the specified value and suit.
            // Value must be between 1 and 13.  Suit must be between
            // 0 and 3.  If the parameters are outside these ranges,
            // the constructed card object will be invalid.
        value = theValue;
        suit = theSuit;

        if ( suit == CLUBS || suit == SPADES ) {
            color = BLACK;
        } else {
            color = RED;
        }
    }

    public int getSuit() {
            // Return the int that codes for this card's suit.
        return suit;
    }

    public int getColor() {
        return color;
    }

    public int getValue() {
            // Return the int that codes for this card's value.
        return value;
    }

    public String getSuitAsString() {
        return Card.getSuitAsString(this.suit);
    }

    public String getValueAsString() {
        return Card.getValueAsString(this.value);
    }

    public String toString() {
           // Return a String representation of this card, such as
           // "10 of Hearts" or "Queen of Spades".
        return getValueAsString() + " of " + getSuitAsString();
    }

    public static String getSuitAsString(int suit) {
            // Return a String representing the card's suit.
            // (If the card's suit is invalid, "??" is returned.)
        switch ( suit ) {
           case SPADES:   return "Spades";
           case HEARTS:   return "Hearts";
           case DIAMONDS: return "Diamonds";
           case CLUBS:    return "Clubs";
           default:       return "??";
        }
    }
    public static String getValueAsString(int value) {
            // Return a String representing the card's value.
            // If the value is invalid, "??" is returned.
        switch ( value ) {
           case 1:   return "Ace";
           case 2:   return "2";
           case 3:   return "3";
           case 4:   return "4";
           case 5:   return "5";
           case 6:   return "6";
           case 7:   return "7";
           case 8:   return "8";
           case 9:   return "9";
           case 10:  return "10";
           case 11:  return "Jack";
           case 12:  return "Queen";
           case 13:  return "King";
           default:  return "??";
        }
    }
}
