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
    private long id;
    @Required
    private int score;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "criteria_id", referencedColumnName = "id")
    private RateCriterion criterion;

    private static Finder<Long, Rate> find = new Finder<Long, Rate>(Long.class, Rate.class);

    public static Rate create(int score, User user, RateCriterion criteria, Project project) {
        if (score < 0) {
            score = 0;
        }
        if (score > 5) {
            score = 5;
        }
        if (findByUserAndProjectAndCriteria(user, project, criteria) == null) {
            if (score == 0) {
                return null;
            }
            Rate rate = new Rate();
            rate.score = score;
            rate.user = user;
            rate.criterion = criteria;
            rate.project = project;
            rate.save();
            return rate;
        }
        Rate rate = findByUserAndProjectAndCriteria(user, project, criteria);
        if (score == 0) {
            rate.delete();
            return null;
        } else {
            rate.score = score;
            rate.update();
        }
        return rate;

    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setCriterion(RateCriterion criterion) {
        this.criterion = criterion;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public Project getProject() {
        return project;
    }

    public RateCriterion getCriterion() {
        return criterion;
    }

    public static Rate findByUserAndProjectAndCriteria(User user, Project project, RateCriterion criteria) {
        return find.where().eq("user", user).eq("project", project).eq("criterion", criteria).findUnique();
    }

    public static List<Rate> findAll() {
        return find.all();
    }

    public static List<Rate> findListByUser(User user) {
        return find.where().eq("user", user).findList();
    }

    public static List<Rate> findListByUserAndProject(User user, Project project) {
        return find.where().eq("user", user).eq("project", project).findList();
    }

    public static List<Rate> findAllByPage(int page) {
        return find.where().findPagingList(13).getPage(--page).getList();
    }

    public static int totalPage() {
        return find.where().findPagingList(13).getTotalPageCount();
    }

    public static List<Rate> findListByProject(Project project) {
        return find.where().eq("project", project).findList();
    }

    public static List<Rate> findListByProjectAndCriteria(Project project, RateCriterion criteria) {
        return find.where().eq("project", project).eq("criterion", criteria).findList();
    }

    public static List<Rate> findRateByCriterion(RateCriterion criteria) {
        return find.where().eq("criterion", criteria).findList();
    }

    public static Rate findById(Long rateId) {
        return find.byId(rateId);
    }

    public static Map<Long, Integer> getRateAndProjectMappingByUser(User user) {
        HashMap<Long, Integer> result = new HashMap<Long, Integer>();
        List<Project> projects = Project.findAll();
        for (Project project : projects) {
            int countRate = findListByUserAndProject(user, project).size();
            result.put(project.getId(), countRate);
        }
        return result;
    }
}
