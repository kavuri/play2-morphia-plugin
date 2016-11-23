package models;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
//import leodagdag.play2morphia.Blob;
import org.bson.types.ObjectId;

@Entity
public class Post extends Model {

    @Id
    public ObjectId id;

    public String title;

//    public Blob picture;

    public static Model.Finder<ObjectId, Post> find(){
    	return new Model.Finder<ObjectId, Post>(ObjectId.class, Post.class);
    }
    

}
