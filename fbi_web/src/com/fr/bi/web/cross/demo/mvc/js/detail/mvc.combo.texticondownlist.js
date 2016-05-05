TextIconDownListComboView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(TextIconDownListComboView.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function(){
        TextIconDownListComboView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var combo = BI.createWidget({
            type: "bi.text_value_down_list_combo",
            width: 200,
            height: 30,
            items: BICst.FILTER_DATE_COMBO
        });
        combo.setValue(BICst.FILTER_DATE.BELONG_DATE_RANGE);
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 20,
            vgap: 30,
            items: [combo]
        })
    }
});

TextIconDownListComboModel = BI.inherit(BI.Model, {

});