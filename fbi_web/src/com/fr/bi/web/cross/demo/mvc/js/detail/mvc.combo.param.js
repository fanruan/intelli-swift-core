ParamComboView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(ParamComboView.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function(){
        ParamComboView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var combo1 = BI.createWidget({
            type: "bi.date_param_combo",
            height: 30
        });
        combo1.on(BI.DateParamCombo.EVENT_CONFIRM, function(){
            BI.Msg.alert("", "start:" + combo1.getCalculationValue().start + " end:" + combo1.getCalculationValue().end);
        });
        var combo2 = BI.createWidget({
            type: "bi.year_param_combo",
            height: 30
        });
        combo2.on(BI.YearParamCombo.EVENT_CONFIRM, function(){
            BI.Msg.alert("", "start:" + combo2.getCalculationValue().start + " end:" + combo2.getCalculationValue().end);
        });
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 20,
            vgap: 30,
            items: [{
                type: "bi.param1_date_item",
                dateType: BI.Param1DateItem.YEAR_DAY
            }, {
                type: "bi.param0_date_item"
            }, combo1, combo2]
        })
    }
});

ParamComboModel = BI.inherit(BI.Model, {

});