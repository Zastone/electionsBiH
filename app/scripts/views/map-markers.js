/*global Electionsbih, Backbone, JST*/

Electionsbih.Views = Electionsbih.Views || {};

(function () {
    'use strict';

    Electionsbih.Views.MapMarkers = Backbone.View.extend({

        initialize: function () {
            this.listenTo(this.collection, 'sync', this.render);
            this.layers = [];
        },

        render: function () {
            //this.$el.html(this.template(this.model.toJSON()));
            console.log(this.collection)

            _.each(this.collection.models, function (x) {
              make a circle
            });
        }

    });

})();
