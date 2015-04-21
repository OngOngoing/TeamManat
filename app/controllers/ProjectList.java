package controllers;

import models.*;
import play.mvc.*;
import views.html.*;

import java.util.Map;

public class ProjectList extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {
        Long userId = Long.parseLong(session().get("userId"));
        User user = User.findByUserId(userId);
        Settings setting = Settings.value("stopTime");
        Map<Long,Boolean> mappedRate = Rate.getRatedVoteAndProjectIdMappingByUserId(userId);
        response().setHeader("Cache-Control","no-cache");
        return ok(projectlist.render(Project.findAll(),mappedRate,user,setting));
    }

}
