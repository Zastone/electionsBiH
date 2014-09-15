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
          this.listenTo(this.collection, 'sync', function() {
            this.render()});
        },

        render: function (view) {
          this.view = view || 'country';
          this.parties = [];

          var that = this,
          parties,
          viewMandates;
          if (this.view === 'country'){
            viewMandates = this.collection.models;
          }
          else {
            viewMandates = [_.find(this.collection.models, function(x){
              return x.get('electoral_unit_id') == that.view;
            })]
          }
          parties = _.unique(_.flatten(_.map(viewMandates, function(d) {
            return _.pluck(d.get('mandates'),'abbreviation');
          })));
          _.each(parties, function(x) {
            that.parties.push({abbreviation: x, status: 'active'})
          })

          this.$el.html(this.template({parties: this.parties}));
        },

        partyToggle: function (ev) {
          var that = this;
          var index = _.indexOf(_.pluck(that.parties,'abbreviation'),$(ev.target).attr('class').split(' ')[0].split('_').join(' '))
          this.parties[index]['status'] = (this.parties[index]['status'] == 'active') ? 'inactive' : 'active'
          $(ev.target).toggleClass('inactive');
          Electionsbih.markerView.render(this.parties);
        },

        selectAll: function (ev) {
          var that = this;
          _.each(that.parties, function(x){
            x['status'] = 'active'
          });
          this.$('.party-select').children().toggleClass('inactive',false);
          Electionsbih.markerView.render(this.parties);
        },

        unselectAll: function (ev) {
          var that = this;
          _.each(that.parties, function(x){
            x['status'] = 'inactive'
          });
          this.$('.party-select').children().toggleClass('inactive',true);
          Electionsbih.markerView.render(this.parties);
        }

    });

})();
