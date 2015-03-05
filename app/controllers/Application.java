package controllers;

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

}
