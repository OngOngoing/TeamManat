package models;

import javax.persistence.*;

import org.apache.commons.collections.map.MultiKeyMap;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;

import java.util.List;

@Entity
public class Vote extends Model {
    @Id
    public long id;
    @Required
    public long criterionId;
    public Long userId;
    public Long projectId;

    public static Vote create(long criterionId , Long userId , Long projectId)
    {
        Vote vote = new Vote();
        vote.criterionId = criterionId;
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

    public static List<Vote> findVotesByCriterionId(Long criterionId) {
        return find.where().eq("criterionId", criterionId).findList();
    }

    public static List<Vote> findVotesByCriterionIdAndProjectId(Long criterionId, Long projectId) {
        return find.where().eq("criterionId", criterionId).eq("projectId", projectId).findList();
    }

    public static MultiKeyMap summarize() {
        MultiKeyMap result = new MultiKeyMap();
        List<VoteCriterion> criteria = VoteCriterion.findAll();
        List<Project> projects = Project.findAll();
        for(VoteCriterion criterion : criteria) {
            for(Project project : projects) {
                ResultBundle bundle = new ResultBundle();
                bundle.sum = findVotesByCriterionIdAndProjectId(criterion.id,project.id).size();
                bundle.totalVotes = findVotesByCriterionId(criterion.id).size();
                bundle.percent = 100.0*bundle.sum/bundle.totalVotes;
                result.put(criterion, project, bundle);
            }
        }
        return result;
    }

    public static class ResultBundle {
        public int sum;
        public double percent;
        public int totalVotes;


    }

}

