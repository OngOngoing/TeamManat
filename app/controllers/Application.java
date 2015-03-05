package controllers;

import models.Project;
import models.Team;
import models.User;
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

    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            User currentUser = User.find.where().eq("username", loginForm.get().username).findUnique();
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
    private static Result mockDatabase(){
        User.create("test1", "test1", "NameTest", 1, 1); // Add new account : username => test1 password => test1
        User.create("test2", "test2", "TestName", 1, 1); // Add new account : username => test2 password => test2
        Project.create("Project Test 1","Description of project test 1", (long) 1);
        Project.create("Project Test 2","Description of project test 2", (long) 1);
        Project.create("Project Test 3","Description of project test 3", (long) 2);
        Team.create("TeamManat", "TeamManat description");
        Team.create("TeamGG", "TeamGG description");
        flash("success", "A initial data for test the system was created.");
        return redirect(routes.Application.index());
    }

}
