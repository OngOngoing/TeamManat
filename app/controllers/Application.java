package controllers;

import java.text.*;
import java.util.*;
import models.*;
import play.libs.Json;
import play.mvc.*;
import play.data.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        String user = session("userId");
        if(user != null) {
            return redirect(routes.ProjectList.index());
        }else{
            return ok(login.render(Form.form(Login.class)));
        }
    }

    public static class Login {

        public String username;
        public String password;

        public String validate() {
            if(User.authenticate(username, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }
    }

    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            User currentUser = User.findByUsername(loginForm.get().username);
            session("userId", String.valueOf(currentUser.id));
            return redirect(routes.ProjectList.index());
        }
    }

    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Application.index());
    }

    public static Result login(){
        return ok(login.render(Form.form(Login.class)));
    }

    public static Result getImgs(Long proId){
        List<ProjectImage> images = ProjectImage.findImageOfProject(proId);
        return ok(Json.toJson(images));
    }
    public static Result getImg(Long imgId){
        ProjectImage image = ProjectImage.findById(imgId);
        return ok(image.getData()).as("image");
    }
    // MockDataBase for testing
    public static Result mockDatabase(){
        //Web App setting
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-y HH:mm");
        Settings.create("startTime", dateFormat.format(calendar.getTime()), Settings.TYPE_DATE, "Date for starting vote.");

        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
        Settings.create("stopTime", dateFormat.format(calendar.getTime()), Settings.TYPE_DATE, "Date for stopping vote.");
        Settings.create("siteType", "1", Settings.TYPE_INTEGER, "1 for vote, 2 for rate.");
        //Mock user and project
        User.create("admin", "admin", "Admin's Firstname", "Admin's Lastname", User.ADMINISTRATOR);
        User.create("test1", "test1", "TestFirstname1", "TestLastName1", User.NORMAL_USER); // Add new account : username => test1 password => test1
        User.create("test2", "test2", "TestFirstname2", "TestLastName2", User.NORMAL_USER); // Add new account : username => test2 password => test2
        Project.create("Project Test 1","Description of project test 1");
        Project.create("Project Test 2","Description of project test 2");
        Project.create("Project Test 3","Description of project test 3");
        flash("success", "A initial data for test the system was created.");
        return redirect(routes.Application.index());
    }

}
