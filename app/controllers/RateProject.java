package controllers;

import models.*;
import play.data.*;
import play.mvc.*;
import views.html.rateproject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RateProject extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index(Long projectId) {
        Long userId = Long.parseLong(session().get("userId"));
        List<Rate> rates = Rate.findListByUserIdAndProjectId(userId,projectId);
        Comment comment = Comment.findByUserIdAndProjectId(userId,projectId);
        List<Settings> webconfig = Settings.findAll();

        Map setting = new HashMap();
        for(Settings item : webconfig) {
            setting.put(item.keyName, item.keyValue);
        }

        if(projectId > Project.findAll().size()) {
            return redirect(routes.ProjectList.index());
        }
        return ok(rateproject.render(userId, Project.findById(projectId), rates , Criteria.findAll() , comment , setting));
    }

    public static Result addRate(){
		DynamicForm form = new DynamicForm().bindFromRequest();
        Long userId = Long.parseLong(session().get("userId"));
        Long projectId = Long.parseLong(form.get("projectId"));
    	for(Criteria c : Criteria.findAll()){
            int score = Integer.parseInt(form.get(""+c.id));
            Long criteriaId = c.id;
            Rate rate = Rate.create(score , userId , criteriaId , projectId);
        }
        Comment comment = Comment.create(userId,projectId,form.get("comment"));
    	return redirect(routes.RateProject.index(projectId));
    }

    public static Result editRate() {
        DynamicForm form = new DynamicForm().bindFromRequest();
        long projectId = Long.parseLong(form.get("projectId"));
        /*Rate newrate = Form.form(Rate.class).bindFromRequest().get();
        Rate oldrate = Rate.findByUserIdAndProjectId(newrate.userId,newrate.projectId);
        oldrate.score = newrate.score;
        oldrate.save();*/
        return redirect(routes.RateProject.index(projectId));
    }

}
