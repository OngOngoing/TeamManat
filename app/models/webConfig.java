package models;

import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class WebConfig extends Model{

    public final static int TYPE_DATE = 1, TYPE_INTEGER = 2, TYPE_STRING = 3, TYPE_BOOLEAN = 4;
    @Id
    public Long id;
    @Column(unique=true)
    public String keyName;
    public String keyValue;
    public int idType; // 1 = Date ; 2 = Integer ; 3 = String ; 4 = boolean
    public String description;
    public WebConfig(String k, String v, int type, String d){
        this.keyName = k;
        this.keyValue = v;
        this.idType = type;
        this.description = d;
    }
    public static WebConfig create(String k, String v, int type, String description){
        if(WebConfig.find.where().eq("keyName",k).findUnique()==null) {
            WebConfig con = new WebConfig(k, v, type, description);
            con.save();
            return con;
        }
        return null;
    }
    public static Finder<Long, WebConfig> find = new Finder<Long, WebConfig>(Long.class, WebConfig.class);
}
