import java.io.File;
import java.util.Scanner;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MidiParse {

    ///////////////////////////////////////////////////
    //  PARSING VIA THE STANDARD JAVA SOUND LIBRARY
    //
    //  This code is largely based off of code
    //  written by StackOverflow user Sami Koivu.
    //
    ///////////////////////////////////////////////////


    public static final int NOTE_ON = 0x90;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    private static String stringOfNotes1 = "";
    private static String stringOfNotes2 = "";

    public static void main(String[] args) throws Exception {
        // User input for the MIDI files.
        System.out.print("Please enter the name of the expert MIDI file: ");
        Scanner keyboard = new Scanner(System.in);
        String filename1 = keyboard.nextLine();
        System.out.print("Please enter the name of the student MIDI file: ");
        String filename2 = keyboard.nextLine();
        Sequence sequence1 = MidiSystem.getSequence(new File(filename1));
        Sequence sequence2 = MidiSystem.getSequence(new File(filename2));

        // The getNotes method is called to get the notes from the
        // MIDI files and put them all into a single string for
        // each song.
        stringOfNotes1 = getNotes(sequence1, stringOfNotes1);
        stringOfNotes2 = getNotes(sequence2, stringOfNotes2);

        // Prints the strings of notes.
        System.out.println(stringOfNotes1);
        System.out.println(stringOfNotes2);


    }

    public static String getNotes(Sequence sequence, String noteString) {
        int trackNumber = 0;

        for (Track track : sequence.getTracks()) {
            trackNumber++;
            //System.out.println("Track " + trackNumber + ": size = " + track.size());

            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();

                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;

                    // Getting data from played notes
                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        // The velocity is the force used to press a key on the keyboard.
                        // We are assuming that anything below velocity 20 is a mistake
                        // on the user's end, thus we will filter those out.
                        if (velocity > 20) {
                            // Reassigning sharp notes to other letters in order to make the
                            // comparison algorithm function correctly.
                            if(noteName.equals("C#"))
                                noteName = "H";
                            if(noteName.equals("D#"))
                                noteName = "I";
                            if(noteName.equals("F#"))
                                noteName = "J";
                            if(noteName.equals("G#"))
                                noteName = "K";
                            if(noteName.equals("A#"))
                                noteName = "L";
                            //System.out.println("@" + event.getTick() + " " + "Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                            // Adding to the string of notes that represents the whole song
                            noteString = noteString + noteName;
                        }
                    }
                }
            }
        }
        return noteString;
    }
}