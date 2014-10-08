function createTranslation(lang) {
    var j = null;
    return function (str) {
        if (!j) {
            $.ajax("data/translations/messages-" + lang + ".json", {async: false}).done(function (d) {
                j = new Jed({locale_data: {messages: d}});
            });
            //NOP
        }
        return j.gettext(str);
    }
}

var translate = createTranslation(
        window.location.hash.replace("#", "").split("/")[0] || "en");
