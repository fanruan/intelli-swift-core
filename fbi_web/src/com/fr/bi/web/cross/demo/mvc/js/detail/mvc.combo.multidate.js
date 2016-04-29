MultiDateComboView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(MultiDateComboView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-date-combo bi-mvc-layout"
        })
    },

    _init: function(){
        MultiDateComboView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 10,
            vgap: 20,
            items: [{
                type: "bi.label",
                whiteSpace: "normal",
                text: "年份面板",
                height: 30,
                width:270
            }, {
                el: {
                    type: "bi.yearcard"
                }
            }, {
                type: "bi.label",
                whiteSpace: "normal",
                text: "季度面板",
                height: 30,
                width:270
            }, {
                el: {
                    type: "bi.quartercard"
                }
            },{
                type: "bi.label",
                whiteSpace: "normal",
                text: "月份面板",
                height: 30,
                width:270
            }, {
                el: {
                    type: "bi.monthcard"
                }
            }, {
                type: "bi.label",
                whiteSpace: "normal",
                text: "周面板",
                height: 30,
                width:270
            }, {
                el: {
                    type: "bi.weekcard"
                }
            }, {
                type: "bi.label",
                whiteSpace: "normal",
                text: "天面板",
                height: 30,
                width:270
            }, {
                el: {
                    type: "bi.daycard"
                }
            },{
                type: "bi.label",
                whiteSpace: "normal",
                text: "日期trigger",
                height: 30,
                width:270
            }, {
                el: {
                    type: "bi.date_trigger"
                }
            }, {
                type: "bi.label",
                whiteSpace: "normal",
                text: "日期控件",
                height: 30,
                width:270
            }, {
                el: {
                    type: "bi.multidate_combo"
                }
            }]
        })
    }
});

MultiDateComboModel = BI.inherit(BI.Model, {

});