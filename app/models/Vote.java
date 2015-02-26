package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import org.mindrot.jbcrypt.BCrypt;
import play.db.ebean.*;

/**
 * Created by Mistral on 2/26/15 AD.
 */
public class Vote extends Model {
    @Id
    public long id;
    @Required
    public int score;
    public String type;
    public String userId;
    public String voteId;

    public static Vote create(int score , String type , String userId , String voteId)
    {
        Vote vote = new Vote();
        vote.score = score;
        vote.type = type;
        vote.userId = userId;
        vote.voteId = voteId;
        vote.save();
        return vote;
    }
}
