/**
 * @class BI.GISMapSetting
 * @extends BI.Widget
 * gis地图样式
 */
BI.GISMapSetting = BI.inherit(BI.AbstractChartSetting, {

    _defaultConfig: function(){
        return BI.extend(BI.GISMapSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting"
        })
    },

    _init: function(){
        BI.GISMapSetting.superclass._init.apply(this, arguments);
        var self = this, constant = BI.AbstractChartSetting;

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.GISMapSetting.EVENT_CHANGE);
        });

        //显示背景图层
        this.isShowBackgroundLayer = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-SHOW_BACKGROUND_LAYER"),
            width: 110
        });

        this.isShowBackgroundLayer.on(BI.Controller.EVENT_CHANGE, function () {
            this.isSelected() ? self.selectLayerCombo.setVisible(true) : self.selectLayerCombo.setVisible(false);
            self.fireEvent(BI.MapSetting.EVENT_CHANGE);
        });

        this.selectLayerCombo = BI.createWidget({
            type: "bi.text_value_combo",
            width: constant.COMBO_WIDTH,
            height: constant.EDITOR_HEIGHT
        });
        this.selectLayerCombo.on(BI.TextValueCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MapSetting.EVENT_CHANGE);
        });

        var showElement = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Element_Show"),
                lgap: constant.SIMPLE_H_LGAP,
                textAlign: "left",
                textHeight: constant.SINGLE_LINE_HEIGHT,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.vertical_adapt",
                    items: [this.showDataLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.isShowBackgroundLayer]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.selectLayerCombo]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_L_GAP
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
            height: constant.SINGLE_LINE_HEIGHT,
            lhgap: constant.SIMPLE_H_GAP
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
        this.isShowBackgroundLayer.setSelected(BI.Utils.getWSShowBackgroundByID(wId));
        this.isShowBackgroundLayer.isSelected() ? this.selectLayerCombo.setVisible(true) : this.selectLayerCombo.setVisible(false);
        var items = BI.map(MapConst.WMS_INFO, function (name, obj) {
            if (obj.type === BICst.TILELAYER_SERVER) {
                return {
                    text: name,
                    title: name,
                    value: name
                }
            }
            if (obj.type === BICst.WMS_SERVER) {
                return {
                    text: name,
                    title: name,
                    value: name
                }
            }
        });
        this.selectLayerCombo.populate(items);
        this.selectLayerCombo.setValue(BI.Utils.getWSBackgroundLayerInfoByID(wId));
    },

    getValue: function(){
        return {
            transfer_filter: this.transferFilter.isSelected(),
            show_data_label: this.showDataLabel.isSelected(),
            show_background_layer: this.isShowBackgroundLayer.isSelected(),
            background_layer_info: this.selectLayerCombo.getValue()[0]
        }
    },

    setValue: function(v){
        this.transferFilter.setSelected(v.transfer_filter);
        this.showDataLabel.setSelected(v.show_data_label);
        this.isShowBackgroundLayer.setSelected(v.show_background_layer);
        this.selectLayerCombo.setValue(v.background_layer_info);
    }
});
BI.GISMapSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.gis_map_setting", BI.GISMapSetting);