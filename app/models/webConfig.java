package models;

import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class WebConfig extends Model{
    @Id
    public Long id;

    public String keyName;
    public String keyValue;
    public WebConfig(String k, String v){
        this.keyName = k;
        this.keyValue = v;
    }
}
