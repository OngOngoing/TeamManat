package controllers;

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
        if(Setting.value("stopTime") == null){
            return redirect(routes.Application.mockDatabase());
        }
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
        if(!EditProject.canEditProject(_user, proId)){
            flash("error", "access denied.");
            return redirect(routes.Application.index());
        }
        Project _project = Project.findById(proId);
        Image image = Image.findByIdAndPro(imgId, _project);
        int isDefault = image.getImgType();
        if(image != null) {
            image.delete();
            List<Image> imgs = Image.findImageOfProject(_project);
            if(isDefault == Image.DEFAULT && imgs.size() != 0){
                imgs.get(0).setImgType(Image.DEFAULT);
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
        if(!EditProject.canEditProject(_user, proId)){
            flash("error", "access denied.");
            return redirect(routes.Application.index());
        }
        Project _project = Project.findById(proId);
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
            Setting.create("startTime", "07-15-10 00:00", Setting.TYPE_DATE, "Date for starting vote.");
            Setting.create("stopTime", "07-15-20 00:00", Setting.TYPE_DATE, "Date for stopping vote.");
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
        Project.create("have a Zeed","description belongs here");
        Project.create("ZeeDown","description belongs here");
        Project.create("OhZeed","description belongs here");
        Project.create("SeatBelt","description belongs here");
        Project.create("Zeedtem","description belongs here");
        Project.create("Zeedluck","description belongs here");
        Project.create("LinkZeed","description belongs here");
        Project.create("Alexzeed","description belongs here");
        Project.create("zeedX","description belongs here");
        Project.create("OA ZeeD","description belongs here");
        Project.create("systema","description belongs here");
        Project.create("zeedSheiShei","description belongs here");

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

        thisUser = User.create("b5710547247", "sanraboss", "sanrasern", "chaihetphon", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("have a Zeed"));
        thisUser = User.create("b5710503576", "magician123", "Siraphat", "Siripatumrat", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("ZeeDown"));
        thisUser = User.create("b5710547221", "***********", "warat", "narattharaksa", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("ZeeDown"));
        thisUser = User.create("b5710500402", "22010439", "nuttapol", "juntarawimon", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("have a Zeed"));
        thisUser = User.create("b5710501557", "161803399", "Nattapong", "Ekudomsuk", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("OhZeed"));
        thisUser = User.create("b5710546178", "pranger", "Kwanwan", "Tantichartkul", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("have a Zeed"));
        thisUser = User.create("b5710546593", "iguru1996", "Voraton", "Lertrattanapaisal", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("SeatBelt"));
        thisUser = User.create("b5710503380", "ship1304", "Tin", "Charoenrungutai", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("OhZeed"));
        thisUser = User.create("b5710503584", "153759asd", "Anawat", "Kaenthong", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("ZeeDown"));
        thisUser = User.create("b5710501590", "gundam", "Santitham", "Ananwattanaporn", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("ZeeDown"));
        thisUser = User.create("b5710503509", "130696", "Phuwit", "Vititayanon", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("have a Zeed"));
        thisUser = User.create("b5530300073", "sugar123456", "Keatchamai", "Manokij", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("LinkZeed"));
        thisUser = User.create("b5710503487", "1q2w3e4r", "Panupong", "Maneerut", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("ZeeDown"));
        thisUser = User.create("b5530300863", "mannysw", "SITHICHAI", "APAIJIT", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedtem"));
        thisUser = User.create("b5710501565", "sdoifjosdsfjidsfji", "Nuttapon", "Thanitsukkan", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("systema"));
        thisUser = User.create("b5710546372", "1q2w3e4r", "Pipatpol", "Tanavongchinda", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("SeatBelt"));
        thisUser = User.create("b5530300120", "wtf", "Jirayut", "wannarat", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Alexzeed"));
        thisUser = User.create("b5710546259", "123456", "TAWEERAT", "CHAIMAN", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("LinkZeed"));
        thisUser = User.create("b5710546364", "pea14733", "Pitchaya", "Namchaisiri", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("LinkZeed"));
        thisUser = User.create("b5710503606", "JellyYellj", "Anchalika", "Kraiboriruk", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("SeatBelt"));
        thisUser = User.create("b5710503347", "xtameertris", "Cheevarit", "Rodnuson", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("LinkZeed"));
        thisUser = User.create("b5710503371", "1q2w3e4r", "Nuttapon", "Lertwittayatrai", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedluck"));
        thisUser = User.create("b5710546429", "72385hf87oqfy786313291u9ur", "Supanat", "Pokturng", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("systema"));
        thisUser = User.create("b5710547204", "seogjweogjwepogwgpjo", "Patinya", "Yongyai", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("systema"));
        thisUser = User.create("b5710503363", "123456789", "Nuttinan", "Liamsakul", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedX"));
        thisUser = User.create("b5530300529", "456789", "Premthip", "Yaowapatsiri", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedtem"));
        thisUser = User.create("b5710503495", "1q2w3e4r", "Parut", "Singhapun", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("SeatBelt"));
        thisUser = User.create("b5710500194", "153359", "Chawin", "Aiemvaravutigul", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedluck"));
        thisUser = User.create("b5710503461", "357359tawei", "POCHARA", "THAIMUNGKORNPAN", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("SeatBelt"));
        thisUser = User.create("b5710501549", "1q2w3e4r", "natchanon", "roopsoung", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("SeatBelt"));
        thisUser = User.create("b5710546402", "31667", "Raksit", "Mantanacharu", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedX"));
        thisUser = User.create("b5710545015", "41312", "Chawakorn", "Aougsuk", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedluck"));
        thisUser = User.create("b5710503517", "1235789", "Methus", "Mungkijpaisan", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedtem"));
        thisUser = User.create("b5710501581", "eiei", "Mongkonchai", "Priyachiwa", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("OA ZeeD"));
        thisUser = User.create("b5710546569", "gameza2014", "Natthapol", "Kluaythes", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedX"));
        thisUser = User.create("b5710500372", "qawsedrf", "Natpapas", "Kriwichchanaporn", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("LinkZeed"));
        thisUser = User.create("b5710546666", "makhamwan", "Apichaya", "Bunchongchit", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Alexzeed"));
        thisUser = User.create("571003410", "3410", "Prachpreeya", "Kittisarapong", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedluck"));
        thisUser = User.create("b5710501573", "riitru", "pornrapee", "kajorndech", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedSheiShei"));
        thisUser = User.create("b5710546275", "Taweesoft", "Norawit", "Urailertprasert", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedtem"));
        thisUser = User.create("b5710505064", "songpon", "SONGPON", "SRISAWAI", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedluck"));
        thisUser = User.create("b5710546348", "zenzen", "Patchara", "Pattiyathanee", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedtem"));
        thisUser = User.create("b5710503339", "zxcvbn", "chawit", "chaikwampien", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Alexzeed"));
        thisUser = User.create("b5710546551", "954902912", "chinatip", "vichian", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedtem"));
        thisUser = User.create("b5710500160", "1q2w3e4r", "Kitchaphan", "Singchai", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedSheiShei"));
        thisUser = User.create("b5710500178", "sexykiki", "Jenwich", "Rattanayenjai", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedtem"));
        thisUser = User.create("b5710503321", "123456", "Jiramaetha", "SINSUEBCHUEA", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("have a Zeed"));
        thisUser = User.create("b5710505765", "123456", "Sithichai", "Tharkulmaiphol", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedtem"));
        thisUser = User.create("b5710503428", "boon1234", "Punjapon", "Kaewwisetkul", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("have a Zeed"));
        thisUser = User.create("b5710547191", "paopao1234", "napat", "thongpao", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("ZeeDown"));
        thisUser = User.create("b5710546640", "ung33224489", "Salilthip", "Phuklang", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedSheiShei"));
        thisUser = User.create("b5710501531", "1234", "Khochapak", "Sunsaksawat", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("systema"));
        thisUser = User.create("b5710546208", "xboct123", "Natanon", "Poonawagul", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Alexzeed"));
        thisUser = User.create("b5710503614", "fucktheDHT", "Udom", "Chaowanakosol", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("SeatBelt"));
        thisUser = User.create("b5710500305", "pan123", "Tunchanok", "Ngamsaowaros", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("OA ZeeD"));
        thisUser = User.create("b5710546291", "5710546291", "Prin", "Angkunanuwat", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedSheiShei"));
        thisUser = User.create("b5710504742", "1212312121", "Krikchai", "Pongtaveewould", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedluck"));
        thisUser = User.create("b5710547182", "3138299", "thanawit", "gerdprasert", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedSheiShei"));
        thisUser = User.create("b5710546186", "T123456789", "Kaninpat", "Tangittisak", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Alexzeed"));
        thisUser = User.create("b5710500291", "rebirth", "Chawanakorn", "Srilamool", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("OA ZeeD"));
        thisUser = User.create("b5710546160", "poompoom", "Kitipoom", "Kongpetch", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("OhZeed"));
        thisUser = User.create("b5710546607", "spaiizz", "Wanchanapon", "Thanwaranurak", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("OA ZeeD"));
        thisUser = User.create("b5710546305", "987654", "Parinvut", "Rochanavedya", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("OA ZeeD"));
        thisUser = User.create("b5710503398", "Thanapol2015", "Thanapol", "Rojanavichanont", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("OA ZeeD"));
        thisUser = User.create("b5710546151", "5710546151", "Kasidit", "Phoncharoen", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("OA ZeeD"));
        thisUser = User.create("b5710503525", "1q2w3e4r1234", "woraprach", "pittayapitak", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedSheiShei"));
        thisUser = User.create("b5710546313", "833006307", "Parisa", "Supitayakul", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("systema"));
        thisUser = User.create("b5710546224", "mind1234", "Natcha", "Pongsupanee", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Alexzeed"));
        thisUser = User.create("b5710546216", "1234567890", "napong", "dungduangsasitorn", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("systema"));
        thisUser = User.create("b5710546321", "5013", "Pongsakorn", "Somsri", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("OhZeed"));
        thisUser = User.create("b5710546542", "noot", "Jidapar", "Jettananurak", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedSheiShei"));
        thisUser = User.create("b5710503479", "drsumruay", "Pichayagone", "Mahaadthai", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("LinkZeed"));
        thisUser = User.create("b5710503401", "1q2w3e4r", "Nitnisit", "Janwatthanadechakul", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("systema"));
        thisUser = User.create("b5710503444", "saharat", "BOORANASIT", "PIYAVATCHARAVIJIT", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedX"));
        thisUser = User.create("b5710503541", "watcharaphat", "Watcharaphat", "Manosatiankul", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Alexzeed"));
        thisUser = User.create("b5710502511", "Pithayuth0910", "Pithayuth", "Charnsethikul", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedX"));
        thisUser = User.create("b5710503592", "1q2w3e4r", "orphitcha", "Lertpringkop", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedX"));
        thisUser = User.create("b5710503312", "12345", "kanyakorn", "jewmaidang", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("OhZeed"));
        thisUser = User.create("b5710546194", "qqqqt1234", "chinthiti", "wisetsombat", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("have a Zeed"));
        thisUser = User.create("b5710546356", "gunhappy", "Phasin", "Sarunpornkul", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("SeatBelt"));
        thisUser = User.create("b5710501522", "416721409", "Krittin", "Meenrattanakorn", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("LinkZeed"));
        thisUser = User.create("b5530300952", "985943899", "suwat", "kiatpao", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedtem"));
        thisUser = User.create("b5710546381", "66661941", "Pakpon", "Jetapai", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("ZeeDown"));
        thisUser = User.create("b5530300596", "123456", "Pantakij", "Chantanajinda", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("OhZeed"));
        thisUser = User.create("b5710546283", "12345678", "narut", "poovorakit", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedX"));
        thisUser = User.create("b5710500208", "1q2w3e4r5t6y7u8i9o", "Nuttapol", "Laoticharoen", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedX"));
        thisUser = User.create("b5710546267", "temporary", "Thongrapee", "Panyapatiphan", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("have a Zeed"));
        thisUser = User.create("b5710546631", "peeraphol", "Sorawit", "Sakulkalanuwat", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Zeedluck"));
        thisUser = User.create("b5710500186", "123456", "chawin", "kasikitpongpan", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("Alexzeed"));
        thisUser = User.create("b5710546232", "12345678", "Nattapat", "Sukpootanan", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("ZeeDown"));
        thisUser = User.create("b5710505412", "12345678", "Phosawat", "Ongmorakot", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("OhZeed"));
        thisUser = User.create("5710546437", "1q2w3e4r", "Arut", "Thanomwatana", User.NORMAL_USER);
        Groups.create(thisUser, Project.findByName("zeedSheiShei"));
    }

}
