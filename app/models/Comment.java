package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;
import java.util.List;

@Entity
public class Comment extends Model {
    @Id
    public long id;
    @Required
    public Long userId;
    public Long projectId;
    public String comment;

    public static Comment create( Long userId , Long projectId, String comment)
    {
        Comment _comment = findByUserIdAndProjectId(userId, projectId);
        if(_comment != null){
            _comment.comment = comment;
            _comment.update();
        }else {
            Comment thisComment = new Comment();
            thisComment.userId = userId;
            thisComment.projectId = projectId;
            thisComment.comment = comment;
            thisComment.save();
        }
        return findByUserIdAndProjectId(userId, projectId);
    }

    private static Finder<Long, Comment> find = new Finder<Long, Comment>(Long.class, Comment.class);

    public static List<Comment> findAll(){
        return find.all();
    }

    public static List<Comment> findListByProjectId(Long projectId){
        return find.where().eq("projectId",projectId).findList();
    }

    public static Comment findByUserIdAndProjectId(long userId, long projectId){
        return find.where().eq("userId", userId).eq("projectId",projectId).findUnique();
    }
}
