/*global Electionsbih, Backbone*/

Electionsbih.Collections = Electionsbih.Collections || {};

(function () {
    'use strict';

    Electionsbih.Collections.Results = Backbone.Collection.extend({
        urlRoot: 'http://192.168.0.89:8090/results/',
        model: Electionsbih.Models.Results,

        url: function() {
            return this.urlRoot+this.options.type+"/"+this.options.year;
        },

        initialize: function (options) {
           this.options = options;
        },

        parse: function(response, options)  {
            console.log("Help me, I'm trapped in the parse function!");
            return response.municipality_results;
        }

    });

})();
