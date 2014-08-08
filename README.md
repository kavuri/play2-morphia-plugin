# play2-morphia-plugin [![Build Status](https://secure.travis-ci.org/leodagdag/play2-morphia-plugin.png)](http://travis-ci.org/leodagdag/play2-morphia-plugin)
Plug-in to use Morphia/MongoDB with [Play! framework](http://www.playframework.org/2.0) 2.0

_inpired by [greenlaw110 / play-morphia](https://github.com/greenlaw110/play-morphia)_

# Publish it

Using
````
sbt publishLocal
````
to publish it to the local repository, so that you could reference it in your play application

# Configuration

Add the following to your build's library dependency:
``````
"leodagdag"  %% "play2-morphia-plugin"  % "0.0.16"
``````

### Configuring the plugin in conf/play.plugins like the following:
``````
5000:leodagdag.play2morphia.MorphiaPlugin
``````
### Configuring the connection in conf/application.conf
``````
morphia.db.host="127.0.0.1"
morphia.db.port="27017"
morphia.db.username=<username>
morphia.db.password=<password>
``````
or
`````
mongodb.uri="mongodb://username:password@localhost:27017/dbname"
`````
For clustering:
``````
morphia.db.seeds="127.0.0.1:27017"
morphia.db.dev.seeds="127.0.0.1:27017"
morphia.db.username=<username>
morphia.db.password=<password>
``````
Other configuration parameters:
``````
%prod.morphia.db.name=yabe
morphia.db.name=dev
morphia.id.type=Long
morphia.defaultWriteConcern=SAFE
morphia.collection.upload=fs
morphia.logger=false
``````

### Database operations
Using the following methods in MorphiaPlugin class for data base operations
`````
    public static Morphia morphia() ;
    public static Datastore ds() ;
    public static GridFS gridFs() ;
`````