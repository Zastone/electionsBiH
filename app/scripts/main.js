/*global Electionsbih, $*/


window.Electionsbih = {
    Models: {},
    Collections: {},
    Views: {},
    Routers: {},
    init: function () {
        'use strict';
        Electionsbih.router = new Electionsbih.Routers.BosniaElection();
        Backbone.history.start();
    }
};

$(document).ready(function () {
    'use strict';
    Electionsbih.init();
});
