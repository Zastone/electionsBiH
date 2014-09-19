/*global Electionsbih, Backbone, JST*/

Electionsbih.Views = Electionsbih.Views || {};

(function () {
    'use strict';

    Electionsbih.Views.ResultsDisplay = Backbone.View.extend({

        template: _.template( $('#results-template').html() ),

        tagName: 'div',

        id: 'results-display',

        el: '#results-display',


        initialize: function () {
          this.loaded = 0
          this.listenTo(this.collection.results, 'sync', this.load);
          this.listenTo(this.collection.mandates, 'sync', this.load);
        },

        render: function (view) {
            this.view = view || 'country';
            this.loaded = 0;

            var partySeats = Electionsbih.router.partyCalc(this.view,this.collection.mandates,this.collection.results);
            var eu = this.view === 'country' ? 'all' : this.view;
            this.$el.html(this.template({partySeats : partySeats, eu: eu}));
        },

        load: function () {
          this.loaded == 0 ? this.loaded += 1 : this.render();
        }

    });

})();
