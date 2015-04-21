package controllers;

import models.Project;
import models.Rate;
import models.ProjectImage;
import models.User;
import org.apache.commons.collections.map.HashedMap;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.*;
import views.html.editproject;

import java.io.File;
import java.util.List;

import play.libs.Json;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
/**
 * Created by Chin on 4/15/2015.
 */
public class EditProject extends Controller {
    @Security.Authenticated(Secured.class)
    public static Result index(Long projectId){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        if(!canEditProject(_user, projectId)) {
            flash("error", "Access Denied.");
            return redirect(routes.ProjectList.index());
        }
        Project project = Project.findById(projectId);
        List<User> members = User.findByTeam(projectId);
        List<ProjectImage> images = ProjectImage.findImageOfProject(projectId);
        response().setHeader("Cache-Control","no-cache");
        return ok(editproject.render(_user, project, members, images));
    }
    @Security.Authenticated(Secured.class)
    public static Result addMember(Long projectId){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        if(!canEditProject(_user, projectId)){
            flash("error", "Access Denied.");
            return redirect(routes.Application.index());
        }
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        String input = dynamicForm.get("user-id");
        if(!input.matches("[0-9]+")){
            flash("error", "Please select some user!");
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
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.EditProject.index(projectId));
    }
    @Security.Authenticated(Secured.class)
    public static Result edit(Long projectId){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        if(!canEditProject(_user, projectId)){
            flash("error", "Access Denied.");
            return redirect(routes.Application.index());
        }
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
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.EditProject.index(projectId));
    }
    @Security.Authenticated(Secured.class)
    public static Result removeUser(Long userId, Long proId){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        if(!canEditProject(_user, proId)){
            flash("error", "Access Denied.");
            return redirect(routes.Application.index());
        }
        User editUser = User.findByUserId(userId);
        if (editUser != null) {
            editUser.projectId = Long.parseLong("-1");
            Logger.info("["+editUser.username+"] project id = -1.");
            editUser.update();
            flash("success", "User is successfully deleted");
        }
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.EditProject.index(proId));
    }
    @Security.Authenticated(Secured.class)
    public static Result searchUser(){
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        List<User> userList = User.findByKeyword(dynamicForm.get("search_keyword"));
        List<Map<String, String>> user_data = new ArrayList();
        for(User item : userList){
            Map<String, String> i = new HashedMap();
            i.put("lastname", item.lastname);
            i.put("firstname", item.firstname);
            i.put("id", item.id.toString());
            user_data.add(i);
        }
        return ok(Json.toJson(user_data));
    }

    public static boolean canEditProject(User user, Long projectId){
        if(user.idtype == User.ADMINISTRATOR)
            return true;
        if(user.projectId == projectId)
            return true;
        return false;
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result deleteProject(){
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        Long proId = Long.parseLong(dynamicForm.get("projectId"));
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Logger.info("["+_user.username+"] delete project:"+proId);
        Project _pro = Project.findById(proId);
        if(_pro != null) {
            String name = _pro.projectName;
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
        flash("d_success", "Project is successfully deleted");
        return redirect(routes.ProjectList.index());
    }
    @Security.Authenticated(Secured.class)
    public static Result upload(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Http.MultipartFormData body = request().body().asMultipartFormData();
        File file = body.getFile("file").getFile();
        String pId = body.asFormUrlEncoded().get("projectId")[0];
        Long proId = Long.parseLong(pId);
        if(_user.projectId != proId && _user.idtype != User.ADMINISTRATOR){
            flash("error", "access denied.");
            return redirect(routes.Application.index());
        }
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
