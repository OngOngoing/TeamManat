package models;

import javax.persistence.*;
import javax.validation.constraints.*;

import play.data.validation.Constraints.Required;
import play.db.ebean.*;

@Entity
@Table(name = "user_account")
public class User extends Model {
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

    public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);

    public static User authenticate(String username, String password){
        return User.find.where().eq("username", username).eq("password", password).findUnique();
    }

    public static User create(String username, String password, String fname,String lname, int type, int projectId){
        User newUser = new User(username, password, fname, lname, type, projectId);
        newUser.save();
        return newUser;
    }
}
