package controllers;

import models.*;
import play.Logger;
import play.mvc.*;
import views.html.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.List;
public class ProjectList extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {
        Long userId = Long.parseLong(session().get("userId"));
        User user = User.findByUserId(userId);
        Map<Long,Integer> mappedRate = Rate.getRateAndProjectMappingByUser(user);
        List<Inbox> comments = Inbox.findAllByReceiver(user);
        int countVotes = Vote.findByUser(user).size();
        int countVotingCriterion = VoteCriterion.findAll().size();
        int voteLeft = countVotingCriterion - countVotes;
        int countProjectRated = 0;
        List<Project> projects = Project.findAll();
        int countProgress = 0;
        for(Project project : projects) {
            countProjectRated += mappedRate.get(project.getId()) == RateCriterion.findAll().size() ? 1:0;
            countProgress += mappedRate.get(project.getId());
        }
        countProgress += countVotes;
        int totalProgress = countVotingCriterion + Project.findAll().size()*RateCriterion.findAll().size();
        int projectsLeft = projects.size() - countProjectRated;
        double percent = (1.0*countProgress/totalProgress)*100.0;
        int roundPercent = Integer.parseInt(String.format("%.0f", percent));
        response().setHeader("Cache-Control", "no-cache");
        return ok(projectlist.render(projects,RateCriterion.findAll(),mappedRate, projectsLeft, voteLeft, roundPercent, user, comments));
    }

}
