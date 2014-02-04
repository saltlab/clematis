function traceDivClickHandler(e) {

    var splitId = this.id.split("_");
    var episodeNumber = splitId[2];

    var relativeMouseX = e.pageX - this.parentNode.getBoundingClientRect().left;
    var relativeMouseY = e.pageY - this.getBoundingClientRect().top;

    var scroll = $(this).scrollLeft();
    relativeMouseX = relativeMouseX + scroll;

    var clickedOnLifelines = false;
    var min = 0;
    var max;
    var a = 90;
    var b = 340;

    for (var n = 0; n < lifeLinesByEpisode[episodeNumber].length; n++) {
        max = lifeLinesByEpisode[episodeNumber][n].visual.getX();
    }

    for (var n = 0; n < lifeLinesByEpisode[episodeNumber].length; n++) {
        var lifelineX = lifeLinesByEpisode[episodeNumber][n].visual.getX();
        var lifelineY = lifeLinesByEpisode[episodeNumber][n].visual.getY();
        var lifelineW = lifeLinesByEpisode[episodeNumber][n].visual.getWidth();
        var lifelineH = lifeLinesByEpisode[episodeNumber][n].visual.getHeight();

        if (relativeMouseX >= lifelineX && relativeMouseX <= lifelineX + lifelineW) {
            if (lifeLinesByEpisode[episodeNumber][n].getDiagramObject() instanceof Actor) {
                viewEventInformation(lifeLinesByEpisode[episodeNumber][n]);
                signalActor = true;
            } else {
                viewDetailedCode(lifeLinesByEpisode[episodeNumber][n]);
            }
            viewEpisodeInfo(); // TODO

            clickedOnLifelines = true;
        }
        if (signalActor == true) {
            signalActor = false;
            $(document.getElementById('elem1EventType')).effect("highlight", {}, 2000);
        }
    }

    // TODO DO EPISODE STUFF (SHOW CAUSAL LINKS FOR TIMEOUTS / XHRS AND ETC)
    if (!clickedOnLifelines) {
        // show general information about the episode
        // pop up? about timeouts and xhrs and ....
    }
}
