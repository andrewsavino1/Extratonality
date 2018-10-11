public class Song {
    public int[] noteCounts;
    public long[] noteLengths;
    public float[] scores;
    public String name;
    public int key_detected, key_generic, key_specific;

    /*
     * Object that contains the name of the piece, array of note counts for the piece,
     * and any scores calculated for the Extratonality of the piece
     */
    public Song(){
        noteCounts = new int[12]; //by default, filled with zeros
        scores =  new float[3];
        name = "";
    }

    protected Song(String name, int[] noteCounts, long[] noteLengths,
                   int key_detected, int key_generic, int key_specific){
        this.name = name;
        this.noteCounts = noteCounts;
        this.key_detected = key_detected;
        this.key_generic = key_generic;
        this.key_specific = key_specific;
        this.noteLengths = noteLengths;
        scores = getScores();
    }

    private float[] getScores(){
        return new float[] {calcScore(key_detected, false),
                calcScore(key_generic, false), calcScore(key_specific, false),
                calcScore(key_detected, true), calcScore(key_generic, true),
                calcScore(key_specific, true)};
    }

    private float calcScore(int aKey, boolean aLength){
        if(aKey == 12) return 0;  // this happens when program is not able to
                                 // automatically detect a key signature
        if(aLength) {
            long keyNotes = noteCounts[(0 + aKey) % 12] + noteCounts[(2 + aKey) % 12] +
                    noteCounts[(4 + aKey) % 12] + noteCounts[(5 + aKey) % 12] +
                    noteCounts[(7 + aKey) % 12] + noteCounts[(9 + aKey) % 12] +
                    noteCounts[(11 + aKey) % 12];
            long nonKeyNotes = noteCounts[(1 + aKey) % 12] + noteCounts[(3 + aKey) % 12] +
                    noteCounts[(6 + aKey) % 12] + noteCounts[(8 + aKey) % 12] +
                    noteCounts[(10 + aKey) % 12];
            return ((float) nonKeyNotes) / keyNotes;
        }
        else {
            long keyNotesL = noteLengths[(0 + aKey) % 12] + noteLengths[(2 + aKey) % 12] +
                    noteLengths[(4 + aKey) % 12] + noteLengths[(5 + aKey) % 12] +
                    noteLengths[(7 + aKey) % 12] + noteLengths[(9 + aKey) % 12] +
                    noteLengths[(11 + aKey) % 12];
            long nonKeyNotesL = noteLengths[(1 + aKey) % 12] + noteLengths[(3 + aKey) % 12] +
                    noteLengths[(6 + aKey) % 12] + noteLengths[(8 + aKey) % 12] +
                    noteLengths[(10 + aKey) % 12];
            return ((float) nonKeyNotesL) / keyNotesL;
        }

    }
}
