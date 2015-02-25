package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import org.mindrot.jbcrypt.BCrypt;
import play.db.ebean.*;

@Entity
public class User extends Model {
    @Id
    public Long id;

    @Required
    public String username;
    public String password;

    public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);

    public static User create(String username, String password){
        User user = new User();
        user.username = username;
        user.password = BCrypt.hashpw(password, BCrypt.gensalt());
        user.save();
        return user;
    }

    public static User login(String username, String password){
        User user = User.find.where().eq("username", username).findUnique();
        if(user != null && user.password == BCrypt.hashpw(password, BCrypt.gensalt())){
            return user;
        }else{
            return null;
        }
    }
}
