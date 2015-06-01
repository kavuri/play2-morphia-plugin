package leodagdag.play2morphia;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import leodagdag.play2morphia.IMorphia;
import leodagdag.play2morphia.utils.StringUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * Created by guofeng on 2015/5/28.
 */
public interface MorphiaApi {
    IMorphia get(String prefix) ;
}