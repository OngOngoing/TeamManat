package controllers;

import models.Project;
import play.*;
import play.mvc.*;
import views.html.projectlist;

public class ProjectList extends Controller {

    public static Result index() {
        return ok(projectlist.render("Project List", Project.find.all()));
    }

}
