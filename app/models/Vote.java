package models;

import javax.persistence.*;

import org.apache.commons.collections.map.MultiKeyMap;
import play.Logger;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;

import java.util.*;

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
        Vote thisVote = findByCriterionAndUserId(criterionId,userId);
        if(thisVote == null) {
            Vote vote = new Vote();
            vote.criterionId = criterionId;
            vote.userId = userId;
            vote.projectId = projectId;
            vote.save();
            return vote;
        }
        thisVote.projectId = projectId;
        thisVote.update();
        return thisVote;
    }

    public static Finder<Long, Vote> find = new Finder<Long, Vote>(Long.class, Vote.class);

    public static List<Vote> findByUserId(long userId){
        return find.where().eq("userId", userId).findList();
    }

    public static Vote findByCriterionAndUserId(Long criterionId, Long userId) {
        return find.where().eq("criterionId", criterionId).eq("userId", userId).findUnique();
    }
    public static Vote findById(Long voteid){return find.byId(voteid);}
    public static List<Vote> findAll(){
        return find.all();
    }

    public static List<Vote> findVotesByCriterionId(Long criterionId) {
        return find.where().eq("criterionId", criterionId).findList();
    }
    public static List<Vote> findByProjectId(Long projectId){
        return find.where().eq("projectId", projectId).findList();
    }
    public static int totalPage(){
        return find.where().findPagingList(5).getTotalPageCount();
    }
    public static List<Vote> findVotesByCriterionIdAndProjectId(Long criterionId, Long projectId) {
        return find.where().eq("criterionId", criterionId).eq("projectId", projectId).findList();
    }

    public static Map<VoteCriterion,Long> getVoteMappingByUserId(Long userId) {
        Map<VoteCriterion,Long> result = new HashMap<VoteCriterion,Long>();
        List<Vote> votes = findByUserId(userId);
        for(Vote vote : votes) {
            result.put(VoteCriterion.findById(vote.criterionId), vote.projectId);
        }
        return result;
    }
    public static List<Vote> findAllByPage(int page){
        return find.where().findPagingList(5).getPage(--page).getList();
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
                bundle.percent = 0;
                if(bundle.totalVotes > 0) {
                    bundle.percent = 100.0*bundle.sum/bundle.totalVotes;
                }
                bundle.roundedPercent = String.format("%.2f",bundle.percent);
                bundle.project = project;
                result.put(criterion, project, bundle);
            }
        }
        return result;
    }

    public static HashMap<VoteCriterion,List<ResultBundle>> summarizeWithReverseOrder() {
        HashMap<VoteCriterion, List<ResultBundle>> result = new HashMap<VoteCriterion, List<ResultBundle>>();
        List<VoteCriterion> criteria = VoteCriterion.findAll();
        List<Project> projects = Project.findAll();
        for(VoteCriterion criterion : criteria) {
            List<ResultBundle> bundleList = new ArrayList<ResultBundle>();
            for(Project project : projects) {
                ResultBundle bundle = new ResultBundle();
                bundle.sum = findVotesByCriterionIdAndProjectId(criterion.id, project.id).size();
                bundle.totalVotes = findVotesByCriterionId(criterion.id).size();
                bundle.project = project;
                bundle.percent = 0;
                if(bundle.totalVotes > 0) {
                    bundle.percent = 100.0*bundle.sum/bundle.totalVotes;
                }
                bundle.roundedPercent = String.format("%.2f",bundle.percent);
                bundleList.add(bundle);
            }
            Collections.sort(bundleList, Collections.reverseOrder(new Comparator() {
                public int compare(Object o1, Object o2) {
                    Integer sum1 = ((ResultBundle)o1).sum;
                    Integer sum2 = ((ResultBundle)o2).sum;
                    return sum1.compareTo(sum2);
                }
            }));
            result.put(criterion, bundleList);
        }
        return result;
    }

    public static HashMap<VoteCriterion,List<ResultBundle>> getWinnerSummary() {
        HashMap<VoteCriterion,List<ResultBundle>> result = summarizeWithReverseOrder();
        List<VoteCriterion> criteria = VoteCriterion.findAll();
        for(VoteCriterion criterion : criteria) {
            int max = result.get(criterion).get(0).sum;
            for(int i= result.get(criterion).size()-1; i >= 0; i-- ) {
                if(result.get(criterion).get(i).sum < max) {
                    result.get(criterion).remove(i);
                }
            }
        }
        return result;
    }

    public static class ResultBundle {
        public int sum;
        public double percent;
        public String roundedPercent;
        public int totalVotes;
        public Project project;
    }

}

