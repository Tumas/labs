package db2lab23;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {
    private Connection connection;

    public ConnectionManager(Connection connection){
        setConnection(connection);
    }

    public ConnectionManager(){}

    public void setConnection(Connection connection){
        this.connection = connection;
    }

    public Connection getConnection(){
        return connection;
    }

    public void setAutoCommit(boolean value) throws SQLException
    {
        getConnection().setAutoCommit(value);
    }

    public void commit() throws SQLException {
         getConnection().commit();
    }

    public void rollback() throws SQLException {
        getConnection().rollback();
    }
}