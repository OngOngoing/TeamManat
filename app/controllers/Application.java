package controllers;

import java.text.*;
import java.util.*;
import models.*;
import play.libs.Json;
import play.mvc.*;
import play.data.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        String user = session("userId");
        if(user != null) {
            return redirect(routes.ProjectList.index());
        }else{
            return ok(login.render(Form.form(Login.class)));
        }
    }

    public static class Login {

        public String username;
        public String password;

        public String validate() {
            if(User.authenticate(username, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }
    }

    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            User currentUser = User.findByUsername(loginForm.get().username);
            session("userId", String.valueOf(currentUser.id));
            return redirect(routes.ProjectList.index());
        }
    }

    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Application.index());
    }

    public static Result login(){
        return ok(login.render(Form.form(Login.class)));
    }

    public static Result getImgs(Long proId){
        List<ProjectImage> images = ProjectImage.findImageOfProject(proId);
        return ok(Json.toJson(images));
    }
    public static Result getImg(Long imgId){
        ProjectImage image = ProjectImage.findById(imgId);
        return ok(image.getData()).as("image");
    }
    // MockDataBase for testing
    public static Result mockDatabase(){
        //Web App setting
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-y HH:mm");
        Settings.create("startTime", dateFormat.format(calendar.getTime()), Settings.TYPE_DATE, "Date for starting vote.");

        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
        Settings.create("stopTime", dateFormat.format(calendar.getTime()), Settings.TYPE_DATE, "Date for stopping vote.");
        Settings.create("siteType", "1", Settings.TYPE_INTEGER, "1 for vote, 2 for rate.");
        //Mock user and project
        User.create("admin", "admin", "Admin's Firstname", "Admin's Lastname", User.ADMINISTRATOR);
        User.create("test1", "test1", "TestFirstname1", "TestLastName1", User.NORMAL_USER); // Add new account : username => test1 password => test1
        User.create("test2", "test2", "TestFirstname2", "TestLastName2", User.NORMAL_USER); // Add new account : username => test2 password => test2

        mockProjectsInDivision1();
        mockUsersInDivision1();
        mockUsersInDivision2();
        mockCriteria();

        flash("success", "A initial data for test the system was created.");
        return redirect(routes.Application.index());
    }

    public static void mockCriteria() {

        VoteCriterion.create("Best Application","Best app for use at ExceedCamp.");

        RateCriterion.create("Ease of use","how easy is to understand how to vote and actually vote? How easy is navigation? Is there clear feedback on what you have done so far?");
        RateCriterion.create("Reliability","can you smoothly login and complete voting? Does app prevent submission of invalid data?");
        RateCriterion.create("Completeness","does it have the features required by customer?");
        RateCriterion.create("Security ","does application prevent unauthorized access? Can you logout of application? Hint: Look at the project's routes file on Github. Try to directly access the URLs without logging in.");
        RateCriterion.create("Quality of UI", "does it have consistent look? Can use on different size screens? Is important info clearly displayed? Is navigation clearly provided (not using browser 'back' button)?");

    }

    public static void mockProjectsInDivision1() {
        Project.create("TeamSaint4","Team Saint4's description belongs here");
        Project.create("TeamManat","TeamManat's description belongs here");
        Project.create("Team2Big2Slim","Team2Big2Slim's description belongs here");
        Project.create("TeamFatCat","TeamFatCat's description belongs here");

    }

    public static void mockUsersInDivision1() {
        //TeamSaint4
        User.create("5610545765", "muninthorn.t", "Muninthorn", "Thongnuc", User.NORMAL_USER);
        User.create("5610545781", "runyasak.c", "Runyasak", "Chaengnaimuang", User.NORMAL_USER);
        User.create("5610545706", "nara.s", "Nara", "Surawit", User.NORMAL_USER);
        User.create("5610546788", "vasupol.c", "Vasupol", "Charmethakul", User.NORMAL_USER);
        User.create("5610545803", "wuttipong.k", "Wuttipong", "Khemphetjetsada", User.NORMAL_USER);


        //TeamManat
        User.create("5610546231", "chinnaporn.s", "Chinnaporn", "Soonue", User.NORMAL_USER);
        User.create("5610545811", "sorrawit.c", "Sorrawit", "Chancherngkit", User.NORMAL_USER);
        User.create("5610546290", "worapon.o", "Worapon", "Olanwanitchakul", User.NORMAL_USER);
        User.create("5610545013", "niti.p", "Niti", "Petcharatmora", User.NORMAL_USER);
        User.create("5610546800", "supason.k", "Supason", "Kotanut", User.NORMAL_USER);


        //Team2Big2Slim
        User.create("5610545722", "punpikorn.r", "Punpikorn", "Rattanawirojkul", User.NORMAL_USER);
        User.create("5610545668", "nathakorn.s", "Nathakorn", "Sukumsirichart", User.NORMAL_USER);
        User.create("5610545731", "piyaphat.t", "Piyaphat", "Tulakoop", User.NORMAL_USER);
        User.create("5610546711", "nabhat.y", "Nabhat", "Yuktadatta", User.NORMAL_USER);
        User.create("5610545676", "nut.k", "Nut", "Kaewnak", User.NORMAL_USER);

        //TeamFatCat
        User.create("5610546702", "jiratchaya.i", "Jiratchaya", "Intaragumhaeng", User.NORMAL_USER);
        User.create("5610545684", "nichamon.h", "Nichamon", "Hanidhikul", User.NORMAL_USER);
        User.create("5610546222", "chonnipa.k", "Chonnipa", "Kittisiriprasert", User.NORMAL_USER);
        User.create("5610546257", "natchanon.c", "Natchanon", "Charoensuk", User.NORMAL_USER);
        User.create("5610546699", "kittipat.p", "Kittipat", "Proomdirek", User.NORMAL_USER);

    }

    public static void mockUsersInDivision2() {
        // TEAMGG
        User.create("b5610545757", "manatsawin.h", "Manatsawin", "Hanmongkolchai", User.NORMAL_USER);
        User.create("b5610546770", "varis.k", "Varis", "Kritpolchai", User.NORMAL_USER);
        User.create("b5610545749", "pongsachon.p", "Pongsachon", "Pornsriniyom", User.NORMAL_USER);

        //TeamTheFrank
        User.create("b5610546681", "kittinan.n", "Kittinan", "Napapongsa", User.NORMAL_USER);
        User.create("b5610546281", "perawith.j", "Perawith", "Jarunithi", User.NORMAL_USER);
        User.create("b5610546753", "nathas.y", "Nathas", "Yingsukamol", User.NORMAL_USER);
        User.create("b5610545692", "thanachote.v", "Thanachote", "Visetsuthimont", User.NORMAL_USER);
        User.create("b5610546729", "thanaphon.k", "Thanaphon", "Ketsin", User.NORMAL_USER);

        //TeamMalee
        User.create("b5610545048", "tanatorn.a", "Tanatorn", "Assawaamnuey", User.NORMAL_USER);
        User.create("b5610545714", "patawat.w", "Patawat", "Watakul", User.NORMAL_USER);
        User.create("b5610546745", "thanyaboon.t", "Thanyaboon", "Tovorapan", User.NORMAL_USER);
        User.create("b5610546761", "mintra.t", "Mintra", "Thirasirisin", User.NORMAL_USER);

        //TEAMJDED
        User.create("b5410545044", "waranyu.r", "Waranyu", "Rerkdee", User.NORMAL_USER);
        User.create("b5410545052", "supayut.r", "Supayut", "Raksuk", User.NORMAL_USER);
        User.create("b5410546334", "wasin.h", "Wasin", "Hawaree", User.NORMAL_USER);
        User.create("b5410546393", "akkarawit.p", "Akkarawit", "Piyawin", User.NORMAL_USER);
        User.create("b5410547594", "nachanok.s", "Nachanok", "Suktarachan", User.NORMAL_USER);

        //STAFF
        User.create("b5510546166", "sarun.wo", "Sarun", "Wongtanakarn", User.NORMAL_USER);
        User.create("b5410545036", "thai.p", "Thai", "Pangsakulyanont", User.NORMAL_USER);
        User.create("fengjeb", "james.b", "Jim", "Brucker", User.NORMAL_USER);
        User.create("geedev", "keeratipong.u", "Keeratipong", "Ukachoke", User.NORMAL_USER);
    }

}
