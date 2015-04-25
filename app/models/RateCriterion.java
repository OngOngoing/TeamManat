package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;
import java.util.List;

@Entity
public class RateCriterion extends Model {
    @Id
    private Long id;

    @Required
    private String name;
    private String description;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "criterion")
    private List<Rate> rates;

    private static Finder<Long, RateCriterion> find = new Finder<Long, RateCriterion>(Long.class, RateCriterion.class);

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

    public List<Rate> getRates() {
        return rates;
    }

    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }

    public static RateCriterion create(String name, String description){
        if(findByCriterionName(name) == null) {
            RateCriterion rate = new RateCriterion();
            rate.name = name;
            rate.description = description;
            rate.save();
            return rate;
        }
        return  null;
    }

    public static RateCriterion findByCriterionName(String name) {
        return find.where().eq("name", name).findUnique();
    }

    public static List<RateCriterion> findAll(){
    	return find.all();
    }

    public static RateCriterion findById(Long c){
        return find.byId(c);
    }
}
