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
            this.info = L.control();

            this.info.onAdd = function (map) {
                this._div = L.DomUtil.create('div', 'info'); // create a div with a class "info"
                this.update();
                return this._div;
            };

            this.info.update = function (props) {
                this._div.innerHTML = (props ? 'Municipality: ' + props.municipality + '<br>Party: ' + props.party + ', Votes: ' + props.votes + ' (' + props.per + '%)' :
                'Hover over markers for more info');
            };

            this.info.addTo(Electionsbih.map);
        },

        render: function (ps) {
            var that = this,
            info = this.info,
            partyStatus = ps || 0;

            var activeParties = _.pluck(_.filter(partyStatus, function(x) {
              return x['status'] === 'active';
            }),'abbreviation');

            if (this.layers.length) {
              _.each(this.layers, function(x) {
              Electionsbih.map.removeLayer(x);
            })}

            var zp = function(n,c) { var s = String(n); if (s.length< c) { return zp("0" + n,c) } else { return s } }

            var partySeats = Electionsbih.router.partyCalc('country',this.collection.mandates,this.collection.results);

            var muni = this.centroids;

            var circles = [],
            circleLayer;

            _.each(this.collection.results.models, function (x) {

              // for each model/municipality, find the top three vote getters
              var results = x.get('electoral_units')[0]['results']
              var resultsSort = _.sortBy(results, function(y) {
                return -y['votes']
              })
              var muniVotes = _.reduce(_.pluck(resultsSort,'votes'), function(a, b){ return a + b; }, 0)
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
                   var per = (y['votes']/muniVotes * 100).toFixed(2);
                   if ((partyStatus === 0 || _.contains(activeParties,y['abbreviation'])) && _.contains(_.pluck(partySeats,'abbreviation'),y['abbreviation'])) {
                     circles.push(L.circleMarker([coord[1]+jiggle*Math.sin((i*2*Math.PI)/3),coord[0]+jiggle*Math.cos((i*2*Math.PI)/3)], {
                            radius: Math.sqrt(y['votes']) / 10,
                            weight: 0.5,
                            color: 'white',
                            opacity: 1,
                            fillColor: color(y['abbreviation']),
                            fillOpacity: 1,
                            values: {party: y['abbreviation'], votes: that.numberWithCommas(y['votes']), municipality: x.get('name'), per: per}
                     })
                     .on('mouseover', function (e) {
                       var layer = e.target;
                       info.update(layer.options.values);
                     })
                     .on('mouseout', function (e) {
                       info.update();
                     })
                     );
                   }
                })

              }
            });

            // add circle layer

            circleLayer = new L.LayerGroup(circles).addTo(Electionsbih.map);
            this.layers.push(circleLayer);

        },

        load: function () {
          this.loaded === 0 ? this.loaded += 1 : this.render();
        },

        numberWithCommas: function (x) {
          return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        }

    });

})();
