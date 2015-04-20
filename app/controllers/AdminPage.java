package controllers;

import models.*;
import play.Logger;
import play.data.*;
import play.mvc.*;
import views.html.adminpage;

import java.util.*;

public class AdminPage extends Controller {

    @Security.Authenticated(AdminSecured.class)
    public static Result index() {
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Logger.info("["+_user.username+"] user admin page.");
        List<Rate> rates = Rate.findAll();
        List<User> users = User.findAll();
        List<Settings> webconfig = Settings.findAll();
        return ok(adminpage.render(users, Project.findAll(), rates, webconfig,RateCriterion.findAll(),VoteCriterion.findAll()));
    }

    @Security.Authenticated(AdminSecured.class)
    public static Result addUser(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        User user = Form.form(User.class).bindFromRequest().get();
        user.save();
        Logger.info("["+_user.username+"] add new user.("+user.id+")");
        return redirect(routes.AdminPage.index()+"#users");
    }

    @Security.Authenticated(AdminSecured.class)
    public static Result addProject(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Project project = Form.form(Project.class).bindFromRequest().get();
        project.save();
        Logger.info("["+_user.username+"] add new project.("+project.id+")");
        return redirect(routes.AdminPage.index()+"#projects");
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result addRateCriterion(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        RateCriterion rateC = Form.form(RateCriterion.class).bindFromRequest().get();
        rateC.save();
        Logger.info("[" + _user.username + "] add new Rate Criterion.(" + rateC.id + ")");
        return redirect(routes.AdminPage.index()+"#criterions");
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result addVoteCriterion(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        VoteCriterion voteC = Form.form(VoteCriterion.class).bindFromRequest().get();
        voteC.save();
        Logger.info("[" + _user.username + "] add new Vote Criterion.(" + voteC.id + ")");
        return redirect(routes.AdminPage.index()+"#criterions");
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result deleteUsers(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Map<String, String[]> map = request().body().asFormUrlEncoded();
        String[] checkedVal = map.get("id"); // get selected topics

        if(checkedVal == null) {
            return redirect(routes.AdminPage.index()+"#users");
        }

        for(String userId : checkedVal) {
            User user = User.findByUserId(Long.parseLong(userId));
            List<Rate> rates = Rate.findListByUserId(Long.parseLong(userId));
            List<Vote> votes = Vote.findByUserId(Long.parseLong(userId));
            Logger.info("[" + _user.username + "] delete user.(" + user.id + ")"+user.username);
            user.delete();
            for(Vote vote : votes ) {
                Logger.info("[" + _user.username + "] delete rate.(" + vote.id + ")("+vote.projectId+")");
                vote.delete();
            }
            for(Rate rate : rates ) {
                Logger.info("[" + _user.username + "] delete rate.(" + rate.id + ")("+rate.projectId+")");
                rate.delete();
            }
        }

        return redirect(routes.AdminPage.index()+"#users");
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result deleteRate(Long id){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Rate rate = Rate.findById(id);
        Logger.info("[" + _user.username + "] delete rate.(" + rate.id + ")("+rate.projectId+")");
        rate.delete();
        return redirect(routes.AdminPage.index()+"#rates");
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result deleteRateCriterion(Long id){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        RateCriterion rateCs = RateCriterion.findById(id);
        if(rateCs != null) {
            List<Rate> rates = Rate.findRateByCriterionId(rateCs.id);
            for(Rate rate : rates){
                Logger.info("[" + _user.username + "] delete rate.(" + rate.id + ")("+rate.projectId+")");
                rate.delete();
            }
            Logger.info("[" + _user.username + "] delete rate criterion.(" + rateCs.id + ")("+rateCs.name+")");
            rateCs.delete();
        }
        return redirect(routes.AdminPage.index()+"#criterions");
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result deleteVoteCriterion(Long id){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        VoteCriterion voteCs = VoteCriterion.findById(id);
        if(voteCs != null) {
            List<Vote> votes = Vote.findVotesByCriterionId(voteCs.id);
            for(Vote vote : votes){
                Logger.info("[" + _user.username + "] delete vote.(" + vote.id + ")("+vote.projectId+")");
                vote.delete();
            }
            Logger.info("[" + _user.username + "] delete rate criterion.(" + voteCs.id + ")("+voteCs.name+")");
            voteCs.delete();
        }
        return redirect(routes.AdminPage.index()+"#criterions");
    }

    @Security.Authenticated(AdminSecured.class)
    public static Result editUser(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        User newuser = Form.form(User.class).bindFromRequest().get();
        User olduser = User.findByUserId(newuser.id);
        olduser.firstname = newuser.firstname;
        olduser.lastname = newuser.lastname;
        olduser.username = newuser.username;
        olduser.password = newuser.password;
        olduser.idtype = newuser.idtype;
        olduser.projectId = newuser.projectId;
        olduser.update();
        Logger.info("[" + _user.username + "] edite user.("+olduser.id+")"+olduser.username);
        return redirect(routes.AdminPage.index()+"#users");
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result saveSetting(){
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        List<Settings> settings = Settings.findAll();
        for(Settings item : settings){
            Settings.update(item.keyName, dynamicForm.get(item.keyName));
        }
        return redirect(routes.AdminPage.index()+"#configs");
    }
}
