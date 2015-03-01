package controllers;

import play.*;
import play.mvc.*;
import models.Project;

import views.html.*;


public class VotePage extends Controller {

    public static Result index(Long id) {
        return ok(votepage.render("VotePage",Project.find.byId(id)));
    }

}
