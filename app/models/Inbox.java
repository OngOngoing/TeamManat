package models;

import play.data.validation.Constraints.Required;
import play.db.ebean.*;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "inbox_message")
public class Inbox extends Model {

    public static final int READ = 1, UNREAD = 2;

    @Id
    private Long id;

    @Required
    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;

    @Required
    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private  User receiver;

    @Required
    @ManyToOne
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private Comment comment;

    private int isRead;

    @Version
    private Timestamp timestamp;

    private static Finder<Long, Inbox> find = new Finder<Long, Inbox>(Long.class, Inbox.class);

    public static Inbox create(User sender, User receiver, Comment comment){
        Inbox _inbox = Inbox.findBySenderAndReceiver(sender, receiver);
        if(_inbox == null) {
            _inbox = new Inbox();
            _inbox.sender = sender;
            _inbox.receiver = receiver;
            _inbox.comment = comment;
            _inbox.isRead = Inbox.UNREAD;
            _inbox.timestamp = new Timestamp((new Date()).getTime());
            _inbox.save();
        }else{
            _inbox.isRead = Inbox.UNREAD;
            _inbox.timestamp = new Timestamp((new Date()).getTime());
            _inbox.setComment(comment);
            _inbox.update();
        }
        return _inbox;
    }

    public static void createByProject(User sender, Project project, Comment comment){
        List<Groups> members = project.getMemnbers();
        for(Groups _g: members){
            Inbox.create(sender, _g.getUser(), comment);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public int isRead() {
        return isRead;
    }

    public void setRead(int read) {
        this.isRead = read;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public static Inbox findBySenderAndReceiver(User sender, User receiver){
        return find.where().eq("sender", sender).eq("receiver", receiver).findUnique();
    }

    public static List<Inbox> findAllByReceiver(User receiver){
        return find.where().eq("receiver", receiver).order().desc("timestamp").findList();
    }

    public static List<Inbox> findUnreadByReceiver(User receiver){
        return find.where().eq("receiver", receiver).eq("isRead", Inbox.UNREAD).order().asc("timestamp").findList();
    }

    public static Inbox findById(Long id){
        return find.byId(id);
    }

    public static int findNumOfUnreadByReceiver(User receiver){
        return Inbox.findUnreadByReceiver(receiver).size();
    }

    public static List<Comment> findCommentByReceiver(User receiver){
        List<Inbox> inboxs = find.where().eq("receiver", receiver).order().desc("timestamp").findList();
        List<Comment> _comment = new ArrayList();
        for(Inbox item: inboxs){
            _comment.add(item.getComment());
        }
        return _comment;
    }
}
