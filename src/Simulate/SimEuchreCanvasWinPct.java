package Simulate;

import Gui.*;
import Game.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

// Extend SimEuchreCanvas, adding the feature of displaying the win percentage of calling trump
//
// This will show the win percentage for calling up the kitty card and picking trump from your hand
//
public class SimEuchreCanvasWinPct extends SimEuchreCanvas implements MouseMotionListener {
	SimEuchreCanvasWinPct () {
		super();
		addMouseMotionListener(this);
	}

	// Add mouse events that show the player what percentage win a trump selection would have
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		x /= (float)this.getWidth()/350;
		y /= (float)this.getHeight()/351;

		// Display win percentage for ordering up trump
		if ( game.getState() == Euchre.ORDERING_TRUMP ) {
			// Determine if we're over the kitty
			boolean overKitty = false;
			int x_min = 154;
			int y_min = 214;
			switch (game.getDealer()) {
				case(1):    x_min = 72;  y_min = 144; break;
				case(2):    x_min = 154; y_min = 72; break;
				case(3):    x_min = 236; y_min = 144; break;
			}
			if ( x > x_min && x < x_min+40 && y > y_min && y < y_min+60 ) {
				overKitty = true;

				// Create a sim hand based on calling up the kitty card
				int trump = game.getKitty().getSuit();
				SimHand simHand;

				// Make the simHand include the kitty card if player0 is dealer
				if ( game.getDealer() == 0 ) {
					Euchre gameClone = new Euchre(game);
					gameClone.playerOrderUp(0, true);
					gameClone.dealerDiscard(gameClone.aiDiscard(0));

					simHand = new SimHand(gameClone.getPlayerHand(0), trump);
				} else {
					simHand = new SimHand(game.getPlayerHand(0), trump);
				}
				HashMap<String, Integer> pctWin = SimEuchre.getData();
				Integer winPct = pctWin.get(new Integer(simHand.hashCode()).toString());

				// Change the pointer to the win percentage value
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				BufferedImage  image = new BufferedImage(30, 17, BufferedImage.TYPE_INT_ARGB);
				if ( winPct == 100 ) { image = new BufferedImage(45, 17, BufferedImage.TYPE_INT_ARGB); }

				Graphics2D g2 = image.createGraphics();
				g2.setColor(Color.DARK_GRAY);
				g2.fillRect(0, 0, 34, 20);
				g2.setColor(Color.WHITE);
				g2.setFont(new Font("Courier", Font.BOLD, 14));
				g2.drawString(winPct.toString()+"%", 2, 13);

				Point hotSpot = new Point(0,0);
				Cursor cursor = toolkit.createCustomCursor(image, hotSpot, "WinPct");
				setCursor(cursor);
				return;
			}
		}

		// Display win percentage for picking trump from hand
		if ( game.getState() == Euchre.CALLING_TRUMP ) {
			// Determine if we're over one of our cards
			int cardNumber = -1;
			if ( x > 70 && x < 42*game.getPlayerHand(0).getCardCount()+70 && y > 284 && y < 348 ) {
				cardNumber = (x - 70) / 42;
			}

			if ( cardNumber >= 0 ) {
				// If the suit of the card we are over is NOT the same as the kitty, change the pointer
				if ( game.getPlayerHand(0).getCard(cardNumber).getSuit() != game.getKitty().getSuit() ) {
					SimHand simHand = new SimHand(game.getPlayerHand(0), game.getPlayerHand(0).getCard(cardNumber).getSuit());
					HashMap<String, Integer> pctWin = SimEuchre.getData();
					Integer winPct = pctWin.get(new Integer(simHand.hashCode()).toString());

					Toolkit toolkit = Toolkit.getDefaultToolkit();
					BufferedImage  image = new BufferedImage(30, 17, BufferedImage.TYPE_INT_ARGB);
					if ( winPct == 100 ) { image = new BufferedImage(45, 17, BufferedImage.TYPE_INT_ARGB); }

					Graphics2D g2 = image.createGraphics();
					g2.setColor(Color.DARK_GRAY);
					g2.fillRect(0, 0, 34, 20);
					g2.setColor(Color.WHITE);
					g2.setFont(new Font("Courier", Font.BOLD, 14));
					g2.drawString(winPct.toString()+"%", 2, 13);

					Point hotSpot = new Point(0,0);
					Cursor cursor = toolkit.createCustomCursor(image, hotSpot, "WinPct");
					setCursor(cursor);
					return;
				}
			}
		}
		// Nothing was done, update the pointer to the default cursor
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

	}
	public void mouseDragged(MouseEvent e) {
	}
}
