package models;

import javax.persistence.*;
import javax.validation.constraints.*;

import play.data.validation.Constraints.Required;
import play.db.ebean.*;
import java.util.List;
@Entity
@Table(name = "user_account")
public class User extends Model {
    public final static int GUEST_USER = 1, NOTMAL_USER = 2, ADMINISTRATOR = 3;
    @Id
    public Long id;

    @Required
    @Column(unique=true)
    public String username;

    @NotNull
    @Required
    public String password;

    @NotNull
    @Required
    public String firstname;
    public String lastname;
    public int idtype; // 0 - Administrator : 1 - Normal Users
    public int projectId; // -1 - None Project // Project Owner

    public User(String username, String password, String fname,String lname, int type, int project){
        this.firstname = fname;
        this.lastname = lname;
        this.username = username;
        this.password = password;
        this.idtype = type;
        this.projectId = project;
    }

    private static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);

    public static User authenticate(String username, String password){
        return User.find.where().eq("username", username).eq("password", password).findUnique();
    }

    public static User create(String username, String password, String fname,String lname, int type){
        if(User.find.where().eq("username", username).findUnique() == null) {
            User newUser = new User(username, password, fname, lname, type, -1);
            newUser.save();
            return newUser;
        }
        return null;
    }

    public static User findByUserId(Long userId){
        return find.byId(userId);
    }

    public static User findByUsername(String username){
        return find.where().eq("username", username).findUnique();
    }

    public static List<User> findAll(){
        return find.all();
    }
}
