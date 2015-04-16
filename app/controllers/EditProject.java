package controllers;

import models.Project;
import models.User;
import play.mvc.*;
import views.html.editproject;
import java.util.List;

/**
 * Created by Chin on 4/15/2015.
 */
public class EditProject extends Controller {
    @Security.Authenticated(Secured.class)
    public static Result index(Long projectId){
        User user = User.findByUserId(Long.parseLong(session().get("userId")));
        if(user == null)
            return redirect(routes.ProjectList.index());
        if(!canEditProject(user, projectId))
            return redirect(routes.ProjectList.index());
        Project project = Project.findById(projectId);
        List<User> users = User.findAll();
        return ok(editproject.render(user, project, users));
    }
    @Security.Authenticated(Secured.class)
    public static Result addMember(){
        User user = User.findByUserId(Long.parseLong(session().get("userId")));
        if(user.idtype != User.ADMINISTRATOR)
            return redirect(routes.ProjectList.index());
        return ok();
    }
    public static boolean canEditProject(User user, Long projectId){
        if(user.idtype == User.ADMINISTRATOR)
            return true;
        if(user.projectId == projectId)
            return true;
        return false;
    }
}
