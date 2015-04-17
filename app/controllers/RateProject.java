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
        User user = User.findByUserId(userId);
        List<Rate> rates = Rate.findListByUserIdAndProjectId(userId, projectId);
        List<User> teamMember = User.findByTeam(projectId);
        List<ProjectImage> images = ProjectImage.findImageOfProject(projectId);
        Comment comment = Comment.findByUserIdAndProjectId(userId,projectId);
        List<Settings> webconfig = Settings.findAll();

        Map setting = new HashMap();
        for(Settings item : webconfig) {
            setting.put(item.keyName, item.keyValue);
        }

        if(Project.findById(projectId) == null) {
            return redirect(routes.ProjectList.index());
        }
        return ok(rateproject.render(user, Project.findById(projectId), rates , RateCriterion.findAll() ,images , teamMember, comment , setting));
    }

    public static Result addRate(){
		DynamicForm form = new DynamicForm().bindFromRequest();
        Long userId = Long.parseLong(session().get("userId"));
        Long projectId = Long.parseLong(form.get("projectId"));
    	for(RateCriterion c : RateCriterion.findAll()){
            int score = Integer.parseInt(form.get(""+c.id));
            Long criteriaId = c.id;
            Rate rate = Rate.create(score , userId , criteriaId , projectId);
        }
        Comment comment = Comment.create(userId,projectId,form.get("comment"));
        flash("rate_success", "Rate submitted");
    	return redirect(routes.RateProject.index(projectId));
    }

    public static Result editRate() {
        DynamicForm form = new DynamicForm().bindFromRequest();
        Long userId = Long.parseLong(session().get("userId"));
        long projectId = Long.parseLong(form.get("projectId"));
        List<Rate> rates = Rate.findListByUserIdAndProjectId(userId,projectId);
        for(Rate r : rates){
            for(RateCriterion c : RateCriterion.findAll()){
                if(r.criteriaId == c.id){
                    int score = Integer.parseInt(form.get(""+c.id));
                    r.score = score;
                }
            }
            r.update();
        }
        Comment thisComment = Comment.findByUserIdAndProjectId(userId,projectId);
        thisComment.comment = form.get("comment");
        thisComment.update();
        flash("edit_success", "Rate updated");
        return redirect(routes.RateProject.index(projectId));
    }
}
