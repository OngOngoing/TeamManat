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

    public RateCriterion(String name, String description){
        this.name = name;
        this.description = description;
    }

    public static RateCriterion create(String name, String description){
        RateCriterion rate = new RateCriterion(name, description);
        rate.name = name;
        rate.description = description;
        rate.save();
        return rate;
    }

    public static List<RateCriterion> findAll(){
    	return find.all();
    }

    public static List<RateCriterion> findById(Long userId){
        return find.where().eq("id", userId).findList();
    }
}
