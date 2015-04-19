package models;

import javax.persistence.*;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;
import java.util.List;

import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

@Entity
public class ProjectImage extends Model {

    public final static int DEFAULT = 1, NORMAL = 2;
    @Id
    public Long Id;
    @Required
    public Long projectId;
    @Lob
    private byte[] data;

    public int imgType;
    private static Finder<Long, ProjectImage> find = new Finder<Long, ProjectImage>(Long.class, ProjectImage.class);

    public ProjectImage(Long projectId, File image, int type) {
        this.projectId = projectId;
        this.data = new byte[(int)image.length()];
        this.imgType = type;
        InputStream inStream = null;
        try {
            inStream = new BufferedInputStream(new FileInputStream(image));
            inStream.read(this.data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        this.save();
    }
    public byte[] getData(){
        return data;
    }
    public static List<ProjectImage> findImageOfProject(Long projectId){
        return ProjectImage.find.where().eq("projectId",projectId).findList();
    }
    public static String getUrlDefaultImage(Long projectId){
        ProjectImage img = ProjectImage.find.where().eq("projectId",projectId).eq("imgType",DEFAULT).findUnique();
        if(img == null){
            return "../assets/img/no_image_s.png";
        }else {
            return "../getimg/" + img.Id;
        }
    }
    public static ProjectImage getDefaultImage(Long projectId){
        ProjectImage img = ProjectImage.find.where().eq("projectId",projectId).eq("imgType",DEFAULT).findUnique();
        return img;
    }
    public static ProjectImage findById(Long id){
        return find.byId(id);
    }
    public static ProjectImage findByIdAndProId(Long id, Long proid){ return find.where().eq("Id",id).eq("projectId",proid).findUnique();}
}
