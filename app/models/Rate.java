package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;
import java.util.List;

@Entity
public class Rate extends Model {
    @Id
    public long id;
    @Required
    public int score;
    public String type;
    public Long userId;
    public Long projectId;
    public Long criteriaId;

    public static Rate create(int score , Long userId , Long criteriaId , Long projectId)
    {
        Rate rate = new Rate();
        rate.score = score;
        rate.userId = userId;
        rate.criteriaId = criteriaId;
        rate.projectId = projectId;
        rate.save();
        return rate;
    }

    private static Finder<Long, Rate> find = new Finder<Long, Rate>(Long.class, Rate.class);

    public static Rate findByUserIdAndProjectIdAndCriteriaId(long userId, Long projectId,Long criteriaId){
        return find.where().eq("userId", userId).eq("projectId",projectId).eq("criteriaId", criteriaId).findUnique();
    }

    public static List<Rate> findAll(){
        return find.all();
    }

    public static List<Rate> findListByUserId(Long userId){
        return find.where().eq("userId", userId).findList();
    }

    public static List<Rate> findListByUserIdAndProjectId(long userId, Long projectId){
        return find.where().eq("userId", userId).eq("projectId",projectId).findList();
    }
}
