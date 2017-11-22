import javax.sound.midi.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
 * Parses through all of the midi files contained in the folder, and parses
 * them by generating a note count for each file
 */
public class Extratonality {

    public static int[] keySigs =     {0,  0,  0,  0,
                                 2,  2,  2,  2,
                                 3,  3,  3,  3,
                                 10, 3,  10, 10,
                                 3,  3,  3,  0,
                                 5, 10,  5,  5, 5,
                                 9,  0,  5,  9,
                                 5, 10,  5,  5,
                                 5,  5,  10, 2, // last movement questionable

    };

    public static int[] genKeySigs = {0,0,0,0, // 1
                                2,2,2,2,       // 2
                                3,3,3,3,       // 3
                                10,10,10,10,    //4
                                3,3,3,3,        //5
                                5,5,5,5,5,      //6
                                9,9,9,9,        //7
                                5,5,5,5,        //8
                                5,5,5,5};       //9

    public static void main(String[] args) throws IOException {
        String[] notes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#",
                "A", "A#", "Error"};


        File dir = new File("./Midi_Files");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".mid"));
        Song[] songs = new Song[files.length];

        //process files into Song object
        for (int i = 0; i<files.length; i++) {
            songs[i] = processFile(files[i], i);
        }

        //export data to CSV file
        FileWriter writer = new FileWriter("Midi_results.csv");
        writer.append("Title,C,C#,D,D#,E,F,F#,G,G#,A,A#,B,Key,Score_detected," +
                "Score_generic,Score_specific\n");
        for(Song s: songs){
            writer.append(s.name);
            for(int c: s.noteCounts){
                writer.append(',');
                writer.append(Integer.toString(c));
            }
            writer.append(',');
            writer.append(Integer.toString(s.key_detected));
            for(float f: s.scores){
                writer.append(',');
                writer.append(Float.toString(f));
            }
            writer.append('\n');
        }
        writer.flush(); writer.close();


    }

    /*
     * input - midi file
     * parses input file, and creates a Song object which contains note count
     * data, and resulting score based on the note distributions
     */
    public static Song processFile(File f, int index) {
        int NOTE_ON = 0x90;
        int NOTE_OFF = 0x80;
        int trackNumber = 0;
        int[] notecounts = new int[12];
        Sequence sequence = null;

        try {
            sequence = MidiSystem.getSequence(f);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int x = 0;
        for (Track track : sequence.getTracks()) {
            trackNumber++;
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                        int note = key % 12;
                        notecounts[note]++;
                        x++;
                    }
                }
            }
        }
        int maxAt = 0; int secondMaxAt = 0;
        for (int i = 0; i < notecounts.length; i++) {
            if(notecounts[i] > notecounts[maxAt]){
                secondMaxAt = maxAt; maxAt = i;
            }
            else if(notecounts[i] > notecounts[secondMaxAt]){
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
                System.out.println((maxAt - secondMaxAt + 12) % 12);
                System.out.println(f.getName());
                key = 12;
                break;
        }

        return new Song(f.getName(), notecounts, key,
                genKeySigs[index], keySigs[index]);
    }
}