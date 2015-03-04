package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import org.mindrot.jbcrypt.BCrypt;
import play.db.ebean.*;

@Entity
public class Vote extends Model {
    @Id
    public long id;
    @Required
    public int score;
    public String type;
    public Long userId;
    public Long projectId;

    public static Vote create(int score , String type , Long userId , Long projectId)
    {
        Vote vote = new Vote();
        vote.score = score;
        vote.type = type;
        vote.userId = userId;
        vote.projectId = projectId;
        vote.save();
        return vote;
    }
    public static Finder<Long, Vote> find = new Finder<Long, Vote>(Long.class, Vote.class);
}
