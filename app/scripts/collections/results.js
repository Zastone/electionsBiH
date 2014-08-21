/*global Electionsbih, Backbone*/

Electionsbih.Collections = Electionsbih.Collections || {};

(function () {
    'use strict';

    Electionsbih.Collections.Results = Backbone.Collection.extend({

        model: Electionsbih.Models.Results

    });

})();
