package controllers;

import play.mvc.*;
import play.data.*;
import models.Project;
import models.Vote;

import views.html.*;



public class VotePage extends Controller {


    @Security.Authenticated(Secured.class)
    public static Result index(Long id) {
        Long userId = Long.parseLong(session().get("userId"));
        Vote vote = Vote.find.where().eq("userId", userId).eq("projectId",id).findUnique();
        return ok(votepage.render(userId ,Project.find.byId(id), vote, Vote.find.all()));
    }

    public static Result addVote(){
		Vote vote = Form.form(Vote.class).bindFromRequest().get();
    	vote.save();
    	return redirect(routes.VotePage.index(vote.projectId));
    }

    public static Result editVote() {
        Vote newvote = Form.form(Vote.class).bindFromRequest().get();
        Vote oldvote = Vote.find.where().eq("userId", newvote.userId).eq("projectId",newvote.projectId).findUnique();
        oldvote.score = newvote.score;
        oldvote.save();
        return redirect(routes.VotePage.index(oldvote.projectId));
    }

}
