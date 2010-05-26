
import db2lab23.ConsoleMenu;
import db2lab23.GameManager;
import db2lab23.PlayerManager;
import db2lab23.TeamManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException
    {
        Connection connection = null;

        try {
            Class.forName("COM.ibm.db2.jdbc.app.DB2Driver");
            connection = DriverManager.getConnection("jdbc:db2:Biblio", "stud", "stud");

            new ConsoleMenu(new PlayerManager(connection), new TeamManager(connection),
                    new GameManager(connection)).run();
        } catch (Exception e){
           System.out.println(e);
        } finally {
            if (connection != null)
                connection.close();
        }
    }

}
