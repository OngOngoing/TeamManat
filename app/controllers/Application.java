package controllers;

import play.mvc.*;
import play.data.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("username", loginForm.get().username);
            return redirect(routes.Application.index());
        }
    }

    public static Result login(){
        return ok(login.render(Form.form(Login.class)));
    }

}
