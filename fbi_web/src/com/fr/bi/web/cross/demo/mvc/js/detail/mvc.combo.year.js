YearComboView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(YearComboView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-year-combo bi-mvc-layout"
        })
    },

    _init: function(){
        YearComboView.superclass._init.apply(this, arguments);
    },

    _createYearCombo: function(){
        var combo = BI.createWidget({
            type: "bi.year_combo",
            height: 30
        });
        //combo.setValue(2010);
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
                text: "year_trigger:输入空格退出输入状态，只存在一个年份值",
                height: 30
            }, {
                el: {
                    type: "bi.year_trigger",
                    height: 30
                }
            }, {
                type: "bi.label",
                whiteSpace: "normal",
                text: "bi.year_combo",
                height: 30
            }, {
                el: this._createYearCombo()
            }]
        })
    }
});

YearComboModel = BI.inherit(BI.Model, {

});