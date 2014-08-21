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
        //    this.listenTo(this.model, 'change', this.render);
              this.render();
        },

        render: function () {
            this.$el.html(this.template({parties: [{abbreviation: "FOO",
            "name":"Friends of Ood"},
            {abbreviation: "UPBIH", name: "Unicorn Party Bosnia I Herzegovina"
            } ]}));
        }

    });

})();
