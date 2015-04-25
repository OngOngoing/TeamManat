package controllers;

import models.*;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.votepage;
import views.html.voteresult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VoteController extends Controller {


    @Security.Authenticated(Secured.class)
    public static Result index() {
        Long userId = Long.parseLong(session().get("userId"));
        User thisUser = User.findByUserId(userId);
        Map<VoteCriterion,Long> voteMapping = Vote.getVoteMappingByUser(thisUser);
        boolean isTimeUp = Setting.isTimeUp();
        if(isTimeUp) {
            flash("time_up","Time is already up. Sorry for the inconvenience.");
        }
        response().setHeader("Cache-Control","no-cache");
        return ok(votepage.render(thisUser, userId, Project.findAll(), voteMapping, VoteCriterion.findAll(),isTimeUp));
    }

    @Security.Authenticated(Secured.class)
    public static Result showResult() {
        Long userId = Long.parseLong(session().get("userId"));
        User thisUser = User.findByUserId(userId);
        if(thisUser.getIdtype() == User.ADMINISTRATOR || Setting.isTimeUp()) {
            List<VoteCriterion> criteria = VoteCriterion.findAll();
            List<Project> projects = Project.findAll();
            HashMap<VoteCriterion, List<Vote.ResultBundle>> orderedSummary = Vote.summarizeWithReverseOrder();
            HashMap<VoteCriterion, List<Vote.ResultBundle>> winnerSummary = Vote.getWinnerSummary();
            return ok(voteresult.render(thisUser, criteria, projects, winnerSummary, orderedSummary ));
        }
        flash("error","Please wait until the voting session is closed. Sorry for the inconvenience.");
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.ProjectList.index());
    }
    @Security.Authenticated(Secured.class)
    public static Result addVote() {
        List<VoteCriterion> criteria = VoteCriterion.findAll();
        Long userId = Long.parseLong(session().get("userId"));
        User user = User.findByUserId(userId);
        if(Setting.isTimeUp()) {
            flash("time_up","Time is already up. Sorry for the inconvenience.");
            return redirect(routes.VoteController.index());
        }
        Map<String, String[]> map = request().body().asFormUrlEncoded();
        if(map.isEmpty()){
            flash("vote_empty", "Please vote at least 1 criterion");
            return redirect(routes.VoteController.index());
        }
        for(VoteCriterion criterion : criteria) {
            if(map.get("criterionId"+criterion.getId()) != null) {
                String[] selectedCriterion = map.get("criterionId" + criterion.getId()); // get selected topics
                // THIS SHOULD HAS ONLY 1 RESULT
                for(String projectId : selectedCriterion) {
                    Project project = Project.findById(Long.parseLong(projectId));
                    Vote.create(criterion, user, project);
                    if (project == null) {
                        Logger.info("[" + User.findByUserId(userId).getUsername() + "] vote (" + criterion.getId() + ")" + criterion.getName() + ", no vote");
                    }else{
                        Logger.info("[" + User.findByUserId(userId).getUsername() + "] vote (" + criterion.getId() + ")" + criterion.getName() + ", project : (" + projectId + ")" + project.getProjectName());
                    }
                }
            }
        }
        flash("vote_success", "Vote submitted");
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.VoteController.index());
    }
    @Security.Authenticated(Secured.class)
    public static Result editVote() {
        List<VoteCriterion> criteria = VoteCriterion.findAll();
        Long userId = Long.parseLong(session().get("userId"));
        User user = User.findByUserId(userId);
        if(Setting.isTimeUp()) {
            flash("time_up","Time is already up. Sorry for the inconvenience.");
            return redirect(routes.VoteController.index());
        }

        Map<String, String[]> map = request().body().asFormUrlEncoded();
        for(VoteCriterion criterion : criteria) {
            if(map.get("criterionId"+criterion.getId()) != null) {
                String[] selectedCriterion = map.get("criterionId" + criterion.getId()); // get selected topics
                // THIS SHOULD HAS ONLY 1 RESULT
                for(String projectId : selectedCriterion) {
                    Vote thisVote = Vote.findByCriterionAndUser(criterion, user);
                    Project project =Project.findById(Long.parseLong(projectId));
                    if (thisVote == null) {
                        Vote.create(criterion, user, project);
                        Logger.info("[" + user.getUsername() + "] vote (" + criterion.getId() + ")" + criterion.getName() + ", project : (" + projectId + ")" + project.getProjectName());
                    }else {
                        if(project == null){
                            Logger.info("[" + User.findByUserId(userId).getUsername() + "] edit vote (" + criterion.getId() + ")" + criterion.getName() + ", to no vote");
                        }else {
                            Logger.info("[" + User.findByUserId(userId).getUsername() + "] edit vote (" + criterion.getId() + ")" + criterion.getName() + ", project : (" + projectId + ")" + project.getProjectName());
                        }
                        thisVote.setProject(project);
                        thisVote.update();
                    }
                }
            }
        }
        flash("edit_success", "Vote updated");
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.VoteController.index());
    }
}
