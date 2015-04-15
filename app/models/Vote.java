package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;

import java.util.List;

@Entity
public class Vote extends Model {
    @Id
    public long id;
    @Required
    public long criterionId;
    public String type;
    public Long userId;
    public Long projectId;

    public static Vote create(long criterionId , String type , Long userId , Long projectId)
    {
        Vote vote = new Vote();
        vote.criterionId = criterionId;
        vote.type = type;
        vote.userId = userId;
        vote.projectId = projectId;
        vote.save();
        return vote;
    }

    public static Finder<Long, Vote> find = new Finder<Long, Vote>(Long.class, Vote.class);

    public static List<Vote> findByUserId(long userId){
        return find.where().eq("userId", userId).findList();
    }

    public static Vote findByCriterionAndUserId(Long criterionId, Long userId) {
        return find.where().eq("criterionId", criterionId).eq("userId", userId).findUnique();
    }
    public static List<Vote> findAll(){
        return find.all();
    }

}
