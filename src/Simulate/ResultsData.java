package Simulate;

// Results data used by BestHands to keep track of times a hand has won, lost, was set, euchred, and called trump
public class ResultsData {
	private int timesPlayed;
	private int timesWon;
	private int timesLost;
	private int timesSet;
	private int timesCalled;
	private int timesEuchred;

	public ResultsData() {
		timesPlayed = 0;
		timesWon = 0;
		timesLost = 0;
		timesSet = 0;
		timesCalled = 0;
		timesEuchred = 0;
	}

	protected void incPlayed() { timesPlayed++; }
	protected void incWon() { timesWon++; }
	protected void incLost() { timesLost++; }
	protected void incSet() { timesSet++; }
	protected void incCalled() { timesCalled++; }
	protected void incEuchred() { timesEuchred++; }

	public int timesPlayed() { return timesPlayed; }
	public int timesWon() { return timesWon; }
	public int timesLost() { return timesLost; }
	public int timesSet() { return timesSet; }
	public int timesCalled() { return timesCalled; }
	public int timesEuchred() { return timesEuchred; }

	public String toString() {
		int winPct = 100*timesWon() / timesPlayed();
		return (winPct+" "+timesPlayed()+" "+timesCalled()+" "+timesSet()+" "+timesEuchred());
	}
	public void print() {
		int winPct = 100*timesWon() / timesPlayed();

		System.out.println("\tWin Percent:   "+winPct+"% ("+timesWon()+"/"+timesPlayed()+")");
		System.out.println("\tTimes Called:  "+timesCalled());
		System.out.println("\tTimes Set:     "+timesSet());
		System.out.println("\tTimes Euchred: "+timesEuchred());
	}
}
