package Simulate;

import Game.*;

// Run a simulated game of euchre with Simulate.SimEuchre instead of the omniscient AI from Game.Euchre
class RealPlayHands extends BestHands {
	public RealPlayHands() { super(); }

	@Override
	public Euchre newGame() {
		return new SimEuchre();
	}

	public static void main(String arg[]) {
		new RealPlayHands().runThreadedSimulate();
	}
}
