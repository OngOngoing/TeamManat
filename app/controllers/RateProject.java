package controllers;

import models.*;
import play.data.Form;
import play.mvc.*;
import views.html.rateproject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RateProject extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index(Long projectId) {
        Long userId = Long.parseLong(session().get("userId"));
        User user = User.findByUserId(userId);
        Rate rate = Rate.findByUserIdAndProjectId(userId,projectId);
        List<Settings> webconfig = Settings.findAll();

        Map setting = new HashMap();
        for(Settings item : webconfig) {
            setting.put(item.keyName, item.keyValue);
        }

        if(projectId > Project.findAll().size()) {
            return redirect(routes.ProjectList.index());
        }
        return ok(rateproject.render(user, Project.findById(projectId), rate, Rate.findAll(), Criteria.findAll(), setting));
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
