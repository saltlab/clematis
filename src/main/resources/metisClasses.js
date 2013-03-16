// Inspired by base2 and Prototype
(function(){
  var initializing = false, fnTest = /xyz/.test(function(){xyz;}) ? /\b_super\b/ : /.*/;
 
  // The base Component implementation (does nothing)
  this.EpisodeComponent = function(){};
 
  // Create a new Component that inherits from this class
  EpisodeComponent.extend = function(prop) {
    var _super = this.prototype;
   
    // Instantiate a base class (but only create the instance,
    // don't run the init constructor)
    initializing = true;
    var prototype = new this();
    initializing = false;
   
    // Copy the properties over onto the new prototype
    for (var name in prop) {
      // Check if we're overwriting an existing function
      prototype[name] = typeof prop[name] == "function" &&
        typeof _super[name] == "function" && fnTest.test(prop[name]) ?
        (function(name, fn){
          return function() {
            var tmp = this._super;
           
            // Add a new ._super() method that is the same method
            // but on the super-class
            this._super = _super[name];
           
            // The method only need to be bound temporarily, so we
            // remove it when we're done executing
            var ret = fn.apply(this, arguments);        
            this._super = tmp;
           
            return ret;
          };
        })(name, prop[name]) :
        prop[name];
    }
   
    // The dummy class constructor
    function EpisodeComponent() {
      // All construction is actually done in the init method
      if ( !initializing && this.init )
        this.init.apply(this, arguments);
    }
   
    // Populate our constructed prototype object
    EpisodeComponent.prototype = prototype;
   
    // Enforce the constructor to be what we expect
    EpisodeComponent.prototype.constructor = EpisodeComponent;
 
    // And make this class extendable
    EpisodeComponent.extend = arguments.callee;
   
    return EpisodeComponent;
  };
})();

// DOM Trace
var DOMEventTrace = EpisodeComponent.extend({
  init: function(isEpisodeSource){
    this.episodeSource = isEpisodeSource;
  },
  isEpisodeSource: function(){
    return this.episodeSource;
  },
  setEventType: function(eventType){
    this.eventType = eventType;
  },
  getEventType: function(){
    return this.eventType;
  },
  setEventHandler: function(eventHandler){
    this.eventHandler = eventHandler;
  },
  getEventHandler: function(){
    return this.eventHandler;
  },
  setTargetElement: function(targetElement){
    this.targetElement = targetElement;
  },
  getTargetElement: function(){
    return this.targetElement;
  }
});

// Timing Trace
var TimingTrace = EpisodeComponent.extend({
  init: function(isEpisodeSource){
    this.episodeSource = isEpisodeSource;
  },
  isEpisodeSource: function(){
    return this.episodeSource;
  },
  setTimeoutId: function(id){
    this.timeoutId = id;
  },
  getTimeoutId: function(){
    return this.timeoutId;
  },
  setCallbackFunction: function(cbf){
    this.callbackFunction = cbf;
  },
  getCallbackFunction: function(){
    if (this.callbackFunction == "") {
		return "anonymous";
    } else {
	    return this.callbackFunction;
	}
  },
  setDelay: function(delay){
    this.timeoutDelay = delay;
  },
  getDelay: function(){
    return this.timeoutDelay;
  }
});

 
// XHR Event
var XHREvent = EpisodeComponent.extend({
  init: function(isEpisodeSource){
    this.episodeSource = isEpisodeSource;
  },
  isEpisodeSource: function(){
    return this.episodeSource;
  },
  setXHRId: function(id){
    this.XHRId = id;
  },
  getXHRId: function(){
    return this.XHRId;
  }
});

var XHROpen = XHREvent.extend({
  init: function(){
    this._super( false );
  },
  getUrl: function() {
    return this.url;
  },
  setUrl: function(url) {
	this.url = url;
  },
  getMethodType: function() {
	return this.methodType;
  },
  setMethodType: function(methodType) {
	this.methodType = methodType;
  },
  isAsync: function() {
	return this.async;
  },
  setAsync: function(async) {
	this.async = async;
  }	
});

var XHRSend = XHREvent.extend({
  init: function(){
    this._super( true );
  },
  setCallbackFunction: function(callbackFunction) {
    this.callbackFunction = callbackFunction;
  },
  getCallbackFunction: function() {
	return this.callbackFunction;
  },
  getResponse: function() {
	return this.response;
  },
  setResponse: function(response) {
	this.response = response;
  }
});

var FunctionTrace = EpisodeComponent.extend({
  init: function(fnName) {
    this.name = fnName;
  },
  getName: function() {
    return this.name;
  },
  setFileName: function(fileName) {
    this.fileName = fileName;
  },
  getFileName: function() {
    return this.fileName;
  },
  setLineNo: function(lineno) {
    this.lineNo = lineno;
  },
  getLineNo: function() {
    return this.lineNo;
  }
});

function Episode () {
  this.internalMessages = [];
  this.internalComponents = [];
  this.dom = null;

  this.getSource = function() {
    return this.internalComponents[0];
  };

  this.addComponent = function (newComponent) {
    this.internalComponents.push(newComponent);
  };

  this.addMessage = function(newMessage) {
    this.internalMessages.push(newMessage);
  };

  this.getMessages = function () {
    return this.internalMessages;
  };

}
