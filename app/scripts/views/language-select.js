/*global Electionsbih, Backbone, JST*/

Electionsbih.Views = Electionsbih.Views || {};

(function () {
    'use strict';

    Electionsbih.Views.LanguageSelect = Backbone.View.extend({

        template: _.template($('#language-template').html()),
        events: {'click a': 'select'},
        initialize: function (options) {
            this.options = options.options[0];
            this.selected = this.options[0].display;
            this.render();
        },

        render: function () {

            this.$el.html(this.template({
                options: this.options,
                selected: this.selected
            }));

            this.$selected = this.$('.selected');
            this.$dropdown = this.$('.dropdown');
        },

        select: function(e) {
            e.preventDefault();
            e = e.toElement;
            this.$selected.text(e.innerHTML);
            this.$dropdown.toggleClass('open');
  
            Electionsbih.router.setLang(e.className);
            return false;
        }

    });

})();
