import javax.sound.midi.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/*
 * Parses through all of the midi files contained in the folder, and parses
 * them by generating a note count for each file
 */
public class Extratonality {

    /* key signatures of Each movement of Beethoven's 9 symphonies*/
    public static final Note[] KEY_SIGNATURES = {Note.C,  Note.C,  Note.C,  Note.C,
                                       Note.D,  Note.D,  Note.D,  Note.D,
                                       Note.D_SHARP,  Note.D_SHARP,  Note.D_SHARP,  Note.D_SHARP,
                                       Note.A_SHARP, Note.D_SHARP,  Note.A_SHARP, Note.A_SHARP,
                                       Note.D_SHARP,  Note.D_SHARP,  Note.D_SHARP,  Note.C,
                                       Note.F, Note.A_SHARP,  Note.F,  Note.F, Note.F,
                                       Note.A,  Note.C,  Note.F,  Note.G_SHARP,
                                       Note.F, Note.A_SHARP,  Note.F,  Note.F,
                                       Note.F,  Note.F,  Note.A_SHARP, Note.D, // last movement questionable

    };

    public static final Note[] GENERIC_KEY_SIGNATURES = {Note.C,Note.C,Note.C,Note.C,       // 1
                                      Note.D,Note.D,Note.D,Note.D,        // 2
                                      Note.D_SHARP,Note.D_SHARP,Note.D_SHARP,Note.D_SHARP,       //3
                                      Note.A_SHARP,Note.A_SHARP,Note.A_SHARP,Note.A_SHARP,       //4
                                      Note.D_SHARP,Note.D_SHARP,Note.D_SHARP,Note.D_SHARP,       //5
                                      Note.F,Note.F,Note.F,Note.F,Note.F,       //6
                                      Note.A,Note.A,Note.A,Note.A,              //7
                                      Note.F,Note.F,Note.F,Note.F,              //8
                                      Note.F,Note.F,Note.F,Note.F};             //9

    public static void main(String[] args) {
        File dir = new File("./Midi_Files");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".mid"));

        if (files == null || files.length == 0) {
            System.err.println("No files Found!");
            System.exit(1);
        }

        List<Song> songs = new ArrayList<>();

        //process files into Song object
        for (int i = 0; i<files.length; i++) {
           songs.add(readSongFromFile(files[i], KEY_SIGNATURES[i], GENERIC_KEY_SIGNATURES[i]));
        }

        writeResultsToCSV(songs);
    }

    private static void writeResultsToCSV(List<Song> songs) {
        //export data to CSV file
        try (FileWriter writer = new FileWriter("Midi_results.csv")) {
            writer.append("Title,C,C#,D,D#,E,F,F#,G,G#,A,A#,B,Key,Score_detected," + "Score_generic,Score_specific\n");
            for (Song s : songs) {
                writer.append(s.getName());
                for (Long c : s.getNoteLengths()) {
                    writer.append(',');
                    writer.append(Long.toString(c));
                }
                writer.append(',');
                writer.append(s.getKey_detected().toString());
                for (double myScore : s.getScores()) {
                    writer.append(',');
                    writer.append(Double.toString(myScore));
                }
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * input - midi file
     * parses input file, and creates a Song object which contains note count
     * data, and resulting score based on the note distributions
     */
    public static Song readSongFromFile(File f, Note aKeySignature, Note aGenericKeySignature) {
        final int NOTE_ON = 0x90;
        final int NOTE_OFF = 0x80;
        int[] myNoteCounts = new int[12];
        long[] noteLengths = new long[12];
        HashMap<Integer, Long> sustained = new HashMap<>();
        Sequence sequence = null;

        try {
            sequence = MidiSystem.getSequence(f);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // tally the note pitch counts and note lengths for each track
        for (Track track : sequence.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    int key = sm.getData1();
                    int note = key % 12;
                    if (sm.getCommand() == NOTE_ON) {
                        myNoteCounts[note]++;

                        //then, set hashmap note
                        if(!sustained.containsKey(key)){
                            sustained.put(key, event.getTick());
                        }
                    }
                    //when note ends, update the array and update the hashmap
                    //accordingly
                    else if (sm.getCommand() == NOTE_OFF && sustained.containsKey(key)){
                        long noteDuration = event.getTick() - sustained.get(key);
                        noteLengths[note] += noteDuration;
                        sustained.remove(key);
                    }
                }
            }
        }
        int maxAt = 0; int secondMaxAt = 0;
        for (int i = 0; i < myNoteCounts.length; i++) {
            if(myNoteCounts[i] > myNoteCounts[maxAt]){
                secondMaxAt = maxAt; maxAt = i;
            }
            else if(myNoteCounts[i] > myNoteCounts[secondMaxAt]){
                secondMaxAt = i;
            }
        }

        // this takes the assumption the tonic and dominant are the most common
        // tones heard in each movement
        int key;
        switch((maxAt - secondMaxAt + 12) % 12){
            case 7:
                key = secondMaxAt;
                break;
            case 5:
                key = maxAt;
                break;
            default:
                key = 12;
                break;
        }

        Note myDetectedKey = Note.values()[key];
        return new Song(f.getName(), myNoteCounts, noteLengths, myDetectedKey, aKeySignature, aGenericKeySignature);
    }
}