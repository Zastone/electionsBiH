/*global Electionsbih, Backbone, JST*/

Electionsbih.Views = Electionsbih.Views || {};

(function () {
    'use strict';

    Electionsbih.Views.Map = Backbone.View.extend({

        //template: JST['app/scripts/templates/map.ejs'],

        tagName: 'div',

        id: 'map',

        className: '',

        events: {},

        initialize: function () {
        //    this.listenTo(this.model, 'change', this.render);
        },

        render: function () {
            this.$el.html(this.template());
        }

    });

})();
