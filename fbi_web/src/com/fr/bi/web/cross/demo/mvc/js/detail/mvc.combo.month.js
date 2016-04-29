MonthComboView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(MonthComboView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-month-combo bi-mvc-layout"
        })
    },

    _init: function(){
        MonthComboView.superclass._init.apply(this, arguments);
    },

    _createYearCombo: function(){
        var combo = BI.createWidget({
            type: "bi.month_combo",
            height: 30
        });
        combo.setValue(2);
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
                text: "bi.month_combo",
                height: 30
            }, {
                el: this._createYearCombo()
            }]
        })
    }
});

MonthComboModel = BI.inherit(BI.Model, {

});