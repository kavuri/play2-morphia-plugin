package leodagdag.play2morphia;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import leodagdag.play2morphia.utils.StringUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * Created by guofeng on 2015/5/28.
 */
public interface IMorphia {
    public Morphia morphia();
    public Datastore ds(String dbName);
    public Datastore ds();
    public DB db();
    public GridFS gridFs();

}