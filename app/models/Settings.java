package models;

import javax.persistence.*;
import play.db.ebean.*;
import java.util.List;

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
    public static List<Settings> findAll(){
        return find.all();
    }
    public static Settings value(String name){
        return find.where().eq("keyName", name).findUnique();
    }
}
