package leodagdag.play2morphia.test;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static leodagdag.play2morphia.utils.ConfigKey.*;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * User: leo
 * Date: 06/10/12
 * Time: 14:00
 */
public class MongoURITest extends AbstractTest {
    @Before
    public void setUp() {
        dropAllCollections();
    }

    private Map<String, String> getBasicConfig() {
        Map<String, String> config = new HashMap<String, String>();
        config.put(PREFIX + "." + ID_TYPE.getKey(), "Long");
        config.put(PREFIX + "." + DEFAULT_WRITE_CONCERN.getKey(), "SAFE");
        config.put(PREFIX + "." + COLLECTION_UPLOADS.getKey(), "fs");
        config.put(PREFIX + "." + LOGGER.getKey(), "true");
        // Disable unused plugin
        config.put("dbplugin", "disabled");
        config.put("evolutionplugin", "disabled");
        config.put("ehcacheplugin", "disabled");

        return config;
    }

    @Test
    public void testMongoURIWithDatabase() {
        System.out.println("testCreate with MongoURI including database name");
        Map<String, String> config = getBasicConfig();
        config.put(PREFIX + "." + DB_MONGOURI.getKey(), "mongodb://127.0.0.1:27017/test");

        running(fakeApplication(config), new Runnable() {
            @Override
            public void run() {
                assertThat(MorphiaPlugin.ds().getDB().getName()).isEqualTo("test");
            }
        });
    }

    @Test
    public void testMongoURIWithoutDatabase() {
        System.out.println("testCreate with MongoURI without database name");
        Map<String, String> config = getBasicConfig();
        config.put(PREFIX + "." + DB_MONGOURI.getKey(), "mongodb://127.0.0.1:27017");
        config.put(PREFIX + "." + DB_NAME.getKey(), "test");

        running(fakeApplication(config), new Runnable() {
            @Override
            public void run() {
                assertThat(MorphiaPlugin.ds().getDB().getName()).isEqualTo("test");
            }
        });
    }
}