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
public class Image extends Model {

    public final static int DEFAULT = 1, NORMAL = 2;
    @Id
    private Long id;
    @Required
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;
    @Lob
    private byte[] data;

    private int imgType;
    private static Finder<Long, Image> find = new Finder<Long, Image>(Long.class, Image.class);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getImgType() {
        return imgType;
    }

    public void setImgType(int imgType) {
        this.imgType = imgType;
    }

    public Image(Project project, File image, int type) {
        this.project = project;
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
    public static List<Image> findImageOfProject(Project project){
        return Image.find.where().eq("project", project).findList();
    }
    public static String getUrlDefaultImage(Project project){
        Image img = Image.find.where().eq("project",project).eq("imgType",DEFAULT).findUnique();
        if(img == null){
//            return "../assets/img/no_image_s.png";
            return null;
        }else {
            return "../getimg/" + img.id;
        }
    }
    public static Image getDefaultImage(Project project){
        Image img = Image.find.where().eq("project",project).eq("imgType",DEFAULT).findUnique();
        return img;
    }
    public static Image findById(Long imgId){
        return find.byId(imgId);
    }
    public static Image findByIdAndPro(Long imgId, Project pro){
        return find.where().eq("Id", imgId).eq("project", pro).findUnique();
    }
}
