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
    An object of type Deck represents an ordinary deck of 52 playing cards.
    The deck can be shuffled, and cards can be dealt from the deck.
 */
public class Deck {
    private Card[] deck;     // An array of 52 Cards, representing the deck.
    private int cardsUsed;   // How many cards have been dealt from the deck.

    protected int cardsInDeck; // The actual number of cards in the deck.

    public Deck() {
       // Create an unshuffled deck of cards.
       cardsInDeck = 52;
       deck = new Card[cardsInDeck];
       int cardCt = 0; // How many cards have been created so far.
       for ( int suit = 0; suit <= 3; suit++ ) {
          for ( int value = 1; value <= 13; value++ ) {
             deck[cardCt] = new Card(value,suit);
             cardCt++;
          }
       }
       cardsUsed = 0;
    }

    public void shuffle() {
          // Put all the used cards back into the deck, and shuffle it into
          // a random order.
        for ( int i = cardsInDeck-1; i > 0; i-- ) {
            int rand = (int)(Math.random()*(i+1));
            Card temp = deck[i];
            deck[i] = deck[rand];
            deck[rand] = temp;
        }
        cardsUsed = 0;
    }

    public int cardsLeft() {
          // As cards are dealt from the deck, the number of cards left
          // decreases.  This function returns the number of cards that
          // are still left in the deck.
        return cardsInDeck - cardsUsed;
    }

    public Card dealCard() {
          // Deals one card from the deck and returns it.
        if (cardsUsed == cardsInDeck)
           shuffle();
        cardsUsed++;
        return deck[cardsUsed - 1];
    }

    protected Card getCard(int position) {
            // For members of our class we allow them to access specific cards
        return deck[position];
    }

    protected void deleteCard(int cardNumber) {
          // Remove a card from the deck for games like euchre.
        for (int i=cardNumber; i<cardsInDeck-1; i++) {
            deck[i] = deck[i+1];
        }
        cardsInDeck--;
    }

    public void print() {
        System.out.println("We have "+cardsInDeck+" cards in this deck");
        for (int i=0; i<cardsInDeck; i++) {
            System.out.println("    "+deck[i].toString());
        }
    }
}
