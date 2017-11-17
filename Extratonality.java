import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

/*
 * Parses through all of the midi files contained in the folder, and parses
 * them by generating a note count for each file
 */
public class Extratonality {
    public static void main(String[] args) {
        File dir = new File("./Midi_Files");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".mid"));

        for (File f : files) {
            //TODO: parse each file and make them into songs
        }
    }

    public Song processFile(File f) {
        // TODO
        // in here I'll more or less copy the ReadFile stuff and refactor it
        // maybe make readFile return an array, and I'll use that to make the song?
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
                //   System.out.print("@" + event.getTick() + " ");
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    //    System.out.print("Channel: " + sm.getChannel() + " ");
                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        //String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        //    System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        notecounts[note]++;
                        x++;
                    }
                }
            }
        }
        Song song = new Song(f.getName(), notecounts);
        return song;
    }
}