package models;

import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class webConfig extends Model{
    @Id
    public Long id;

    public String keyName;
    public String keyValue;
    public webConfig(String k, String v){
        this.keyName = k;
        this.keyValue = v;
    }
}
