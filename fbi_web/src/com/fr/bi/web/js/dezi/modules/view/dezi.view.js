/**
 * Created by GUY on 2015/6/24.
 */
BIDezi.View = BI.inherit(BI.View, {
    
    _defaultConfig: function () {
        return BI.extend(BIDezi.PaneView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dezi-view"
        })
    },
    
    _init: function () {
        BIDezi.View.superclass._init.apply(this, arguments);
        this.populate();
    },

    _render: function (vessel) {
        var self = this;
        vessel.css("z-index", 0);
        var subvessel = BI.createWidget();
        var saveAs = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "toolbar-save-font save-as",
            text: BI.i18nText("BI-Save_As"),
            height: 30,
            width: 70
        });
        saveAs.on(BI.IconTextItem.EVENT_CHANGE, function(){
            self._saveAs();
        });
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: subvessel,
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }, {
                el: saveAs,
                left: 20,
                top: 5
            }]
        });
        this.addSubVessel("pane", subvessel);
    },

    _saveAs: function(){
        var self = this;
        var id = BI.UUID();
        var saveAs = BI.createWidget({
            type: "bi.report_save_as_float_box",
            name: this.model.get("reportName")
        });
        saveAs.on(BI.ReportSaveAsFloatBox.EVENT_CHANGE, function (data) {
            BI.requestAsync("fr_bi", "add_report", {
                reportName: data.report_name,
                reportLocation: data.report_location,
                popConfig: self.model.get("popConfig")
            }, function(res, model){
                if (BI.isNotNull(res) && BI.isNotNull(res.reportId)) {
                    BI.Msg.toast("另存成功");
                }
            });
        });
        BI.Popovers.create(id, saveAs, {width: 400, height: 320}).open(id);
        saveAs.setTemplateNameFocus();
    },

    refresh: function () {
        this.skipTo("pane", "pane", "popConfig");
    }
});