package controllers;

import models.*;
import net.sf.ehcache.search.expression.Criteria;
import play.Logger;
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
        Project project = Project.findById(projectId);
        List<Rate> rates = Rate.findListByUserAndProject(user, project);
        List<User> teamMember = User.findByProject(project);
        List<Image> images = Image.findImageOfProject(project);
        Comment comment = Comment.findByUserAndProject(user, project);
        List<Setting> webconfig = Setting.findAll();

        Map setting = new HashMap();
        for (Setting item : webconfig) {
            setting.put(item.getKeyName(), item.getKeyValue());
        }

        if (Project.findById(projectId) == null) {
            return redirect(routes.ProjectList.index());
        }
        return ok(rateproject.render(user, Project.findById(projectId), rates, RateCriterion.findAll(), images, teamMember, comment, setting));
    }

    @Security.Authenticated(Secured.class)
    public static Result addRate() {
        DynamicForm form = new DynamicForm().bindFromRequest();
        Long userId = Long.parseLong(session().get("userId"));
        User user = User.findByUserId(userId);
        Long projectId = Long.parseLong(form.get("projectId"));
        Project project = Project.findById(projectId);
        if (Setting.isTimeUp()) {
            flash("error", "Time is already up.");
            return redirect(routes.RateProject.index(projectId));
        }
        String log = "[" + User.findByUserId(userId).getUsername() + "] rate (" + projectId + ")" + Project.findById(projectId).getProjectName();
        for (RateCriterion c : RateCriterion.findAll()) {
            int score = Integer.parseInt(form.get("" + c.getId()));
            if (score > 0 && score < 6) {
                Rate rate = Rate.create(score, user, c, project);
            }
        }
        Logger.info(log);
        String thisComment = form.get("comment");
        if (thisComment.length() > 0) {
            Comment comment = Comment.create(user, project, thisComment);
        }
        if (Rate.findListByUserAndProject(user, project).size() > 0 || thisComment.length() > 0) {
            flash("rate_success", "Rate and Comment submitted");
        }
        return redirect(routes.RateProject.index(projectId));
    }

    @Security.Authenticated(Secured.class)
    public static Result editRate() {
        DynamicForm form = new DynamicForm().bindFromRequest();
        Long userId = Long.parseLong(session().get("userId"));
        Long projectId = Long.parseLong(form.get("projectId"));
        if (Setting.isTimeUp()) {
            flash("error", "Time is already up.");
            return redirect(routes.RateProject.index(projectId));
        }
        Project project = Project.findById(projectId);
        User user = User.findByUserId(userId);
        List<Rate> rates = Rate.findListByUserAndProject(user, project);
        String log = "[" + User.findByUserId(userId).getUsername() + "] edit rate (" + projectId + ")" + project.getProjectName();
        for (RateCriterion c : RateCriterion.findAll()) {
            int score = Integer.parseInt(form.get("" + c.getId()));
            if (score >= 0 && score < 6) {
                Rate r = Rate.create(score, user, c, project);
            }
        }
        Logger.info(log);
        Logger.info("[" + User.findByUserId(userId).getUsername() + "] edit comment (" + projectId + ")" + project.getProjectName() + "");
        Comment thisComment = Comment.findByUserAndProject(user, project);
        String getComment = form.get("comment");
        if (thisComment == null) {
            if (getComment.length() > 0)
                Comment.create(user, project, getComment);
        } else {
            thisComment.setComment(getComment);
            thisComment.update();
        }
        flash("edit_success", "Rate updated");
        response().setHeader("Cache-Control", "no-cache");
        return redirect(routes.RateProject.index(projectId));
    }
}
