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
    An object of type Hand represents a hand of cards.  The maximum number of
    cards in the hand can be specified in the constructor, but by default
    is 5.  A utility function is provided for computing the value of the
    hand in the game of Blackjack.
 */
import java.util.Vector;

@SuppressWarnings("unchecked")
public class Hand {
   private Vector hand;   // The cards in the hand.

   public Hand() {
           // Create a Hand object that is initially empty.
      hand = new Vector<Card>();
   }

   public void clear() {
         // Discard all the cards from the hand.
      hand.removeAllElements();
   }

   public void addCard(Card c) {
         // Add the card c to the hand.  c should be non-null.  (If c is
         // null, nothing is added to the hand.)
      if (c != null)
         hand.addElement(c);
   }

   public void removeCard(Card c) {
         // If the specified card is in the hand, it is removed.
      hand.removeElement(c);
   }

   public void removeCard(int position) {
         // If the specified position is a valid position in the hand,
         // then the card in that position is removed.
      if (position >= 0 && position < hand.size())
         hand.removeElementAt(position);
   }

   public void replaceCard(int position, Card c) {
       if (position >= 0 && position < hand.size() ) {
           hand.removeElementAt(position);
           hand.addElement(c);
       }
   }

   public int getCardCount() {
         // Return the number of cards in the hand.
      return hand.size();
   }

   public Card getCard(int position) {
          // Get the card from the hand in given position, where positions
          // are numbered starting from 0.  If the specified position is
          // not the position number of a card in the hand, then null
          // is returned.
      if (position >= 0 && position < hand.size())
         return (Card)hand.elementAt(position);
      else
         return null;
   }

   public void sortBySuit() {
         // Sorts the cards in the hand so that cards of the same suit are
         // grouped together, and within a suit the cards are sorted by value.
         // Note that aces are considered to have the lowest value, 1.
      Vector<Card> newHand = new Vector<Card>();
      while (hand.size() > 0) {
         int pos = 0;  // Position of minimal card.
         Card c = (Card)hand.elementAt(0);  // Minumal card.
         for (int i = 1; i < hand.size(); i++) {
            Card c1 = (Card)hand.elementAt(i);
            if ( c1.getSuit() < c.getSuit() ||
                    (c1.getSuit() == c.getSuit() && c1.getValue() < c.getValue()) ) {
                pos = i;
                c = c1;
            }
         }
         hand.removeElementAt(pos);
         newHand.addElement(c);
      }
      hand = newHand;
   }

   public void sortByValue() {
         // Sorts the cards in the hand so that cards of the same value are
         // grouped together.  Cards with the same value are sorted by suit.
         // Note that aces are considered to have the lowest value, 1.
      Vector<Card> newHand = new Vector<Card>();
      while (hand.size() > 0) {
         int pos = 0;  // Position of minimal card.
         Card c = (Card)hand.elementAt(0);  // Minumal card.
         for (int i = 1; i < hand.size(); i++) {
            Card c1 = (Card)hand.elementAt(i);
            if ( c1.getValue() < c.getValue() ||
                    (c1.getValue() == c.getValue() && c1.getSuit() < c.getSuit()) ) {
                pos = i;
                c = c1;
            }
         }
         hand.removeElementAt(pos);
         newHand.addElement(c);
      }
      hand = newHand;
   }

   public String toString() {
       String handName = new String();
       for (int i=0; i < hand.size(); i++) {
           handName += getCard(i).toString();
           if ( i < hand.size()-1 ) {
               handName += " ";
           }
       }
       return handName;
   }
   public void print() {
       for (int i=0; i < hand.size(); i++) {
           Card card = (Card)hand.elementAt(i);
           System.out.println("    "+card.toString());
       }
   }

}
