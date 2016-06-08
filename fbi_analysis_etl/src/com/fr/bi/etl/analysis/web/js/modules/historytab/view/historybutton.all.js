BI.AllHistoryButton = FR.extend(BI.BasicButton, {

    _defaultConfig: function () {
        return BI.extend(BI.AllHistoryButton.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-all-history-tab-button",
            text: "",
            value: 1,
            height: 30,
            gap:10,
            buttonWidth:120
        })
    },

    _init: function () {
        BI.AllHistoryButton.superclass._init.apply(this, arguments);
        var o = this.options;
        var button =  BI.createWidget({
            type:"bi.button",
            level: 'ignore',
            text: BI.i18nText("BI-ETL_Merge_History"),
            title: BI.i18nText("BI-ETL_Merge_History"),
            height: o.height,
            width: o.buttonWidth
        });
        var self = this;
        button.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments)
        })
        BI.createWidget({
            type:"bi.vertical",
            scrollable:false,
            element:this.element,
            height: o.height + o.gap,
            items : [
                {
                    type:"bi.center_adapt",
                    items : [button],
                    height : o.height,
                },{
                    type:"bi.layout",
                    height:o.gap
                }
            ]
        })

    }
})
$.shortcut("bi.all_history_button",BI.AllHistoryButton)