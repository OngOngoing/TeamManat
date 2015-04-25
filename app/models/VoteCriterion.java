package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;
import java.util.List;

@Entity
public class VoteCriterion extends Model {
    @Id
    private Long id;

    @Required
    private String name;
    private String description;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "criterion")
    private List<Vote> votes;

    private static Finder<Long, VoteCriterion> find = new Finder<Long, VoteCriterion>(Long.class, VoteCriterion.class);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

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

    public static VoteCriterion findByCriterionName(String name) {
        return find.where().eq("name",name).findUnique();
    }

    public static List<VoteCriterion> findAll() {
        return find.all();
    }

    public static VoteCriterion findById(Long c) {
        return find.byId(c);
    }
}