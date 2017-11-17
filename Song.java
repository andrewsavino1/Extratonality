public class Song {
    public int[] notecounts;
    public int score;
    public String name;


    /*
     * Object that contains the name of the piece, array of note counts for the piece,
     * and any scores calculated for the Extratonality of the piece
     */
    public Song(){
        notecounts = new int[12]; //by default, filled with zeros
        score = 0;
        name = "";
    }
    public Song(String name, int[] notecounts){
        this.name = name;
        this.notecounts = notecounts;
        score = getScore();

    }

    private int getScore(){
        //TODO
        return 0;
    }
}
