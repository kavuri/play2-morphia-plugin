package leodagdag.play2morphia;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import leodagdag.play2morphia.IMorphia;
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.utils.*;
import org.mongodb.morphia.AbstractEntityInterceptor;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.ValidationExtension;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.mapping.Mapper;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import play.Application;
import play.Configuration;
import play.Logger;
import play.Play;
import play.inject.ApplicationLifecycle;
import play.libs.F;

import javax.inject.Inject;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by guofeng on 2015/5/28.
 */
public class MorphiaImpl implements IMorphia {
    private Application application;
    private ApplicationLifecycle lifecycle ;
    private IPasswordDecryptor passwordDecryptor ;
    private String name ;

    private Morphia morphia = null;
    private MongoClient mongo = null;
    private Datastore ds = null;
    private GridFS gridfs;

    public MorphiaImpl(String name, Application application, ApplicationLifecycle lifecycle, IPasswordDecryptor passwordDecryptor) {
        this.name = name ;
        this.application = application ;
        this.lifecycle = lifecycle ;
        this.passwordDecryptor = passwordDecryptor ;
        lifecycle.addStopHook(() -> {
            stop();
            return F.Promise.pure(null);
        });

        init() ;
    }

    @Override
    public Datastore ds(String dbName) {
        if (StringUtils.isBlank(dbName)) {
            return ds();
        }
        Datastore ds = dataStores.get(dbName);
        if (null == ds) {
            Datastore ds0 = morphia.createDatastore(mongo, dbName);
            ds = dataStores.putIfAbsent(dbName, ds0);
            if (null == ds) {
                ds = ds0;
            }
        }
        return ds;
    }

    @Override
    public Morphia morphia() {
        return morphia ;
    }

    @Override
    public Datastore ds() {
        return ds;
    }

    @Override
    public GridFS gridFs() {
        return gridfs;
    }

    @Override
    public DB db() {
        return ds().getDB();
    }

    public void stop() {
        morphia = null;
        ds = null;
        gridfs = null;
        if ( mongo != null )
            mongo.close();
    }

    private void init() {
        // Register SLF4JLogrImplFactory as Logger -- upgrade to 0.107, no SLF4JLogrImplFactory defined
        // @see http://nesbot.com/2011/11/28/play-2-morphia-logging-error
//        MorphiaLoggerFactory.reset();
//        MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);

        String dbName = null;
        String username = null;
        String password = null;
        int connectionsPerHost = -1 ; // the defualt
        Configuration morphiaConf = null ;

        try {
            morphiaConf = Configuration.root().getConfig(ConfigKey.PREFIX);
            if (morphiaConf == null) {
                throw Configuration.root().reportError(ConfigKey.PREFIX, "Missing Morphia configuration", null);
            }

            MorphiaLogger.debug(morphiaConf);

            String mongoURIstr = morphiaConf.getString(ConfigKey.DB_MONGOURI.getKey());
            Logger.debug("mongoURIstr:" + mongoURIstr);
            String seeds = null ;
            if(Play.isDev()) {
                seeds = morphiaConf.getString(ConfigKey.DB_DEV_SEEDS.getKey());
            } else {
                seeds = morphiaConf.getString(ConfigKey.DB_SEEDS.getKey());
            }

            if (StringUtils.isBlank(dbName)) {
                dbName = morphiaConf.getString(ConfigKey.DB_NAME.getKey());
                if (StringUtils.isBlank(dbName)) {
                    throw morphiaConf.reportError(ConfigKey.DB_NAME.getKey(), "Missing Morphia configuration", null);
                }
            }

            //Check if credentials parameters are present
            if (StringUtils.isBlank(username)) {
                username = morphiaConf.getString(ConfigKey.DB_USERNAME.getKey());
            }
            if (StringUtils.isBlank(password)) {
                password = morphiaConf.getString(ConfigKey.DB_PASSWORD.getKey());
            }

            connectionsPerHost = morphiaConf.getInt(ConfigKey.CONNECTIONS_PER_HOST.getKey(), connectionsPerHost) ;
            MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
            if ( connectionsPerHost != -1 )
                builder.connectionsPerHost(connectionsPerHost);
            MongoClientOptions options = builder.build();

            Logger.debug("Max connections per host: " + options.getConnectionsPerHost());

            if(StringUtils.isNotBlank(mongoURIstr)) {
                MongoClientURI mongoURI = new MongoClientURI(mongoURIstr);
                dbName = mongoURI.getDatabase();  // used by morphia.createDatastore() in the following
                //username = mongoURI.getUsername();
                //if(mongoURI.getPassword() != null) {
                //    password = new String(mongoURI.getPassword());
                //}
                mongo = connect(mongoURI);
            } else if (StringUtils.isNotBlank(seeds)) {
                mongo = connect(seeds, dbName, username, password, options);
            } else {
                mongo = connect(
                        morphiaConf.getString(ConfigKey.DB_HOST.getKey()),
                        morphiaConf.getString(ConfigKey.DB_PORT.getKey()),
                        dbName, username, password, options);
            }

            morphia = new Morphia();
            // To prevent problem during hot-reload
            if (application.isDev()) {
                morphia.getMapper().getOptions().setObjectFactory( new PlayCreator()) ;
            }
            // Configure validator
            new ValidationExtension(morphia);

            // Create datastore
            ds = morphia.createDatastore(mongo, dbName);

            MorphiaLogger.debug("Datastore [%s] created", dbName);
            // Create GridFS
            String uploadCollection = morphiaConf.getString(ConfigKey.COLLECTION_UPLOADS.getKey());
            if (StringUtils.isBlank(uploadCollection)) {
                uploadCollection = "uploads";
                MorphiaLogger.warn("Missing Morphia configuration key [%s]. Use default value instead [%s]", ConfigKey.COLLECTION_UPLOADS, "uploads");
            }
            gridfs = new GridFS(ds.getDB(), uploadCollection);
            MorphiaLogger.debug("GridFS created", "");
            MorphiaLogger.debug("Add Interceptor...", "");
            morphia.getMapper().addInterceptor(new AbstractEntityInterceptor() {

                @Override
                public void postLoad(final Object ent, final DBObject dbObj, final Mapper mapr) {
                    if (ent instanceof Model) {
                        Model m = (Model) ent;
                        m._post_Load();
                    }
                }
            });
            MorphiaLogger.debug("Classes mapping...", "");
            mapClasses();
            MorphiaLogger.debug("End of initializing Morphia", "");
        } catch (MongoException e) {
            MorphiaLogger.error(e, "Problem connecting MongoDB");
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            MorphiaLogger.error(e, "Problem mapping class");
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            MorphiaLogger.error(e, "Problem connecting MongoDB");
            throw new RuntimeException(e);
        }
    }

    private void mapClasses() throws ClassNotFoundException {
        // Register all models.Class
        Set<Class> classes = new HashSet<Class>();
        classes.addAll(getTypesAnnotatedWith(application, "models", Entity.class));
        classes.addAll(getTypesAnnotatedWith(application, "models", Embedded.class));
        for (Class clazz : classes) {
            MorphiaLogger.debug("mapping class: %1$s", clazz);
            morphia.map(clazz);
        }
        // @see http://code.google.com/p/morphia/wiki/Datastore#Ensure_Indexes_and_Caps
        ds.ensureCaps(); //creates capped collections from @Entity
        ds.ensureIndexes(); //creates indexes from @Index annotations in your entities
    }

    public Set<Class<?>> getTypesAnnotatedWith(Application app, String packageName, Class<? extends java.lang.annotation.Annotation> annotation) {
        return getReflections(app, packageName).getTypesAnnotatedWith(annotation);
    }
    private Reflections getReflections(Application app, String packageName) {
        return new Reflections(
                new ConfigurationBuilder()
                        .addClassLoader(app.classloader())
                        .addUrls(ClasspathHelper.forPackage(packageName, app.classloader()))
                        .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName + ".")))
                        .setScanners(new TypeElementsScanner(), new TypeAnnotationsScanner(), new SubTypesScanner())) ;
    }

    private final ConcurrentMap<String, Datastore> dataStores = new ConcurrentHashMap<String, Datastore>();

    private MongoClient connect(MongoClientURI mongoURI) throws UnknownHostException {
        return new MongoClient(mongoURI);
    }

    private MongoClient connect(String seeds, String dbName, String username, String password, MongoClientOptions options) throws UnknownHostException {
        String[] sa = seeds.split("[;,\\s]+");
        List<ServerAddress> addrs = new ArrayList<ServerAddress>(sa.length);
        for (String s : sa) {
            String[] hp = s.split(":");
            if (0 == hp.length) {
                continue;
            }
            String host = hp[0];
            int port = 27017;
            if (hp.length > 1) {
                port = Integer.parseInt(hp[1]);
            }
            addrs.add(new ServerAddress(host, port));
        }
        if (addrs.isEmpty()) {
            throw Configuration.root().reportError(ConfigKey.DB_SEEDS.getKey(), "Cannot connect to mongodb: no replica can be connected", null);
        }
        MongoCredential mongoCredential = getMongoCredential(dbName, username, password) ;
        return mongoCredential == null ? new MongoClient(addrs, options) : new MongoClient(addrs, Arrays.asList(mongoCredential), options) ;
    }

    private MongoClient connect(String host, String port, String dbName, String username, String password, MongoClientOptions options) {
        String[] ha = host.split("[,\\s;]+");
        String[] pa = port.split("[,\\s;]+");
        int len = ha.length;
        if (len != pa.length) {
            throw Configuration.root().reportError(ConfigKey.DB_HOST.getKey() + "-" + ConfigKey.DB_PORT.getKey(), "host and ports number does not match", null);
        }

        List<ServerAddress> addrs = new ArrayList<ServerAddress>(ha.length);
        for (int i = 0; i < len; ++i) {
            try {
                addrs.add(new ServerAddress(ha[i], Integer.parseInt(pa[i])));
            } catch (Exception e) {
                MorphiaLogger.error(e, "Error creating mongo connection to %s:%s", host, port);
            }
        }
        if (addrs.isEmpty()) {
            throw Configuration.root().reportError(
                    ConfigKey.DB_HOST.getKey() + "-" + ConfigKey.DB_PORT.getKey(), "Cannot connect to mongodb: no replica can be connected",
                    null);
        }

        MongoCredential mongoCredential = getMongoCredential(dbName, username, password) ;
        return mongoCredential == null ? new MongoClient(addrs, options) : new MongoClient(addrs, Arrays.asList(mongoCredential), options);
    }

    private MongoCredential getMongoCredential(String dbName, String username, String password) {
        if (StringUtils.isBlank(username) && StringUtils.isBlank(password))
            return null ;

        if (StringUtils.isNotBlank(username) ^ StringUtils.isNotBlank(password)) {
            throw Configuration.root().reportError(ConfigKey.DB_NAME.getKey(), "Missing username or password", null);
        }

        String decryptedPassword = passwordDecryptor.decrypt(password) ;  // CHANGED: decrypt the password
        return MongoCredential.createCredential(username, dbName, decryptedPassword.toCharArray());
    }

}
