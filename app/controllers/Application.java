package controllers;

import java.text.*;
import java.util.*;

import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.*;
import play.data.*;

import util.Authenticator;
import views.html.*;

public class Application extends Controller {

    public static Result index() {
        String user = session("userId");
        if(user != null) {
            return redirect(routes.ProjectList.index());
        }else{
            return ok(login.render("",Setting.isTimeUp()));
        }
    }

    public static Result authenticate() {
        DynamicForm dynamicForm = new DynamicForm().bindFromRequest();
        String username = dynamicForm.get("username");
        String password = dynamicForm.get("password");

        User user = Authenticator.getInstance().authenticate(username,password);
        if (user == null) {
            Logger.error("Login failed : cannot found user.");
            flash("error", "Username and password are incorrect.");
            return badRequest(login.render(username,Setting.isTimeUp()));
        }
        Logger.info("["+username+"] login success.");
        session().clear();
        User currentUser = User.findByUsername(username);
        session("userId", String.valueOf(currentUser.getId()));
        return redirect(routes.ProjectList.index());
    }

    public static Result logout() {
        if(session().get("userId") == null){
            Logger.info("[Guest]'ve been logged out.");
            return redirect(routes.Application.index());
        }
        Logger.info("["+User.findByUserId(Long.parseLong(session().get("userId"))).getUsername()+"]'ve been logged out.");
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Application.index());
    }

    public static Result login(){
        return ok(login.render("",Setting.isTimeUp()));
    }
    @Security.Authenticated(Secured.class)
    public static Result getImgs(Long proId){
        Project project = Project.findById(proId);
        List<Image> images = Image.findImageOfProject(project);
        List<Map<String, Long>> img = new ArrayList();
        for(Image item : images){
            Map<String, Long> detail = new HashMap();
            detail.put("Id", item.getId());
            detail.put("projectId", item.getProject().getId());
            detail.put("imgType", Long.parseLong(item.getImgType()+""));
            img.add(detail);
        }
        return ok(Json.toJson(img));
    }
    @Security.Authenticated(Secured.class)
    public static Result getImg(Long imgId){
        Image image = Image.findById(imgId);
        return ok(image.getData()).as("image");
    }
    @Security.Authenticated(Secured.class)
    public static Result deleteImg(Long imgId, Long proId, String h){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Project _project = Project.findById(proId);
        if(_user.getGroup().getProject().getId() != proId && _user.getIdtype() != User.ADMINISTRATOR){
            flash("error", "access denied.");
            return redirect(routes.Application.index());
        }
        Image image = Image.findByIdAndPro(imgId, _project);
        if(image != null) {
            image.delete();
            List<Image> imgs = Image.findImageOfProject(_project);
            if(imgs.size() != 0){
                imgs.get(0).setImgType(image.DEFAULT);
                imgs.get(0).save();
            }
            flash("success", "Screenshot is deleted.");
        }else {
            flash("error", "Con't found Screenshot.");
        }
        return redirect(routes.EditProject.index(proId, h));
    }
    @Security.Authenticated(Secured.class)
    public static Result inboxMobile(){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        List<Comment> comments = Inbox.findCommentByReceiver(_user);
        List<Inbox> inboxs = Inbox.findUnreadByReceiver(_user);
        for(Inbox inbox: inboxs){
            inbox.setRead(Inbox.READ);
            inbox.update();
        }
        return ok(inbox_mobile.render(_user, comments));
    }
    @Security.Authenticated(Secured.class)
    public static Result inboxRead(Long id){
        Inbox _inbox = Inbox.findById(id);
        if(_inbox != null){
            if(_inbox.isRead() == Inbox.UNREAD) {
                _inbox.setRead(Inbox.READ);
                _inbox.update();
                return ok("READ");
            }else{
                _inbox.setRead(Inbox.UNREAD);
                _inbox.update();
                return ok("UNREAD");
            }
        }
        return badRequest();
    }
    @Security.Authenticated(Secured.class)
    public static Result setImgDefault(Long imgId, Long proId,String h){
        User _user = User.findByUserId(Long.parseLong(session("userId")));
        Project _project = Project.findById(proId);
        if(_user.getGroup().getProject().getId() != proId && _user.getIdtype() != User.ADMINISTRATOR){
            flash("error", "access denied.");
            return redirect(routes.Application.index());
        }
        Image oldimg = Image.getDefaultImage(_project);
        Image newimg = Image.findById(imgId);
        if(newimg == null){
            flash("error", "Can't found image.");
            return redirect(routes.EditProject.index(proId, h));
        }
        if(oldimg != null) {
            oldimg.setImgType(Image.NORMAL);
            oldimg.save();
        }
        newimg.setImgType(Image.DEFAULT);
        newimg.save();
        flash("success", "Project is updated.");
        return redirect(routes.EditProject.index(proId, h));
    }
    // MockDataBase for testing
    public static Result mockDatabase(){
        //Web App setting
        if(User.findByUsername("admin") == null) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Bangkok"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-y HH:mm");
            Setting.create("startTime", dateFormat.format(calendar.getTime()), Setting.TYPE_DATE, "Date for starting vote.");

            calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
            Setting.create("stopTime", dateFormat.format(calendar.getTime()), Setting.TYPE_DATE, "Date for stopping vote.");
            Setting.create("siteType", "1", Setting.TYPE_INTEGER, "1 for vote, 2 for rate.");
            //Mock user and project
            User.create("admin", "admin", "Admin's Firstname", "Admin's Lastname", User.ADMINISTRATOR);
            User.create("test1", "test1", "TestFirstname1", "TestLastName1", User.NORMAL_USER); // Add new account : username => test1 password => test1
            User.create("test2", "test2", "TestFirstname2", "TestLastName2", User.NORMAL_USER); // Add new account : username => test2 password => test2

            mockProjects();
            mockUsersInDivision1();
            mockUsersInDivision2();
            mockCriteria();

            flash("success", "A initial data for test the system was created.");
        }
        return redirect(routes.Application.index());
    }

    public static void mockCriteria() {

        VoteCriterion.create("Most beautiful UI","");
        VoteCriterion.create("Most feature-complete","");
        VoteCriterion.create("Easiest to use","");

        RateCriterion.create("Ease of use","how easy is to understand how to vote and actually vote? How easy is navigation? Is there clear feedback on what you have done so far?");
        RateCriterion.create("Reliability","can you smoothly login and complete voting? Does app prevent submission of invalid data?");
        RateCriterion.create("UI/UX Quality", "does it have consistent look? Can use on different size screens? Is important info clearly displayed? Is navigation clearly provided (not using browser 'back' button)?");
        RateCriterion.create("Security ","does application prevent unauthorized access? Can you logout of application? Hint: Look at the project's routes file on Github. Try to directly access the URLs without logging in.");
        RateCriterion.create("Suitability","does it have the features required by customer?");
    }

    public static void mockProjects() {
        Project.create("Team2Big2Slim","Team2Big2Slim's description belongs here");
        Project.create("TeamJDED","TeamJDED's description belongs here");
        Project.create("TeamMalee","TeamMalee's description belongs here");
        Project.create("TeamTheFrank","TeamTheFrank's description belongs here");

    }

    public static void mockUsersInDivision1() {

        User thisUser;

        //TeamSaint4
        User.create("b5610545765", "muninthorn.t", "Muninthorn", "Thongnuch", User.NORMAL_USER);
        User.create("b5610545781", "runyasak.c", "Runyasak", "Chaengnaimuang", User.NORMAL_USER);
        User.create("b5610545706", "nara.s", "Nara", "Surawit", User.NORMAL_USER);
        User.create("b5610546788", "vasupol.c", "Vasupol", "Charmethakul", User.NORMAL_USER);
        User.create("b5610545803", "wuttipong.k", "Wuttipong", "Khemphetjetsada", User.NORMAL_USER);


        //TeamManat
        User.create("b5610546231", "chinnaporn.s", "Chinnaporn", "Soonue", User.NORMAL_USER);
        User.create("b5610545811", "sorrawit.c", "Sorrawit", "Chancherngkit", User.NORMAL_USER);
        User.create("b5610546290", "worapon.o", "Worapon", "Olanwanitchakul", User.NORMAL_USER);
        User.create("b5610545013", "niti.p", "Niti", "Petcharatmora", User.NORMAL_USER);
        User.create("b5610546800", "supason.k", "Supason", "Kotanut", User.NORMAL_USER);


        //Team2Big2Slim
        thisUser = User.create("b5610545722", "punpikorn.r", "Punpikorn", "Rattanawirojkul", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Team2Big2Slim"));
        thisUser = User.create("b5610545668", "nathakorn.s", "Nathakorn", "Sukumsirichart", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Team2Big2Slim"));
        thisUser = User.create("b5610545731", "piyaphat.t", "Piyaphat", "Tulakoop", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Team2Big2Slim"));
        thisUser = User.create("b5610546711", "nabhat.y", "Nabhat", "Yuktadatta", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Team2Big2Slim"));
        thisUser = User.create("b5610545676", "nut.k", "Nut", "Kaewnak", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Team2Big2Slim"));




        //TeamFatCat
        User.create("b5610546702", "jiratchaya.i", "Jiratchaya", "Intaragumhaeng", User.NORMAL_USER);
        User.create("b5610545684", "nichamon.h", "Nichamon", "Hanidhikul", User.NORMAL_USER);
        User.create("b5610546222", "chonnipa.k", "Chonnipa", "Kittisiriprasert", User.NORMAL_USER);
        User.create("b5610546257", "natchanon.c", "Natchanon", "Charoensuk", User.NORMAL_USER);
        User.create("b5610546699", "kittipat.p", "Kittipat", "Proomdirek", User.NORMAL_USER);

    }

    public static void mockUsersInDivision2() {
        User thisUser;

        // TEAMGG
        User.create("b5610545757", "manatsawin.h", "Manatsawin", "Hanmongkolchai", User.NORMAL_USER);
        User.create("b5610546770", "varis.k", "Varis", "Kritpolchai", User.NORMAL_USER);
        User.create("b5610545749", "pongsachon.p", "Pongsachon", "Pornsriniyom", User.NORMAL_USER);

        //TeamTheFrank
        thisUser = User.create("b5610546681", "kittinan.n", "Kittinan", "Napapongsa", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("TeamTheFrank"));
        thisUser = User.create("b5610546281", "perawith.j", "Perawith", "Jarunithi", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("TeamTheFrank"));
        thisUser = User.create("b5610546753", "nathas.y", "Nathas", "Yingsukamol", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("TeamTheFrank"));
        thisUser = User.create("b5610545692", "thanachote.v", "Thanachote", "Visetsuthimont", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("TeamTheFrank"));
        thisUser = User.create("b5610546729", "thanaphon.k", "Thanaphon", "Ketsin", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("TeamTheFrank"));

        //TeamMalee
        thisUser = User.create("b5610545048", "tanatorn.a", "Tanatorn", "Assawaamnuey", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("TeamMalee"));
        thisUser = User.create("b5610545714", "patawat.w", "Patawat", "Watakul", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("TeamMalee"));
        thisUser = User.create("b5610546745", "thanyaboon.t", "Thanyaboon", "Tovorapan", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("TeamMalee"));
        thisUser = User.create("b5610546761", "mintra.t", "Mintra", "Thirasirisin", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("TeamMalee"));

        //TEAMJDED
        thisUser = User.create("b5410545044", "waranyu.r", "Waranyu", "Rerkdee", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("TeamJDED"));
        thisUser = User.create("b5410545052", "supayut.r", "Supayut", "Raksuk", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("TeamJDED"));
        thisUser = User.create("b5410546334", "wasin.h", "Wasin", "Hawaree", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("TeamJDED"));
        thisUser = User.create("b5410546393", "akkarawit.p", "Akkarawit", "Piyawin", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("TeamJDED"));
        thisUser = User.create("b5410547594", "nachanok.s", "Nachanok", "Suktarachan", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("TeamJDED"));

        //STAFF
        User.create("b5510546166", "sarun.wo", "Sarun", "Wongtanakarn", User.NORMAL_USER);
        User.create("b5410545036", "thai.p", "Thai", "Pangsakulyanont", User.NORMAL_USER);
        User.create("fengjeb", "james.b", "Jim", "Brucker", User.NORMAL_USER);
        User.create("geedev", "keeratipong.u", "Keeratipong", "Ukachoke", User.NORMAL_USER);
    }

}
