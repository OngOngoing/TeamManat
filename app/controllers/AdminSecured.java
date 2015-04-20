package controllers;

import models.User;
import play.mvc.*;
import play.mvc.Http.*;

public class AdminSecured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        String _userId = ctx.session().get("userId");
        Long userId = Long.parseLong(_userId);
        User currentUser = User.findByUserId(userId);
        if(currentUser == null)
            return null;
        if(currentUser.idtype != User.ADMINISTRATOR)
            return null;
        return ctx.session().get("userId");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        ctx.flash().put("error","Access Denied");
        return redirect(routes.ProjectList.index());
    }

}