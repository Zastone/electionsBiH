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
          this.listenTo(this.collection, 'sync', this.render);
        },

        render: function () {
            console.log("Why does this render function get called twice? I guess this sync happens twice? No idea")
            console.log(this.collection)
            // maybe do some stuff to the collection first
            this.$el.html(this.template({results : this.collection}));
        }

    });

})();
