/*global Electionsbih, Backbone, JST*/

Electionsbih.Views = Electionsbih.Views || {};

(function () {
    'use strict';

    Electionsbih.Views.Map = Backbone.View.extend({

        initialize: function () {
            this.listenTo(this.collection, 'sync', this.render);
            this.layers = [];
            var that = this;
            $.getJSON("data/maps/bosnia.topojson",function(d) {
              //that.geojson=topojson.feature(d,d.objects.bosnia);
              that.topojson = d;
            })

        },

        render: function () {
            //this.$el.html(this.template(this.model.toJSON()));
            console.log(this.collection)

            var electoral_units = _.unique(_.flatten(
                _.map(this.collection.models, function(d) {
                    return _.pluck(d.get("electoral_units"),"id");
                        })));
            console.log(electoral_units);
        }


    });

})();
