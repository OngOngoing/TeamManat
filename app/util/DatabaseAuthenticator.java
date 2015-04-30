package util;

import models.User;
import org.mindrot.jbcrypt.BCrypt;

public class DatabaseAuthenticator extends Authenticator {

    private static DatabaseAuthenticator dba = null;

    public static DatabaseAuthenticator newInstance() {
        if(dba == null) {
            dba = new DatabaseAuthenticator();
        }
        return dba;
    }


    public User authenticate(String username, String password){
        User _login = User.findByUsername(username);
        if(_login == null) return null;
        if(BCrypt.checkpw(password, _login.getPassword()))
            return _login;
        return null;
    }
}
