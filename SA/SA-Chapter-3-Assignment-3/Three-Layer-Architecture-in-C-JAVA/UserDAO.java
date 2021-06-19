package three_layers;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private dbConnection conn=null;
    public UserDAO(){
        conn=new dbConnection(null,null,null);
    }
    public ResultSet searchByname(String _username) throws SQLException {
        String query = "select * from [t01_user] \n" +
                "where t01_firstname like "+ _username+
                "or t01_lastname like " + _username;
        ResultSet resultSet = conn.executeSelectQuery(query);
        return resultSet;
    }
    public ResultSet searchById(String _id) throws SQLException {
        String query="select * from [t01_id] where to1_id="+_id;
        ResultSet resultSet = conn.executeSelectQuery(query);
        return resultSet;
    }
}
