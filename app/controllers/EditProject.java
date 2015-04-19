package controllers;

import models.Project;
import models.Rate;
import models.ProjectImage;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.*;
import views.html.editproject;

import java.io.File;
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
        List<ProjectImage> images = ProjectImage.findImageOfProject(projectId);
        return ok(editproject.render(user, project, members, images));
    }
    @Security.Authenticated(Secured.class)
    public static Result addMember(Long projectId){
        User user = User.findByUserId(Long.parseLong(session().get("userId")));
        if(user.idtype != User.ADMINISTRATOR)
            return redirect(routes.ProjectList.index());
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        String input = dynamicForm.get("user-id");
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
        Logger.info("["+editUser.username+"] project id = "+projectId);
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
            Logger.info("["+User.findByUserId(Long.parseLong(session("userId")+"")).username+"] edit project("+projectId+")"+project.projectName);
            flash("success", "Project is updated!");
        }
        return redirect(routes.EditProject.index(projectId));
    }
    public static Result removeUser(Long userId, Long proId){
        User editUser = User.findByUserId(userId);
        if (editUser != null) {
            editUser.projectId = Long.parseLong("-1");
            Logger.info("["+editUser.username+"] project id = -1.");
            editUser.update();
            flash("success", "User is successfully deleted");
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
    public static Result deleteProject(){
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        Long proId = Long.parseLong(dynamicForm.get("projectId"));
        Project _pro = Project.findById(proId);
        String name = _pro.projectName;
        if(_pro != null) {
            List<Rate> _rates = Rate.findListByProjectId(_pro.id);
            for (Rate item : _rates) {
                Logger.info("rate ["+item.id+"] is delete. ProId:"+item.projectId+" UserId:"+item.userId);
                item.delete();
            }
            List<User> _users = User.findByTeam(_pro.id);
            for (User item : _users) {
                item.projectId = Long.parseLong("-1");
                Logger.info("["+item.username+"] project id = -1.");
                item.update();
            }
            _pro.delete();
            Logger.info("Project ("+proId+")"+name+" is deleted");
        }
        flash("success", "Project is successfully deleted");
        return redirect(routes.ProjectList.index());
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
        ProjectImage image;
        if(imgs.size() == 0) {
            image = new ProjectImage(proId, file, ProjectImage.DEFAULT);
        }else{
            image = new ProjectImage(proId, file, ProjectImage.NORMAL);
        }
        Logger.info(Project.findById(proId).projectName+" upload new image["+image.Id+"]");
        return ok("success!");
    }
}
