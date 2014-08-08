# play2-morphia-plugin [![Build Status](https://secure.travis-ci.org/leodagdag/play2-morphia-plugin.png)](http://travis-ci.org/leodagdag/play2-morphia-plugin)
Plug-in to use Morphia/MongoDB with [Play! framework](http://www.playframework.org/2.0) 2.0

_inpired by [greenlaw110 / play-morphia](https://github.com/greenlaw110/play-morphia)_

# Configuration

morphia.db.seeds="127.0.0.1:27017"
morphia.db.dev.seeds="127.0.0.1:27017"

or 
morphia.db.host="127.0.0.1"
morphia.db.port="27017"

%prod.morphia.db.name=yabe
morphia.db.name=dev
morphia.id.type=Long
morphia.defaultWriteConcern=SAFE
morphia.collection.upload=fs
morphia.logger=false