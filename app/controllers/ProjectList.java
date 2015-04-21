package controllers;

import models.*;
import play.mvc.*;
import views.html.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class ProjectList extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {
        Long userId = Long.parseLong(session().get("userId"));
        User user = User.findByUserId(userId);
        Settings setting = Settings.value("stopTime");
        Map<Long,Integer> mappedRate = Rate.getRateAndProjectIdMappingByUserId(userId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-y HH:mm");
        Calendar calendar = Calendar.getInstance();
        String date = setting.keyValue;
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
        response().setHeader("Cache-Control","no-cache");
        return ok(projectlist.render(Project.findAll(),RateCriterion.findAll(),mappedRate,user,_time));
    }

}
