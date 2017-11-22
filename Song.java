public class Song {
    public int[] noteCounts;
    public float score;
    public String name;
    public int key;
    public boolean isMajor;

    /*
     * Object that contains the name of the piece, array of note counts for the piece,
     * and any scores calculated for the Extratonality of the piece
     */
    public Song(){
        noteCounts = new int[12]; //by default, filled with zeros
        score = 0;
        name = "";
    }
    protected Song(String name, int[] noteCounts, int key){
        this.name = name;
        this.noteCounts = noteCounts;
        this.key = key;
        score = getScore();

    }

    private float getScore(){
        if(key == 12) return 0;
        int keyNotes = noteCounts[(0 + key) % 12] + noteCounts[(2 + key) % 12] +
                noteCounts[(4 + key) % 12] + noteCounts[(5 + key) % 12] +
                noteCounts[(7 + key) % 12] + noteCounts[(9 + key) % 12] +
                noteCounts[(11 + key) % 12];
        int nonKeyNotes = noteCounts[(1 + key) % 12] + noteCounts[(3 + key) % 12] +
                noteCounts[(6 + key) % 12] + noteCounts[(8 + key) % 12] +
                noteCounts[(10 + key) % 12];
        return ((float) nonKeyNotes) / keyNotes;

    }
}
