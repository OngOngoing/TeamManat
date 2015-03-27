package controllers;

import models.*;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.adminpage;

import java.util.List;
import java.util.Map;

public class AdminPage extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {
        Long userId = Long.parseLong(session().get("userId"));
        List rates = Rate.find.all();
        List users = User.find.all();

        User thisUser = User.find.byId(userId);

        if(thisUser.idtype == 0) {
            return ok(adminpage.render(users, Project.find.all(), rates));
        }
        else {
            return redirect(routes.ProjectList.index());
        }
    }

    public static Result addUser(){
        User user = Form.form(User.class).bindFromRequest().get();
        user.save();
        return redirect(routes.AdminPage.index()+"#users");
    }
    public static Result addProject(){
        Project project = Form.form(Project.class).bindFromRequest().get();
        project.save();
        return redirect(routes.AdminPage.index()+"#projects");
    }

    public static Result deleteUsers(){

        Map<String, String[]> map = request().body().asFormUrlEncoded();
        String[] checkedVal = map.get("id"); // get selected topics

        for(String id : checkedVal) {
            User user = User.find.byId(Long.parseLong(id));
            Logger.debug("DELETE "+ user.id);
            user.delete();
        }

        return redirect(routes.AdminPage.index()+"#users");
    }

}
