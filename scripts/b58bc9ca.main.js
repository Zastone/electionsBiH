function createTranslation(a){var b=null;return function(c){return b||$.ajax("data/translations/messages-"+a+".json",{async:!1}).done(function(a){b=new Jed({locale_data:{messages:a}})}),b.gettext(c)}}var translate=createTranslation(window.location.hash.replace("#","").split("/")[0]||"en"),color=function(a){var b="#999";switch(a){case"СНСД МД":b="#af251b";break;case"SDP BiH":b="#de0023";break;case"SDA":b="#009900";break;case"СДС":b="#2e3293"}return b};window.Electionsbih={Models:{},Collections:{},Views:{},Routers:{},init:function(){"use strict";Electionsbih.router=new Electionsbih.Routers.BosniaElection,Backbone.history.start()}},$(document).ready(function(){"use strict";Electionsbih.init()}),this.JST=this.JST||{},this.JST["app/scripts/templates/election-select.ejs"]=function(obj){obj||(obj={});{var __p="";_.escape}with(obj)__p+="<p>Your content here.</p>\n\n";return __p},this.JST["app/scripts/templates/language-select.ejs"]=function(obj){obj||(obj={});{var __p="";_.escape}with(obj)__p+="<p>Your content here.</p>\n\n";return __p},this.JST["app/scripts/templates/map-markers.ejs"]=function(obj){obj||(obj={});{var __p="";_.escape}with(obj)__p+="<p>Your content here.</p>\n\n";return __p},this.JST["app/scripts/templates/map.ejs"]=function(obj){obj||(obj={});{var __p="";_.escape}with(obj)__p+="<p>Your content here.</p>\n\n";return __p},this.JST["app/scripts/templates/party-select.ejs"]=function(obj){obj||(obj={});{var __p="";_.escape}with(obj)__p+="<p>Your content here.</p>\n\n";return __p},this.JST["app/scripts/templates/results-display.ejs"]=function(obj){obj||(obj={});{var __p="";_.escape}with(obj)__p+="<p>Your content here.</p>\n\n";return __p},this.JST["app/scripts/templates/year-select.ejs"]=function(obj){obj||(obj={});{var __p="";_.escape}with(obj)__p+="<p>Your content here.</p>\n\n";return __p},Electionsbih.Models=Electionsbih.Models||{},function(){"use strict";Electionsbih.Models.Results=Backbone.Model.extend({url:"",initialize:function(){},defaults:{},validate:function(){}})}(),Electionsbih.Collections=Electionsbih.Collections||{},function(){"use strict";Electionsbih.Collections.Results=Backbone.Collection.extend({urlRoot:"http://82.113.148.174:8090/v1/results/",model:Electionsbih.Models.Results,url:function(){return this.urlRoot+this.options.type+"/"+this.options.year},initialize:function(a){this.options=a},parse:function(a){return a.municipality_results}})}(),Electionsbih.Models=Electionsbih.Models||{},function(){"use strict";Electionsbih.Models.Mandates=Backbone.Model.extend({url:"",initialize:function(){},defaults:{},validate:function(){}})}(),Electionsbih.Collections=Electionsbih.Collections||{},function(){"use strict";Electionsbih.Collections.Mandates=Backbone.Collection.extend({urlRoot:"http://82.113.148.174:8090/v1/mandates/",model:Electionsbih.Models.Mandates,url:function(){return this.urlRoot+this.options.type+"/"+this.options.year},initialize:function(a){this.options=a},parse:function(a){return a.electoral_unit_mandates}})}(),Electionsbih.Routers=Electionsbih.Routers||{},function(){"use strict";function a(){Electionsbih.collections={mandates:new Electionsbih.Collections.Mandates({options:{year:"",type:""}}),results:new Electionsbih.Collections.Results({options:{year:"",type:""}})},Electionsbih.mapView=new Electionsbih.Views.Map({el:"#map",id:"map",map:Electionsbih.map,collection:Electionsbih.collections.results}),Electionsbih.markerView=new Electionsbih.Views.MapMarkers({el:"#map",id:"map",collection:{mandates:Electionsbih.collections.mandates,results:Electionsbih.collections.results}}),Electionsbih.resultsDisplay=new Electionsbih.Views.ResultsDisplay({collection:{mandates:Electionsbih.collections.mandates,results:Electionsbih.collections.results}}),Electionsbih.partySelect=new Electionsbih.Views.PartySelect({collection:{mandates:Electionsbih.collections.mandates,results:Electionsbih.collections.results}}),Electionsbih.yearSelect=new Electionsbih.Views.YearSelect({el:"#toggle-year",options:[d,f]}),Electionsbih.electionSelect=new Electionsbih.Views.ElectionSelect({el:"#toggle-election",options:[e,f]}),Electionsbih.languageSelect=new Electionsbih.Views.LanguageSelect({el:"#toggle-language",options:[c,f]}),$.getJSON("data/maps/bosnia.topojson",function(a){Electionsbih.topojson=a}),b=!0}var b=!1,c=[{display:"English",val:"en"},{display:"Bosnian",val:"bs"},{display:"Croatian",val:"hr"},{display:"Serbian",val:"sr"}],d=[{val:2010},{val:2014}],e=["parliament_bih","parliament_fbih","parliament_rs","kanton"],f={year:"",type:"",lang:""};L.mapbox.accessToken="pk.eyJ1IjoiZGV2c2VlZCIsImEiOiJnUi1mbkVvIn0.018aLhX0Mb0tdtaT2QNe2Q",Electionsbih.map=L.mapbox.map("map","devseed.31eaf6e2").setView([44,18],7),Electionsbih.Routers.BosniaElection=Backbone.Router.extend({routes:{"":"newload",":language/:year/:election":"newfilter"},newload:function(){f.year=2010,f.type="parliament_bih",f.lang="en",a(),Electionsbih.collections.results.options={year:f.year,type:f.type},Electionsbih.collections.results.fetch(),Electionsbih.collections.mandates.options={year:f.year,type:f.type},Electionsbih.collections.mandates.fetch(),this.navigate("en/2010/parliament_bih",{trigger:!1})},newfilter:function(g,h,i){_.contains(_.pluck(d,"val"),Number(h))&&_.contains(e,i)&&_.contains(_.pluck(c,"val"),g)?(this.navigate(g+"/"+h+"/"+i,{trigger:!1}),f.lang=g,f.year=h,f.type=i,b||a()):(this.navigate("en/2010/parliament_bih",{trigger:!1}),f.year=2010,f.type="parliament_bih",f.lang="en",b||a()),Electionsbih.electionSelect.render(),Electionsbih.yearSelect.render(),Electionsbih.collections.results.options={year:f.year,type:f.type},Electionsbih.collections.results.fetch(),Electionsbih.collections.mandates.options={year:f.year,type:f.type},Electionsbih.collections.mandates.fetch()},setLang:function(a){f.lang=a,translate=createTranslation(a),this.navigate(f.lang+"/"+f.year+"/"+f.type,{trigger:!0})},partyCalc:function(a,b,c){var d,e,f={},g=[];e="country"===a?b.models:[_.find(b.models,function(b){return b.get("electoral_unit_id")==a})],_.each(e,function(a){_.each(a.get("mandates"),function(a){f[a.abbreviation]?f[a.abbreviation].seats+=a.seats:f[a.abbreviation]={name:a.name,seats:a.seats,abbreviation:a.abbreviation}})}),"country"===a&&(d=_.find(e,function(a){return"00"===a.get("electoral_unit_id").toString().substring(1,3)}),d&&(g=d.get("mandates")),_.each(g,function(a){f[a.abbreviation].comp="("+String(a.seats)+")"}));var h,i={};return _.each(c.models,function(b){_.each(b.get("electoral_units"),function(b){_.each(b.results,function(c){("country"===a||a==b.id)&&(i[c.abbreviation]?i[c.abbreviation].votes+=c.votes:i[c.abbreviation]={name:c.name,abbreviation:c.abbreviation,votes:c.votes})})})}),h=_.reduce(i,function(a,b){return a+b.votes},0),_.each(i,function(a){a.per=(a.votes/h*100).toFixed(2)}),_.each(f,function(a){a.per=i[a.abbreviation].per}),f=_.sortBy(f,function(a){var b=Number(a.per)/100,c=a.comp||"0";return-(a.seats+Number(c.replace(/\(|\)/g,""))+b)})}})}(),Electionsbih.Views=Electionsbih.Views||{},function(){"use strict";Electionsbih.Views.Map=Backbone.View.extend({initialize:function(){this.listenTo(this.collection,"sync",this.render),this.layers=[]},render:function(){this.layers.length&&_.each(this.layers,function(a){Electionsbih.map.removeLayer(a)}),Electionsbih.map.setView([44,18],7);var a,b=_.unique(_.flatten(_.map(this.collection.models,function(a){return _.pluck(a.get("electoral_units"),"id")}))),c=this,d=function(a,b){var c=String(a);return c.length<b?d("0"+a,b):c},e=_.map(b,function(a){return{electoral_unit:a,municipalities:_.map(_.pluck(_.filter(c.collection.models,function(b){return _.contains(_.pluck(b.get("electoral_units"),"id"),a)}),"id"),function(a){return d(a,3)})}});_.each(e,function(b){a=topojson.merge(Electionsbih.topojson,_.filter(Electionsbih.topojson.objects.bosnia.geometries,function(a){return _.contains(b.municipalities,a.id)}));var d=String(b.electoral_unit),e=L.geoJson(a,{style:function(){return{className:d,color:"#ccc",opacity:.7,fillColor:"#555",fillOpacity:.4,weight:1}}}).on({click:function(){c.layers.length&&_.each(c.layers,function(a){a.setStyle({color:"#ccc",weight:1})}),c.selected==this.options.style().className?(Electionsbih.map.setView([44,18],7),c.selected=0,Electionsbih.resultsDisplay.render("country"),Electionsbih.partySelect.render("country")):(this.setStyle({color:"#dc3",weight:3}),c.selected=this.options.style().className,Electionsbih.resultsDisplay.render(c.selected),Electionsbih.partySelect.render(c.selected)),Electionsbih.map.fitBounds(e.getBounds())}}).addTo(Electionsbih.map);c.layers.push(e)})}})}(),Electionsbih.Views=Electionsbih.Views||{},function(){"use strict";Electionsbih.Views.PartySelect=Backbone.View.extend({template:_.template($("#party-select-template").html()),tagName:"div",id:"party-select",el:"#party-select",className:"",events:{"click .party-select li":"partyToggle","click .sl-all":"selectAll","click .unsl-all":"unselectAll"},initialize:function(){this.loaded=0,this.listenTo(this.collection.results,"sync",this.load),this.listenTo(this.collection.mandates,"sync",this.load)},render:function(a){this.view=a||"country",this.parties=[];var b=this,c=Electionsbih.router.partyCalc(this.view,this.collection.mandates,this.collection.results);_.each(c,function(a){b.parties.push({abbreviation:a.abbreviation,status:"active"})}),this.$el.html(this.template({parties:this.parties}))},partyToggle:function(a){var b=this,c=_.indexOf(_.pluck(b.parties,"abbreviation"),$(a.target).attr("class").split(" ")[0].split("_").join(" "));this.parties[c].status="active"==this.parties[c].status?"inactive":"active",$(a.target).toggleClass("inactive"),Electionsbih.markerView.render(this.parties)},selectAll:function(){var a=this;_.each(a.parties,function(a){a.status="active"}),this.$(".party-select").children().toggleClass("inactive",!1),Electionsbih.markerView.render(this.parties)},unselectAll:function(){var a=this;_.each(a.parties,function(a){a.status="inactive"}),this.$(".party-select").children().toggleClass("inactive",!0),Electionsbih.markerView.render(this.parties)},load:function(){0==this.loaded?this.loaded+=1:this.render()}})}(),Electionsbih.Views=Electionsbih.Views||{},function(){"use strict";Electionsbih.Views.ResultsDisplay=Backbone.View.extend({template:_.template($("#results-template").html()),tagName:"div",id:"results-display",el:"#results-display",initialize:function(){this.loaded=0,this.listenTo(this.collection.results,"sync",this.load),this.listenTo(this.collection.mandates,"sync",this.load)},render:function(a){this.view=a||"country",this.loaded=0;var b=Electionsbih.router.partyCalc(this.view,this.collection.mandates,this.collection.results),c="country"===this.view?"all":this.view;this.$el.html(this.template({partySeats:b,eu:c}))},load:function(){0==this.loaded?this.loaded+=1:this.render()}})}(),Electionsbih.Views=Electionsbih.Views||{},function(){"use strict";Electionsbih.Views.ElectionSelect=Backbone.View.extend({template:_.template($("#election-type-template").html()),id:"toggle-election",className:"",events:{},initialize:function(a){this.options=a.options[0],this.state=a.options[1],this.render()},render:function(){this.$el.html(this.template({navs:this.options,state:this.state}))}})}(),Electionsbih.Views=Electionsbih.Views||{},function(){"use strict";Electionsbih.Views.YearSelect=Backbone.View.extend({template:_.template($("#election-year-template").html()),id:"toggle-year",className:"",events:{},initialize:function(a){this.options=a.options[0],this.state=a.options[1],this.render()},render:function(){this.$el.html(this.template({navs:this.options,state:this.state}))}})}(),Electionsbih.Views=Electionsbih.Views||{},function(){"use strict";Electionsbih.Views.MapMarkers=Backbone.View.extend({initialize:function(){this.layers=[],this.loaded=0;var a=this;$.getJSON("data/maps/centroids.geojson",function(b){a.centroids=b}),this.listenTo(this.collection.results,"sync",this.load),this.listenTo(this.collection.mandates,"sync",this.load),this.info=L.control(),this.info.onAdd=function(){return this._div=L.DomUtil.create("div","info"),this.update(),this._div},this.info.update=function(a){this._div.innerHTML=a?"Municipality: "+a.municipality+"<br>Party: "+a.party+", Votes: "+a.votes+" ("+a.per+"%)":"Hover over markers for more info"},this.info.addTo(Electionsbih.map)},render:function(a){var b=this,c=this.info,d=a||0,e=_.pluck(_.filter(d,function(a){return"active"===a.status}),"abbreviation");this.layers.length&&_.each(this.layers,function(a){Electionsbih.map.removeLayer(a)});var f,g=function(a,b){var c=String(a);return c.length<b?g("0"+a,b):c},h=Electionsbih.router.partyCalc("country",this.collection.mandates,this.collection.results),i=this.centroids,j=[];_.each(this.collection.results.models,function(a){var f=a.get("electoral_units")[0].results,k=_.sortBy(f,function(a){return-a.votes}),l=_.reduce(_.pluck(k,"votes"),function(a,b){return a+b},0),m=k.slice(0,3),n=_.find(i.features,function(b){return b.properties.id==g(a.get("id"),3)});if(n){var o=n.geometry.coordinates,p=(Electionsbih.map.getBounds()._northEast.lat-Electionsbih.map.getBounds()._southWest.lat)/200;_.each(m,function(f,g){var i=(f.votes/l*100).toFixed(2);(0===d||_.contains(e,f.abbreviation))&&_.contains(_.pluck(h,"abbreviation"),f.abbreviation)&&j.push(L.circleMarker([o[1]+p*Math.sin(2*g*Math.PI/3),o[0]+p*Math.cos(2*g*Math.PI/3)],{radius:Math.sqrt(f.votes)/10,weight:.5,color:"white",opacity:1,fillColor:color(f.abbreviation),fillOpacity:1,values:{party:f.abbreviation,votes:b.numberWithCommas(f.votes),municipality:a.get("name"),per:i}}).on("mouseover",function(a){var b=a.target;c.update(b.options.values)}).on("mouseout",function(){c.update()}))})}}),f=new L.LayerGroup(j).addTo(Electionsbih.map),this.layers.push(f)},load:function(){0===this.loaded?this.loaded+=1:this.render()},numberWithCommas:function(a){return a.toString().replace(/\B(?=(\d{3})+(?!\d))/g,",")}})}(),Electionsbih.Views=Electionsbih.Views||{},function(){"use strict";Electionsbih.Views.LanguageSelect=Backbone.View.extend({template:_.template($("#language-template").html()),events:{"click a":"select"},initialize:function(a){this.options=a.options[0],this.selected=_.find(this.options,function(b){return b.val==a.options[1].lang}).display,this.render()},render:function(){this.$el.html(this.template({options:this.options,selected:this.selected})),this.$selected=this.$(".selected"),this.$dropdown=this.$(".dropdown")},select:function(a){return a.preventDefault(),a=a.toElement,this.$selected.text(a.innerHTML),this.$dropdown.toggleClass("open"),Electionsbih.router.setLang(a.className),!1}})}();