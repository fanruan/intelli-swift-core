/**
 * Created by GUY on 2015/6/24.
 */
BIShow.View = BI.inherit(BI.View, {

    _init: function(){
        BIShow.View.superclass._init.apply(this, arguments);
        this.populate();
    },

    _render: function(vessel){
        vessel.css("z-index", 0);
    },

    refresh: function(){
        this.skipTo("pane","", "popConfig");
    }
});
