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
    An object of class table represents a card game table with
    all the hands of each player, who the dealer is, and whatever card
    has been played.
 */
import java.util.Vector;

@SuppressWarnings("unchecked")
public class Table {
    private Vector players; // Each player at the table
    private int dealer;     // Player that is the dealer

    protected Deck deck;    // Deck for this game

    public Table() {
            // Create a Table object that is initially empty
        players = new Vector<Hand>();
        deck = new Deck();
        dealer = 0;
    }

    public void addPlayer(Hand h) {
            // Add a player to the table
        if (h != null) {
            players.addElement(h);
        } else {
            players.addElement(new Hand());
        }
    }

    public void removePlayer(Hand h) {
            // Remove a player from the table
        players.removeElement(h);
    }

    public int getPlayerCount() {
            // Number of players at the table
        return players.size();
    }

    public Hand getPlayer(int position) {
            // Return the player requested, or null if position out of range
        if (position >= 0 && position < players.size()) {
            return (Hand)players.elementAt(position);
        } else {
            return null;
        }
    }

    public void passTheDeal() {
        dealer = (dealer+1)%players.size();
    }

    public void setDealer(int player) {
        if ( player >= 0 && player < players.size() ) {
            dealer = player;
        }
    }
    public int getDealer() {
        return dealer;
    }

    public Deck getDeck() {
        return deck;
    }
}
