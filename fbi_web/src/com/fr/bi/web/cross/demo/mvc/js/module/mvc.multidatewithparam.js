MultiDateWithParamView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(MultiDateWithParamView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-etl-multi-date-with-param-view"
        })
    },

    _init: function () {
        MultiDateWithParamView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var pane = BI.createWidget({
            type: "bi.multi_date_param_pane"
        });
        pane.on(BI.MultiDateParamPane.EVENT_CHANGE, function(v){
            BI.Msg.alert("", v);
        });
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [pane]
        });
    }
});

MultiDateWithParamModel = BI.inherit(BI.Model, {

});