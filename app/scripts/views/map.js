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
            console.log("Initialized the map view with this guy:",this.topojson)
            // make electoral unit list
            var electoral_units = _.unique(
              _.flatten(
                _.pluck(
                  _.map(this.collection.models, function (x) {
                      return x.get("electoral_units")
                  }), "id")));
            console.log(electoral_units)
            var eu = {};
            // find the elements (municipalities) of each electoral unit
            // for each electoral unit id, construct a merged geojson by looping though the geojson features and merging those municipalities which match
            _.each(electoral_units, function (x) {

            });
        }


    });

})();
