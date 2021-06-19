package three_layers;

import java.sql.*;

public class dbConnection {
    private static final String JDBC_DRIVER="com.mysql.jdbc.Driver";
    private Connection conn=null;
    private Statement stmt=null;
    public dbConnection(String db_url,String user,String password){
        try {
            Class.forName(JDBC_DRIVER);
            conn= DriverManager.getConnection(db_url,user,password);
            stmt=conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ResultSet executeSelectQuery(String _query) throws SQLException {
        ResultSet resultSet = stmt.executeQuery(_query);
        return resultSet;
    }
    public boolean executeInsertQuery(String _query){
        try {
            stmt.execute(_query);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
