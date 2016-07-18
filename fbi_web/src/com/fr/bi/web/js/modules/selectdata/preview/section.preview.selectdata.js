/**
 * 字段预览界面Section
 *
 * Created by GUY on 2015/10/13.
 * @class BI.DetailSelectDataPreviewSection
 * @extends BI.BarPopoverSection
 */
BI.DetailSelectDataPreviewSection = BI.inherit(BI.BarPopoverSection, {

    _defaultConfig: function () {
        return BI.extend(BI.DetailSelectDataPreviewSection.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-select-data-preview-section",
            text: "",
            value: ""
        });
    },

    _init: function () {
        BI.DetailSelectDataPreviewSection.superclass._init.apply(this, arguments);
    },

    rebuildNorth : function(north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            textAlign: "left",
            text: BI.i18nText("BI-Preview"),
            height: 50
        });
    },

    rebuildCenter : function(center) {
        var o = this.options;
        BI.createWidget({
            type: "bi.detail_select_data_preview_pane",
            element: center,
            text: o.text,
            value: o.value
        })
    },

    rebuildSouth: function(south){
        var self = this;
        BI.createWidget({
            type: "bi.absolute",
            cls: "bi-detail-select-data-preview-section-south",
            element: south,
            items: [{
                el: {
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Data_Preview_Comment"),
                    height: 60,
                    cls: "preview-comment",
                    textAlign: "left"
                },
                top: 0,
                left: 0,
                bottom: 0,
                right: 0
            }, {
                el: {
                    type: "bi.button",
                    text: BI.i18nText("BI-Sure"),
                    height: 28,
                    handler: function(){
                        self.close();
                    }
                },
                top: 15,
                right: 20
            }]
        })
    },

    end: function(){

    },

    setValue: function (v) {

    },

    getValue: function () {

    }
});
BI.DetailSelectDataPreviewSection.EVENT_CHANGE = "DetailSelectDataPreviewSection.EVENT_CHANGE";
$.shortcut('bi.detail_select_data_preview_section', BI.DetailSelectDataPreviewSection);