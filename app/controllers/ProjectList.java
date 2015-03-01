package controllers;

import models.Project;
import play.mvc.*;
import views.html.*;

public class ProjectList extends Controller {

    public static Result index() {
        return ok(projectlist.render(Project.find.all()));
    }

}
