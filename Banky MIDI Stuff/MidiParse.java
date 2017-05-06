////////////////////////////////////
//
//  MIDI SONG COMPARISON PROGRAM
//
//  Joseph Sasis
//  Banky Kitchpanich
//
////////////////////////////////////

import java.io.*;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MidiParse extends LevenshteinEvaluation {

    ///////////////////////////////////////////////////
    //  PARSING VIA THE STANDARD JAVA SOUND LIBRARY
    //
    //  This code is largely based off of code
    //  written by StackOverflow user Sami Koivu.
    //
    ///////////////////////////////////////////////////

    public static final int NOTE_ON = 0x90;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    private static String expertStr = "";
    private static String studentStr = "";
    private static char exitFlag = 'y';
    private static String filename1 = "";
    private static String filename2 = "";

    public static void main(String[] args) throws Exception {

        while(exitFlag == 'y' || exitFlag == 'Y') {
        Scanner keyboard = new Scanner(System.in);
            boolean done = false;

            do{
                try {
                    System.out.print("Please enter the name of the expert MIDI file: ");

                    Scanner in = new Scanner(System.in);
                    String tempFilename = in.nextLine();
                    File file1 = new File(tempFilename);
                    Scanner scan =  new Scanner(file1);

                    setFilename1(tempFilename);

                    done = true;
                } catch (FileNotFoundException e) {
                    System.out.println("File not found, please try again.");
                }
            }while(!done);

            do{
                try {
                    done = false;

                    System.out.print("Please enter the name of the student MIDI file: ");

                    Scanner in = new Scanner(System.in);
                    String tempFilename = in.nextLine();
                    File file1 = new File(tempFilename);
                    Scanner scan =  new Scanner(file1);

                    setFilename2(tempFilename);

                    done = true;
                } catch (FileNotFoundException e) {
                    System.out.println("File not found, please try again.");
                }
            }while(!done);

            // Generates a sequence for each MIDI file, where the
            // note data will be collected from.

            Sequence expertSeq = MidiSystem.getSequence(new File(filename1));
            Sequence studentSeq = MidiSystem.getSequence(new File(filename2));

            // The getNotes method is called to get the notes from the
            // MIDI files and put them all into a single string for
            // each song.

            expertStr = "";
            studentStr = "";

            expertStr = getNotes(expertSeq, expertStr);
            studentStr = getNotes(studentSeq, studentStr);

            // Uncomment this to print the strings of notes.
            System.out.println("Notes from expert file:  " + expertStr);
            System.out.println("Notes from student file: " + studentStr);

            LevenshteinEvaluation scoreEvaluator = new LevenshteinEvaluation();
            System.out.println("Total score: " + scoreEvaluator.levenshteinDistance(expertStr, studentStr));

            /*

            // This is the modified Levenshtein algorithm that display the global alignment of the strings, but does
            // not correctly count the number of substitutions made.  Uncomment this block to use this version.

            scoreEvaluator.align(expertStr, studentStr);
            System.out.println("Global alignment: ");
            System.out.println(scoreEvaluator.getGlobalString1());
            System.out.println(scoreEvaluator.getGlobalString2());
            System.out.println("Total score: " + scoreEvaluator.getAlignmentScore());

            */

            System.out.println("Would you like to compare another set of files?  Type 'y' to continue.  Otherwise, type 'n'.");
            exitFlag = keyboard.next().charAt(0);
        }
        System.exit(0);
    }

    public static void setFilename1(String newName)
    {
        filename1 = newName;
    }

    public static void setFilename2(String newName)
    {
        filename2 = newName;
    }

    public static String getNotes(Sequence sequence, String noteString) {
        int trackNumber = 0;

        for (Track track : sequence.getTracks()) {
            trackNumber++;

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