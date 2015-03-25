package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;

@Entity
public class Rate extends Model {
    @Id
    public long id;
    @Required
    public int score;
    public String type;
    public Long userId;
    public Long projectId;
    public String comment;

    public static Rate create(int score , String type , Long userId , Long projectId, String comment)
    {
        Rate rate = new Rate();
        rate.score = score;
        rate.type = type;
        rate.userId = userId;
        rate.projectId = projectId;
        rate.comment = comment;
        rate.save();
        return rate;
    }

    public static Finder<Long, Rate> find = new Finder<Long, Rate>(Long.class, Rate.class);
}
