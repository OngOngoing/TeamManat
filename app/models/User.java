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
    @Column(name="firstname")
    public String firstname;
    @Column(name="lastname")
    public String lastname;
    @Column(name="idtype")
    public int idtype; // 0 - Administrator : 1 - Normal Users
    @Column(name="teamNum")
    public int teamNum;

    public User(String username, String password, String fname,String lname, int type, int team){
        this.firstname = fname;
        this.lastname = lname;
        this.username = username;
        this.password = password;
        this.idtype = type;
        this.teamNum = team;
    }

    public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);

    public static User authenticate(String username, String password){
        return find.where().eq("username", username).eq("password", password).findUnique();
    }

    public static User create(String username, String password, String fname,String lname, int type, int team){
        User newUser = new User(username, password, fname, lname, type, team);
        newUser.save();
        return newUser;
    }
}
