package controllers;

import models.*;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.data.*;
import play.libs.Json;
import play.mvc.*;

import views.html.*;
import java.util.*;

public class registerController extends Controller {
    public static Result addUser(){
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        String firstname = dynamicForm.get("firstname");
        String lastname = dynamicForm.get("lastname");
        String username = dynamicForm.get("username");
        String password = dynamicForm.get("password");
        String idType = dynamicForm.get("idtype");
        User user = User.create(username, password, firstname,lastname,Integer.parseInt(idType));
        Logger.info("["+user.getUsername()+"] has regis.("+user.getId()+")");
        response().setHeader("Cache-Control", "no-cache");
        flash("registration_complete", "User added");
        return redirect(routes.ProjectList.index());
    }
}