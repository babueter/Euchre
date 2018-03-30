package Simulate;

import Gui.*;

// Extend EuchreCanvas replacing Euchre() with SimEuchre()
//
// This lets you play the game with Simulated AI instead of the omniscient AI from Game.Euchre
//
public class SimEuchreCanvas extends EuchreCanvas {
	SimEuchreCanvas () {
		super();
	}

	@Override
	public void doNewGame() {
		game = new SimEuchre();
		game.deal();
		catchUpToHuman();
	}
}
