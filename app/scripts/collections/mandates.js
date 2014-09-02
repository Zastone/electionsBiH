/*global Electionsbih, Backbone*/

Electionsbih.Collections = Electionsbih.Collections || {};

(function () {
    'use strict';

    Electionsbih.Collections.Mandates = Backbone.Collection.extend({
        urlRoot: 'http://kdp-ztm.epf.p2.tiktalik.com:8090/v1/mandates/',
        model: Electionsbih.Models.Mandates,

        url: function() {
            return this.urlRoot+this.options.type+"/"+this.options.year;
        },

        initialize: function (options) {
           this.options = options;
        },

        parse: function(response, options)  {
            return response.electoral_unit_mandates;
        }

    });

})();
