package leodagdag.play2morphia;

import play.Application;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by guofeng on 2015/5/28.
 */
@Singleton
public class DefaultMorphiaApi implements MorphiaApi {
    @Inject
    private Application application;
    @Inject
    ApplicationLifecycle lifecycle ;

    @Inject
    private IPasswordDecryptor passwordDecryptor ;

    private final Map<String, IMorphia> morphiaMap;

    public  DefaultMorphiaApi() {
        morphiaMap = new HashMap<>() ;
    }
    public IMorphia get(String prefix) {
        IMorphia morphia = morphiaMap.get(prefix) ;
        if ( morphia == null ) {
            morphia = new MorphiaImpl(prefix, application, lifecycle, passwordDecryptor);
            morphiaMap.put(prefix, morphia) ;
        }
        return morphia ;
    }
}
