package models;

import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class Config extends Model{
    @Id
    public int id;

    public String key;
    public String value;
    public Config(String k, String v){
        this.key = k;
        this.value = v;
    }
}
