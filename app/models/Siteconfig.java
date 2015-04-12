package models;

import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class Siteconfig extends Model{

    public final static int TYPE_DATE = 1, TYPE_INTEGER = 2, TYPE_STRING = 3, TYPE_BOOLEAN = 4;
    @Id
    public Long id;
    @Column(unique=true)
    public String keyName;
    public String keyValue;
    public int idType; // 1 = Date ; 2 = Integer ; 3 = String ; 4 = boolean
    public String description;
    public Siteconfig(String k, String v, int type, String d){
        this.keyName = k;
        this.keyValue = v;
        this.idType = type;
        this.description = d;
    }
    public static Siteconfig create(String k, String v, int type, String description){
        if(Siteconfig.find.where().eq("keyName",k).findUnique()==null) {
            Siteconfig con = new Siteconfig(k, v, type, description);
            con.save();
            return con;
        }
        return null;
    }
    public static Finder<Long, Siteconfig> find = new Finder<Long, Siteconfig>(Long.class, Siteconfig.class);
}
