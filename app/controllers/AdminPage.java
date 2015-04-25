package controllers;

import models.*;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.data.*;
import play.libs.Json;
import play.mvc.*;

import views.html.*;
import java.util.*;

public class AdminPage extends Controller {

    @Security.Authenticated(AdminSecured.class)
    public static Result index() {
        response().setHeader("Cache-Control","no-cache");
        return ok(adminpage.render());
    }
    public static Result user(int page){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Logger.info("["+_user.getUsername()+"] user admin page.");
        List<User> users = User.findAllByPage(page);
        List<Project> projects = Project.findAll();
        int num_page= User.totalPage();
        response().setHeader("Cache-Control","no-cache");
        return ok(admin_user.render(_user,users,num_page,page,projects));
    }
    public static Result score(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Logger.info("["+_user.getUsername()+"] user admin page.");
        List<User> users = User.findAll();
        response().setHeader("Cache-Control", "no-cache");
        return ok(admin_score.render(_user,users));
    }
    public static Result rate(int page){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Logger.info("["+_user.getUsername()+"] user admin page.");
        List<User> users = User.findAll();
        List<Rate> rates = Rate.findAllByPage(page);
        int num_page= Rate.totalPage();
        response().setHeader("Cache-Control","no-cache");
        return ok(admin_rate.render(_user, users, rates, num_page, page));
    }

    public static Result vote() {
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Logger.info("[" + _user.getUsername() + "] user admin page.");
        List<User> users = User.findAll();
        List<Vote> votes = Vote.findAll();
        List<VoteCriterion> voteCs = VoteCriterion.findAll();
        response().setHeader("Cache-Control", "no-cache");
        return ok(admin_vote.render(_user, users, votes, voteCs));
    }

    @Security.Authenticated(AdminSecured.class)
    public static Result ratebyuserid(){
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        String id = dynamicForm.get("user_id");
        Long _id;
        try {
            _id = Long.parseLong(id);
        }catch (Exception e){
            return ok();
        }
        List<Map> rate_data = new ArrayList();
        List<Project> projects = Project.findAll();
        List<RateCriterion> criterions = RateCriterion.findAll();
        User user = User.findByUserId(_id);
        for(Project item : projects){
            Map _rate = new HashMap();
            _rate.put("projectName", item.getProjectName());
            List rates = new ArrayList();
            for(RateCriterion _item : criterions) {
                Map<String, String> _r = new HashMap();
                Rate rate = Rate.findByUserAndProjectAndCriteria( user, item, _item);
                String value;
                if(rate == null){
                    value = "0";
                }else{
                    value = String.valueOf(rate.getScore());
                }
                _r.put("name", _item.getName());
                _r.put("value", value);
                rates.add(_r);
            }
            _rate.put("criteria", rates);
            rate_data.add(_rate);
        }
        return ok(Json.toJson(rate_data));
    }

    @Security.Authenticated(AdminSecured.class)
    public static Result searchUser(){
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        List<User> userList = User.findByKeyword(dynamicForm.get("search_keyword"));
        List<Map<String, String>> user_data = new ArrayList();
        for(User item : userList){
            Map<String, String> i = new HashMap();
            if(item.getProject() == null){
                i.put("project", "None");
            }else{
                i.put("project", Project.findById(item.getProject().getId()).getProjectName());
            }
            i.put("projectId", item.getProject().getId().toString());
            i.put("userType", (item.getIdtype() == User.ADMINISTRATOR) ? "Administrator" : "Normal" );
            i.put("userIdType", String.valueOf(item.getIdtype()));
            i.put("username", item.getUsername());
            i.put("lastname", item.getLastname());
            i.put("firstname", item.getFirstname());
            i.put("userid", item.getId().toString());
            user_data.add(i);
        }
        return ok(Json.toJson(user_data));
    }
    public static Result comment()
    {
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Logger.info("["+_user.getUsername()+"] user admin page.");

        List<Comment> comments = Comment.findAll();
        response().setHeader("Cache-Control","no-cache");
        return ok(admin_comment.render(_user,comments));

    }
    public static Result project(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Logger.info("["+_user.getUsername()+"] user admin page.");
        List<User> users = User.findAll();
        List<Project> projects = Project.findAll();
        response().setHeader("Cache-Control","no-cache");
        return ok(admin_project.render(_user,users,projects));
    }
    public static Result criteria(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Logger.info("["+_user.getUsername()+"] user admin page.");
        List<RateCriterion> rateCriteria = RateCriterion.findAll();
        List<VoteCriterion> voteCriteria = VoteCriterion.findAll();
        response().setHeader("Cache-Control","no-cache");
        return ok(admin_criteria.render(_user, rateCriteria, voteCriteria));
    }
    public static Result systemConfig(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Logger.info("["+_user.getUsername()+"] user admin page.");
        List<Setting> webconfig = Setting.findAll();
        response().setHeader("Cache-Control","no-cache");
        return ok(admin_systemconfig.render(_user, webconfig));
    }

    @Security.Authenticated(AdminSecured.class)
    public static Result addUser(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        String firstname = dynamicForm.get("firstname");
        String lastname = dynamicForm.get("lastname");
        String username = dynamicForm.get("username");
        String password = dynamicForm.get("password");
        String idType = dynamicForm.get("idtype");
        User user = User.create(username, password, firstname,lastname,Integer.parseInt(idType));
        Logger.info("["+_user.getUsername()+"] add new user.("+user.getId()+")");
        response().setHeader("Cache-Control", "no-cache");
        flash("user_add_success", "User added");
        return redirect(routes.AdminPage.user(1));
    }

    @Security.Authenticated(AdminSecured.class)
    public static Result addProject(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Project project = Form.form(Project.class).bindFromRequest().get();
        project.save();
        Logger.info("[" + _user.getUsername() + "] add new project.(" + project.getId()+")");
        response().setHeader("Cache-Control", "no-cache");
        flash("project_add_success", "Project added");
        return redirect(routes.AdminPage.project());
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result addRateCriterion(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        RateCriterion rateC = Form.form(RateCriterion.class).bindFromRequest().get();
        rateC.save();
        Logger.info("[" + _user.getUsername() + "] add new Rate Criterion.(" + rateC.getId() + ")");
        response().setHeader("Cache-Control", "no-cache");
        flash("rate_criterion_add_success", "Rating criterion added");
        return redirect(routes.AdminPage.criteria());
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result addVoteCriterion(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        VoteCriterion voteC = Form.form(VoteCriterion.class).bindFromRequest().get();
        voteC.save();
        Logger.info("[" + _user.getUsername() + "] add new Vote Criterion.(" + voteC.getId() + ")");
        response().setHeader("Cache-Control", "no-cache");
        flash("vote_criterion_add_success", "Voting criterion added");
        return redirect(routes.AdminPage.criteria());
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result deleteUsers(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Map<String, String[]> map = request().body().asFormUrlEncoded();
        String[] checkedVal = map.get("id"); // get selected topics

        if(checkedVal == null) {
            return redirect(routes.AdminPage.user(1));
        }
        int count=0;
        for(String userId : checkedVal) {
            User user = User.findByUserId(Long.parseLong(userId));
            List<Rate> rates = Rate.findListByUser(user);
            List<Vote> votes = Vote.findByUser(user);
            Logger.info("[" + _user.getUsername() + "] delete user.(" + user.getId() + ")"+user.getUsername());
            user.delete();
            count++;
            for(Vote vote : votes ) {
                Logger.info("[" + _user.getUsername() + "] delete vote.(" + vote.getId() + ")("+vote.getProject().getProjectName()+")");
                vote.delete();
            }
            for(Rate rate : rates ) {
                Logger.info("[" + _user.getUsername() + "] delete rate.(" + rate.getId() + ")("+rate.getProject().getId()+")");
                rate.delete();
            }
        }
        flash("user_delete_success",count+ " Users deleted");
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.AdminPage.user(1));
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result deleteRate(Long id){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Rate rate = Rate.findById(id);
        Logger.info("[" + _user.getUsername() + "] delete rate.(" + rate.getId() + ")("+rate.getProject().getId()+")");
        rate.delete();
        flash("rate_delete_success", "Rate deleted");
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.AdminPage.rate(1));
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result deleteVote(Long id){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Vote vote = Vote.findById(id);
        Logger.info("[" + _user.getUsername() + "] delete rate.(" + vote.getId() + ")("+vote.getProject().getId()+")");
        vote.delete();
        flash("vote_delete_success", "Vote deleted");
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.AdminPage.vote());
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result deleteComment(Long userId , Long projectId){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Project _project = Project.findById(projectId);
        Comment comment = Comment.findByUserAndProject(_user, _project);
        Logger.info("[" + _user.getUsername() + "] delete comment.(" + comment.getId() + ")("+comment.getProject().getId()+")");
        comment.delete();
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.AdminPage.comment());

    }
    @Security.Authenticated(AdminSecured.class)
    public static Result deleteRateCriterion(Long id){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        RateCriterion rateCs = RateCriterion.findById(id);
        if(rateCs != null) {
            Logger.info("[" + _user.getUsername() + "] delete rate criterion.(" + rateCs.getId() + ")("+rateCs.getName()+")");
            rateCs.delete();
        }
        flash("rate_criterion_delete_success","Rating criterion deleted");
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.AdminPage.criteria());
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result deleteVoteCriterion(Long id){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        VoteCriterion voteCs = VoteCriterion.findById(id);
        if(voteCs != null) {
            Logger.info("[" + _user.getUsername() + "] delete Vote criterion.(" + voteCs.getId() + ")(" + voteCs.getName() + ")");
            voteCs.delete();
        }

        flash("vote_criterion_delete_success","Voting criterion deleted");
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.AdminPage.criteria());
    }

    @Security.Authenticated(AdminSecured.class)
    public static Result editUser(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        User newuser = Form.form(User.class).bindFromRequest().get();
        User olduser = User.findByUserId(newuser.getId());
        olduser.setUsername(newuser.getUsername());
        olduser.setFirstname(newuser.getFirstname());
        olduser.setLastname(newuser.getLastname());
        if(!newuser.getPassword().equals("")) {
            olduser.setPassword(BCrypt.hashpw(newuser.getPassword(), BCrypt.gensalt()));
        }
        olduser.setIdtype(newuser.getIdtype());
        olduser.update();
        Logger.info("[" + _user.getUsername() + "] edite user.(" + olduser.getId() + ")" + olduser.getUsername());
        flash("user_edit_success", "User edited");
        response().setHeader("Cache-Control","no-cache");
        return redirect(routes.AdminPage.user(1));
    }
    @Security.Authenticated(AdminSecured.class)
    public static Result saveSetting(){
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        List<Setting> settings = Setting.findAll();
        for(Setting item : settings){
            Setting.update(item.getKeyName(), dynamicForm.get(item.getKeyName()));
        }
        flash("setting_save_success","Settings saved");
        response().setHeader("Cache-Control", "no-cache");
        return redirect(routes.AdminPage.systemConfig());
    }
}
