package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;
import java.util.List;

@Entity
public class VoteCriterion extends Model {
    @Id
    public Long id;

    @Required
    public Long type; // 0 = Rate , 1 = Vote
    public String name;
    public String description;

    public static Finder<Long, VoteCriterion> find = new Finder<Long, VoteCriterion>(Long.class, VoteCriterion.class);

    public static List<VoteCriterion> findAll(){
    	return find.all();
    }

    public static List<VoteCriterion> findByType(long id){
        return find.where().eq("type", id).findList();
    }
}
