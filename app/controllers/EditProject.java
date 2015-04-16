package controllers;

import models.Project;
import models.User;
import play.data.DynamicForm;
import play.mvc.*;
import views.html.editproject;
import java.util.List;
import play.libs.Json;
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
        List<User> members = User.findByTeam(projectId);
        return ok(editproject.render(user, project, members));
    }
    @Security.Authenticated(Secured.class)
    public static Result addMember(int projectId){
        User user = User.findByUserId(Long.parseLong(session().get("userId")));
        if(user.idtype != User.ADMINISTRATOR)
            return redirect(routes.ProjectList.index());
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        String input = dynamicForm.get("searchUser");
        if(!input.matches("[0-9]+")){
            flash("error", "Please input UserId only!");
            return redirect(routes.EditProject.index(projectId));
        }
        Long userId = Long.parseLong(input);
        User editUser = User.findByUserId(userId);
        if(editUser == null){
            flash("error", "Can't found user!");
            return redirect(routes.EditProject.index(projectId));
        }
        editUser.projectId = projectId;
        editUser.save();
        flash("success", "User was added!");
        return redirect(routes.EditProject.index(projectId));
    }
    public static Result delete(Long userId, int proId){
        User editUser = User.findByUserId(userId);
        if (editUser != null) {
            editUser.projectId = -1;
            editUser.save();
            flash("d_success", "User was deleted!");
        }
        return redirect(routes.EditProject.index(proId));
    }
    public static Result searchUser(){
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        List<User> userList = User.findByKeyword(dynamicForm.get("search_keyword"));
        return ok(Json.toJson(userList));
    }
    public static boolean canEditProject(User user, Long projectId){
        if(user.idtype == User.ADMINISTRATOR)
            return true;
        if(user.projectId == projectId)
            return true;
        return false;
    }
}
