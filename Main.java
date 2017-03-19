import java.io.File;
import java.util.Scanner;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class Main {

    ///////////////////////////////////////////////////
    //  PARSING VIA THE STANDARD JAVA SOUND LIBRARY
    ///////////////////////////////////////////////////


    public static final int NOTE_ON = 0x90;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    public static String stringOfNotes1 = "";
    public static String stringOfNotes2 = "";

    public static void main(String[] args) throws Exception {
        System.out.print("Please enter the name of the expert MIDI file: ");
        Scanner keyboard = new Scanner(System.in);
        String filename1 = keyboard.nextLine();
        System.out.print("Please enter the name of the student MIDI file: ");
        String filename2 = keyboard.nextLine();
        Sequence sequence1 = MidiSystem.getSequence(new File(filename1));
        Sequence sequence2 = MidiSystem.getSequence(new File(filename2));

        getNotes(sequence1, stringOfNotes1);
        getNotes(sequence2, stringOfNotes2);

        System.out.println(stringOfNotes1);
        System.out.println(stringOfNotes2);
        System.out.println(stringOfNotes2);
        System.out.println(stringOfNotes2);
        System.out.println(stringOfNotes2);

    }

    public static void getNotes(Sequence sequence, String noteString) {
        int trackNumber = 0;

        for (Track track : sequence.getTracks()) {
            trackNumber++;
            System.out.println("Track " + trackNumber + ": size = " + track.size());
            System.out.println();

            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();

                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;

                    //Getting data from played notes
                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        if (velocity != 0 && velocity > 20) {
                            System.out.println("@" + event.getTick() + " " + "Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                            //Adding to the string of notes that represents the whole song
                            noteString = noteString + noteName;
                        }
                    }
                }
            }
        }
        System.out.println(noteString);
    }
}