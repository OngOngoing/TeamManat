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
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "criteria_id", referencedColumnName = "id")
    private VoteCriterion criterion;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    private static Finder<Long, Vote> find = new Finder<Long, Vote>(Long.class, Vote.class);

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

    public VoteCriterion getCriterion() {
        return criterion;
    }

    public void setCriterion(VoteCriterion criterion) {
        this.criterion = criterion;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public static Vote create(VoteCriterion criterion, User user, Project project) {
        Vote thisVote = findByCriterionAndUser(criterion, user);
        if(thisVote == null) {
            Vote vote = new Vote();
            vote.criterion = criterion;
            vote.user = user;
            vote.project = project;
            vote.save();
            return vote;
        }
        thisVote.project = project;
        thisVote.update();
        return thisVote;
    }

    public static List<Vote> findByUser(User user){
        return find.where().eq("user", user).findList();
    }

    public static Vote findByCriterionAndUser(VoteCriterion criterion, User user) {
        return find.where().eq("criterion", criterion).eq("user", user).findUnique();
    }

    public static Vote findById(Long voteId){return find.byId(voteId);}

    public static List<Vote> findAll(){
        return find.all();
    }

    public static List<Vote> findVotesByCriterion(VoteCriterion criterion) {
        return find.where().eq("criterion", criterion).findList();
    }

    public static List<Vote> findByProject(Project project){
        return find.where().eq("project", project).findList();
    }

    public static int totalPage(){
        return find.where().findPagingList(5).getTotalPageCount();
    }

    public static List<Vote> findVotesByCriterionAndProject(VoteCriterion criterion, Project project) {
        return find.where().eq("criterion", criterion).eq("project", project).findList();
    }

    public static Map<VoteCriterion,Long> getVoteMappingByUser(User user) {
        Map<VoteCriterion,Long> result = new HashMap<VoteCriterion,Long>();
        List<Vote> votes = findByUser(user);
        for(Vote vote : votes) {
            if(vote.getProject() == null){
                result.put(VoteCriterion.findById(vote.getCriterion().getId()), Long.parseLong("-1"));
            }else{
                result.put(VoteCriterion.findById(vote.getCriterion().getId()), vote.getProject().getId());
            }
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
                bundle.sum = findVotesByCriterionAndProject(criterion, project).size();
                bundle.totalVotes = findVotesByCriterion(criterion).size();
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
                bundle.sum = findVotesByCriterionAndProject(criterion, project).size();
                bundle.totalVotes = findVotesByCriterion(criterion).size();
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

