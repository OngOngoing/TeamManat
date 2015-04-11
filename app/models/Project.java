package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;
import java.util.List;

@Entity
public class Project extends Model
{
    @Id
    public int id;

    @Required
    public String name;
    public String description;
    public String picture = "img/TeamDummy.jpg";

    public static Project create(String name , String description)
    {
        if(Project.find.where().eq("name", name).findUnique() == null) {
            Project project = new Project();
            project.name = name;
            project.description = description;
            project.picture = "img/TeamDummy.jpg";
            project.save();
            return project;
        }
        return null;
    }

    public static Finder<Long, Project> find = new Finder<Long, Project>(Long.class, Project.class);

    public static Project findById(Long projectId){
        return find.byId(projectId);
    }

    public static List<Project> findAll(){
        return find.all();
    }
}
