package db2lab23;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types.*;

public class TeamManager extends ConnectionManager
{
    private String SQL_ALL_TEAMS = "SELECT Nr, Pavadinimas FROM tuba7791.Komanda";
    private String SQL_TEAM_NAME = "SELECT Pavadinimas FROM tuba7791.Komanda as K " +
            "WHERE K.Nr = ?";

    public TeamManager(Connection connection)
    {
        super(connection);
    }

    public ResultSet getAllTeams() throws SQLException
    {
        PreparedStatement st = getConnection().prepareStatement(SQL_ALL_TEAMS);
        return st.executeQuery();
    }

    public ResultSet getTeamName(int number) throws SQLException
    {
        PreparedStatement st = getConnection().prepareStatement(SQL_TEAM_NAME);
        st.setInt(1, number);
        return st.executeQuery();
    }

}