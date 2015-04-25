package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;
import java.util.List;

@Entity
public class Comment extends Model {
    @Id
    private long id;

    @Required
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Required
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    private String comment;

    private static Finder<Long, Comment> find = new Finder<Long, Comment>(Long.class, Comment.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static Comment create(User user, Project project, String comment) {
        Comment _comment = findByUserAndProject(user, project);
        if(_comment != null){
            _comment.comment = comment;
            _comment.update();
            return _comment;
        }
        Comment thisComment = new Comment();
        thisComment.user = user;
        thisComment.project = project;
        thisComment.comment = comment;
        thisComment.save();
        return thisComment;
    }

    public static List<Comment> findAll(){
        return find.all();
    }

    public static List<Comment> findListByProject(Project project){
        return find.where().eq("project",project).findList();
    }

    public static Comment findByUserAndProject(User user, Project project){
        return find.where().eq("user", user).eq("project",project).findUnique();
    }
}
