package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import org.mindrot.jbcrypt.BCrypt;
import play.db.ebean.*;

/**
 * Created by Mistral on 2/26/15 AD.
 */
public class Project extends Model
{
    @Id
    public Long id;

    @Required
    public String name;
    public String description;
    public Team team;

    public static Project create(String name , String description , Team team)
    {
        Project project = new Project();
        project.name = name;
        project.description = description;
        project.team = team;
        project.save();
        return project;
    }
}
