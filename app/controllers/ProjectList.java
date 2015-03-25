package controllers;

import models.*;
import play.mvc.*;
import views.html.*;

import java.util.List;

public class ProjectList extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {
        Long userId = Long.parseLong(session().get("userId"));
        List votes = Rate.find.where().eq("userId", userId).findList();
        User user = User.find.byId(userId);

        return ok(projectlist.render(Project.find.all(),votes,user));
    }

}
