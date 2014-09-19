/*global Electionsbih, Backbone*/

Electionsbih.Routers = Electionsbih.Routers || {};

(function () {
    'use strict';

    var defaultMap = '',
        init = false,
        languages = [
            {
                display: 'English',
                val: 'en'
            },
            {
                display: 'Bosnian',
                val: 'bs'
            },
            {
                display: 'Croatian',
                val: 'hr'
            },
            {
                display: 'Serbian',
                val: 'sr'
            }
        ],
        years = [
            {
                val: 2010
            },
            {
                val: 2014
            }
        ],
        electionType = ['parliament_bih','parliament_fbih','parliament_rs','kanton']
        ,
        state = {
            year: '',
            type: '',
            lang: ''
        };

    function bootstrap() {

        Electionsbih.collections = {
            mandates: new Electionsbih.Collections.Mandates({options: {year: '', type: ''}}),
            results: new Electionsbih.Collections.Results({options: {year: '', type: ''}})
        };

        Electionsbih.mapView = new Electionsbih.Views.Map({
            el: '#map', id: 'map', map: Electionsbih.map, collection: Electionsbih.collections.results
        });

        Electionsbih.markerView = new Electionsbih.Views.MapMarkers({
            el: '#map', id: 'map', collection: {mandates: Electionsbih.collections.mandates, results: Electionsbih.collections.results}
        });

        Electionsbih.resultsDisplay = new Electionsbih.Views.ResultsDisplay({
            collection: {mandates: Electionsbih.collections.mandates, results: Electionsbih.collections.results}
        });

        Electionsbih.partySelect = new Electionsbih.Views.PartySelect({
            collection: {mandates: Electionsbih.collections.mandates, results: Electionsbih.collections.results}
        });
        //Electionsbih.models = {};


        Electionsbih.yearSelect = new Electionsbih.Views.YearSelect({
                //id: 'toggle-year',
                el: '#toggle-year',
                options: [years,state]
                //className: 'toggle-year'
            });

        Electionsbih.electionSelect = new Electionsbih.Views.ElectionSelect({
                //id: 'toggle-election',
                el: '#toggle-election',
                options: [electionType,state]
                //className: 'toggle-election'
            });

        Electionsbih.languageSelect = new Electionsbih.Views.LanguageSelect({
                el: '#toggle-language',
                options: [languages,state]
        });

        $.getJSON("data/maps/bosnia.topojson",function(d) {
            Electionsbih.topojson = d;
        })
        //Electionsbih.mapView.load();
        init = true;
    }

    L.mapbox.accessToken = 'pk.eyJ1IjoiZGV2c2VlZCIsImEiOiJnUi1mbkVvIn0.018aLhX0Mb0tdtaT2QNe2Q';
    Electionsbih.map = L.mapbox.map('map', 'devseed.31eaf6e2')
                        .setView([44, 18], 7)

    Electionsbih.Routers.BosniaElection = Backbone.Router.extend({
        routes: {
            ''                              : 'newload',
            ':language/:year/:election'     : 'newfilter'
        },
        newload: function() {
            state['year'] = 2010;
            state['type'] = 'parliament_bih';
            state['lang'] = 'en';
            bootstrap();

            Electionsbih.collections.results.options = {year: state['year'], type: state['type']};
            Electionsbih.collections.results.fetch();

            Electionsbih.collections.mandates.options = {year: state['year'], type: state['type']};
            Electionsbih.collections.mandates.fetch();

            this.navigate('en/2010/parliament_bih', {trigger: false});

        },

        newfilter: function(lang, year, type) {
            if (_.contains(_.pluck(years, 'val'),Number(year)) &&
                _.contains(electionType,type) &&
                _.contains(_.pluck(languages, 'val'),lang)) {
                this.navigate(lang + '/' + year + '/' + type, {trigger: false});
                state['lang'] = lang;
                state['year'] = year;
                state['type'] = type;
                if (!init) bootstrap();
            }
            else {
                this.navigate('en/2010/parliament_bih', {trigger: false});
                state['year'] = 2010;
                state['type'] = 'parliament_bih';
                state['lang'] = 'en';
                if (!init) bootstrap();
            }
            Electionsbih.electionSelect.render();
            Electionsbih.yearSelect.render();

            Electionsbih.collections.results.options = {year: state['year'], type: state['type']} ;
            Electionsbih.collections.results.fetch();

            Electionsbih.collections.mandates.options = {year: state['year'], type: state['type']} ;
            Electionsbih.collections.mandates.fetch();

        },

        setLang: function(language) {
            state['lang'] = language;
            translate = createTranslation(language);
            this.navigate(state['lang'] +'/' + state['year'] + '/' + state['type'], {trigger: true});

        },

        partyCalc: function(view, mandates, results) {
            var partySeats = {},comps = [],compModel,viewMandates;

            // all models for country view, only one at the electoral unit level
            if (view === 'country'){
              viewMandates = mandates.models;
            }
            else {
              viewMandates = [_.find(mandates.models, function(x){
                return x.get('electoral_unit_id') == view;
              })]
            }

            // aggregate the mandates by party
            _.each(viewMandates, function(d) {
              _.each(d.get('mandates'), function (x){
                if (!partySeats[x['abbreviation']]){
                  partySeats[x['abbreviation']] = {name: x['name'], seats: x['seats'], abbreviation: x['abbreviation']};
                }
                else {
                  partySeats[x['abbreviation']]['seats'] += x['seats'];
                }
              });
            });

            // add comps if we're at the country level
            if (view === 'country'){
              compModel = _.find(viewMandates, function (x){
                return x.get('electoral_unit_id').toString().substring(1,3) === '00';
              })

              if (compModel) comps = compModel.get('mandates');

              //store them as strings with parens around them for rendering purposes
              _.each(comps,function(comp){
                partySeats[comp['abbreviation']]['comp'] = '(' + String(comp['seats']) + ')';
              })
            }

            var partyVotes = {},totalVotes;

            _.each(results.models, function (d){
              _.each(d.get('electoral_units'), function (x){
                _.each(x['results'], function (y){
                  if (view === 'country' || view == x['id']){
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

            // join partyVotes to partySeats
            _.each(partySeats, function(x){
              x['per'] = partyVotes[x['abbreviation']]['per']
            })

            // sort
            partySeats = _.sortBy(partySeats, function (x){
              var v = Number(x['per'])/100;
              var c = x['comp'] || '0';
              return -(x['seats'] + Number(c.replace(/\(|\)/g,"")) + v);
            })

            return partySeats;
        }

    });

})();
