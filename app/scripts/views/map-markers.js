/*global Electionsbih, Backbone, JST*/

Electionsbih.Views = Electionsbih.Views || {};

(function () {
    'use strict';

    Electionsbih.Views.MapMarkers = Backbone.View.extend({

        initialize: function () {
            this.layers = [];
            this.loaded = 0;
            var that = this
            $.getJSON("data/maps/centroids.geojson",function(d) {
                that.centroids = d;
            })

            this.listenTo(this.collection.results, 'sync', this.load);
            this.listenTo(this.collection.mandates, 'sync', this.load);


        },

        render: function (ps) {

            var partyStatus = ps || 0;

            var activeParties = _.pluck(_.filter(partyStatus, function(x) {
              return x['status'] === 'active';
            }),'abbreviation');

            if (this.layers.length) {
              _.each(this.layers, function(x) {
              Electionsbih.map.removeLayer(x);
            })}

            var zp = function(n,c) { var s = String(n); if (s.length< c) { return zp("0" + n,c) } else { return s } }

            var parties = _.unique(_.flatten(_.map(this.collection.mandates.models, function(d) {
              return _.pluck(d.get('mandates'),'abbreviation');
            })));

            var muni = this.centroids;

            var circles = [];

            _.each(this.collection.results.models, function (x) {

              // for each model/municipality, find the top three vote getters
              var results = x.get('electoral_units')[0]['results']
              var resultsSort = _.sortBy(results, function(y) {
                return -y['votes']
              })
              var topThree = resultsSort.slice(0,3)

              // let's find the municipality centroid as a latLng
              var match = _.find(muni.features, function(y) {
                  return y['properties']['id'] == zp(x.get('id'),3)
                })
              //if (!match) console.log(x.get('id'))
              if (match){

                var coord = match['geometry']['coordinates'];

                var jiggle = (Electionsbih.map.getBounds()._northEast.lat - Electionsbih.map.getBounds()._southWest.lat)/200

                _.each(topThree, function(y,i){

                   if (partyStatus === 0 || _.contains(activeParties,y['abbreviation'])) {
                     circles.push(L.circleMarker([coord[1]+jiggle*Math.sin((i*2*Math.PI)/3),coord[0]+jiggle*Math.cos((i*2*Math.PI)/3)], {
                            radius: Math.sqrt(y['votes']) / 10,
                            weight: 1.5,
                            color: color(y['abbreviation']),
                            opacity: 0.8,
                            fillColor: color(y['abbreviation']),
                            fillOpacity: 0.8,
                     }))
                   }
                })

              }
            });

            var circleLayer = new L.LayerGroup(circles).addTo(Electionsbih.map);

            this.layers.push(circleLayer);
        },

        load: function () {
          this.loaded === 0 ? this.loaded += 1 : this.render();
        }

    });

})();
