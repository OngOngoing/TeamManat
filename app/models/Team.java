package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;

/**
 * Created by Mistral on 2/26/15 AD.
 */
public class Team extends Model {

    @Id
    public long id;

    @Required
    public User[] users;

    public static Team create(User[] users)
    {
        Team team = new Team();
        team.users = users;
        team.save();
        return team;
    }
}
