DateComboView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(DateComboView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-date-combo bi-mvc-layout"
        })
    },

    _init: function(){
        DateComboView.superclass._init.apply(this, arguments);
    },

    _createYearCombo: function(){
        var combo = BI.createWidget({
            type: "bi.year_combo",
            height: 30
        });
        combo.setValue(2010);
        return combo;
    },

    _render: function(vessel){

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 30,
            vgap: 20,
            items: [{
                type: "bi.label",
                whiteSpace: "normal",
                text: "选择日期",
                height: 30
            }, {
                el: {
                    type: "bi.date_picker"
                }
            }, {
                type: "bi.label",
                whiteSpace: "normal",
                text: "日期面板",
                height: 30
            }, {
                el: {
                    type: "bi.date_calendar_popup"
                }
            }, {
                type: "bi.label",
                whiteSpace: "normal",
                text: "日期Trigger",
                height: 30
            }, {
                el: {
                    type: "bi.date_trigger"
                }
            }, {
                type: "bi.label",
                whiteSpace: "normal",
                text: "日期下拉框",
                height: 30
            }, {
                el: {
                    type: "bi.date_combo"
                }
            }]
        })
    }
});

DateComboModel = BI.inherit(BI.Model, {

});