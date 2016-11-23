package leodagdag.play2morphia.test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import org.junit.Before;
import play.Application;
import play.Mode;
import play.api.inject.guice.GuiceApplicationBuilder;
import play.libs.akka.AkkaGuiceSupport;
import play.test.Helpers;

import javax.inject.Inject;

public class NewTest {

    public static class Module extends AbstractModule implements AkkaGuiceSupport {
        @Override
        public void configure() {
        }
    }

    @Inject
    Application application;

    @Before
    public void setup() {
        GuiceApplicationBuilder builder = new GuiceApplicationBuilder()
                .in(Mode.TEST)
                .in(NewTest.class.getClassLoader())
                .bindings(new Module()) ;

        Guice.createInjector(builder.applicationModule()).injectMembers(this);
        Helpers.start(application);
    }}
