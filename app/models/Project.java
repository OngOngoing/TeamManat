package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import org.mindrot.jbcrypt.BCrypt;
import play.db.ebean.*;

@Entity
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

    public static Finder<Long, Project> find = new Finder<Long, Project>(Long.class, Project.class);
}
