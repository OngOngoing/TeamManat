package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;

import java.io.File;
import java.util.List;

@Entity
public class Project extends Model
{
    @Id
    public Long id;

    @Required
    public String projectName;
    public String projectDescription;
    public ProjectImage projectImage;

    public static Project create(String name , String description)
    {
        if(findByName(name) == null) {
            Project project = new Project();
            project.projectName = name;
            project.projectDescription = description;
            project.save();
            return project;
        }
        return null;
    }

    public static Project create(String name , File img , String description)
    {
        if(findByName(name) == null) {
            Project project = new Project();
            project.projectName = name;
            project.projectImage =  project.projectImage.create(project.id,img);
            project.projectDescription = description;
            project.save();
            return project;
        }
        return null;
    }

    public static Finder<Long, Project> find = new Finder<Long, Project>(Long.class, Project.class);

    public static Project findById(Long projectId){
        return find.byId(projectId);
    }

    public static Project findByName(String name){
        return find.where().eq("projectName", name).findUnique();
    }
    public static List<Project> findAll(){
        return find.all();
    }
}
