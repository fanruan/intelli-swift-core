FormulaView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(FormulaView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-formula bi-mvc-layout"
        })
    },

    _init: function () {
        FormulaView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        this.formula = BI.createWidget({
            type : 'bi.formula',
            cls: "mvc-border",
            width : 300,
            height : 200,
            value : 'SUM(C5, 16, 26)'
        });
        BI.createWidget({
            type: "bi.left",
            element: vessel,
            items: [this.formula],
            hgap: 20,
            vgap: 20
        })
    }
});

FormulaModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(FormulaModel.superclass._defaultConfig.apply(this, arguments), {

        })
    },
    init : function() {
        FormulaModel.superclass._init.apply(this, arguments);
    }
});