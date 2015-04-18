package controllers;

import models.*;
import play.Logger;
import play.data.*;
import play.mvc.*;
import views.html.adminpage;

import java.util.*;

public class AdminPage extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {
        Long userId = Long.parseLong(session().get("userId"));
        List<Rate> rates = Rate.findAll();
        List<User> users = User.findAll();
        List<Settings> webconfig = Settings.findAll();

        User thisUser = User.findByUserId(userId);

        if(thisUser.idtype == User.ADMINISTRATOR) {
            return ok(adminpage.render(users, Project.findAll(), rates, webconfig,RateCriterion.findAll(),VoteCriterion.findAll()));
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
    public static Result addRateCriterion(){
        RateCriterion rateC = Form.form(RateCriterion.class).bindFromRequest().get();
        rateC.save();
        return redirect(routes.AdminPage.index()+"#criterions");
    }
    public static Result addVoteCriterion(){
        VoteCriterion voteC = Form.form(VoteCriterion.class).bindFromRequest().get();
        voteC.save();
        return redirect(routes.AdminPage.index()+"#criterions");
    }

    public static Result deleteUsers(){

        Map<String, String[]> map = request().body().asFormUrlEncoded();
        String[] checkedVal = map.get("id"); // get selected topics

        if(checkedVal == null) {
            return redirect(routes.AdminPage.index()+"#users");
        }

        for(String userId : checkedVal) {
            User user = User.findByUserId(Long.parseLong(userId));
            List<Rate> rates = Rate.findListByUserId(Long.parseLong(userId));
            List<Vote> votes = Vote.findByUserId(Long.parseLong(userId));
            user.delete();
            for(Vote vote : votes ) {
                vote.delete();
            }
            for(Rate rate : rates ) {
                rate.delete();
            }
        }

        return redirect(routes.AdminPage.index()+"#users");
    }

    public static Result deleteRate(Long id){
        List<Rate> rates = Rate.findById(id);
        for(Rate rate : rates)
        {
            rate.delete();
        }
        return redirect(routes.AdminPage.index()+"#rates");
    }

    public static Result deleteRateCriterion(Long id){
        RateCriterion rateCs = RateCriterion.findById(id);
        if(rateCs != null) {
            rateCs.delete();
        }
        return redirect(routes.AdminPage.index()+"#criterions");
    }

    public static Result deleteVoteCriterion(Long id){
        VoteCriterion voteCs = VoteCriterion.findById(id);
        if(voteCs != null) {
            voteCs.delete();
        }
        return redirect(routes.AdminPage.index()+"#criterions");
    }


    public static Result editUser(){
        User newuser = Form.form(User.class).bindFromRequest().get();
        User olduser = User.findByUserId(newuser.id);
        olduser.firstname = newuser.firstname;
        olduser.lastname = newuser.lastname;
        olduser.username = newuser.username;
        olduser.password = newuser.password;
        olduser.idtype = newuser.idtype;
        olduser.projectId = newuser.projectId;
        olduser.update();
        return redirect(routes.AdminPage.index()+"#users");
    }

    public static Result saveSetting(){
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        List<Settings> settings = Settings.findAll();
        for(Settings item : settings){
            Settings.update(item.keyName, dynamicForm.get(item.keyName));
        }
        return redirect(routes.AdminPage.index()+"#configs");
    }

}
