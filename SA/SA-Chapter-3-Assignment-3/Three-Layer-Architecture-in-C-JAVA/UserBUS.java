package three_layers;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserBUS {
    private UserDAO _userDAO=null;
    public UserBUS(){
        _userDAO=new UserDAO();
    }
    public UserVO getUserEmailByName(String name){
        UserVO userVO=new UserVO();
        try  {
            ResultSet resultSet = _userDAO.searchByname(name);
            while (resultSet.next()){
                userVO.set_idUser(Integer.parseInt(resultSet.getString("t01_id")));
                userVO.set_firstname(resultSet.getString("t01_firstname"));
                userVO.set_lastname(resultSet.getString("t01_lastname"));
                userVO.set_email(resultSet.getString("t01_email"));
                return userVO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public UserVO getUserById(String _id){
        UserVO userVO=new UserVO();
        try  {
            ResultSet resultSet = _userDAO.searchById(_id);
            while (resultSet.next()){
                userVO.set_idUser(Integer.parseInt(resultSet.getString("t01_id")));
                userVO.set_firstname(resultSet.getString("t01_firstname"));
                userVO.set_lastname(resultSet.getString("t01_lastname"));
                userVO.set_email(resultSet.getString("t01_email"));
                return userVO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
