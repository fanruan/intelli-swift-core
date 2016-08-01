/**
 * @class BI.GISMapSetting
 * @extends BI.Widget
 * 柱状，堆积柱状，组合图样式
 */
BI.GISMapSetting = BI.inherit(BI.Widget, {
    constant: {
        SIMPLE_H_LARGE_GAP: 10,
        SINGLE_LINE_HEIGHT: BICst.CHART.SINGLE_LINE_HIGHT,
        SIMPLE_H_GAP: BICst.CHART.SIMPLE_H_GAP,
        SIMPLE_L_GAP: BICst.CHART.SIMPLE_L_GAP,
        SIMPLE_H_LGAP: BICst.CHART.SIMPLE_H_LGAP,
        CHECKBOX_WIDTH: BICst.CHART.CHECKBOX_WIDTH,
        EDITOR_WIDTH: BICst.CHART.EDITOR_WIDTH,
        EDITOR_HEIGHT: BICst.CHART.EDITOR_HEIGHT,
        BUTTON_WIDTH: BICst.CHART.BUTTON_WIDTH,
        BUTTON_HEIGHT: BICst.CHART.BUTTON_HEIGHT,
        ICON_WIDTH: BICst.CHART.ICON_WIDTH,
        ICON_HEIGHT: BICst.CHART.ICON_HEIGHT,
        NUMBER_LEVEL_SEGMENT_WIDTH: BICst.CHART.NUMBER_LEVEL_SEGMENT_WIDTH,
        FORMAT_SEGMENT_WIDTH: BICst.CHART.FORMAT_SEGMENT_WIDTH,
        LEGEND_SEGMENT_WIDTH: BICst.CHART.LEGEND_SEGMENT_WIDTH
    },

    _defaultConfig: function(){
        return BI.extend(BI.GISMapSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting"
        })
    },

    _init: function(){
        BI.GISMapSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.GISMapSetting.EVENT_CHANGE);
        });

        var showElement = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Element_Show"),
                lgap: this.constant.SIMPLE_H_LGAP,
                textAlign: "left",
                textHeight: this.constant.SINGLE_LINE_HEIGHT,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.center_adapt",
                    items: [this.showDataLabel]
                }], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                }),
                lgap: this.constant.SIMPLE_L_GAP
            }]
        });

        //联动传递指标过滤条件
        this.transferFilter = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Bind_Target_Condition"),
            width: 170
        });
        this.transferFilter.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
        });

        var otherAttr = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Interactive_Attr"),
                    cls: "line-title"
                }, this.transferFilter]
            },
            height: this.constant.SINGLE_LINE_HEIGHT,
            lhgap: this.constant.SIMPLE_H_GAP
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [showElement, otherAttr],
            hgap: 10
        })
    },

    populate: function(){
        var wId = this.options.wId;
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
    },

    getValue: function(){
        return {
            transfer_filter: this.transferFilter.isSelected(),
            show_data_label: this.showDataLabel.isSelected()
        }
    },

    setValue: function(v){
        this.transferFilter.setSelected(v.transfer_filter);
        this.showDataLabel.setSelected(v.show_data_label);
    }
});
BI.GISMapSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.gis_map_setting", BI.GISMapSetting);