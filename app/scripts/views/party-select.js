/*global Electionsbih, Backbone, JST*/

Electionsbih.Views = Electionsbih.Views || {};

(function () {
    'use strict';

    Electionsbih.Views.PartySelect = Backbone.View.extend({

        template: _.template( $("#party-select-template").html() ),

        tagName: 'div',

        id: 'party-select',

        el: '#party-select',

        className: '',

        events: {},

        initialize: function () {
          this.listenTo(this.collection, 'sync', this.render);
        },

        render: function () {
            console.log(this.collection)


            var parties = _.unique(_.flatten(
              _.map(this.collection.models, function(d) {
                  return _.pluck(d.get("mandates"),"name");
                })));

            console.log(parties)
            this.$el.html(this.template({parties: parties}));
        }

    });

})();
