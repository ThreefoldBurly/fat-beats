package fatbeats.main;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

class PatternPanel extends JPanel {
	private static final long serialVersionUID = -81803078919667246L;

	private ArrayList<JCheckBox> pattern;
	private LinkedList<boolean[]> patternStates;
	private boolean isUndoable, isRedoable;

	//Redo mechanism controllers
	private ArrayList<boolean[]> statesSnapshot;
	private int numberOfHops;

	private String currentName; //used through accessors by PatternControls, PlaylistControls and FileMenu and never locally

	PatternPanel(ActionListener patternListener) {
		pattern = new ArrayList<JCheckBox>();
		patternStates = new LinkedList<boolean[]>();
		currentName = "";

		GridLayout grid = new GridLayout(16, 16);
		grid.setVgap(3);
		grid.setHgap(2);
		this.setLayout(grid);
		for (int i = 0; i < 256; i++) {
			JCheckBox c = new JCheckBox();
			c.addActionListener(patternListener);
			c.setSelected(false);
			pattern.add(c);
			this.add(c);
		}
		patternStates.addLast(extractPatternState()); //adding ZERO-state
	}

	boolean isUndoable() {
		return isUndoable;
	}

	boolean isRedoable() {
		return isRedoable;
	}

	String getCurrentName() {
		return currentName;
	}

	void setCurrentName(String currentName) {
		this.currentName = currentName;
	}

	void clearRow(int row) {
		for (int i = 0; i < 16; i++) {
			pattern.get(row * 16 + i).setSelected(false);
		}
	}

	void disableRow(int row) {
		for (int i = 0; i < 16; i++) {
			pattern.get(row * 16 + i).setEnabled(false);
		}
	}

	void enableRow(int row) {
		for (int i = 0; i < 16; i++) {
			pattern.get(row * 16 + i).setEnabled(true);
		}
	}

	boolean hasNothingSelected() {
		boolean nothingSelected = true;
		for (JCheckBox jcb : pattern) {
			if (jcb.isSelected()) {
				nothingSelected = false;
				return nothingSelected;
			}
		}
		return nothingSelected;
	}

	void clearPattern() {
		for (JCheckBox jcb : pattern) {
			jcb.setSelected(false);
		}
	}

	void changePattern(boolean[] patternState) {
		for (int i = 0; i < 256; i++) {
			JCheckBox jcb = pattern.get(i);
			if (patternState[i]) {
				jcb.setSelected(true);
			} else {
				jcb.setSelected(false);
			}
		}
	}

	boolean[] extractPatternState() {
		boolean[] patternState = new boolean[256];
		for (int i = 0; i < 256; i++) {
			JCheckBox jcb = pattern.get(i);
			if (jcb.isSelected() && jcb.isEnabled()) {
				patternState[i] = true;
			}
		}
		return patternState;
	}

	void randomizePattern() {
		int random = 0;
		int factor = 0;
		for (JCheckBox jcb : pattern) {
			random = (int) (Math.random() * 20) + 1; //setting random to 1-20 range and factor to 3-8
			factor = (int) (Math.random() * 6) + 3;
			if (random < factor) {
				jcb.setSelected(true);
			} else {
				jcb.setSelected(false);
			}
		}
	}

	//undo-redo mechanism methods
	void addPatternState() { //called by PatternListener, PlaylistListener and Clear, ClearRow and Randomize actions
		patternStates.addLast(extractPatternState());
		isUndoable = true; //if sth's been added, it can be undone - setting this flag is crucial (if there was any full-sequence Undo before, without it, a new one would end after one hop)
		numberOfHops = 0; //resetting Redo controllers
		statesSnapshot = null;
	}

	void doUndo() {
		if (numberOfHops == 0) {
			takeSnapshot();
		}
		if (patternStates.size() > 1) { //do Undo unless you've reached the initial state
			patternStates.removeLast();
			numberOfHops++;
			changePattern(patternStates.getLast());
			if (patternStates.size() == 1) { //we're at the initial state, so let's mark it for PatternControls to disable Undo action
				isUndoable = false;
			}
			isRedoable = true; //if there was Undo, we can get Redo
		}
	}

	void doRedo() {
		if (numberOfHops > 0) { //do Redo unless we've already backtracked all Undo hops
			patternStates.addLast(statesSnapshot.get(statesSnapshot.size() - numberOfHops));
			numberOfHops--;
			changePattern(patternStates.getLast());
			if (numberOfHops == 0) {
				isRedoable = false; //we've gone the whole way back, so let's mark it for PatternControls to disable Redo action
			}
			isUndoable = true; //if there was Redo, we can get Undo
		}
	}

	void resetToLoadedState() { //reset ZERO-state to the loaded one
		patternStates.clear();
		patternStates.add(extractPatternState());
	}

	private void takeSnapshot() { //called at each beginning of Undo sequence - we have to know how to return with Redo
		statesSnapshot = new ArrayList<boolean[]>();
		for (boolean[] stateToCopy : patternStates) {
			statesSnapshot.add(stateToCopy);
		}
	}

	//debugging method - it lists the statesSnapshot contents each time the snapshot is taken
	/*private void printRow(boolean[] array, int row) {
		for (int i = 0; i < 16; i++) {
			if (array[row * 16 + i]) {
				System.out.print("1");
			} else {
				System.out.print("0");
			}
			System.out.print(" ");
		}
		System.out.println();
	}*/

	//code to insert into takeSnapshot() for debugging
	/*System.out.println("*******************************"); //debugging
		for (boolean[] tmp : statesSnapshot) {
			for (int i = 0; i < 16; i++) {
				printRow(tmp, i);
			}
			System.out.println("*******************************");
		}
		System.out.println("end of print");*/
}
