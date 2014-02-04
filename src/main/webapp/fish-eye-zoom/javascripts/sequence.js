var storyTable = document.createElement('table');
storyTable.id = "storyTable";
storyTable.border = "2"; // TODO add CSS for style

var storyTableRow = document.createElement('tr');
storyTableRow.id = "storyTableRow";
var episodeTable = new Array;
var episodeCell = new Array;
var episodeTraceDiv = new Array;
for (var i = 0; i < episodeCounter; i++) {
    episodeCell[i] = document.createElement('td');
    episodeTable[i] = document.createElement('table');
    episodeTable[i].id = "episodeTable_" + i;

    var episodeTableFirstRow = document.createElement('tr');
    var episodeTableSecondRow = document.createElement('tr');
    var episodeSourceCell = document.createElement('td');
    // TODO add episode source

    var episodeSourceDiv = document.createElement('div');
    episodeSourceDiv.id = "source_div_" + i;
    //episodeSourceDiv
    var actorType = "Episode " + i;

    // TODO check for empty component list
    var episodeActor = allEpisodes[i].getComponents()[0];

    if (episodeActor instanceof DOMEventTrace) {
        actorType += "\nDOM_EVENT";
    } else if (episodeActor instanceof TimingTrace) {
        actorType += "\nTIMING_EVENT";
    } else if (episodeActor instanceof XHREvent) {
        actorType += "\nXHR_EVENT";
    }

    episodeSourceDiv.appendChild(episodeSourceDiv.ownerDocument.createTextNode(actorType));
    episodeSourceCell.appendChild(episodeSourceDiv);

    var episodeTraceCell = document.createElement('td');
    episodeTraceCell.rowspan = "2";

    episodeTraceDiv[i] = document.createElement('div');

    episodeTraceDiv[i].id = "trace_div_" + i;
    episodeTraceDiv[i].addEventListener('click', traceDivClickHandler, false);
    episodeTraceDiv[i].setAttribute('class', 'ud_diagram_div');
    episodeTraceDiv[i].style.width = width + 'px';
    episodeTraceDiv[i].style.height = height + 'px';

    // Change this to assign the trace class based on the type of the episode trace (dom/timing/xhr)
    if (actorType.contains("DOM_EVENT")) {
        episodeCell[i].setAttribute('class', 'episode_cell_dom');
    } else if (actorType.contains("TIMING_EVENT")) {
        episodeCell[i].setAttribute('class', 'episode_cell_timing');
    } else if (actorType.contains("XHR_EVENT")) {
        episodeCell[i].setAttribute('class', 'episode_cell_xhr');
    }

    episodeTraceCell.appendChild(episodeTraceDiv[i]);
    var canvas = document.createElement('canvas');
    canvas.setAttribute('class', 'ud_diagram_canvas');
    canvas.width = this.width;
    canvas.height = this.height;
    canvas.id = "canvas_" + i;
    var mainContext = canvas.getContext('2d');
    episodeTraceDiv[i].appendChild(canvas);

    var canvas2 = document.createElement('canvas');
    canvas2.setAttribute('class', 'ud_diagram_canvas');
    canvas2.width = this.width;
    canvas2.height = this.height;
    canvas2.id = "canvas_" + i;
    canvas2.onmousedown = function () {
        return false;
    }
    var motionContext = canvas2.getContext('2d');

    episodeTraces[i] = (function (zero, div, mainContext, motionContext, width, height) {
        var initialX = 30;
        var initialY = 60;

        allEpisodes[i].sequenceDiagram = new UMLSequenceDiagram({
            backgroundNodes: '#FF9900'
        });
        allEpisodes[i].sequenceDiagram.initialize(zero, div, mainContext, motionContext, width, height);

        // Add the components into the UMLSequenceDiagram
        for (var h = 0; h < allEpisodes[i].internalComponents.length; h++) {
            if (allEpisodes[i].internalComponents[h] instanceof DOMEventTrace) {
                // DOM event, Actor should be created for sequence diagram
                var newVisual = new UMLActor({
                    x: allEpisodes[i].internalComponents[h].x_pos,
                    y: allEpisodes[i].internalComponents[h].y_pos
                });
                newVisual.setName('Event type:' + allEpisodes[i].internalComponents[h].getEventType() + '\nHandler:' + allEpisodes[i].internalComponents[h].getEventHandler() + '\nTarget ID:' + allEpisodes[i].internalComponents[h].targetElement.attributes.id);
                newVisual.notifyChange();
                allEpisodes[i].internalComponents[h].visual = newVisual;
            } else if (allEpisodes[i].internalComponents[h] instanceof XHREvent) {
                // XMLHttpRequest, Actor should be created
                var newVisual = new UMLActor({
                    x: allEpisodes[i].internalComponents[h].x_pos,
                    y: allEpisodes[i].internalComponents[h].y_pos
                });
                newVisual.setName('XHR ID: ' + allEpisodes[i].internalComponents[h].getXHRId().toString());
                newVisual.notifyChange();
                allEpisodes[i].internalComponents[h].visual = newVisual;
            } else if (allEpisodes[i].internalComponents[h] instanceof TimingTrace) {
                // Timeing event, Actor should be created
                var newVisual = new UMLActor({
                    x: allEpisodes[i].internalComponents[h].x_pos,
                    y: allEpisodes[i].internalComponents[h].y_pos
                });
                newVisual.setName('TID: ' + allEpisodes[i].internalComponents[h].getTimeoutId().toString());
                newVisual.notifyChange();
                allEpisodes[i].internalComponents[h].visual = newVisual;
            } else {
                // Function trace, create lifeline
                var newVisual = new UMLLifeline({
                    x: allEpisodes[i].internalComponents[h].x_pos,
                    y: allEpisodes[i].internalComponents[h].y_pos
                });
                newVisual.setName(allEpisodes[i].internalComponents[h].getName());
                newVisual.notifyChange();
                allEpisodes[i].internalComponents[h].visual = newVisual;
                //newVisual.addEventListener('click', traceDivClickHandler2, false);
            }
            allEpisodes[i].sequenceDiagram.addElement(allEpisodes[i].internalComponents[h].getDiagramObject());
        }

        // Shift components of sequence diagram to better accomadate longer names
        // (i.e. long function names overlap with nearby components if not fixed)
        for (var h = 1; h < allEpisodes[i].internalComponents.length; h++) {
            if (allEpisodes[i].internalComponents[h - 1].getDiagramObject()._width > 200) {
                for (var j = h; j < allEpisodes[i].internalComponents.length; j++) {
                    allEpisodes[i].internalComponents[j].visual._x +=
                        allEpisodes[i].internalComponents[h - 1].getDiagramObject()._width / 2;
                    allEpisodes[i].internalComponents[j].visual.notifyChange();
                }
            }
        }

        // Add dotted lines for actors within sequence diagrams
        for (var f = 0; f < allEpisodes[i].internalComponents.length; f++) {
            if (allEpisodes[i].internalComponents[f].getDiagramObject() instanceof Actor) {
                var dottedLine = new UMLLifeline({
                    x: allEpisodes[i].internalComponents[f].getDiagramObject()._x + 25,
                    y: allEpisodes[i].internalComponents[f].getDiagramObject()._y + 35
                });
                dottedLine.setName("");
                dottedLine._heightSmallRectangle = -1;
                dottedLine._width = 0;
                allEpisodes[i].sequenceDiagram.addElement(dottedLine);
            }
        }

        // Add the messages into the UMLSequenceDiagram
        for (var j = 0; j < allEpisodes[i].internalMessages.length; j++) {
            var newMessage = new UMLCallMessage({
                a: (allEpisodes[i].internalMessages[j].a).visual,
                b: (allEpisodes[i].internalMessages[j].b).visual,
                y: allEpisodes[i].internalMessages[j].y
            });

            if (newMessage._elemB == newMessage._elemA) {
                newMessage._objB._visible = false;
            }

            if (newMessage._points[0]._x == undefined &&
                newMessage._points[1]._x == undefined) {
                // Recursive call to Actor
                var dottedLine = new UMLLifeline({
                    x: newMessage._elemA._x + 25,
                    y: newMessage._elemA._y + 35
                });
                dottedLine.setName("");
                dottedLine._heightSmallRectangle = -1;
                dottedLine._width = 0;
                newMessage._elemA = dottedLine;
                newMessage._elemB = dottedLine;
            } else if (newMessage._points[0]._x == undefined) {
                // Source of message is an actor (not lifeline)
                newMessage._points[0].setX(newMessage._elemA._x + 23);
                newMessage._objA = newMessage._objB;
            } else if (newMessage._points[1]._x == undefined) {
                // Destination of message is an actor (not lifeline)
                newMessage._points[1].setX(newMessage._elemB._x + 23);
                newMessage._objB = newMessage._objA;
            }
            // Add message to sequence diagram
            newMessage.notifyChange();
            allEpisodes[i].sequenceDiagram.addElement(newMessage);
        }

        allEpisodes[i].sequenceDiagram._width = allEpisodes[i].internalComponents[h - 1].getDiagramObject()._x + 165;
        if (j > 0) {
            allEpisodes[i].sequenceDiagram._height = allEpisodes[i].internalMessages[j - 1].y + 120;
        } else {
            allEpisodes[i].sequenceDiagram._height = 300;
        }
        return allEpisodes[i].sequenceDiagram;

    })(0, episodeTraceDiv[i], mainContext, motionContext, width, height);






    episodeTraceDiv[i].style.width = episodeTraces[i]._width + 30;
    canvas.width = episodeTraces[i]._width;
    canvas.height = episodeTraces[i]._height;


    lifeLinesByEpisode[i] = new Array();
    messagesByEpisode[i] = new Array();
    mutationsByEpisode[i] = new Array();

    episodeTableFirstRow.appendChild(episodeSourceCell);
    episodeTableFirstRow.appendChild(episodeTraceCell);


    if (allEpisodes[i].getMutations().length > 0) {
        signalMutation2 = true;
        var mutationNotification = document.createElement('div');
        mutationNotification.className = 'mutationnotification';
        mutationNotification.innerHTML = "Mutation Present"
        episodeTableSecondRow.appendChild(mutationNotification);
    }
    // episodeDomCell = first column, episodeDomCell2 = second column
    episodeTable[i].appendChild(episodeTableFirstRow);
    episodeTable[i].appendChild(episodeTableSecondRow);

    episodeCell[i].appendChild(episodeTable[i]);

    storyTableRow.appendChild(episodeCell[i]);
}
