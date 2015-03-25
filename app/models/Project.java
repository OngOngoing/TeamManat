package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;

@Entity
public class Project extends Model
{
    @Id
    public int id;

    @Required
    public String name;
    public String description;
    public String picture = "img/teamDummy.jpg";

    public static Project create(String name , String description)
    {
        Project project = new Project();
        project.name = name;
        project.description = description;
        project.picture = "img/teamDummy.jpg";
        project.save();
        return project;
    }

    public static Finder<Long, Project> find = new Finder<Long, Project>(Long.class, Project.class);
}
