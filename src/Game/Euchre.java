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

import Game.Card.*;
import java.util.Vector;

@SuppressWarnings("unchecked")
public class Euchre {
    public final Table table;   // The table where we are going to play
    private Card kitty;         // The kitty card

    private int currentPlayer;  // The current player whose turn it is
    private Vector trick;       // The current trick being played
    private Vector tricksPlayed;    // All tricks played
    private int trump;          // The current trump
    private int trumpColor;     // The color of the current trump
    private int playerCalledTrump;  // The player who called the trump
    private int playerLeadTrick;    // The player who lead this trick

    private int team0Tricks;    // How many tricks has team 0 won
    private int team1Tricks;    // How many tricks has team 1 won
    private int[] playerTricks; // Number of tricks each player won
    private int team0Score;     // Team 0 consists of players 0 and 2
    private int team1Score;     // Team 1 consists of players 1 and 3

    private boolean debug;      // Turn on debugging output
    private int state;          // The current state of the game (see below)
    public static final int DEALING = 0,
                            ORDERING_TRUMP = 1,
                            CALLING_TRUMP = 2,
                            DEALER_DISCARDING = 3,
                            WAITING_TO_START = 4,
                            PLAYING_HAND = 5,
                            PICKING_UP_TRICK = 6,
                            GAME_OVER = 7;

  // Functions that set up our Euchre game
  //
  //
    public Euchre() {
        // Add a table with 4 players
        table = new Table();
        table.addPlayer(null);
        table.addPlayer(null);
        table.addPlayer(null);
        table.addPlayer(null);

        table.setDealer((int)(Math.random()*4));

        // Remove cards 2-8 from the deck
        for (int i=0; i<table.deck.cardsInDeck; i++) {
            Card c = table.deck.getCard(i);
            if ( c.getValue() > 1 && c.getValue() < 9 ) {
                table.deck.deleteCard(i);
                i--;
            }
        }

        // Initialize all our variables
        debug = false;

        state = DEALING;
        currentPlayer = (table.getDealer()+1)%4;

        trick = new Vector<Card>();
        tricksPlayed = new Vector<Card>();
        
        team0Score = 0;
        team1Score = 0;

        playerTricks = new int[4];
        playerTricks[0] = 0;
        playerTricks[1] = 0;
        playerTricks[2] = 0;
        playerTricks[3] = 0;

        team0Tricks = 0;
        team1Tricks = 0;
    }
    public Euchre(Euchre game) {
      // Clone the game passed
        table = new Table();
        table.addPlayer(null);
        table.addPlayer(null);
        table.addPlayer(null);
        table.addPlayer(null);
	table.setDealer(game.getDealer());

        for (int player=0; player<4; player++) {
            for (int card=0; card<game.getPlayerHand(player).getCardCount(); card++) {
                table.getPlayer(player).addCard(game.getPlayerHand(player).getCard(card));
            }
        }

        kitty = game.kitty;
        debug = game.debug();
        state = game.getState();
        currentPlayer = game.getCurrentPlayer();

        trump = game.trump;
        trumpColor = game.trumpColor;

        trick = (Vector<Card>)game.trick.clone();
        tricksPlayed = (Vector<Card>)game.tricksPlayed.clone();

        playerTricks = new int[4];
        playerTricks[0] = game.playerTricks[0];
        playerTricks[1] = game.playerTricks[1];
        playerTricks[2] = game.playerTricks[2];
        playerTricks[3] = game.playerTricks[3];


        team0Score = game.team0Score;
        team1Score = game.team1Score;
        team0Tricks = game.team0Tricks;
        team1Tricks = game.team1Tricks;
    }
    public void deal() {
      // Deal each player the cards from the deck per standard rules of euchre
        table.getDeck().shuffle();
        table.getDeck().shuffle();

      // Deal 2 or 3 cards to each player once
        int player = table.getDealer();
        do {
            player = (player+1)%4;
            table.getPlayer(player).clear();
            for (int cards=(((int)(Math.random()*2))+1); cards>=0; cards--) {
                table.getPlayer(player).addCard( table.deck.dealCard() );
            }
        } while (player != table.getDealer());

      // Then deal 2 or 3 cards to each player a second time
        player = table.getDealer();
        do {
            player = (player+1)%4;
            for (int cards=table.getPlayer(player).getCardCount(); cards<5; cards++) {
                table.getPlayer(player).addCard( table.deck.dealCard() );
            }
            table.getPlayer(player).sortBySuit();
        } while (player != table.getDealer());

        kitty = table.getDeck().dealCard();

        trick.clear();
        tricksPlayed.clear();

        state = ORDERING_TRUMP;
        currentPlayer = (table.getDealer()+1)%4;

        team0Tricks = 0;
        team1Tricks = 0;

        playerTricks[0] = 0;
        playerTricks[1] = 0;
        playerTricks[2] = 0;
        playerTricks[3] = 0;

        if ( debug ) {
            System.out.println("Cards delt, player hands: ");
            System.out.println("\tPlayer0: "+getPlayerHand(0).toString());
            System.out.println("\tPlayer1: "+getPlayerHand(1).toString());
            System.out.println("\tPlayer2: "+getPlayerHand(2).toString());
            System.out.println("\tPlayer3: "+getPlayerHand(3).toString());
        }
    }

  // These functions pertain to card value in relation to the current trump
  //
  //
    public int cardCmp(Card c1, Card c2) {
      // Return -1 if c1 is less, 0 if they are even, and +1 if c1 is greater
        return (realValue(c1) - realValue(c2));
    }
    public int realValue(Card card) {
      // Calculate the real value of the card, considering trump if necessary
        int value = card.getValue();

        // Aces are high in this game, adjust them appropriately
        if ( card.getValue() == 1 ) { value = 14; }

        // Trump has been set, adjust the value of the card
        if (state == PLAYING_HAND || state == PICKING_UP_TRICK || state == WAITING_TO_START || state == DEALER_DISCARDING ) {
          // The card is trump and a jack, adjust the value
            if (card.getValue() == Card.JACK && card.getColor() == trumpColor) {
                value = 15;
                if (card.getSuit() == trump) {
                    value = 16;
                }
            }

          // Make sure all trump cards beat non-trump cards
            if (cardIsTrump(card)) {
                value += 40;
            } else if ( ! trick.isEmpty() && getLeadSuit() == card.getSuit() ) {
                value += 20;
            }
        }
        return value;
    }
    public int realSuit(Card card) {
      // Return the real suit of the card
        if ( card.getValue() == Card.JACK && card.getColor() == trumpColor ) {
            if ( state >= DEALER_DISCARDING ) {
                return trump;
            } else {
                return card.getSuit();
            }
        }
        return card.getSuit();
    }
    public boolean cardIsTrump(Card card) {
      // Determine if the card is trump
        if ( realSuit(card) == trump ) { return true; }
        return false;
    }
    public boolean playerHasSuit(int player, int suit) {
      // Determine if the player has the suit
        if ( state < DEALER_DISCARDING ) { return false; }

        for (int i=0; i < getPlayerHand(player).getCardCount(); i++) {
            if ( realSuit(getPlayerHand(player).getCard(i)) == suit ) { return true; }
        }
        return false;
    }

  // These functions handle choosing the trump card
  //
  //
    public void playerOrderUp(int player, boolean orderUp) {
        if ( player != currentPlayer || state != ORDERING_TRUMP ) {
            System.out.println("Error: "+player+" vs "+currentPlayer+", "+state);
            return;
        }

        if ( orderUp ) {
            state = DEALER_DISCARDING;
            trump = kitty.getSuit();
            trumpColor = kitty.getColor();
            playerCalledTrump = player;

            currentPlayer = table.getDealer();
        } else {
            if ( currentPlayer == table.getDealer() ) {
                state = CALLING_TRUMP;
            }
            currentPlayer = (currentPlayer+1)%4;
        }

    }
    public boolean aiOrderUp(int player) {
      // Determine if we should order up the trump or not
        if ( state != ORDERING_TRUMP ) { return false; }
        if ( currentPlayer != player ) { return false; }

      // Simulate the game to see if we would get a point or not
        Euchre game = new Euchre(this);

      // Call up the card and make the dealer discard
        game.playerOrderUp(player, true);
        game.dealerDiscard(game.aiDiscard(game.getDealer()));

      // Simulate and determine how many points we would get
        int points = game.aiSimulateGame(player);

      // Call on anything that gets us a point if we have a better hand then our partner
        int partner = (player+2)%4;
        if ( points > 0 && game.playerTricks[player] >= game.playerTricks[partner] ) {
            return true;
        } else {
            return false;
        }
    }
    public void playerCallTrump(int player, int trump) {
      // The current player is calling trump, make the necessary changes
        if ( player != currentPlayer || state != CALLING_TRUMP || trump == kitty.getSuit() ) { return; }

      // Allow the caller to send us a negative trump value, likely passed from aiCallTrump()
        if ( trump < 0 ) {
            playerPassCall(player);
            return;
        }

        currentPlayer = (table.getDealer()+1)%4;
        state = WAITING_TO_START;

      // Actually set the trump
        this.trump = trump;
        if ( trump == Card.CLUBS || trump == Card.SPADES ) {
            trumpColor = Card.BLACK;
        } else {
            trumpColor = Card.RED;
        }
        playerCalledTrump = player;
        state = WAITING_TO_START;
    }
    public void playerPassCall(int player) {
      // The current player is passing the call, make the necessary changes
        if ( player != currentPlayer || state != CALLING_TRUMP ) { return; }

      // Dont let the dealer pass the call
        if ( currentPlayer == table.getDealer() ) { return; }

        currentPlayer = (currentPlayer+1)%4;
    }
    public int aiCallTrump(int player ) {
        if ( state != CALLING_TRUMP ) { return -1; }
        if ( currentPlayer != player ) { return -1; }

        int calledSuit = -1;
        int mostPoints = 1;
        int suit = (getKitty().getSuit()+1)%4;
        while (suit != getKitty().getSuit()) {
          // Simulate the game to see if we would get a point or not
            Euchre game = new Euchre(this);

          // Call up the card and make the dealer discard
            game.playerCallTrump(player, suit);
            game.startPlaying();

          // Simulate and determine how many points we would get
            int points = game.aiSimulateGame(player);

            int partner = (player+2)%4;
            if ( points > 0 && points > mostPoints && game.playerTricks[player] >= game.playerTricks[partner]) {
                calledSuit = suit;
                mostPoints = points;
            }
            suit = (suit+1)%4;
            continue;
        }

        if ( calledSuit < 0 && player == getDealer() ) {
          // We have to call something because we got stuck
            suit = (kitty.getSuit()+1)%4;
            while ( suit != kitty.getSuit() ) {
              // Find out how many tricks we could get by suit.  Call with the most
                Euchre game = new Euchre(this);
                game.playerCallTrump(player, suit);
                game.startPlaying();

                game.aiSimulateGame(player);
                if ( player == 0 || player == 1 ) {
                    if ( game.team0Tricks > mostPoints ) {
                        calledSuit = suit;
                        mostPoints = game.team0Tricks;
                    }
                } else {
                    if ( game.team1Tricks > mostPoints ) {
                        calledSuit = suit;
                        mostPoints = game.team1Tricks;
                    }
                }
                suit = (suit+1)%4;
            }
        }
        return calledSuit;
    }
    public void dealerDiscard(int card) {
        getPlayerHand(this.getDealer()).replaceCard(card, kitty);
        getPlayerHand(this.getDealer()).sortBySuit();

        currentPlayer = (getDealer()+1)%4;
        state = PLAYING_HAND;
    }
    public int aiDiscard(int player) {
        if ( state != DEALER_DISCARDING ) { return -1; }
        if ( getCurrentPlayer() != player ) { return -1; }

        return aiThrowOff(player);
    }

  // These functions handle playing a trick
  //
  //
    public void startPlaying() {
      // We have an intermediate step so the GUI can pause before clearing the screen for play
        if ( state != WAITING_TO_START ) { return; }

        state = PLAYING_HAND;
        currentPlayer = (table.getDealer()+1)%4;
    }
    public void pickUpTrick() {
        if ( state != PICKING_UP_TRICK ) { return; }
        currentPlayer = getTrickWinner();
        playerTricks[currentPlayer]++;

        if ( currentPlayer == 0 || currentPlayer == 2 ) {
            team0Tricks++;
        } else {
            team1Tricks++;
        }

        if ( debug ) {
            System.out.println("\tPlayer "+getLeadPlayer()+" lead, Winner is "+currentPlayer+", trump is: "+Card.getSuitAsString(getTrump()));
            for (int i=0; i < trick.size(); i++) {
                int thisPlayer = (getLeadPlayer()+i)%4;
                System.out.println("\t\tPlayer "+thisPlayer+" played: "+((Card)trick.elementAt(i)).toString());
            }
        }

        trick.clear();

        if ( team0Tricks + team1Tricks == 5 ) {
            state = DEALING;
            table.passTheDeal();

            if ( playerCalledTrump == 0 || playerCalledTrump == 2 ) {
                if ( team0Tricks < 3 ) {
                  // Team 0 got set, give team 1 two points
                    team1Score += 2;
                } else if ( team0Tricks == 5 ) {
                  // Team 0 got all tricks, give them 2 points
                    team0Score += 2;
                } else {
                    team0Score += 1;
                }
            } else {
                if ( team1Tricks < 3 ) {
                  // Team 1 got set, give team 0 two points
                    team0Score += 2;
                } else if ( team1Tricks == 5 ) {
                  // Team 1 got all tricks, give them 2 points
                    team1Score += 2;
                } else {
                    team1Score += 1;
                }
            }

          // End the game if someone scored 10 or more
            if ( team0Score >= 10 || team1Score >= 10 ) {
                state = GAME_OVER;
            }
        } else {
            state = PLAYING_HAND;
        }
    }
    public void playerPlayCard(int player, int card) {
        if ( player != currentPlayer ) { return; }

      // Check to see if the player is reneging
        if ( playerHasSuit(player, getLeadSuit()) && realSuit(getPlayerHand(player).getCard(card)) != getLeadSuit()) {
            if ( debug ) {
                System.out.println("Player "+player+" attempted to renege with "+getPlayerHand(player).getCard(card).toString());
                Thread.currentThread().dumpStack();
            }
            return;
        }

        trick.addElement(getPlayerHand(player).getCard(card));
        getPlayerHand(player).removeCard(card);
        if ( getTrickCount() == 4 ) {
            state = PICKING_UP_TRICK;
            tricksPlayed.add(trick.clone());
        } else if ( getTrickCount() == 1 ) {
            playerLeadTrick = player;
        }
        currentPlayer = (currentPlayer+1)%4;
    }
    public void aiPlayCard(int player) {
      // Play the hand to the best of our ability
        if ( getState() != PLAYING_HAND ) {
            return;
        }
        if ( getCurrentPlayer() != player ) {
            return;
        }
        if ( getTrickCount() == 0 ) {
          // We are leading off, chose the best play using aiChoseLead() call
            playerPlayCard(player, aiChoseLead(player));
            return;

        } else {
          // We are not the first player to lay down this trick
            int leadSuit = getLeadSuit();
            int partner = (player+2)%4;
            int opp1 = (player+1)%4;    // Opponent to the left

            int currentWinner = getTrickWinner();
            Card currentWinnerCard = getTrickWinnerCard();

            if ( getTrickCount() == 3 ) {
              // Last one to play, do so accordingly
                if ( currentWinner == partner ) {
                    playerPlayCard(player, aiThrowOff(player));
                    return;
                }
                playerPlayCard(player, aiTakeTrick(player));
                return;
            }

          // Opponent 1 has yet to play, determine if he can beat our best card or not
            int opp1Winner = aiTakeTrick(opp1);
            int myWinner = aiTakeTrick(player);

            if ( currentWinner == partner ) {
              // Partner has it, make sure we take the trick if it prevents opponent 1 from winning

                if ( cardCmp(getPlayerHand(opp1).getCard(opp1Winner), currentWinnerCard) > 0 ) {
                    if ( cardCmp(getPlayerHand(player).getCard(myWinner), getPlayerHand(opp1).getCard(opp1Winner)) > 0 ) {
                        playerPlayCard(player, myWinner);
                        return;

                    } else {
                      // We cant beat opponent 1, throw off
                        playerPlayCard(player, aiThrowOff(player));
                        return;
                    }
                }
            }

          // Play the winner
            playerPlayCard(player, myWinner);
            return;
        }
    }
    public int getLeadSuit() {
        if ( getTrickCount() > 0 ) {
            return(realSuit((Card)trick.elementAt(0)));
        }
        return -1;
    }
    public int getLeadPlayer() {
       if ( getTrickCount() > 0 ) {
           return(playerLeadTrick);
       }
       return -1;
    }
    public int getTrickWinner() {
        if ( getTrickCount() == 0 ) { return -1; }

        int winner = getLeadPlayer();
        Card highestCard = (Card)trick.elementAt(0);
        for (int i=1; i<getTrickCount(); i++) {
            Card nextCard = (Card)trick.elementAt(i);
            if ( cardCmp(highestCard, nextCard) < 0 ) {
                winner = (getLeadPlayer()+i)%4;
                highestCard = nextCard;
            }
        }
        return winner;
    }
    public Card getTrickWinnerCard() {
        if ( getTrickCount() == 0 ) { return null; }

        Card highestCard = (Card)trick.elementAt(0);
        for (int i=1; i<getTrickCount(); i++) {
            Card nextCard = (Card)trick.elementAt(i);
            if ( cardCmp(highestCard, nextCard) < 0 ) {
                highestCard = nextCard;
            }
        }
        return highestCard;
    }
    public int getTrickCount() {
        return trick.size();
    }
    public Card getTrickCard(int position) {
        Card card = (Card)trick.elementAt(position);
        return card;
    }
    public int getHandWinner() {
       if ( state != PICKING_UP_TRICK && state != DEALING && state != GAME_OVER ) { return -1; }

       if ( team0Tricks + team1Tricks != 5 ) { return -1; }

        if ( team0Tricks >=3 ) {
            return 0;
        } else {
            return 1;
        }
    }

  // Public functions returning private objects
  //
  //
    public int getTrump() {
        if ( state < DEALER_DISCARDING ) {
            return -1;
        }
        return trump;
    }
    public Card getKitty() {
        return kitty;
    }
    public int getDealer() {
        return table.getDealer();
    }
    public Hand getPlayerHand(int player) {
        return table.getPlayer(player);
    }
    public Deck getDeck() {
        return table.getDeck();
    }
    public boolean debug() {
        return debug;
    }
    public void debug(boolean value) {
        debug = value;
    }
    public int getState() {
        return state;
    }
    public int getCurrentPlayer() {
        return currentPlayer;
    }
    public int getPlayerCalledTrump() {
        return playerCalledTrump;
    }
    public int getTeam0Score() {
        return team0Score;
    }
    public int getTeam1Score() {
        return team1Score;
    }

  // AI helper functions
  //
  //
    private int aiChoseLead(int player) {
      // Find the best lead by as follows:
      //   1. Find all the winning leads for us and our parnter
      //   2. Find the lowest value winner from our hand
      //   3. Find a card in our hand that leads into our parnters winner
      //   4. Play the lower of the two from #2 and #3

      // Return the card we want to lead with.  On error, return -1
        if ( getState() != PLAYING_HAND ) {
            return -1;
        }
        if ( getCurrentPlayer() != player ) {
            return -1;
        }
        if ( getTrickCount() != 0 ) {
            return -1;
        }

      // Get the winning leads for this AI and its partner
        int partner = (player+2)%4;
        boolean myWinners[] = aiWinningLeads(player);
        boolean partnersWinners[] = aiWinningLeads(partner);

      // Evaluate all our winners to find the best
        int best_winner = -1;
        for (int i=0; i<getPlayerHand(player).getCardCount(); i++ ) {
            if ( !myWinners[i] ) { continue; }

          // We already have a best winner, see if this one is better
            if ( best_winner >= 0 ) {
                if ( !cardIsTrump(getPlayerHand(player).getCard(i)) ) {
                  // Current best card is trump, use this non trump winner instead
                    if ( cardIsTrump(getPlayerHand(player).getCard(best_winner)) ) {
                        best_winner = i;
                    }
                }

                // Pick the lesser of the two cards
                if (cardCmp(getPlayerHand(player).getCard(best_winner), getPlayerHand(player).getCard(i)) > 0 ) {
                    best_winner = i;
                }
            } else {
                best_winner = i;
            }
        }

      // Evaluate all leads and chose the best
        int best_lead = -1;
        for (int i=0; i<getPlayerHand(partner).getCardCount(); i++) {
            if ( !partnersWinners[i] ) { continue; }

            if ( cardIsTrump(getPlayerHand(partner).getCard(i)) ) {
              // Find a suit our partner doesnt have, lead that so our parnter can trump it
                for (int suit=0; suit < 4; suit++) {
                    if ( suit == trump ) { continue; }

                    // Make this our new best lead if its not one of our winners
                    if ( !playerHasSuit(partner, suit) && playerHasSuit(player, suit) ) {
                        int new_best_lead = aiLowestCard(player, suit);
                        if ( !myWinners[new_best_lead] ) {
                            if (best_lead < 0) {
                                best_lead = new_best_lead;
                            } else {
                                if ( cardCmp(getPlayerHand(player).getCard(best_lead), getPlayerHand(player).getCard(new_best_lead)) > 0 ) {
                                    best_lead = new_best_lead;
                                }
                            }
                        }
                    }
                }

            } else if ( playerHasSuit(player, realSuit(getPlayerHand(partner).getCard(i))) ) {
              // Lay the lowest, non trump card so our partner can play his winner,
              // unless the card is one of our winners, or a previous best_lead is lower
                int new_best_lead = aiLowestCard(player, realSuit(getPlayerHand(partner).getCard(i)));
                if (!myWinners[new_best_lead]) {
                    if (best_lead < 0) {
                        best_lead = new_best_lead;
                    } else {
                        if (cardCmp(getPlayerHand(player).getCard(best_lead), getPlayerHand(player).getCard(new_best_lead)) > 0) {
                            best_lead = new_best_lead;
                        }
                    }
                }
            }

        }

        if ( best_winner < 0 && best_lead < 0 ) {
          // Dont have a best winner or best lead, find something to play
            best_winner = aiThrowOff(player);

        } else if ( best_winner > 0 && best_lead > 0 ) {
          // We have a winner and a lead, play the lesser of the two cards
            if ( cardCmp(getPlayerHand(player).getCard(best_winner),getPlayerHand(player).getCard(best_lead)) > 0 ) {
                best_winner = best_lead;
            }

        } else if ( best_winner < 0 ) {
          // We dont have a winner, play the best lead, if any
            best_winner = best_lead;
        }

        return best_winner;
    }
    private int aiThrowOff(int player) {
      // Find the lowest play possible and return that, avoiding trump if possible
        int tmpSuit = trump;
        int lowestPlay = -1;

        int opp1 = (player+1)%4;
        int opp2 = (player+3)%4;

      // Must follow suit
        if ( playerHasSuit(player, getLeadSuit()) ) {
            return aiLowestCard(player, getLeadSuit());
        }

        do {
          // Loop through the suits to find a low card.  End with trump
            tmpSuit = (tmpSuit + 1) % 4;

          // First determine if we can get rid of a suit
            if ( aiNumberOfSuit(player, tmpSuit) == 1 && tmpSuit != trump ) {
                if ( playerHasSuit(opp1, tmpSuit) && playerHasSuit(opp2, tmpSuit) ) {
                    return aiLowestCard(player, tmpSuit);
                }
            }

          // Find lowest suit
            int lowestOfSuit = aiLowestCard(player, tmpSuit);
            if (lowestOfSuit >= 0) {
                if (lowestPlay >= 0) {
                    if ( tmpSuit != trump && realValue(getPlayerHand(player).getCard(lowestPlay)) > realValue(getPlayerHand(player).getCard(lowestOfSuit)) ) {
                        lowestPlay = lowestOfSuit;
                    }
                } else {
                    lowestPlay = lowestOfSuit;
                }
            }

        } while (tmpSuit != trump);
        return lowestPlay;
    }
    private int aiTakeTrick(int player) {
      // Take the trick if we can

        if ( playerHasSuit(player, getLeadSuit()) ) {
          // We must follow suit play the highest card if it wins
            if ( cardCmp(getPlayerHand(player).getCard(aiHighestCard(player, getLeadSuit())), getTrickWinnerCard()) > 0 ) {
                return aiHighestCard(player, getLeadSuit());
            }

        } else {
          // We dont have to follow suit, try to trump it
            if ( getLeadSuit() != getTrump() && playerHasSuit(player, getTrump()) ) {
                if ( cardCmp(getPlayerHand(player).getCard(aiLowestCard(player, getTrump())), getTrickWinnerCard()) > 0 ) {
                    return aiLowestCard(player, getTrump());

                } else if ( cardCmp(getPlayerHand(player).getCard(aiHighestCard(player, getTrump())), getTrickWinnerCard()) > 0 ) {
                    return aiHighestCard(player, getTrump());
                }
            }
        }

      // We cant take the trick, throw off
        return aiThrowOff(player);
    }
    private int aiHighestCard(int player, int suit) {
      // Return the highest card of suit for a player
        if ( ! playerHasSuit(player, suit) ) { return -1; }

        int highest_card = -1;
        for (int i=0; i<getPlayerHand(player).getCardCount(); i++) {
            if ( realSuit(getPlayerHand(player).getCard(i)) == suit ) {
                if ( highest_card >= 0 ) {
                    if ( cardCmp(getPlayerHand(player).getCard(i), getPlayerHand(player).getCard(highest_card)) > 0 ) {
                        highest_card = i;
                    }
                } else {
                    highest_card = i;
                }
            }
        }
        return highest_card;
    }
    private int aiLowestCard(int player, int suit) {
      // Return the lowest card of suit for a player
        if ( ! playerHasSuit(player, suit) ) { return -1; }

        int lowest_card = -1;
        for (int i=0; i<getPlayerHand(player).getCardCount(); i++) {
            if ( realSuit(getPlayerHand(player).getCard(i)) == suit ) {
                if ( lowest_card >= 0 ) {
                    if ( cardCmp(getPlayerHand(player).getCard(i), getPlayerHand(player).getCard(lowest_card)) < 0 ) {
                        lowest_card = i;
                    }
                } else {
                    lowest_card = i;
                }
            }
        }
        return lowest_card;
    }
    private int aiNumberOfSuit(int player, int suit) {
        int count = 0;
        for (int i=0; i<getPlayerHand(player).getCardCount(); i++) {
            if ( realSuit(getPlayerHand(player).getCard(i)) == suit ) {
                count++;
            }
        }
        return count;
    }
    private boolean[] aiWinningLeads(int player) {
      // Return an array of boolean values indicating if the coorelating
      // card in the players hand will be a winner or not
        boolean hand[] = new boolean[getPlayerHand(player).getCardCount()];

      // Check each card in AI's hand against all oppnents hands
      // it is a winner if its higher than enything in the opponents hand and
      // the opponent has to follow suit
        int opp1 = (player+1)%4;
        int opp2 = (player+3)%4;
        for (int i=0; i<getPlayerHand(player).getCardCount(); i++) {
            Card aiCard = getPlayerHand(player).getCard(i);
            boolean isaWinner = true;

          // Verify that this card beats everything in opponent 1's hand
            for (int j=0; j<getPlayerHand(opp1).getCardCount(); j++) {
                Card oppCard = getPlayerHand(opp1).getCard(j);
                
              // Player has to follow suit
                if ( playerHasSuit(opp1, realSuit(aiCard)) ) {
                    if ( realSuit(oppCard) != realSuit(aiCard) ) { continue; }
                    if (cardCmp(aiCard, oppCard) < 0) {
                        isaWinner = false;
                        break;
                    }
              // Player does not have this suit in hand
                } else {

                  // One of these cards is trump
                    if (cardIsTrump(aiCard) || cardIsTrump(oppCard)) {
                        if (cardCmp(aiCard, oppCard) < 0) {
                            isaWinner = false;
                            break;
                        }
                    }
                }
            }

          // Verify that this card beats everything in opponent 2's hand
            if (isaWinner) {
                for (int j = 0; j < getPlayerHand(opp2).getCardCount(); j++) {
                    Card oppCard = getPlayerHand(opp2).getCard(j);

                    // Player has to follow suit
                    if (playerHasSuit(opp2, realSuit(aiCard))) {
                        if (realSuit(oppCard) != realSuit(aiCard)) {
                            continue;
                        }
                        if (cardCmp(aiCard, oppCard) < 0) {
                            isaWinner = false;
                            break;
                        }
                    // Player does not have this suit in hand
                    } else {

                        // One of these cards is trump
                        if (cardIsTrump(aiCard) || cardIsTrump(oppCard)) {
                            if (cardCmp(aiCard, oppCard) < 0) {
                                isaWinner = false;
                                break;
                            }
                        }
                    }
                }
            }

          // Store the results
            hand[i] = isaWinner;
        }
        return hand;
    }
    public int aiSimulateGame(int player) {
      // Simulate the gameplay as if everyone was an AI
        team0Score = 0;
        team1Score = 0;

        currentPlayer = (table.getDealer()+1)%4;
        while ( state != DEALING && state != GAME_OVER ) {
            while ( state == PLAYING_HAND ) {
                aiPlayCard(getCurrentPlayer());
            }
            pickUpTrick();
        }
        if ( player == 0 || player == 2 ) {
            return this.team0Score;
        } else {
            return this.team1Score;
        }
    }

}
