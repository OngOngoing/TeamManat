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
    private Long id;

    @NotNull
    @Required
    @Column(unique=true)
    private String username;

    @NotNull
    @Required
    private String password;

    @NotNull
    @Required
    private String firstname;
    private String lastname;
    private int idtype;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
    private List<Comment> comments;

    private static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getIdtype() {
        return idtype;
    }

    public void setIdtype(int idtype) {
        this.idtype = idtype;
    }

    public Project getProject() {
        if(project == null){
            Project _project = new Project();
            _project.setProjectName("Mock");
            _project.setId(Long.parseLong("-1"));
            return _project;
        }
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public static User authenticate(String username, String password){
        User _login = User.find.where().eq("username", username).findUnique();
        if(_login == null) return null;
        if(BCrypt.checkpw(password, _login.password))
            return _login;
        return null;
    }

    public static User create(String username, String password, String fname,String lname, int type){
        if(User.find.where().eq("username", username).findUnique() == null) {
            User newUser = new User();
            newUser.username = username;
            newUser.firstname = fname;
            newUser.lastname = lname;
            newUser.password = BCrypt.hashpw(password, BCrypt.gensalt());
            newUser.idtype = type;
            newUser.project = null;
            newUser.save();
            return newUser;
        }
        return null;
    }

    public static User findByUserId(Long id){
        return find.byId(id);
    }

    public static String findName(User user){
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
                        Expr.or(Expr.like("username", "%" + key + "%"),
                                Expr.eq("id", key)))).findList();
        return listUser;
    }
    public static List<User> findByProject(Project project){
        List<User> members = find.where().eq("project", project).findList();
        return members;
    }
}
