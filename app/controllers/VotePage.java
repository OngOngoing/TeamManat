package controllers;

import play.data.Form;
import play.mvc.*;
import models.Project;
import models.Vote;

import views.html.*;



public class VotePage extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index(Long id) {
        return ok(votepage.render(Project.find.byId(id), Vote.find.all()));
    }

    public static Result addVote(){
		Vote vote = Form.form(Vote.class).bindFromRequest().get();
    	vote.save();
    	return index((long) 01);
    }

}
