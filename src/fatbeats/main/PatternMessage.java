package fatbeats.main;

import java.io.Serializable;

class PatternMessage implements Serializable {
	private static final long serialVersionUID = 5066724470330454349L;

	private boolean[] patternState;
	protected String patternName;
	protected int messageNumber;

	PatternMessage(boolean[] patternState, String patternName, int messageNumber) {
		super();
		this.patternState = patternState;
		this.patternName = patternName;
		this.messageNumber = messageNumber;
	}

	boolean[] getPatternState() {
		return patternState;
	}

	String getPatternName() {
		return patternName;
	}

	@Override
	public String toString() {
		return "#" + messageNumber + ": " + patternName;
	}
}
