package leodagdag.play2morphia;

/**
 * Created by guofeng on 2015/5/28.
 */
import com.google.common.collect.ImmutableList;
import leodagdag.play2morphia.utils.ConfigKey;
import play.api.*;
import play.api.inject.*;
import play.libs.Scala;
import scala.collection.Seq;

import java.util.List;

public class MorphiaModule extends Module {
    @Override
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        String defaultDBPrefix = configuration.underlying().getString("play.modules.morphia.defaultPrefix") ;
        List<String> prefixes = configuration.underlying().getStringList("play.modules.morphia.prefixes") ;

        ImmutableList.Builder<Binding<?>> list = new ImmutableList.Builder<Binding<?>>();

        list.add(bind(IMorphia.class).to(new MorphiaProvider(ConfigKey.PREFIX)));  // @Inject IMorphia morphia
        list.add(bind(MorphiaApi.class).to(DefaultMorphiaApi.class));

        list.add(bindDB(defaultDBPrefix)) ;

        for ( String prefix : prefixes) {
            list.add(bindDB(prefix)) ;
        }
        return Scala.toSeq(list.build());
    }

    // Creates a prefix qualifier
    private ConfigPrefix prefixedWith(String name) {
        return new ConfigPrefixImpl(name) ;
    }

    // bind to the given prefix name
   private Binding<?> bindDB(String name) {
       ConfigPrefix configPrefix = prefixedWith(name) ;
       BindingKey<IMorphia> key = bind(IMorphia.class).qualifiedWith(configPrefix) ;
       Binding<?> binding = key.to(new MorphiaProvider(name)) ;
       return binding ;
   }
}