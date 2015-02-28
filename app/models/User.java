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
    public String username,password,name;
    public int type;


    public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);

    public static User create(String name ,String username, String password , int type ){
        User user = new User();
        user.name = name;
        user.username = username;
        user.password = BCrypt.hashpw(password, BCrypt.gensalt());
        user.type = type;
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
