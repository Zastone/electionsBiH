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
            var that = this;
            var partySeats = {},comps = [],compModel,viewMandates;

            // all models for country view, only one at the electoral unit level
            if (this.view === 'country'){
              viewMandates = this.collection.mandates.models;
            }
            else {
              viewMandates = [_.find(this.collection.mandates.models, function(x){
                return x.get('electoral_unit_id') == that.view;
              })]
            }

            // aggregate the mandates by party
            _.each(viewMandates, function(d) {
              _.each(d.get('mandates'), function (x){
                if (!partySeats[x['name']]){
                  partySeats[x['name']] = {name: x['name'], seats: x['seats']};
                }
                else {
                  partySeats[x['name']]['seats'] += x['seats'];
                }
              });
            });

            // add comps if we're at the country level
            if (this.view === 'country'){
              compModel = _.find(viewMandates, function (x){
                return x.get('electoral_unit_id').toString().substring(1,3) === '00';
              })

              if (compModel) comps = compModel.get('mandates');

              _.each(comps,function(comp){
                partySeats[comp['name']]['comp'] = comp['seats'];
              })
            }

            // sort
            partySeats = _.sortBy(partySeats, function (x){
              return -x['seats'];
            })


            var partyVotes = {},totalVotes;

            _.each(this.collection.results.models, function (d){
              _.each(d.get('electoral_units'), function (x){
                _.each(x['results'], function (y){
                  if (that.view === 'country' || that.view == x['id']){
                    if (!partyVotes[y['abbreviation']]){
                      partyVotes[y['abbreviation']] = {name: y['name'], abbreviation: y['abbreviation'], votes: y['votes']};
                    }
                    else {
                      partyVotes[y['abbreviation']]['votes'] += y['votes'];
                    }
                  }
                })
              })
            })

            totalVotes = _.reduce(partyVotes, function(a, b){ return a + b['votes']; }, 0);
            _.each(partyVotes, function(x){
              x['per'] = (x['votes']/totalVotes * 100).toFixed(2);
            })

            partyVotes = _.sortBy(partyVotes, function (x){
              return -x['per'];
            })

            this.$el.html(this.template({partySeats : partySeats, partyVotes: partyVotes}));

        },

        load: function () {
          this.loaded == 0 ? this.loaded += 1 : this.render();
        }

    });

})();
