package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;

@Entity
@Table(name = "user_account")
public class User extends Model {
    @Id
    public Long id;

    @Column(name="username")
    public String username;
    @Column(name="password")
    public String password;
    @Column(name="fullname")
    public String fullname;
    @Column(name="type")
    public int type; // 0 - Normal member : 1 - Administrator
    @Column(name="teamNum")
    public int teamNum;

    public User(String username, String password, String name, int type, int team){
        this.fullname = name;
        this.username = username;
        this.password = password;
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
