package controllers;

import models.VoteCriterion;
import models.Project;
import models.Vote;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.votepage;

import java.util.List;
import java.util.Map;


public class VoteController extends Controller {


    @Security.Authenticated(Secured.class)
    public static Result index() {
        Long userId = Long.parseLong(session().get("userId"));
        List<Vote> votes = Vote.findByUserId(userId);
        return ok(votepage.render(userId, Project.findAll(), votes, VoteCriterion.findByType(1)));
    }

    public static Result addVote() {
        List<VoteCriterion> criteria = VoteCriterion.findByType(1);
        Long userId = Long.parseLong(session().get("userId"));

        Map<String, String[]> map = request().body().asFormUrlEncoded();
        for(VoteCriterion criterion : criteria) {
            if(map.get("criterionId"+criterion.id) != null) {
                String[] selectedCriterion = map.get("criterionId" + criterion.id); // get selected topics
                // THIS SHOULD HAS ONLY 1 RESULT
                for(String projectId : selectedCriterion) {
                    Vote.create(criterion.id,"0",userId,Long.parseLong(projectId));
                }
            }
        }
        return redirect(routes.VoteController.index());
    }

    public static Result editVote() {
        List<VoteCriterion> criteria = VoteCriterion.findByType(1);
        Long userId = Long.parseLong(session().get("userId"));

        Map<String, String[]> map = request().body().asFormUrlEncoded();
        for(VoteCriterion criterion : criteria) {
            if(map.get("criterionId"+criterion.id) != null) {
                String[] selectedCriterion = map.get("criterionId" + criterion.id); // get selected topics
                // THIS SHOULD HAS ONLY 1 RESULT
                for(String projectId : selectedCriterion) {
                    Vote thisVote = Vote.findByCriterionAndUserId(criterion.id,userId);
                    if (thisVote == null) {
                        Vote.create(criterion.id, "0", userId, Long.parseLong(projectId));
                    }
                    else {
                        thisVote.projectId = Long.parseLong(projectId);
                        thisVote.update();
                    }
                }
            }
        }
        return redirect(routes.VoteController.index());
    }



}
