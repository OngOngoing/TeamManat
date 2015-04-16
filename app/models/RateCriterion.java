package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;
import java.util.List;

@Entity
public class RateCriterion extends Model {
    @Id
    public Long id;

    @Required
    public String name;
    public String description;

    public static Finder<Long, RateCriterion> find = new Finder<Long, RateCriterion>(Long.class, RateCriterion.class);

    public static List<RateCriterion> findAll(){
    	return find.all();
    }
}
