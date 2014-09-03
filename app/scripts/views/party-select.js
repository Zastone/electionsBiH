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

        events: {
          'click .party-select li'         : 'partyToggle',
          'click .sl-all'                  : 'selectAll',
          'click .unsl-all'                : 'unselectAll'
        },

        initialize: function () {
          this.listenTo(this.collection, 'sync', this.render);
        },

        render: function () {

          var parties = _.unique(_.flatten(_.map(this.collection.models, function(d) {
            return _.pluck(d.get("mandates"),"name");
          })));

          this.$el.html(this.template({parties: parties}));

        },

        partyToggle: function (ev) {
          $(ev.target).toggleClass('active')
        },

        selectAll: function () {
          this.$('.party-select').children().toggleClass('active',true);

        },

        unselectAll: function () {
          this.$('.party-select').children().toggleClass('active',false);
        }

    });

})();
