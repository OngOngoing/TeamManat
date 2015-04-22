package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;
import play.mvc.Result;

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
    public static void deleteProject(Long proId){
        Project project = findById(proId);
        project.delete();
    }

    public static Finder<Long, Project> find = new Finder<Long, Project>(Long.class, Project.class);

    public static Project findById(Long projectId){
        if(projectId == -1) {
            Project dummyProject = new Project();
            dummyProject.id = projectId;
            dummyProject.projectName = "No Vote";
            dummyProject.projectDescription = "";
            return dummyProject;
        }
        return find.byId(projectId);
    }

    public static Project findByName(String name){
        return find.where().eq("projectName", name).findUnique();
    }
    public static List<Project> findAll(){
        return find.all();
    }
}
