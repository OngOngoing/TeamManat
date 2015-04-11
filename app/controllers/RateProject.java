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
    public static Result index(Long projectId) {
        Long userId = Long.parseLong(session().get("userId"));
        Rate rate = Rate.findByUserIdAndProjectId(userId,projectId);
        if(projectId > Project.findAll().size()) {
            return redirect(routes.ProjectList.index());
        }
        return ok(rateproject.render(userId, Project.findById(projectId), rate, Rate.findAll()));
    }

    public static Result addRate(){
		Rate rate = Form.form(Rate.class).bindFromRequest().get();
    	rate.save();
    	return redirect(routes.RateProject.index(rate.projectId));
    }

    public static Result editRate() {
        Rate newrate = Form.form(Rate.class).bindFromRequest().get();
        Rate oldrate = Rate.findByUserIdAndProjectId(newrate.userId,newrate.projectId);
        oldrate.score = newrate.score;
        oldrate.comment = newrate.comment;
        oldrate.save();
        return redirect(routes.RateProject.index(oldrate.projectId));
    }

}
