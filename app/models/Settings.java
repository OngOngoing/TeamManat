package models;

import javax.persistence.*;

import play.Logger;
import play.db.ebean.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
@Entity
public class Settings extends Model{

    public final static int TYPE_DATE = 1, TYPE_INTEGER = 2, TYPE_STRING = 3, TYPE_BOOLEAN = 4;
    @Id
    public Long id;
    @Column(unique=true)
    public String keyName;
    public String keyValue;
    public int idType; // 1 = Date ; 2 = Integer ; 3 = String ; 4 = boolean
    public String description;

    private static Finder<Long, Settings> find = new Finder<Long, Settings>(Long.class, Settings.class);

    public Settings(String k, String v, int type, String d){
        this.keyName = k;
        this.keyValue = v;
        this.idType = type;
        this.description = d;
    }
    public static Settings create(String k, String v, int type, String description){
        if(Settings.find.where().eq("keyName",k).findUnique()==null) {
            Settings con = new Settings(k, v, type, description);
            con.save();
            return con;
        }
        return null;
    }
    public static void update(String n, String v){
        Settings con = Settings.value(n);
        con.keyValue = v;
        con.save();
    }
    public static List<Settings> findAll(){
        return find.all();
    }
    public static Settings value(String name){
        return find.where().eq("keyName", name).findUnique();
    }

    public static boolean isTimeUp() {

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Bangkok"));
        DateFormat dateFormat = new SimpleDateFormat("M-d-y HH:mm");

        Settings endingTimeSetting = Settings.value("stopTime");
        Calendar endingCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Bangkok"));
        try {
            endingCalendar.setTime(dateFormat.parse(endingTimeSetting.keyValue));
        } catch (ParseException e) {
            Logger.info("Cannot parse setting time to Calendar Object");
        }
        return calendar.compareTo(endingCalendar) > 0;
    }
}
