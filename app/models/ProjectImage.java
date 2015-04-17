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

    @Id
    private Long Id;
    @Required
    private Long projectId;
    @Lob
    private byte[] data;

    private static Finder<Long, ProjectImage> find = new Finder<Long, ProjectImage>(Long.class, ProjectImage.class);

    private ProjectImage(Long projectId, byte[] image)
    {
        this.projectId = projectId;
        this.data = image;
    }
    public ProjectImage create(Long projectId, File image)
    {
        this.projectId = projectId;
        this.data = new byte[(int)image.length()];
        
        /* write the image data into the byte array */
        InputStream inStream = null;
        try 
        {
            inStream = new BufferedInputStream(new FileInputStream(image));
            inStream.read(this.data);
        } 
        catch (IOException e) {  e.printStackTrace();} 
        finally 
        {
            if (inStream != null) 
            {
                try 
                {
                    inStream.close();
                } 
                catch (IOException e) {  e.printStackTrace();}
            }
        }
        ProjectImage img = new ProjectImage(projectId, data);
        img.save();
        return img;
    }
    public List<ProjectImage> findAllPicByProjectId(Long projectId)
    {
        return find.where().eq("projectId", projectId).findList();
    }

}
