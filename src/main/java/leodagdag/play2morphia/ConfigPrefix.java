package leodagdag.play2morphia;

import leodagdag.play2morphia.utils.ConfigKey;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigPrefix {
    String value() default ConfigKey.PREFIX;
}
