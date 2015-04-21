package controllers;

import models.*;
import org.apache.commons.collections.map.MultiKeyMap;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.votepage;
import views.html.voteresult;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VoteController extends Controller {


    @Security.Authenticated(Secured.class)
    public static Result index() {
        Long userId = Long.parseLong(session().get("userId"));
        User thisUser = User.findByUserId(userId);
        Map<VoteCriterion,Long> voteMapping = Vote.getVoteMappingByUserId(userId);
        boolean isTimeUp = Settings.isTimeUp();
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
        if(thisUser.idtype == User.ADMINISTRATOR || Settings.isTimeUp()) {
            List<VoteCriterion> criteria = VoteCriterion.findAll();
            List<Project> projects = Project.findAll();
            MultiKeyMap result = Vote.summarize();
            HashMap<VoteCriterion, List<Vote.ResultBundle>> winnerSummary = Vote.getWinnerSummary();
            return ok(voteresult.render(thisUser, criteria, projects, winnerSummary, result ));
        }
        flash("error","Please wait until the voting session is closed. Sorry for the inconvenience.");
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.ProjectList.index());
    }
    @Security.Authenticated(Secured.class)
    public static Result addVote() {
        List<VoteCriterion> criteria = VoteCriterion.findAll();
        Long userId = Long.parseLong(session().get("userId"));
        if(Settings.isTimeUp()) {
            flash("time_up","Time is already up. Sorry for the inconvenience.");
            return redirect(routes.VoteController.index());
        }
        Map<String, String[]> map = request().body().asFormUrlEncoded();
        if(map.isEmpty()){
            flash("vote_empty", "Please vote at least 1 criterion");
            return redirect(routes.VoteController.index());
        }
        for(VoteCriterion criterion : criteria) {
            if(map.get("criterionId"+criterion.id) != null) {
                String[] selectedCriterion = map.get("criterionId" + criterion.id); // get selected topics
                // THIS SHOULD HAS ONLY 1 RESULT
                for(String projectId : selectedCriterion) {
                    Vote.create(criterion.id,userId,Long.parseLong(projectId));
                    Logger.info("[" + User.findByUserId(userId).username + "] vote ("+criterion.id+")"+criterion.name+", project : ("+projectId+")"+Project.findById(Long.parseLong(projectId)));
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

        if(Settings.isTimeUp()) {
            flash("time_up","Time is already up. Sorry for the inconvenience.");
            return redirect(routes.VoteController.index());
        }

        Map<String, String[]> map = request().body().asFormUrlEncoded();
        for(VoteCriterion criterion : criteria) {
            if(map.get("criterionId"+criterion.id) != null) {
                String[] selectedCriterion = map.get("criterionId" + criterion.id); // get selected topics
                // THIS SHOULD HAS ONLY 1 RESULT
                for(String projectId : selectedCriterion) {
                    Vote thisVote = Vote.findByCriterionAndUserId(criterion.id,userId);
                    if (thisVote == null) {
                        Vote.create(criterion.id, userId, Long.parseLong(projectId));
                        Logger.info("[" + User.findByUserId(userId).username + "] vote ("+criterion.id+")"+criterion.name+", project : ("+projectId+")"+Project.findById(Long.parseLong(projectId)).projectName);
                    }else {
                        Logger.info("[" + User.findByUserId(userId).username + "] edit vote ("+criterion.id+")"+criterion.name+", project : ("+projectId+")"+Project.findById(Long.parseLong(projectId)).projectName);
                        thisVote.projectId = Long.parseLong(projectId);
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
