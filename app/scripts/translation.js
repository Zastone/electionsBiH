var translate;
$(document).ready(
    function() {
        var lang=window.location.hash.replace("#","").split("/")[0] || "en";
        var j = null;
        $.ajax("data/translations/messages-"+lang+".json",{async: false}).done(function(d) {
            var j=new Jed(d); });
        translate= function(str) {
            while (!j) {
                //NOP
                }
            return j.gettext(str);
                }
            });
