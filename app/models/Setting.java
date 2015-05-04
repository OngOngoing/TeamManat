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
public class Setting extends Model{

    public final static int TYPE_DATE = 1, TYPE_INTEGER = 2, TYPE_STRING = 3, TYPE_BOOLEAN = 4;
    @Id
    private Long id;
    @Column(unique=true)
    private String keyName;
    private String keyValue;
    private int idType; // 1 = Date ; 2 = Integer ; 3 = String ; 4 = boolean
    private String description;

    private static Finder<Long, Setting> find = new Finder<Long, Setting>(Long.class, Setting.class);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Setting create(String k, String v, int type, String description){
        if(Setting.find.where().eq("keyName",k).findUnique()==null) {
            Setting con = new Setting();
            con.description = description;
            con.idType = type;
            con.keyName = k;
            con.keyValue = v;
            con.save();
            return con;
        }
        return null;
    }
    public static void update(Setting s, String v){
        s.setKeyValue(v);
        s.update();
    }
    public static List<Setting> findAll(){
        return find.all();
    }
    public static Setting value(String name){
        return find.where().eq("keyName", name).findUnique();
    }

    public static boolean isTimeUp() {

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Bangkok"));
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
        DateFormat dateFormat = new SimpleDateFormat("M-d-y HH:mm");

        Setting endingTimeSetting = Setting.value("stopTime");
        Calendar endingCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Bangkok"));
        try {
            endingCalendar.setTime(dateFormat.parse(endingTimeSetting.keyValue));
            endingCalendar.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
        } catch (ParseException e) {
            Logger.error("Cannot parse setting time to Calendar Object");
        }
        return calendar.compareTo(endingCalendar) > 0;
    }
}
