// Inspired by base2 and Prototype
(function () {
    var initializing = false,
        fnTest = /xyz/.test(function () {
            xyz;
        }) ? /\b_super\b/ : /.*/;

    // The base Component implementation (does nothing)
    this.EpisodeComponent = function () {};

    // Create a new Component that inherits from this class
    EpisodeComponent.extend = function (prop) {
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
                (function (name, fn) {
                return function () {
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
            if (!initializing && this.init)
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

// DOM Event Trace
var DOMEventTrace = EpisodeComponent.extend({
    init: function (isEpisodeSource) {
        this.episodeSource = isEpisodeSource;
    },
    isEpisodeSource: function () {
        return this.episodeSource;
    },
    setEventType: function (eventType) {
        this.eventType = eventType;
    },
    getEventType: function () {
        return this.eventType;
    },
    setEventHandler: function (eventHandler) {
        this.eventHandler = eventHandler;
    },
    getEventHandler: function () {
        return this.eventHandler;
    },
    setTargetElement: function (targetElement) {
        this.targetElement = targetElement;
    },
    getTargetElement: function () {
        return this.targetElement;
    },
    createDiagramObject: function (x_pos, y_pos) {
        this.visual = new UMLActor({
            x: x_pos,
            y: y_pos
        });
        this.visual.setName('Event type:' + this.getEventType() 
                            + '\nHandler:' + this.getEventHandler() 
                            + '\nTarget ID:' + this.targetElement.attributes.id);
        this.visual.notifyChange();
    },
    getDiagramObject: function () {
        return this.visual;
    }
});

// DOM Mutation Trace
var DOMMutationTrace = EpisodeComponent.extend({
    init: function (isEpisodeSource) {
        this.episodeSource = isEpisodeSource;
    },
    isEpisodeSource: function () {
        return this.episodeSource;
    },
    setMutationType: function (mutationType) {
        this.mutationType = mutationType;
    },
    getMutationType: function () {
        return this.mutationType;
    },
    setData: function (data) {
        this.data = data;
    },
    getData: function () {
        return this.data;
    },
    setNodeName: function (nodeName) {
        this.nodeName = nodeName;
    },
    getNodeName: function () {
        return this.nodeName;
    },
    setNodeValue: function (nodeValue) {
        this.nodeValue = nodeValue;
    },
    getNodeValue: function () {
        return this.nodeValue;
    },
    setNodeType: function (nodeType) {
        this.nodeType = nodeType;
    },
    getNodeType: function () {
        return this.nodeType;
    },
    setParentNodeValue: function (parentNodeValue) {
        this.parentNodeValue = parentNodeValue;
    },
    getParentNodeValue: function () {
        return this.parentNodeValue;
    },
    getSummary: function () {
        var summary = "Type: " + this.getMutationType() 
                     + "\nElement ID: " + this.getParentNodeValue() 
                     + "\nData " + this.getNodeValue() 
                     + "\nNode Type: " + this.getNodeName() + " (type " + this.getNodeType() + ")";
        return summary;
    },
    setMutationObject: function (targetElement) {
        this.targetElement = targetElement;
    },
    getMutationObject: function () {
        return this.targetElement;
    }
});

//DOM Element Value Trace
var DOMElementValueTrace = EpisodeComponent.extend({
    init: function (isEpisodeSource) {
        this.episodeSource = isEpisodeSource;
    },
    isEpisodeSource: function () {
        return this.episodeSource;
    },
    setElementId: function (elementId) {
        this.elementId = elementId;
    },
    getElementId: function () {
        return this.elementId;
    },
    setElementType: function (elementType) {
        this.elementType = elementType;
    },
    getElementType: function () {
        return this.elementType;
    },
    setNodeName: function (nodeName) {
        this.nodeName = nodeName;
    },
    getNodeName: function () {
        return this.nodeName;
    },
    setNodeType: function (nodeType) {
        this.nodeType = nodeType;
    },
    getNodeType: function () {
        return this.nodeType;
    },
    setNewValue: function (newValue) {
        this.newValue = newValue;
    },
    getNewValue: function () {
        return this.newValue;
    },
    setOldValue: function (oldValue) {
        this.oldValue = oldValue;
    },
    getOldValue: function () {
        return this.oldValue;
    },
    setParentNodeValue: function (parentNodeValue) {
        this.parentNodeValue = parentNodeValue;
    },
    getParentNodeValue: function () {
        return this.parentNodeValue;
    },
    getSummary: function () {
        /*var summary = "Element ID : " + this.getElementId() + "\nOld Value: " + this.getOldValue() + "\nNew Value " + this.getNewValue() + "\nElement Type: "
	+ this.getElementType() + "\nNode Type: " + this.getNodeName() + " (type " + this.getNodeType() + ")";*/
        var summary = "Element ID : " + this.getElementId() + "Parent : " + this.getParentNodeValue() + "\nOld Value: " + this.getOldValue() + "\nNew Value " + this.getNewValue();
        return summary;
    },
    setValueChangeObject: function (targetElement) {
        this.targetElement = targetElement;
    },
    getValueChangeObject: function () {
        return this.targetElement;
    }
});

// Timing Trace
var TimingTrace = EpisodeComponent.extend({
    init: function (isEpisodeSource) {
        this.episodeSource = isEpisodeSource;
    },
    isEpisodeSource: function () {
        return this.episodeSource;
    },
    setTimeoutId: function (id) {
        this.timeoutId = id;
    },
    getTimeoutId: function () {
        return this.timeoutId;
    },
    setCallbackFunction: function (cbf) {
        this.callbackFunction = cbf;
    },
    getCallbackFunction: function () {
        if (this.callbackFunction == "") {
            return "anonymous";
        } else {
            return this.callbackFunction;
        }
    },
    setDelay: function (delay) {
        this.timeoutDelay = delay;
    },
    getDelay: function () {
        return this.timeoutDelay;
    },
    createDiagramObject: function (x_pos, y_pos) {
        this.visual = new UMLActor({
            x: x_pos,
            y: y_pos
        });
        this.visual.setName('TID: ' + this.getTimeoutId().toString());
        this.visual.notifyChange();
    },
    getDiagramObject: function () {
        return this.visual;
    }
});


// XHR Event
var XHREvent = EpisodeComponent.extend({
    init: function (isEpisodeSource) {
        this.episodeSource = isEpisodeSource;
    },
    isEpisodeSource: function () {
        return this.episodeSource;
    },
    setXHRId: function (id) {
        this.XHRId = id;
    },
    getXHRId: function () {
        return this.XHRId;
    },
    getUrl: function () {
        return this.url;
    },
    setUrl: function (url) {
        this.url = url;
    },
    getMethodType: function () {
        return this.methodType;
    },
    setMethodType: function (methodType) {
        this.methodType = methodType;
    },
    isAsync: function () {
        return this.async;
    },
    setAsync: function (async) {
        this.async = async;
    },
    setMessage: function (msg) {
        this.msg = msg;
    },
    getMessage: function () {
        return this.msg;
    },
    setCallbackFunction: function (cbfn) {
        this.cbfn = cbfn;
    },
    getCallbackFunction: function () {
        return this.cbfn;
    },
    setResponse: function (resp) {
        this.resp = resp;
    },
    getResponse: function () {
        return this.resp;
    },
    createDiagramObject: function (x_pos, y_pos) {
        this.visual = new UMLActor({
            x: x_pos,
            y: y_pos
        });
        this.visual.setName('XHR ID: ' + this.getXHRId().toString());
        this.visual.notifyChange();
    },
    getDiagramObject: function () {
        return this.visual;
    }
});

var XHROpen = XHREvent.extend({
    init: function () {
        this._super(false);
    },
    getUrl: function () {
        return this.url;
    },
    setUrl: function (url) {
        this.url = url;
    },
    getMethodType: function () {
        return this.methodType;
    },
    setMethodType: function (methodType) {
        this.methodType = methodType;
    },
    isAsync: function () {
        return this.async;
    },
    setAsync: function (async) {
        this.async = async;
    },
    setMessage: function (msg) {
        this.msg = msg;
    },
    getMessage: function () {
        return this.msg;
    },
    setCallbackFunction: function (cbfn) {
        this.cbfn = cbfn;
    },
    getCallbackFunction: function () {
        return this.cbfn;
    },
    setResponse: function (resp) {
        this.resp = resp;
    },
    getResponse: function () {
        return this.resp;
    }
});

var XHRSend = XHREvent.extend({
    init: function () {
        this._super(true);
    },
    setCallbackFunction: function (callbackFunction) {
        this.callbackFunction = callbackFunction;
    },
    getCallbackFunction: function () {
        return this.callbackFunction;
    },
    getResponse: function () {
        return this.response;
    },
    setResponse: function (response) {
        this.response = response;
    }
});

/*
 *  Object for keeping track of function trace information
 *  See allEpisodes.js for example initialization/declaration
 */
var FunctionTrace = EpisodeComponent.extend({
    init: function (fnName) {
        this.name = fnName;
    },
    getName: function () {
        return this.name;
    },
    setFileName: function (fileName) {
        this.fileName = fileName;
    },
    getFileName: function () {
        return this.fileName;
    },
    setLineNo: function (lineno) {
        this.lineNo = lineno;
    },
    getLineNo: function () {
        return this.lineNo;
    },
    createDiagramObject: function (x_pos, y_pos) {
        this.visual = new UMLLifeline({
            x: x_pos,
            y: y_pos
        });
        this.visual.setName(this.getName());
        this.visual.notifyChange();
    },
    getDiagramObject: function () {
        return this.visual;
    }
});

function Episode() {
    // Initializing all internal objects
    this.internalMessages = [];
    this.internalComponents = [];
    this.domComponents = [];
    this.elementValueChanges = [];
    this.dom = null;
    this.sequenceDiagram = null;

    // The first component within the episode (usually an event such as a DOM event)
    this.getSource = function () {
        return this.internalComponents[0];
    };

    this.addComponent = function (newComponent) {
        this.internalComponents.push(newComponent);
    };

    // All the components within this episode (actors included)	
    this.getComponents = function () {
        return this.internalComponents;
    };

    // Used to add mutations (called from allEpisodes.js)
    this.addMutations = function (mutation) {
        this.domComponents.push(mutation);
    };

    // Returns array of altered DOM nodes (JSON format info.)
    this.getMutations = function () {
        return this.domComponents;
    };

    // Used for adding 'element value changes' to episode
    // Similar to DOM mutations
    this.addElementValueTraces = function (elementValueTraces) {
        this.elementValueChanges.push(elementValueTraces);
    };

    // Returns the associated 'element value changes' for this episode.
    this.getElementValueTraces = function () {
        return this.elementValueChanges;
    };

    // Use to add messages to an episode (callMessage object)
    this.addMessage = function (newMessage) {
        this.internalMessages.push(newMessage);
    };

    // Returns an array containing all messages (call, return, etc.) for 'this' episode
    this.getMessages = function () {
        return this.internalMessages;
    };

    // Returns the sequence diagram for 'this' episode
    this.getSequenceDiagram = function () {
        return this.sequenceDiagram;
    };
}

/*
 * Object representing messages within sequence diagram.
 */
function callMessage(object) {
    // 'From' object, the calling function or event
    this.a = object.a;
    // 'To' object, the called function
    this.b = object.b;
    // The position of the message within the sequence diagram.
    // Represents time and the order of the function call
    this.y = object.y;

    // Function for settinging the name/label of the message
    this.setName = function (newName) {
        this.name = newName;
    };

    this.getName = function () {
        return this.name;
    };
}

// Initialize array for episodes, populated from within allEpisodes.js
var allEpisodes = [];