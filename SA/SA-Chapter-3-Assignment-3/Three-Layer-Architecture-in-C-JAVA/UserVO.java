package three_layers;

public class UserVO {
    private int _idUser;
    private String _firstname;
    private String _lastname;
    private String _email;
    public UserVO(){

    }

    public void set_idUser(int _idUser) {
        this._idUser = _idUser;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public void set_firstname(String _firstname) {
        this._firstname = _firstname;
    }

    public void set_lastname(String _lastname) {
        this._lastname = _lastname;
    }

    public int get_idUser() {
        return _idUser;
    }

    public String get_email() {
        return _email;
    }

    public String get_firstname() {
        return _firstname;
    }

    public String get_lastname() {
        return _lastname;
    }
}
