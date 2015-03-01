package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import org.mindrot.jbcrypt.BCrypt;
import play.db.ebean.*;

@Entity
public class Team extends Model {

    @Id
    public long id;

    @Required
    public String teamName;
    public String description;

    public static Team create(String teamName, String description)
    {
        Team team = new Team();
        team.teamName = teamName;
        team.description = description;
        team.save();
        return team;
    }
}
