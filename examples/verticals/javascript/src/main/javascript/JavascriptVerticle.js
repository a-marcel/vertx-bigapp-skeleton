var logger = Java.type('io.vertx.core.logging.LoggerFactory').getLogger('JavascriptVerticle.js');

console.log = function(msg) {
    logger.info(msg);
}

console.error = function(error) {
    logger.error(error);
}

console.warn = function(warn) {
    logger.warn(warn);
}


console.log('vertxStartAsync');


exports.vertxStartAsync = function(startFuture) {
	console.log("Deploy Javascript Verticle");

	var eb = vertx.eventBus();
	
	var consumer = eb.consumer('httpGetWebChannel', function (message) {
	});
	
	consumer.completionHandler(function (res, res_err) {
	    if (res_err == null) {
	        console.log('The handler registration has reached all nodes')
	
	        startFuture.complete()
	    } else {
	        console.log('Registration failed!')
	
	        startFuture.fail()
	    }
	});
}