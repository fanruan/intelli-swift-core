/**
 * Created by Young's on 2016/6/1.
 */
BI.PlateHangoutReport = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend(BI.PlateHangoutReport.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-plate-hangout-report"
        })
    },

    _init: function(){
        BI.PlateHangoutReport.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function(north){
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText(""),
            height: 50,
            textAlign: "left",
            hgap: 10
        });
    },

    rebuildCenter: function(center){
        var reportsCombo = BI.createWidget({
            type: "bi.multilayer_single_tree_combo",
            items: [],
            width: 200,
            height: 30
        });
        var reportName = BI.createWidget({
            type: "bi.sign_editor",
            height: 30,
            width: 220
        });
        var description = BI.createWidget({
            type: "bi.textarea_editor",
            width: 220,
            height: 100
        });
        BI.createWidget({
            type: "bi.vertical",
            element: center,
            items: [{
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Path"),
                    height: 30,
                    width: 90,
                    textAlign: "left",
                    cls: ""
                }, reportsCombo]
            }, {
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Name_Title"),
                    height: 30,
                    width: 90,
                    textAlign: "left",
                    cls: ""
                }, reportName]
            }, {
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Describe"),
                    height: 30,
                    width: 90,
                    textAlign: "left",
                    cls: ""
                }, description]
            }],
            hgap: 10,
            vgap: 5
        })
    }
});
$.shortcut("bi.plate_hangout_report", BI.PlateHangoutReport);