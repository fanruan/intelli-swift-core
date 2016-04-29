/**
 * Created by GUY on 2015/6/24.
 */
BIDezi.View = BI.inherit(BI.View, {

    _init: function () {
        BIDezi.View.superclass._init.apply(this, arguments);
        this.populate();
    },

    _render: function (vessel) {
        vessel.css("z-index", 0);
        var subvessel = BI.createWidget();
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: subvessel,
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }]
        });
        this.addSubVessel("pane", subvessel);
    },

    refresh: function () {
        this.skipTo("pane", "pane", "popConfig");
    }
});