package db2lab23;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class PlayerManager extends ConnectionManager
{
    private String SQL_PLAIN_PLAYER = "SELECT * FROM tuba7791.Zaidejas as R WHERE " +
            " R.Vardas = ? and R.Pavarde = ?";
    private String SQL_PLAIN_PLAYER_AK = "SELECT * FROM tuba7791.Zaidejas as Z WHERE " +
            "Z.AK = ?";
    
    private String SQL_FULL_PLAYER = "SELECT Vardas, Pavarde, Pavadinimas, " +
            "DECIMAL(AVG(DECIMAL(Taskai, 5, 2)), 4, 2) Rezultatyvumas " +
            "FROM tuba7791.Komanda as K, tuba7791.Zaidejas as Z, tuba7791.Statistika as S " +
            "WHERE Z.AK = S.AK and Z.Komanda = K.Nr and Z.Vardas = ? and Z.Pavarde = ? " +
            "GROUP BY Vardas, Pavarde, Pavadinimas";

    private String SQL_DELETE_PLAYER = "DELETE FROM tuba7791.Zaidejas as Z WHERE Z.AK = ?";

    private String SQL_UPDATE_PLAYER = "UPDATE tuba7791.Zaidejas as Z SET " +
            "Z.Nr = ?, Z.Vardas = ?, Z.Pavarde = ?, Z.Ugis = ?, Z.Svoris = ?, Z.Komanda = ?" +
            " WHERE Z.Ak = ?";

    private String SQL_ALL_PLAYERS = "SELECT AK, Vardas, Pavarde, Pavadinimas FROM tuba7791.Zaidejas as Z," +
            " tuba7791.Komanda as K WHERE Z.Komanda = K.Nr";

    private String SQL_TEAM_PLAYERS = "SELECT AK, Vardas, Pavarde, Nr FROM tuba7791.Zaidejas as Z" +
            " WHERE Z.Komanda = ?";

    public PlayerManager(Connection connection)
    {
        super(connection);
    }

    public ResultSet getPlainPlayer(String name, String surname) throws SQLException
    {
        PreparedStatement st = getConnection().prepareStatement(SQL_PLAIN_PLAYER);
        st.setString(1, name);
        st.setString(2, surname);
        return st.executeQuery();
    }

     public ResultSet getPlainPlayerAK(String ak) throws SQLException
    {
        PreparedStatement st = getConnection().prepareStatement(SQL_PLAIN_PLAYER_AK);
        st.setString(1, ak);
        return st.executeQuery();
    }

    public ResultSet getFullPlayer(String name, String surname) throws SQLException
    {
        PreparedStatement st = getConnection().prepareStatement(SQL_FULL_PLAYER);
        st.setString(1, name);
        st.setString(2, surname);
        return st.executeQuery();
    }
    
    public ResultSet getAllPlayers() throws SQLException
    {
        PreparedStatement st = getConnection().prepareStatement(SQL_ALL_PLAYERS);
        return st.executeQuery();
    }

    public ResultSet getPlayersByTeam(int teamNumber) throws SQLException
    {
        PreparedStatement st = getConnection().prepareStatement(SQL_TEAM_PLAYERS);
        st.setInt(1, teamNumber);
        return st.executeQuery();
    }

    //Returns the row count
    public int deletePlayer(String ak) throws SQLException
    {
        PreparedStatement stmt = getConnection().prepareStatement(SQL_DELETE_PLAYER);
        stmt.setString(1, ak);
        return stmt.executeUpdate();
    }

    public int updatePlayer(String ak, String name, String surname, int number, float height, int weight,
                             int teamNumber) throws SQLException
    {
        PreparedStatement stmt = getConnection().prepareStatement(SQL_UPDATE_PLAYER);
        stmt.setInt(1, number);
        stmt.setString(2, name);
        stmt.setString(3, surname);
        stmt.setFloat(4, height);
        stmt.setInt(5, weight);
        stmt.setInt(6, teamNumber);
        stmt.setString(7, ak);
        return stmt.executeUpdate();
    }
}