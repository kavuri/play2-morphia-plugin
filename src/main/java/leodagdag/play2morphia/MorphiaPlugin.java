package leodagdag.play2morphia;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
//import org.mongodb.morphia.logging.MorphiaLoggerFactory;
//import org.mongodb.morphia.logging.slf4j.SLF4JLogrImplFactory;

import play.*;

import javax.inject.Inject;

/*
 * Being compatible with previous version. You should use DI.
 *
 * see https://playframework.com/documentation/2.4.x/JavaDependencyInjection
 */
public class MorphiaPlugin extends Plugin {
    @Inject
    private IMorphia morphia;

    @Inject
    private Application application ;

    public MorphiaPlugin() {
    }

    public static Datastore ds(String dbName) {
        IMorphia mp = play.Play.application().plugin(MorphiaPlugin.class).morphia;
        return mp.ds(dbName) ;
    }
    
    public static Morphia morphia() {
        IMorphia mp = play.Play.application().plugin(MorphiaPlugin.class).morphia;
        return mp.morphia() ;
    }

    public static Datastore ds() {
        IMorphia mp = play.Play.application().plugin(MorphiaPlugin.class).morphia;
        return mp.ds();
    }

    public static GridFS gridFs() {
        IMorphia mp = play.Play.application().plugin(MorphiaPlugin.class).morphia;
        return mp.gridFs();
    }

    public static DB db() {
        IMorphia mp = play.Play.application().plugin(MorphiaPlugin.class).morphia;
        return mp.db();
    }
}
