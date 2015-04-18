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
    public String name;
    public String description;

    public static VoteCriterion create(String name, String description) {
        VoteCriterion thisCriterion = findByCriterionName(name);
        if(thisCriterion == null) {
            VoteCriterion criterion = new VoteCriterion();
            criterion.name = name;
            criterion.description = description;
            criterion.save();
            return criterion;
        }
        thisCriterion.description = description;
        thisCriterion.update();
        return thisCriterion;
    }

    public static Finder<Long, VoteCriterion> find = new Finder<Long, VoteCriterion>(Long.class, VoteCriterion.class);

    public static VoteCriterion findByCriterionName(String name) {
        return find.where().eq("name",name).findUnique();
    }

    public static List<VoteCriterion> findAll() {
        return find.all();
    }

    public static VoteCriterion findById(Long id) {
        return find.byId(id);
    }
}