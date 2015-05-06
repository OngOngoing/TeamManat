package controllers;

import models.*;
import play.mvc.*;
import views.html.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.List;
public class ProjectList extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {
        Long userId = Long.parseLong(session().get("userId"));
        User user = User.findByUserId(userId);
        Setting setting = Setting.value("stopTime");
        Map<Long,Integer> mappedRate = Rate.getRateAndProjectMappingByUser(user);
        SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-y HH:mm");
        Calendar calendar = Calendar.getInstance();
        String date = setting.getKeyValue();
        String _time = "";
        try {
            calendar.setTime(dateFormat.parse(date));
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            _time = String.format("%d/%02d/%02d %02d:%02d:%02d", year, month + 1, day, hour, minute, second);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Inbox> comments = Inbox.findAllByReceiver(user);
        int voteLeft = VoteCriterion.findAll().size() - Vote.findByUser(user).size();
        response().setHeader("Cache-Control","no-cache");
        return ok(projectlist.render(Project.findAll(),RateCriterion.findAll(),mappedRate,VoteCriterion.findAll(), voteLeft, user,_time, comments));
    }

}
