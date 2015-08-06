package models;

import org.mongodb.morphia.annotations.*;
import leodagdag.play2morphia.Model;
import org.bson.types.ObjectId;

@Entity
@Indexes({
        @Index(options = @IndexOptions(name = "title", background = true),
                fields = @Field("title")),
        @Index(options = @IndexOptions(name = "ownerId-contextId", background = true),
                fields = {@Field("title"), @Field("type")})
})
/*
@Indexes({
        @Index(value = "title"),
        @Index(value = "type"),
        @Index(value = "title, type")
})
*/
public class Post extends Model {

    @Id
    public ObjectId id;

    public String title;

    public String type;

    //public Blob picture;

    public static Finder<ObjectId, Post> find() {
        return new Finder<ObjectId, Post>(ObjectId.class, Post.class);
    }


}
