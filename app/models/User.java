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
    public int type,teamNum;

    public User(String username, String password, String name, int type, int team){
        this.name = name;
        this.username = username;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        this.type = type;
        this.teamNum = team;
    }

    public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);

    public static User authenticate(String username, String password){
        return find.where().eq("username", username).eq("password", password).findUnique();
    }

    public static User create(String username, String password, String name, int type, int team){
        User newUser = new User(username, password, name, type, team);
        newUser.save();
        return newUser;
    }
}
