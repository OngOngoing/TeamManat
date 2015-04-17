package controllers;

import models.VoteCriterion;
import models.Project;
import models.Vote;
import org.apache.commons.collections.map.MultiKeyMap;
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
        List<Vote> votes = Vote.findByUserId(userId);
        return ok(votepage.render(userId, Project.findAll(), votes, VoteCriterion.findAll()));
    }

    @Security.Authenticated(Secured.class)
    public static Result showResult() {
        Long userId = Long.parseLong(session().get("userId"));
        List<Vote> votes = Vote.findAll();
        List<VoteCriterion> criteria = VoteCriterion.findAll();
        List<Project> projects = Project.findAll();
        MultiKeyMap result = Vote.summarize();

        return ok(voteresult.render(criteria, projects,result ));
    }

    public static Result addVote() {
        List<VoteCriterion> criteria = VoteCriterion.findAll();
        Long userId = Long.parseLong(session().get("userId"));

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
                }
            }
        }
        flash("vote_success", "Vote submitted");
        return redirect(routes.VoteController.index());
    }

    public static Result editVote() {
        List<VoteCriterion> criteria = VoteCriterion.findAll();
        Long userId = Long.parseLong(session().get("userId"));

        Map<String, String[]> map = request().body().asFormUrlEncoded();
        for(VoteCriterion criterion : criteria) {
            if(map.get("criterionId"+criterion.id) != null) {
                String[] selectedCriterion = map.get("criterionId" + criterion.id); // get selected topics
                // THIS SHOULD HAS ONLY 1 RESULT
                for(String projectId : selectedCriterion) {
                    Vote thisVote = Vote.findByCriterionAndUserId(criterion.id,userId);
                    if (thisVote == null) {
                        Vote.create(criterion.id, userId, Long.parseLong(projectId));
                    }
                    else {
                        thisVote.projectId = Long.parseLong(projectId);
                        thisVote.update();
                    }
                }
            }
        }
        flash("edit_success", "Vote updated");
        return redirect(routes.VoteController.index());
    }



}
