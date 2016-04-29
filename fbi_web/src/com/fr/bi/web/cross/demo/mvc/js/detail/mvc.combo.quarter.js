QuarterComboView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(QuarterComboView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-quarter-combo bi-mvc-layout"
        })
    },

    _init: function(){
        QuarterComboView.superclass._init.apply(this, arguments);
    },

    _createYearCombo: function(){
        var combo = BI.createWidget({
            type: "bi.quarter_combo",
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
                text: "bi.quarter_combo",
                height: 30
            }, {
                el: this._createYearCombo()
            }]
        })
    }
});

QuarterComboModel = BI.inherit(BI.Model, {

});