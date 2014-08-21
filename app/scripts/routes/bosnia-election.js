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
        electionType = _.map(['parliament_bih','parliament_fbih','parliament_rs','kanton'],
            function(x) { return {"val": x, "display": translate(x)} })
        ,
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

        Electionsbih.languageSelect = new Electionsbih.Views.LanguageSelect({
                el: '#toggle-language',
                options: [languages,state]
        });

        Electionsbih.resultsDisplay = new Electionsbih.Views.ResultsDisplay();

        Electionsbih.partySelect = new Electionsbih.Views.PartySelect();

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

            this.navigate('en/2010/parliament_bih', {trigger: false});

        },

        newfilter: function(lang, year, type) {
            if (_.contains(_.pluck(years, 'val'),Number(year)) &&
                _.contains(_.pluck(electionType, 'val'),type) &&
                _.contains(_.pluck(languages, 'val'),lang)) {
                //Electionsbih.markerView.setFilter({type: type, year: year});
                this.navigate(lang + '/' + year + '/' + type, {trigger: false});
                state['lang'] = lang;
                state['year'] = year;
                state['type'] = type;
                if (!init) bootstrap();
            }
            else {
                //Electionsbih.markerView.setFilter({lang: 'en', type: 'parliament_bih', year: 2010});
                this.navigate('en/2010/parliament_bih', {trigger: false});
                state['year'] = 2010;
                state['type'] = 'parliament_bih';
                state['lang'] = 'en';
                if (!init) bootstrap();
            }
            Electionsbih.electionSelect.render();
            Electionsbih.yearSelect.render();
            //Electionsbih.markerView.load();

        },

        setLang: function(language) {
            state['lang'] = language;
            translate = createTranslation(language);
            this.navigate(state['lang'] +'/' + state['year'] + '/' + state['type'], {trigger: false});


        },

    });

})();
