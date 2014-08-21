/*global Electionsbih, Backbone*/

Electionsbih.Routers = Electionsbih.Routers || {};

(function () {
    'use strict';

    var defaultMap = '',
        init = false,
        language = [
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
        electionType = [
            {
                display: 'BiH Parliament',
                val: 'parliament_bih'
            },
            {
                display: 'FBiH Parliament',
                val: 'parliament_fbih'
            },
            {
                display: 'RS Parliament',
                val: 'parliament_rs'
            },
            {
                display: 'Kanton Parliament',
                val: 'kanton'
            }
        ],
        state = {
            year: '',
            type: '',
            lang: ''
        };

    function bootstrap() {

        Electionsbih.collections = {
            mandates: new Electionsbih.Collections.Mandates(),
            results: new Electionsbih.Collections.Results()
        };

        /*Electionsbih.mapView = new Electionsbih.Views.Map({
            el: '#map', id: 'map', map: Electionsbih.map
        }); */

        /* Electionsbih.markerView = new Electionsbih.Views.MapMarkers({
            el: '#map', id: 'map', map: Electionsbih.map, collection: WHO.collections.results,
            //model: new Electionsbih.Models.()
        }); */

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


        //Electionsbih.mapView.load();
        init = true;
    }

    L.mapbox.accessToken = 'pk.eyJ1IjoiZGV2c2VlZCIsImEiOiJnUi1mbkVvIn0.018aLhX0Mb0tdtaT2QNe2Q';
    Electionsbih.map = L.mapbox.map('map', 'devseed.ca125804')
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

            //Electionsbih.markerView.setFilter({lang: 'en', year: 2010, type: 'parliament_bih'});
            //Electionsbih.markerview.load();

            //this.navigate('en/2010/parliament_bih', {trigger: false});

        },

        newfilter: function(lang, year, type) {
            if (!init) bootstrap();
            if (_.map(years, function(t) { return t.val }).indexOf(year) !== -1 &&
                _.map(electionTypes, function(t) { return t.val }).indexOf(type) !== -1 &&
                _.map(language, function(t) { return t.val }).indexOf(lang) !== -1) {
                //Electionsbih.markerView.setFilter({type: type, year: year});
                this.navigate(lang + '/' + year + '/' + type, {trigger: false});
                state['lang'] = lang;
                state['year'] = year;
                state['type'] = type;
            }
            else {
                Electionsbih.markerView.setFilter({lang: 'en', type: 'parliament_bih', year: 2010});
                this.navigate('en/2010/parliament_bih', {trigger: false});
                state['year'] = 2010;
                state['type'] = 'parliament_bih';
                state['lang'] = 'en';
            } */

            //Electionsbih.markerView.load();

        },
    });

})();
