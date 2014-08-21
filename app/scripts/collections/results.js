/*global Electionsbih, Backbone*/

Electionsbih.Collections = Electionsbih.Collections || {};

(function () {
    'use strict';

    Electionsbih.Collections.Results = Backbone.Collection.extend({
        url: '',
        model: Electionsbih.Models.Results

    });

})();
