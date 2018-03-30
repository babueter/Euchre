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

package Gui;

import Game.*;
import Simulate.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.*;
import javax.swing.*;

public class EuchreCanvas extends Canvas implements MouseListener {
    protected Euchre game;      // The Euchre game we are playing

    private Image cardImages;   // Images of all the cards.  Each card is 40-by-60
                                // pixels.  The cards are arranged in 4 rows and 13
                                // columns, according to suit and value.   Ace is at
                                // the beginning.
    private Image trumpImages;  // Images of the trump cards.  Really just an ace card
                                // of the same format as cardImages.

    private Font bigFont;       // Font used to display messages
    private Font smallFont;     // Font used to draw the cards

    private String playerMessages[];

    public EuchreCanvas() {
      // Constructor.  Creates fonts and starts the game.
        setBackground( new Color(0,120,0) );
        setForeground( Color.green );

      // Images of all the cards
        URL cardGraphicsURL = getClass().getResource("/Images/smallcards.gif");
        ImageIcon cardGraphicsIcon = new javax.swing.ImageIcon(cardGraphicsURL);
        cardImages = cardGraphicsIcon.getImage();

      // Images of all the trumps
        URL trumpGraphicsURL = getClass().getResource("/Images/trumpcards.gif");
        ImageIcon trumpGraphicsIcon = new javax.swing.ImageIcon(trumpGraphicsURL);
        trumpImages = trumpGraphicsIcon.getImage();

        smallFont = new Font("Courier", Font.PLAIN, 12);
        bigFont = new Font("Courier", Font.BOLD, 14);

        playerMessages = new String[4];
        playerMessages[0] = "";
        playerMessages[1] = "";
        playerMessages[2] = "";
        playerMessages[3] = "";

        addMouseListener(this);
        
      // Initialize the SimEuchre data
        SimEuchre.getData();

        doNewGame();
    }

  // Paint the canvas
    @Override
    public void paint(Graphics g) {
        // Paint a 351x351 buffered image
        java.awt.Image bufferImage = createImage(350, 351);
        paint300(bufferImage.getGraphics());

        // Scale the current window size
        int width = getWidth();
        int height = getHeight();
        ReplicateScaleFilter scale = new ReplicateScaleFilter(width, height);
        FilteredImageSource fis = new FilteredImageSource(bufferImage.getSource(), scale);
        Image scaledImage = createImage(fis);

        g.drawImage(scaledImage, 0, 0, null);
    }
    public void paint300(Graphics g) {
        // Draw the player boxes
        drawPlayer0Cards(g);
        drawPlayer1Cards(g);
        drawPlayer2Cards(g);
        drawPlayer3Cards(g);
        drawTrumpCalled(g);

      // Draw kitty and any messages on the board
        if ( game.getState() == Euchre.DEALING ) {
            if ( game.getPlayerCalledTrump() == 0 || game.getPlayerCalledTrump() == 2 ) {
                if ( game.getHandWinner() == 0 ) {
                    drawUserMessage(g, "YOU WON");
                } else {
                    drawUserMessage(g, "YOU WERE SET");
                }
            } else {
                if ( game.getHandWinner() == 1 ) {
                    drawUserMessage(g, "YOU LOST");
                } else {
                    drawUserMessage(g, "YOU SET YOUR OPPONENT");
                }
            }
            drawScore(g);

      // Draw things related to picking trump
        } else if ( game.getState() < Euchre.PLAYING_HAND ) {
            drawMessages(g);
            drawUserPrompt(g);
            drawKitty(g);

      // Draw cards being played
        } else {
            drawCardsPlayed(g);

            if (game.getState() == Euchre.PICKING_UP_TRICK) {
                int winner = game.getTrickWinner();
                switch (winner) {
                    case 0: drawUserMessage(g, "TRICK IS YOURS"); break;
                    case 1: drawUserMessage(g, "TRICK IS OPPONENT 1s");; break;
                    case 2: drawUserMessage(g, "TRICK IS YOUR PARTNERS");; break;
                    case 3: drawUserMessage(g, "TRICK IS OPPONENT 2s");; break;
                }
            } else if ( game.getState() == Euchre.GAME_OVER ) {
                if ( game.getTeam0Score() < 10 ) {
                    drawUserMessage(g, "GAME OVER - YOU LOSE");
                } else {
                    drawUserMessage(g, "GAME OVER - YOU WIN");
                }
                drawScore(g);
            }
        }
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

  // Drawing events on the center of the board
    private void drawMessages(Graphics g) {
      // Draw text messages for each user
        for (int i=0; i<4; i++) {
            int x=0;
            int y=0;
            switch (i) {
                case 0: 
                    drawUserMessage(g, playerMessages[i]);
                    break;

                case 1: x=75;  y=174; break;
                case 2: x=174-(int)((playerMessages[i].length()*6.5)/2); y=85;  break;
                case 3: x=273-(int)(playerMessages[i].length()*6.5); y=174; break;
            }
            g.setFont(smallFont);
            g.drawString(playerMessages[i], x, y);
        }
    }
    private void drawUserPrompt(Graphics g) {
        if ( game.getState() == Euchre.DEALER_DISCARDING ) {
            if ( game.getDealer() == 0 ) {
                drawUserMessage(g, "PICK YOUR DISCARD");
            } else if ( game.getPlayerCalledTrump() == game.getDealer() ) {
                drawUserMessage(g, "DEALER CALLED IT UP");
            }
        }
        if ( game.getCurrentPlayer() != 0 ) { return; }

        if ( game.getState() == Euchre.ORDERING_TRUMP ) {
            drawPercentWin(g);
            drawUserMessage(g, "PICK IT UP?");
        } else if ( game.getState() == Euchre.CALLING_TRUMP ) {
            drawUserMessage(g, "PICK TRUMP FROM HAND");
        } else if ( game.getState() == Euchre.WAITING_TO_START ) {
            switch (game.getTrump()) {
                case Card.CLUBS:    drawUserMessage(g, "TRUMP IS CLUBS"); break;
                case Card.DIAMONDS: drawUserMessage(g, "TRUMP IS DIAMONDS"); break;
                case Card.HEARTS:   drawUserMessage(g, "TRUMP IS HEARTS"); break;
                case Card.SPADES:   drawUserMessage(g, "TRUMP IS SPADES"); break;
            }
        }
    }
    private void drawUserMessage(Graphics g, String message) {
        if ( message.length() == 0 ) { return; }
        
        int textHeight = 10;
        int textWidth = 8*message.length();

        int x = 174;
        int y = 125;
        if ( game.getDealer() == 2 && game.getState() < Euchre.WAITING_TO_START && game.getState() != Euchre.DEALING ) {
            x = 174;
            y = 150;
        }

        g.setColor(Color.DARK_GRAY);
        g.fill3DRect(x-(int)(textWidth/2)-5, y-(int)(textHeight/2)-5, textWidth+10, textHeight+10, true);
        g.setColor(Color.WHITE);
        g.setFont(bigFont);
        g.drawString(message, x-(int)(textWidth/2), y+(int)(textHeight/2));
    }
    private void drawScore(Graphics g) {
        String message0 = "YOUR TEAM: "+game.getTeam0Score();
        int textHeight = 10;
        int textWidth = 8*message0.length();

        int x = 174;
        int y = 150;

        g.setColor(Color.DARK_GRAY);
        g.fill3DRect(x-(int)(textWidth/2)-5, y-(int)(textHeight/2)-5, textWidth+10, textHeight+10, true);
        g.setColor(Color.WHITE);
        g.setFont(bigFont);
        g.drawString(message0, x-(int)(textWidth/2), y+(int)(textHeight/2));

        String message1 = "OPPONENTS: "+game.getTeam1Score();
        textHeight = 10;
        textWidth = 8*message1.length();

        x = 174;
        y = 175;

        g.setColor(Color.DARK_GRAY);
        g.fill3DRect(x-(int)(textWidth/2)-5, y-(int)(textHeight/2)-5, textWidth+10, textHeight+10, true);
        g.setColor(Color.WHITE);
        g.setFont(bigFont);
        g.drawString(message1, x-(int)(textWidth/2), y+(int)(textHeight/2));

    }
    private void drawKitty(Graphics g) {
      // Draw the kitty where it is supposed to be
        int x=0;
        int y=0;
        switch (game.getDealer()) {
            case 0:     x=154; y=214; break;
            case 1:     x=72;  y=144; break;
            case 2:     x=154; y=72;  break;
            case 3:     x=236; y=144; break;
        }

      // Print the kitty card, face down if we are calling trump
        if ( game.getState() == Euchre.ORDERING_TRUMP || game.getState() == Euchre.DEALER_DISCARDING ) {
            drawCard(g, game.getKitty(), x, y);
        }
    }
    private void drawPercentWin(Graphics g) {
        String winPctText = null;

        if ( game.getState() == Euchre.ORDERING_TRUMP && game.getDealer() != 0 ) {
          // Find percentage win for this hand if we call up trump
            SimHand hand = new SimHand(game.getPlayerHand(0), game.getKitty().getSuit());
            String handText = new Integer(hand.hashCode()).toString();

            Integer winPct = Simulate.SimEuchre.pctWin.get(handText);
            winPctText = winPct + "%";
        }

        if ( game.getState() == Euchre.ORDERING_TRUMP && game.getDealer() == 0 ) {
          // Find percentage win for this hand if we pick up kitty
            Euchre newGame = new Euchre(game);
            newGame.playerOrderUp(0, true);
            newGame.dealerDiscard(newGame.aiDiscard(0));

            SimHand hand = new SimHand(newGame.getPlayerHand(0), newGame.getTrump());
            String handText = new Integer(hand.hashCode()).toString();

            Integer winPct = Simulate.SimEuchre.pctWin.get(handText);
            winPctText = winPct + "%";
        }

        if ( winPctText != null ) {
          // x = 25
          // y = 310
          // Text height = 10
          // Text width = 8 * 3
            g.setColor(Color.DARK_GRAY);
            g.fill3DRect(25, 310, 24, 10, true);
            g.setColor(Color.WHITE);
            g.setFont(bigFont);
            g.drawString(winPctText, 25, 320);
        }
    }

  // Drawing events for player cards
    private void drawCardsPlayed(Graphics g) {
        int x=0;
        int y=0;
        int player = game.getLeadPlayer();
        for (int i=0; i<game.getTrickCount(); i++) {
            switch (player) {
                case 0:     x=154; y=180;  break;
                case 1:     x=110; y=144;  break;
                case 2:     x=154; y=110;  break;
                case 3:     x=198; y=144;  break;
            }

            drawCard(g, game.getTrickCard(i), x, y);
            player = (player+1)%4;
        }
    }
    private void drawTrumpCalled(Graphics g) {
      // Draw the trump value next to whoever called it
        if ( game.getState() < Euchre.DEALER_DISCARDING ) { return; }

        switch (game.getPlayerCalledTrump()) {
            case (0):   drawTrump(g, 214, 214); break;
            case (1):   drawTrump(g, 72, 204); break;
            case (2):   drawTrump(g, 94, 72); break;
            case (3):   drawTrump(g, 236, 84); break;
        }
    }
    private void drawCard(Graphics g, Card card, int x, int y) {
        if ( card == null ) {
            g.setColor(Color.blue);
            g.fillRect(x,y,40,60);
            g.setColor(Color.white);
            g.drawRect(x+3,y+3,33,53);
            g.drawRect(x+4,y+4,31,51);
        } else {
            int row = card.getSuit();

            int sx, sy;  // coordinates of upper left corner of the image
            sx = 40*(card.getValue()-1);
            sy = 60*(row);

            g.drawImage(cardImages, x, y, x+40, y+60, sx, sy, sx+40, sy+60, this);
        }
    }
    private void drawTrump(Graphics g, int x, int y) {
        int row = game.getTrump();

        int sx, sy;  // coordinates of upper left corner of the image
        sx = 0;
        sy = 60 * (row);

        g.drawImage(trumpImages, x, y, x + 40, y + 60, sx, sy, sx + 40, sy + 60, this);
    }
    private void drawPlayer0Cards(Graphics g) {
        int x=70;
        int y=284;
        g.drawRect(68,282,212,64);
        for (int card=0; card < game.getPlayerHand(0).getCardCount(); card++) {
            this.drawCard(g, game.getPlayerHand(0).getCard(card), x, y);
            x += 42;
        }
    }
    private void drawPlayer1Cards(Graphics g) {
        int x=4;
        int y=70;
        g.drawRect(2,68,64,212);
        for (int card=0; card < game.getPlayerHand(1).getCardCount(); card++) {
            g.setColor(Color.blue);
            g.fillRect(x,y,60,40);
            g.setColor(Color.white);
            g.drawRect(x+3,y+3,53,33);
            g.drawRect(x+4,y+4,51,31);
            y += 42;
        }
    }
    private void drawPlayer2Cards(Graphics g) {
        int x=70;
        int y=4;
        g.drawRect(68,2,212,64);
        for (int card=0; card < game.getPlayerHand(2).getCardCount(); card++) {
            g.setColor(Color.blue);
            g.fillRect(x,y,40,60);
            g.setColor(Color.white);
            g.drawRect(x+3,y+3,33,53);
            g.drawRect(x+4,y+4,31,51);
            x += 42;
        }
    }
    private void drawPlayer3Cards(Graphics g) {
        int x=284;
        int y=70;
        g.drawRect(282,68,64,212);
        for (int card=0; card < game.getPlayerHand(3).getCardCount(); card++) {
            g.setColor(Color.blue);
            g.fillRect(x,y,60,40);
            g.setColor(Color.white);
            g.drawRect(x+3,y+3,53,33);
            g.drawRect(x+4,y+4,51,31);
            y += 42;
        }
    }

  // Start and advance the game
    public void doNewGame() {
        game = new Euchre();
        game.deal();
        catchUpToHuman();
    }
    public void catchUpToHuman() {
        while ( game.getCurrentPlayer() != 0 && game.getState() != Euchre.WAITING_TO_START && game.getState() != Euchre.PICKING_UP_TRICK ) {
          // While the current player is not player 0, do the AI portion of the game

            if ( game.getState() == Euchre.PLAYING_HAND ) {
                game.aiPlayCard(game.getCurrentPlayer());

            } else if ( game.getState() == Euchre.DEALER_DISCARDING ) {
                game.dealerDiscard(game.aiDiscard(game.getDealer()));

            } else if ( game.getState() == Euchre.ORDERING_TRUMP ) {
                if ( game.aiOrderUp(game.getCurrentPlayer()) ) {
                    if ( game.getDealer() != game.getCurrentPlayer() ) {
                        playerMessages[game.getCurrentPlayer()] = "Pick it up!";
                    } else {
                        playerMessages[0] = "";
                        playerMessages[1] = "";
                        playerMessages[2] = "";
                        playerMessages[3] = "";
                    }
                    game.playerOrderUp(game.getCurrentPlayer(), game.aiOrderUp(game.getCurrentPlayer()));
                    break;
                } else {
                    if ( game.getDealer() != game.getCurrentPlayer() ) {
                        playerMessages[game.getCurrentPlayer()] = "Pass";
                    }
                }
                game.playerOrderUp(game.getCurrentPlayer(), game.aiOrderUp(game.getCurrentPlayer()));

            } else if ( game.getState() == Euchre.CALLING_TRUMP ) {
              // First clear everyone else's board
                int nextPlayer = (game.getCurrentPlayer()+1)%4;
                while ( nextPlayer != game.getDealer() ) {
                    playerMessages[nextPlayer] = "";
                    nextPlayer = (nextPlayer+1)%4;
                }

              // Determine if player is going to call trump then do so
                int calledSuit = game.aiCallTrump(game.getCurrentPlayer());
                switch (calledSuit) {
                    case Card.CLUBS:    playerMessages[game.getCurrentPlayer()] = "Clubs!"; break;
                    case Card.HEARTS:   playerMessages[game.getCurrentPlayer()] = "Hearts!"; break;
                    case Card.SPADES:   playerMessages[game.getCurrentPlayer()] = "Spades!"; break;
                    case Card.DIAMONDS: playerMessages[game.getCurrentPlayer()] = "Diamonds!"; break;
                    default:            playerMessages[game.getCurrentPlayer()] = "Check"; break;
                }
                game.playerCallTrump(game.getCurrentPlayer(), calledSuit);
                if ( calledSuit > 0 ) { return; }
            } else {
                break;
            }
        }
        repaint();
    }

  // Mouse events
    public void mouseClicked(MouseEvent e) {
        if ( e.getButton() != 1 ) { return; }

        if ( game.getState() == Euchre.GAME_OVER ) {
          // Game over, start again
            doNewGame();
        }

        if ( game.getState() == Euchre.DEALING ) {
            playerMessages[0] = "";
            playerMessages[1] = "";
            playerMessages[2] = "";
            playerMessages[3] = "";

            game.deal();
            catchUpToHuman();
            
            repaint();
            return;
        }
        if ( game.getState() == Euchre.PICKING_UP_TRICK ) {
            game.pickUpTrick();
            repaint();
        }
        if ( game.getState() == Euchre.WAITING_TO_START ) {
            game.startPlaying();
            repaint();
        }
        if ( game.getCurrentPlayer() != 0 ) {
            catchUpToHuman();
            return;
        }

        // Translate x and y coordinates to 350x351 scale
        int x = e.getX();
        int y = e.getY();
        
        x /= (float)this.getWidth()/350;
        y /= (float)this.getHeight()/351;

        if ( game.getState() == Euchre.DEALER_DISCARDING ) {
          // Determine if we've clicked on a card
            if ( x > 70 && x < 42*game.getPlayerHand(0).getCardCount()+70 && y > 284 && y < 348 ) {
                int cardNumber = (x - 70) / 42;
                game.dealerDiscard(cardNumber);
                repaint();
            }
        } else if (game.getState() == Euchre.ORDERING_TRUMP ) {
          // Allow user to pick up card
            int x_min = 154;
            int y_min = 214;
            switch (game.getDealer()) {
                case(1):    x_min = 72;  y_min = 144; break;
                case(2):    x_min = 154; y_min = 72; break;
                case(3):    x_min = 236; y_min = 144; break;
            }
            if ( x > x_min && x < x_min+40 && y > y_min && y < y_min+60 ) {
                game.playerOrderUp(0, true);
            } else {
                game.playerOrderUp(0, false);
            }
            catchUpToHuman();

        } else if (game.getState() == Euchre.CALLING_TRUMP) {
          // Have user pick a trump from hand
            if ( x > 70 && x < 42*game.getPlayerHand(0).getCardCount()+70 && y > 284 && y < 348 ) {
                int cardNumber = (x - 70) / 42;
                game.playerCallTrump(0, game.getPlayerHand(0).getCard(cardNumber).getSuit());

                playerMessages[0] = "";
                playerMessages[1] = "";
                playerMessages[2] = "";
                playerMessages[3] = "";
            } else if ( game.getDealer() != 0 ) {
                game.playerPassCall(0);
            }
            catchUpToHuman();

        } else if (game.getState() == Euchre.PLAYING_HAND) {
            // Play the card we selected
            if (x > 70 && x < 42 * game.getPlayerHand(0).getCardCount() + 70 && y > 284 && y < 348) {
                int cardNumber = (x - 70) / 42;
                game.playerPlayCard(0, cardNumber);
                catchUpToHuman();
            }
            repaint();
        }

        repaint();
    }
    public void mousePressed(MouseEvent e) {
    }
    public void mouseReleased(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }

}
