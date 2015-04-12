package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;

@Entity
public class Criteria extends Model {
    @Id
    public Long id;

    @Required
    public String name;
    public String description;

    public static Finder<Long, Criteria> find = new Finder<Long, Criteria>(Long.class, Criteria.class);
}
