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

            // add info div for hover info
            var info = L.control();

            info.onAdd = function (map) {
                this._div = L.DomUtil.create('div', 'info'); // create a div with a class "info"
                this.update();
                return this._div;
            };

            info.update = function (props) {
                this._div.innerHTML = (props ? '<h4>Municipality: </h4>' + props.muni + '<br><h5>Party: </h5>' + props.party + ', Votes: ' + props.votes :
                'hover for more info');
            };

            info.addTo(Electionsbih.map);
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

            var partySeats = Electionsbih.router.partyCalc('country',this.collection.mandates,this.collection.results);
            console.log(partySeats)
            var muni = this.centroids;

            var circles = [],
            circleLayer;

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

                   if ((partyStatus === 0 || _.contains(activeParties,y['abbreviation'])) && _.contains(_.pluck(partySeats,'abbreviation'),y['abbreviation'])) {
                     circles.push(L.circleMarker([coord[1]+jiggle*Math.sin((i*2*Math.PI)/3),coord[0]+jiggle*Math.cos((i*2*Math.PI)/3)], {
                            radius: Math.sqrt(y['votes']) / 10,
                            weight: 0.5,
                            color: 'white',
                            opacity: 1,
                            fillColor: color(y['abbreviation']),
                            fillOpacity: 1,
                     }));
                   }
                })

              }
            });

            // add circle layer

            circleLayer = new L.LayerGroup(circles, {onEachFeature: this.onEachFeature}).addTo(Electionsbih.map);
            console.log(circleLayer)
            this.layers.push(circleLayer);

        },

        load: function () {
          this.loaded === 0 ? this.loaded += 1 : this.render();
        },

        onEachFeature: function (feature, layer) {
          console.log(this)
          layer.on({
            mouseover: this.markerInfo,
            mouseout: this.resetInfo
          });
        },

        markerInfo: function (e) {
          console.log("I'm hovering!")
          var layer = e.target;
          info.update(layer.feature.properties);
        },

        resetInfo: function (e) {
          circleLayer.resetStyle(e.target);
          info.update();
        }

    });

})();
