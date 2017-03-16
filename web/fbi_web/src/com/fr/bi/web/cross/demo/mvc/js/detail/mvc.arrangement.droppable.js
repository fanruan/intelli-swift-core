ArrangementDroppableView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ArrangementDroppableView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-arrangement-droppable bi-mvc-layout"
        })
    },

    _init: function () {
        ArrangementDroppableView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;
        this.droppable = BI.createWidget({
            type: "bi.arrangement_droppable"
        });
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: this.droppable,
                left: 30,
                right: 30,
                top: 30,
                bottom: 30
            }]
        });
    }
});

ArrangementDroppableModel = BI.inherit(BI.Model, {});