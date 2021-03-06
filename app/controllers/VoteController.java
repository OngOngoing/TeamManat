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
            flash("error","Time is already up.");
        }
        response().setHeader("Cache-Control","no-cache");
        return ok(votepage.render(thisUser, Project.findAll(), voteMapping, VoteCriterion.findAll()));
    }

    //@Security.Authenticated(Secured.class)
    public static Result showResult() {
        User thisUser; 
        if(session().get("userId") == null){
            thisUser = new User();
            thisUser.setIdtype(-1);
            thisUser.setId(Long.parseLong("-1"));
        }else{
            Long userId = Long.parseLong(session().get("userId"));
            thisUser = User.findByUserId(userId);    
        }
        if(thisUser.getIdtype() == User.ADMINISTRATOR || Setting.isTimeUp()) {
            List<VoteCriterion> criteria = VoteCriterion.findAll();
            List<Project> projects = Project.findAll();
            HashMap<VoteCriterion, List<Vote.ResultBundle>> orderedSummary = Vote.summarizeWithReverseOrder();
            HashMap<VoteCriterion, List<Vote.ResultBundle>> winnerSummary = Vote.getWinnerSummary();
            HashMap<VoteCriterion, Vote.ResultBundle> noVoteMapping = Vote.getNoVoteMapping();
            return ok(voteresult.render(thisUser, criteria, projects, winnerSummary, orderedSummary, noVoteMapping ));
        }
        flash("error","Please wait until the voting session is closed.");
        response().setHeader("Cache-Control","no-cache");
        if(thisUser.getIdtype() == -1){
            return redirect(routes.Application.index());
        }
        return redirect(routes.ProjectList.index());
    }
    @Security.Authenticated(Secured.class)
    public static Result addVote() {
        List<VoteCriterion> criteria = VoteCriterion.findAll();
        Long userId = Long.parseLong(session().get("userId"));
        User user = User.findByUserId(userId);
        if(Setting.isTimeUp()) {
            flash("error","Time is already up. Sorry for the inconvenience.");
            return redirect(routes.VoteController.index());
        }
        Map<String, String[]> map = request().body().asFormUrlEncoded();
        if(map.isEmpty()){
            flash("error", "Please vote at least 1 criterion");
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
        flash("success", "Vote submitted");
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.VoteController.index());
    }
    @Security.Authenticated(Secured.class)
    public static Result editVote() {
        List<VoteCriterion> criteria = VoteCriterion.findAll();
        Long userId = Long.parseLong(session().get("userId"));
        User user = User.findByUserId(userId);
        if(Setting.isTimeUp()) {
            flash("error","Time is already up. Sorry for the inconvenience.");
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
                        if(project == null){
                            Logger.info("[" + user.getUsername() + "] vote (" + criterion.getId() + ")" + criterion.getName() + ", to no vote");
                        }else {
                            Logger.info("[" + user.getUsername() + "] vote (" + criterion.getId() + ")" + criterion.getName() + ", project : (" + projectId + ")" + project.getProjectName());
                        }

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
        flash("success", "Vote updated");
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.VoteController.index());
    }
}
