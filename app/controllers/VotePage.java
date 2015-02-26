package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class VotePage extends Controller {

    public static Result index() {
        return ok(votepage.render("VotePage"));
    }

}
