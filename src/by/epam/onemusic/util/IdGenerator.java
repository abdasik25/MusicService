package by.epam.onemusic.util;

public class IdGenerator {

    private static long idAuthorCounter = 1;
    private static long idSongCounter = 1;


    private IdGenerator() {
    }

    public static long generateIdAuthorID() {
        return idAuthorCounter++;
    }
    public static long generateSongId(){return  idSongCounter++;}

}
