/**
 * Created by Young's on 2017/4/12.
 */
BI.AnalysisAllHistoryButton = BI.inherit(BI.BasicButton, {

    props: {
        extraCls: "bi-all-history-tab-button",
        text: "",
        value: 1,
        height: 30,
        gap: 10,
        forceSelected: true,
        buttonWidth: 120
    },

    render: function () {
        var o = this.options;
        var button = BI.createWidget({
            type: "bi.button",
            level: 'ignore',
            text: BI.i18nText("BI-ETL_Merge_History"),
            title: BI.i18nText("BI-ETL_Merge_History"),
            height: o.height,
            width: o.buttonWidth
        });
        var self = this;
        button.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments)
        })
        BI.createWidget({
            type: "bi.vertical",
            scrollable: false,
            element: this,
            height: o.height + o.gap,
            items: [
                {
                    type: "bi.center_adapt",
                    items: [button],
                    height: o.height
                }, {
                    type: "bi.layout",
                    height: o.gap
                }
            ]
        })

    }
});
BI.shortcut("bi.analysis_all_history_button", BI.AnalysisAllHistoryButton);