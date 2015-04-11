package controllers;

import com.avaje.ebean.Ebean;
import models.*;
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

    // MockDataBase for testing
    public static Result mockDatabase(){
        User.create("admin", "admin", "Admin's Firstname", "Admin's Lastname", 0, -1);
        User.create("test1", "test1", "TestFirstname1", "TestLastName1", 1, 1); // Add new account : username => test1 password => test1
        User.create("test2", "test2", "TestFirstname2", "TestLastName2", 1, 1); // Add new account : username => test2 password => test2
        Project.create("Project Test 1","Description of project test 1");
        Project.create("Project Test 2","Description of project test 2");
        Project.create("Project Test 3","Description of project test 3");
        flash("success", "A initial data for test the system was created.");
        return redirect(routes.Application.index());
    }

}
