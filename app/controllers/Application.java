package controllers;

import play.mvc.*;
import play.data.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        String user = session("username");
        if(user != null) {
            return redirect(routes.ProjectList.index());
        }else{
            return ok(index.render(Form.form(Login.class)));
        }
    }

    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(index.render(loginForm));
        } else {
            session().clear();
            session("username", loginForm.get().username);
            return redirect(routes.ProjectList.index());
        }
    }

    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
                routes.Application.index()
        );
    }

    public static Result login(){
        return ok(login.render(Form.form(Login.class)));
    }

}
