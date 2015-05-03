package models;

import play.data.validation.Constraints.Required;
import play.db.ebean.*;
import javax.persistence.*;

@Entity
public class Inbox extends Model {
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

    private boolean read;

    private static Finder<Long, Inbox> find = new Finder<Long, Inbox>(Long.class, Inbox.class);

    public Inbox create(User sender, User receiver, Comment comment){
        Inbox _inbox = Inbox.findBySenderAndReceiver(sender, receiver);
        if(_inbox == null) {
            _inbox = new Inbox();
            _inbox.sender = sender;
            _inbox.receiver = receiver;
            _inbox.comment = comment;
            _inbox.save();
        }else{
            _inbox.setComment(comment);
        }
        return _inbox;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public static Inbox findBySenderAndReceiver(User sender, User receiver){
        return find.where().eq("sender", sender).eq("receiver", receiver).findUnique();
    }
}
