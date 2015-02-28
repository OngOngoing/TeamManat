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

    public static Team create(String teamName)
    {
        Team team = new Team();
        team.teamName = teamName;
        team.save();
        return team;
    }
}
