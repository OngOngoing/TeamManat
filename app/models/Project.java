package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;

import java.util.List;

@Entity
public class Project extends Model
{
    @Id
    private Long id;

    @Required
    private String projectName;
    private String projectDescription;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "project")
    private List<Groups> memnbers;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "project")
    private List<Image> images;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "project")
    private List<Comment> comments;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "project")
    private List<Rate> rates;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "project")
    private List<Vote> votes;

    private static Finder<Long, Project> find = new Finder<Long, Project>(Long.class, Project.class);

    public static Project create(String name , String description) {
        if(findByName(name) == null) {
            Project project = new Project();
            project.projectName = name;
            project.projectDescription = description;
            project.save();
            return project;
        }
        return null;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<Groups> getMemnbers() {
        return memnbers;
    }

    public void setMemnbers(List<Groups> memnbers) {
        this.memnbers = memnbers;
    }

    public static void deleteProject(Project pro){
        if(pro == null){
            Project project = findById(Long.parseLong("-1"));
            project.delete();
            return;
        }
        Project project = findById(pro.getId());
        project.delete();
    }

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
