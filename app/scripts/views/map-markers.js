/*global Electionsbih, Backbone, JST*/

Electionsbih.Views = Electionsbih.Views || {};

(function () {
    'use strict';

    Electionsbih.Views.MapMarkers = Backbone.View.extend({

        template: JST['app/scripts/templates/map-markers.ejs'],

        tagName: 'div',

        id: '',

        className: '',

        events: {},

        initialize: function () {
        //    this.listenTo(this.model, 'change', this.render);
        },

        render: function () {
            this.$el.html(this.template(this.model.toJSON()));
        }

    });

})();