package fatbeats.main;

import javax.sound.midi.*;

class MusicBox {

	private final static int[] INSTRUMENTS = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

	private Sequencer sequencer;
	private Sequence sequence;
	private Track track;

	MusicBox() {
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequence = new Sequence(Sequence.PPQ, 4);
			track = sequence.createTrack();
			sequencer.setTempoInBPM(120);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static MidiEvent makeMidiEvent(int comd, int chan, int one, int two, int tick) {
		MidiEvent event = null;
		try {
			ShortMessage a = new ShortMessage();
			a.setMessage(comd, chan, one, two);
			event = new MidiEvent(a, tick);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return event;
	}

	Sequencer getSequencer() {
		return sequencer;
	}

	void buildTrackAndPlay(boolean[] pattern) {
		int[] trackList = null;

		sequence.deleteTrack(track);
		track = sequence.createTrack();

		for (int i = 0; i < 16; i++) {
			trackList = new int[16];
			int key = INSTRUMENTS[i];

			for (int k = 0; k < 16; k++) {
				if (pattern[k + 16 * i]) {
					trackList[k] = key;
				} else {
					trackList[k] = 0;
				}
			}

			addNotesToTrack(trackList);
			track.add(makeMidiEvent(ShortMessage.CONTROL_CHANGE, 1, 127, 0, 16));
		}

		track.add(makeMidiEvent(ShortMessage.PROGRAM_CHANGE, 9, 1, 0, 15));
		try {
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
			sequencer.start();
			sequencer.setTempoInBPM(120);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void speedUp() {
		float tempoFactor = sequencer.getTempoFactor();
		sequencer.setTempoFactor((float) (tempoFactor * 1.03));
	}

	void slowDown() {
		float tempoFactor = sequencer.getTempoFactor();
		sequencer.setTempoFactor((float) (tempoFactor * 0.97));
	}

	String readTempo() {
		if (sequencer == null) {
			return "0";
		}
		int inttempo = (int) (sequencer.getTempoInBPM() * sequencer.getTempoFactor());
		return "" + inttempo;
	}

	private void addNotesToTrack(int[] list) {
		for (int i = 0; i < 16; i++) {
			int key = list[i];
			if (key != 0) {
				track.add(makeMidiEvent(ShortMessage.NOTE_ON, 9, key, 100, i));
				track.add(makeMidiEvent(ShortMessage.NOTE_OFF, 9, key, 100, i + 1));
			}
		}
	}
}