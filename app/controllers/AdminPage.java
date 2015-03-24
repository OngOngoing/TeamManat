package controllers;

import models.Project;
import models.User;
import models.Vote;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.adminpage;
import views.html.projectlist;

import java.util.List;

public class AdminPage extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {
        Long userId = Long.parseLong(session().get("userId"));
        List votes = Vote.find.all();
        List users = User.find.all();

        return ok(adminpage.render(users, Project.find.all(), votes));
    }

    public static Result addUser(){
        User user = Form.form(User.class).bindFromRequest().get();
        user.save();
        return redirect(routes.AdminPage.index()+"#users");
    }

}
