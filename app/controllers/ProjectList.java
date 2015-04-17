package controllers;

import models.*;
import play.mvc.*;
import views.html.*;

import java.util.List;
import java.util.ArrayList;
public class ProjectList extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {
        Long userId = Long.parseLong(session().get("userId"));
        List rates = Rate.findListByUserId(userId);
        User user = User.findByUserId(userId);
        Settings setting = Settings.value("stopTime");
        return ok(projectlist.render(Project.findAll(),rates,user,setting));
    }

}
