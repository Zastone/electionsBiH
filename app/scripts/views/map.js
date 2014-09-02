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

            if (this.layers.length) {
              _.each(this.layers, function(x) {
              Electionsbih.map.removeLayer(x);
            })}
            var electoral_units = _.unique(_.flatten(
                _.map(this.collection.models, function(d) {
                    return _.pluck(d.get("electoral_units"),"id");
                        })));

            // find the elements (municipalities) of each electoral unit
            var that = this;
            var zp = function(n,c) { var s = String(n); if (s.length< c) { return zp("0" + n,c) } else { return s } }
            var municipalities=_.map(electoral_units,function(u) {
              return {electoral_unit: u,
                municipalities: _.map(_.pluck(_.filter(that.collection.models, function(x) {
                    return _.contains(_.pluck(x.get("electoral_units"),"id"),u);
                }),"id"),function(d) {return zp(d,3)})};
            });
            // for each electoral unit id, construct a merged geojson by looping though the geojson features and merging those municipalities which match
            var eu;
            _.each(municipalities, function(x) {
              //create the merged one
              eu = topojson.merge(that.topojson, _.filter(that.topojson.objects.bosnia.geometries, function(y) {
                return _.contains(x.municipalities, y.id);
              }));
              //add to map
              var layer = L.geoJson(eu, {
                    style: function(feature) {
                        return {
                            color: '#ccc',
                            fillColor: 'hotpink',
                            opacity: 0.7,
                            fillOpacity: 0.5,
                            weight: 1
                        }
                      }
                }).on({
                  click: function() {
                    Electionsbih.map.fitBounds(layer.getBounds());
                  }
                }).addTo(Electionsbih.map);
              that.layers.push(layer);
            });
        }


    });

})();
