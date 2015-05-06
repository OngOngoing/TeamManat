package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Groups extends Model {
    @Id
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    private static Model.Finder<Long, Groups> find = new Model.Finder<Long, Groups>(Long.class, Groups.class);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public static Groups create(User user, Project project){
        Groups _tmp = findByUserAndProject(user, project);
        if( _tmp != null){
            return _tmp;
        }else {
            Groups g = new Groups();
            g.user = user;
            g.project = project;
            g.save();
            return g;
        }
    }

    public static Groups findByUserAndProject(User user, Project project){
        return find.where().eq("user", user).eq("project", project).findUnique();
    }

    public static List<Groups> findByProject(Project project){
        List<Groups> members = find.where().eq("project", project).findList();
        return members;
    }
}
