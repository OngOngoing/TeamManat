package models;

import javax.persistence.*;
import javax.validation.constraints.*;

import com.avaje.ebean.Expr;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;
import java.util.List;
@Entity
@Table(name = "user_account")
public class User extends Model {
    public final static int GUEST_USER = 1, NORMAL_USER = 2, ADMINISTRATOR = 3;
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
    public Long projectId; // -1 - None Project // Project Owner

    public User(String username, String password, String fname,String lname, int type, Long project){
        this.firstname = fname;
        this.lastname = lname;
        this.username = username;
        this.password =  BCrypt.hashpw(password, BCrypt.gensalt());
        this.idtype = type;
        this.projectId = project;
    }

    private static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);

    public static User authenticate(String username, String password){
        User _login = User.find.where().eq("username", username).findUnique();
        if(_login == null) return null;
        if(BCrypt.checkpw(password, _login.password))
            return _login;
        return null;
    }

    public static User create(String username, String password, String fname,String lname, int type){
        if(User.find.where().eq("username", username).findUnique() == null) {
            User newUser = new User(username, password, fname, lname, type, Long.parseLong("-1"));
            newUser.save();
            return newUser;
        }
        return null;
    }

    public static User findByUserId(Long userId){
        return find.byId(userId);
    }

    public static String findName(Long userId){
        User user = find.byId(userId);
        return user.firstname + " " + user.lastname;
    }

    public static User findByUsername(String username){
        return find.where().eq("username", username).findUnique();
    }

    public static List<User> findAll(){
        return find.all();
    }

    public static List<User> findAllByPage(int page){
        return find.where().findPagingList(10).getPage(--page).getList();
    }

    public static int totalPage(){
        return  find.where().findPagingList(10).getTotalPageCount();
    }
    public static List<User> findByKeyword(String key){
        List < User > listUser = find.setMaxRows(5).where().or(
                Expr.like("firstname", "%" + key + "%"),
                Expr.or(Expr.like("lastname", "%" + key + "%"),
                        Expr.eq("id", key))).findList();
        return listUser;
    }
    public static List<User> findByTeam(Long teamId){
        List<User> members = find.where().eq("projectId", teamId).findList();
        return members;
    }
}
