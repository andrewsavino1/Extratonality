import java.util.Arrays;
import java.util.List;

public class Song {
    private int[] noteCounts;
    private long[] noteLengths;
    private String name;
    private Note key_detected, key_generic, key_specific;
    private double theDetectedScoreCount, theGenericScoreCount, theSpecificScoreCount;
    private double theDetectedScoreLength, theGenericScoreLength, theSpecificScoreLength;

    /*
     * Object that contains the name of the piece, array of note counts for the piece,
     * and any scores calculated for the Extratonality of the piece
     */
    public Song(){
        noteCounts = new int[12]; //by default, filled with zeros
        name = "";
    }

    public Song(String name, int[] noteCounts, long[] noteLengths,
                Note key_detected, Note key_generic, Note key_specific){
        this.name = name;
        this.noteCounts = noteCounts;
        this.key_detected = key_detected;
        this.key_generic = key_generic;
        this.key_specific = key_specific;
        this.noteLengths = noteLengths;
        calculateScores();
    }

    Note getKey_detected() {
        return key_detected;
    }

    long[] getNoteLengths() {
        return noteLengths;
    }

    String getName() {
        return name;
    }

    List<Double> getScores() {
        return Arrays.asList(theDetectedScoreCount, theGenericScoreCount, theSpecificScoreCount, theDetectedScoreLength,
                theGenericScoreLength, theSpecificScoreLength);
    }

    private void calculateScores(){
        theDetectedScoreCount = calcScore(key_detected, false);
        theGenericScoreCount = calcScore(key_generic, false);
        theSpecificScoreCount = calcScore(key_specific, false);
        theDetectedScoreLength = calcScore(key_detected, true);
        theGenericScoreLength = calcScore(key_generic, true);
        theSpecificScoreCount = calcScore(key_specific, true);
    }

    private double calcScore(Note aKey, boolean aLength){
        if(aKey.ordinal() == 12) return 0;  // this happens when program is not able to
                                              // automatically detect a key signature
        int myKeyVal = aKey.ordinal();
        List<Integer> myKeyIndexes = Arrays.asList(0, 2, 4, 5, 7, 9, 11); //C,D,E,F,G,A,B, for example
        List<Integer> myNoneKeyIndexes = Arrays.asList(1, 3, 6, 8, 10);   // C#, D#, F#, G#, A#, for example

        if(aLength) {
            int keyNotes = myKeyIndexes.stream().mapToInt(aKeyIndex -> noteCounts[(aKeyIndex + myKeyVal) % 12]).sum();
            int nonKeyNotes = myNoneKeyIndexes.stream()
                    .mapToInt(aNonKeyIndex -> noteCounts[(aNonKeyIndex + myKeyVal) % 12]).sum();
            return ((double) nonKeyNotes) / keyNotes;
        }
        else {
            double keyNotesL = myKeyIndexes.stream()
                    .mapToDouble(aKeyIndex -> noteLengths[(aKeyIndex + myKeyVal) % 12]).sum();
            double nonKeyNotesL = myNoneKeyIndexes.stream()
                    .mapToDouble(aNonKeyIndex -> noteLengths[(aNonKeyIndex + myKeyVal) % 12]).sum();
            return nonKeyNotesL / keyNotesL;
        }

    }
}
