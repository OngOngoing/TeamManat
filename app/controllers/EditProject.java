package controllers;

import models.Project;
import models.ProjectImage;
import models.Settings;
import models.User;
import play.data.DynamicForm;
import play.mvc.*;
import views.html.editproject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<ProjectImage> images = ProjectImage.findImageOfProject(projectId);
        return ok(editproject.render(user, project, members, images));
    }
    @Security.Authenticated(Secured.class)
    public static Result addMember(Long projectId){
        User user = User.findByUserId(Long.parseLong(session().get("userId")));
        if(user.idtype != User.ADMINISTRATOR)
            return redirect(routes.ProjectList.index());
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        String input = dynamicForm.get("searchUser");
        if(!input.matches("[0-9]+")){
            flash("error", "Please input User ID only!");
            return redirect(routes.EditProject.index(projectId));
        }
        Long userId = Long.parseLong(input);
        User editUser = User.findByUserId(userId);
        if(editUser == null){
            flash("error", "User not found");
            return redirect(routes.EditProject.index(projectId));
        }
        editUser.projectId = projectId;
        editUser.update();
        flash("success", "User is added!");
        return redirect(routes.EditProject.index(projectId));
    }
    public static Result edit(Long projectId){
        Project project = Project.findById(projectId);
        if(project != null){
            DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
            String name = dynamicForm.get("projectName");
            String description = dynamicForm.get("description");
            project.projectName = name;
            project.projectDescription = description;
            project.update();
            flash("p_success", "Project is updated!");
        }
        return redirect(routes.EditProject.index(projectId));
    }
    public static Result delete(Long userId, Long proId){
        User editUser = User.findByUserId(userId);
        if (editUser != null) {
            editUser.projectId = Long.parseLong("-1");
            editUser.update();
            flash("d_success", "User is successfully deleted");
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
    public static Result upload(){
        Http.MultipartFormData body = request().body().asMultipartFormData();
        File file = body.getFile("file").getFile();
        String pId = body.asFormUrlEncoded().get("projectId")[0];
        Long proId = Long.parseLong(pId);
        List<ProjectImage> imgs = ProjectImage.findImageOfProject(proId);
        if(imgs.size() >= 10){
            return status(1);
        }
        ProjectImage image = new ProjectImage(proId, file);
        return ok("success!");
    }
}
