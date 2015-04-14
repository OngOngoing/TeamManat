package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;
import java.util.List;

@Entity
public class ProjectImage extends Model {
    @Id
    private Long Id;
    @Required
    private Long projectId;
    @Lob
    private byte[] image;
    private static Finder<Long, ProjectImage> find = new Finder<Long, ProjectImage>(Long.class, ProjectImage.class);
    private ProjectImage(Long projectId, byte[] image){
        this.projectId = projectId;
        this.image = image;
    }
    public ProjectImage create(Long projectId, byte[] image){
        ProjectImage img = new ProjectImage(projectId, image);
        img.save();
        return img;
    }
    public List<ProjectImage> findAllPicByProjectId(Long projectId){
        return find.where().eq("projectId", projectId).findList();
    }
}
