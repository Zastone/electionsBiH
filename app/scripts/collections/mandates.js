/*global Electionsbih, Backbone*/

Electionsbih.Collections = Electionsbih.Collections || {};

(function () {
    'use strict';

    Electionsbih.Collections.Mandates = Backbone.Collection.extend({

        model: Electionsbih.Models.Mandates

    });

})();
