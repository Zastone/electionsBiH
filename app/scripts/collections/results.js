/*global Electionsbih, Backbone*/

Electionsbih.Collections = Electionsbih.Collections || {};

(function () {
    'use strict';

    Electionsbih.Collections.Results = Backbone.Collection.extend({
        urlRoot: 'http://kdp-ztm.epf.p2.tiktalik.com:8090/v1/results/',
        model: Electionsbih.Models.Results,

        url: function() {
            return this.urlRoot+this.options.type+"/"+this.options.year;
        },

        initialize: function (options) {
           this.options = options;
        },

        parse: function(response, options)  {
            return response.municipality_results;
        }

    });

})();
