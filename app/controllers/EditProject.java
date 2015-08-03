package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import org.apache.commons.collections.map.HashedMap;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.*;
import views.html.editproject;

import java.io.File;
import java.util.List;

import play.libs.Json;
import java.util.Map;
import java.util.ArrayList;

public class EditProject extends Controller {
    @Security.Authenticated(Secured.class)
    public static Result index(Long projectId, String h){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        if(!canEditProject(_user, projectId)) {
            flash("error", "Access Denied.");
            return redirect(routes.ProjectList.index());
        }
        Project project = Project.findById(projectId);
        List<User> members = User.findByProject(project);
        List<Image> images = Image.findImageOfProject(project);
        response().setHeader("Cache-Control","no-cache");
        return ok(editproject.render(_user, project, members, images, h));
    }
    @Security.Authenticated(Secured.class)
    public static Result addMember(Long projectId, String h){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        if(!canEditProject(_user, projectId)){
            flash("error", "Access Denied.");
            return redirect(routes.Application.index());
        }
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        String input = dynamicForm.get("user-id");
        if(!input.matches("[0-9]+")){
            flash("error", "Please select some user!");
            return redirect(routes.EditProject.index(projectId, h));
        }
        Long userId = Long.parseLong(input);
        User editUser = User.findByUserId(userId);
        if(editUser == null){
            flash("error", "User not found");
            return redirect(routes.EditProject.index(projectId, h));
        }
        Project project = Project.findById(projectId);
        if(editUser == null){
            flash("error", "Project not found");
            return redirect(routes.EditProject.index(projectId, h));
        }
        Groups g = Groups.create(editUser, project);
        Logger.info("["+editUser.getUsername()+"] project = "+project.getProjectName()+"("+project.getId()+")");
        flash("success", "User is added!");
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.EditProject.index(projectId, h));
    }
    @Security.Authenticated(Secured.class)
    public static Result edit(Long projectId, String h){
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
            project.setProjectName(name);
            project.setProjectDescription(description);
            project.update();
            Logger.info("["+User.findByUserId(Long.parseLong(session("userId")+"")).getUsername()+"] edit project("+projectId+")"+project.getProjectName());
            flash("success", "Project is updated!");
        }
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.RateProject.index(projectId));
//        return redirect(routes.EditProject.index(projectId ,h));
    }
    @Security.Authenticated(Secured.class)
    public static Result removeUser(Long userId, Long proId ,String h){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        if(!canEditProject(_user, proId)){
            flash("error", "Access Denied.");
            return redirect(routes.Application.index());
        }
        User editUser = User.findByUserId(userId);
        if (editUser != null) {
            editUser.getGroup().delete();
            Logger.info("[" + editUser.getUsername() + "] is removed from project");
            editUser.update();
            flash("success", "User is successfully deleted");
        }
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.EditProject.index(proId, h));
    }
    @Security.Authenticated(Secured.class)
    public static Result searchUser(){
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        List<User> userList = User.findByKeyword(dynamicForm.get("search_keyword"));
        List<Map<String, String>> user_data = new ArrayList();
        for(User item : userList){
            Map<String, String> i = new HashedMap();
            i.put("lastname", item.getLastname());
            i.put("firstname", item.getFirstname());
            i.put("id", item.getId().toString());
            user_data.add(i);
        }
        return ok(Json.toJson(user_data));
    }

    public static boolean canEditProject(User user, Long projectId){
        if(user.getIdtype() == User.ADMINISTRATOR)
            return true;
        if(user.getGroup() == null)
            return false;
        if(user.getGroup().getProject().getId() == projectId)
            return true;
        return false;
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result deleteProject(){
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        Long proId = Long.parseLong(dynamicForm.get("projectId"));
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Logger.info("["+_user.getUsername()+"] delete project:"+proId);
        Project _pro = Project.findById(proId);
        if(_pro != null) {
            String name = _pro.getProjectName();
            _pro.delete();
            Logger.info("Project ("+proId+")"+name+" is deleted");
        }
        flash("success", "Project is successfully deleted");
        return redirect(routes.ProjectList.index());
    }
    @Security.Authenticated(Secured.class)
    public static Result upload(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Http.MultipartFormData body = request().body().asMultipartFormData();
        File file = body.getFile("file").getFile();
        String pId = body.asFormUrlEncoded().get("projectId")[0];
        Long proId = Long.parseLong(pId);
        Project pro = Project.findById(proId);
        if(!canEditProject(_user, proId)) {
            flash("error", "access denied.");
            return redirect(routes.Application.index());
        }
        if(!body.getFile("file").getContentType().split("/")[0].equals("image")){
            ObjectNode result = Json.newObject();
            result.put("error_code","2");
            result.put("message", "Wrong file type");
            return ok(result);
        }
        List<Image> imgs = Image.findImageOfProject(pro);
        if(imgs.size() >= 10){
            ObjectNode result = Json.newObject();
            result.put("error_code","1");
            result.put("message", "Attachment limit");
            return ok(result);
        }
        Image image;
        if(imgs.size() == 0) {
            image = new Image(pro, file, Image.DEFAULT);
        }else{
            image = new Image(pro, file, Image.NORMAL);
        }
        System.out.println("Upload new Image");
        Logger.info(Project.findById(proId).getProjectName()+" upload new image["+image.getId()+"]");
        return ok("success!");
    }
}
