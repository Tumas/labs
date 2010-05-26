package db2lab23;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameManager extends ConnectionManager
{
    private String SQL_INSERT_PLAYER_GAME_STATS = "INSERT into tuba7791.Statistika " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private String SQL_INSERT_GAMES = "INSERT into tuba7791.Rungtynes (Komanda, Varzovas, Imesta, Praleista, Data)" +
            " VALUES(?, ?, ?, ?, ?)";

    private String SQL_GUESS_GAME_ID = "SELECT Nr from tuba7791.Rungtynes as R" +
            " WHERE R.Komanda = ? AND R.Varzovas = ? AND R.Imesta = ? AND R.Praleista = ? AND R.Data = ?";

    public GameManager(Connection con)
    {
        super(con);
    }

    //Grazina sugeneruota rungtyniu id
    public int insertGame(int komanda, int varzovas, int imesta, int praleista, String data) throws SQLException
    {
        PreparedStatement st = getConnection().prepareStatement(SQL_INSERT_GAMES);
        st.setInt(1, komanda);
        st.setInt(2, varzovas);
        st.setInt(3, imesta);
        st.setInt(4, praleista);
        st.setString(5, data);
        return st.executeUpdate();
    }
    public ResultSet guessGameNumber(int komanda, int varzovas, int imesta, int praleista, String data) throws SQLException
    {
        PreparedStatement st = getConnection().prepareStatement(SQL_GUESS_GAME_ID);
        st.setInt(1, komanda);
        st.setInt(2, varzovas);
        st.setInt(3, imesta);
        st.setInt(4, praleista);
        st.setString(5, data);
        return st.executeQuery();
    }

    public int insertPlayerGameStats(String ak, int gameNumber, short rebounds, short assists, short fouls,
            short turnOvers, short blocks, short steals, short pts) throws SQLException
    {
        PreparedStatement st = getConnection().prepareStatement(SQL_INSERT_PLAYER_GAME_STATS);
        st.setString(1, ak);
        st.setInt(2, gameNumber);
        st.setShort(3, rebounds);
        st.setShort(4, assists);
        st.setShort(5, fouls);
        st.setShort(6, turnOvers);
        st.setShort(7, blocks);
        st.setShort(8, steals);
        st.setShort(9, pts);
        return st.executeUpdate();
    }
}
