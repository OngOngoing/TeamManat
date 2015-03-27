package controllers;

import play.mvc.*;
import play.data.*;
import models.Project;
import models.Vote;
import models.Rate;

import views.html.*;



public class VotePage extends Controller {


    @Security.Authenticated(Secured.class)
    public static Result index(Long id) {
        Long userId = Long.parseLong(session().get("userId"));
        Rate rate = Rate.find.where().eq("userId", userId).eq("projectId",id).findUnique();
        if(id > Project.find.all().size()) {
            return redirect(routes.ProjectList.index());
        }
        return ok(votepage.render(userId ,Project.find.byId(id), rate, Vote.find.all(),Rate.find.all()));
    }

    public static Result addRate(){
		Rate rate = Form.form(Rate.class).bindFromRequest().get();
    	rate.save();
    	return redirect(routes.VotePage.index(rate.projectId));
    }

    public static Result editRate() {
        Rate newrate = Form.form(Rate.class).bindFromRequest().get();
        Rate oldrate = Rate.find.where().eq("userId", newrate.userId).eq("projectId",newrate.projectId).findUnique();
        oldrate.score = newrate.score;
        oldrate.comment = newrate.comment;
        oldrate.save();
        return redirect(routes.VotePage.index(oldrate.projectId));
    }

}
