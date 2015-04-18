package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Rate extends Model {
    @Id
    public long id;
    @Required
    public int score;
    public Long userId;
    public Long projectId;
    public Long criteriaId;

    public static Rate create(int score , Long userId , Long criteriaId , Long projectId)
    {

        if(findByUserIdAndProjectIdAndCriteriaId(userId,projectId,criteriaId) == null) {
            Rate rate = new Rate();
            rate.score = score;
            rate.userId = userId;
            rate.criteriaId = criteriaId;
            rate.projectId = projectId;
            rate.save();
            return rate;
        }
        Rate rate = findByUserIdAndProjectIdAndCriteriaId(userId,projectId,criteriaId);
        rate.score = score;
        rate.update();
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

    public static List<Rate> findListByProjectId(long projectId){
        return find.where().eq("projectId",projectId).findList();
    }

    public static List<Rate> findListByProjectIdAndCriteriaId(long projectId, Long criteriaId){
        return find.where().eq("projectId",projectId).eq("criteriaId",criteriaId).findList();
    }

    public static List<Rate> findRateByCriterionId(Long criteriaId){
        return find.where().eq("criteriaId", criteriaId).findList();
    }
    public static Rate findById(Long rateId){
        return find.byId(rateId);
    }

    public static Map<Long,Boolean> getRatedVoteAndProjectIdMappingByUserId(Long userId) {
        HashMap<Long,Boolean> result = new HashMap<Long,Boolean>();
        List<Project> projects = Project.findAll();
        for(Project project : projects) {
            Boolean isVoted = findListByUserIdAndProjectId(userId, project.id).size() > 0;
            result.put(project.id, isVoted);
        }
        return result;
    }
}
