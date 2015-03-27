package controllers;

import models.Project;
import models.Rate;
import models.Vote;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.rateproject;


public class RateProject extends Controller {


    @Security.Authenticated(Secured.class)
    public static Result index(Long id) {
        Long userId = Long.parseLong(session().get("userId"));
        Rate rate = Rate.find.where().eq("userId", userId).eq("projectId",id).findUnique();
        if(id > Project.find.all().size()) {
            return redirect(routes.ProjectList.index());
        }
        return ok(rateproject.render(userId, Project.find.byId(id), rate, Rate.find.all()));
    }

    public static Result addRate(){
		Rate rate = Form.form(Rate.class).bindFromRequest().get();
    	rate.save();
    	return redirect(routes.RateProject.index(rate.projectId));
    }

    public static Result editRate() {
        Rate newrate = Form.form(Rate.class).bindFromRequest().get();
        Rate oldrate = Rate.find.where().eq("userId", newrate.userId).eq("projectId",newrate.projectId).findUnique();
        oldrate.score = newrate.score;
        oldrate.comment = newrate.comment;
        oldrate.save();
        return redirect(routes.RateProject.index(oldrate.projectId));
    }

}
