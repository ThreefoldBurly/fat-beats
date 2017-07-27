package fatbeats.server;

import java.io.Serializable;

class BroadcastMessage extends PatternMessage implements Serializable {
	private static final long serialVersionUID = -3262247255837585342L;

	private String senderName;

	BroadcastMessage(boolean[] patternState, String patternName, int messageNumber) {
		super(patternState, patternName, messageNumber);
		senderName = "";
	}

	void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	@Override
	public String toString() {
		return "[" + senderName + "] #" + messageNumber + ": " + patternName;
	}
}
