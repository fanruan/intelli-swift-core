/**
 * 图表值轴配置
 * Created by AstronautOO7 on 2016/10/12.
 */
BI.ValueAxisBlockSetting = BI.inherit(BI.Widget, {
    _defaultConfig: function() {
        return BI.extend(BI.ValueAxisBlockSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-value-axis-block-setting bi-charts-setting"
        })
    },

    _init: function() {
        BI.ValueAxisBlockSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        //数量级
        this.numberLevel = BI.createWidget({
            type: "bi.segment",
            width: 300,
            height: 26,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.numberLevel.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.ValueAxisBlockSetting.EVENT_CHANGE);
        });

        //单位
        this.unit = BI.createWidget({
            type: "bi.sign_editor",
            width: 80,
            height: 26,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.unit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.ValueAxisBlockSetting.EVENT_CHANGE);
        });

        //格式
        this.axisStyle = BI.createWidget({
            type: "bi.segment",
            width: 240,
            height: 30,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.axisStyle.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.ValueAxisBlockSetting.EVENT_CHANGE);
        });

        //千分符
        this.separator = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.separator.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.ValueAxisBlockSetting.EVENT_CHANGE);
        });

        //显示标题
        this.isShowTitle = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitle.on(BI.Controller.EVENT_CHANGE, function () {
            this.isSelected() ? self.editTitle.setVisible(true) : self.editTitle.setVisible(false);
            self.fireEvent(BI.ValueAxisBlockSetting.EVENT_CHANGE);
        });

        this.editTitle = BI.createWidget({
            type: "bi.sign_editor",
            width: 80,
            height: 26,
            cls: "unit-input"
        });
        this.editTitle.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.ValueAxisBlockSetting.EVENT_CHANGE);
        });

        //值轴标签
        this.showLable = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.showLable.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.ValueAxisBlockSetting.EVENT_CHANGE)
        });

        this.label = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.label.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.ValueAxisBlockSetting.EVENT_CHANGE)
        });

        //值轴线颜色
        this.lineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.lineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.ValueAxisBlockSetting.EVENT_CHANGE)
        });

        //轴逆序
        this.reversed = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Reversed_Axis"),
            width: 80
        });

        this.reversed.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.ValueAxisBlockSetting.EVENT_CHANGE);
        });

        //轴刻度自定义
        this.showCustomScale = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Scale_Customize"),
            width: 115
        });

        this.showCustomScale.on(BI.Controller.EVENT_CHANGE, function () {
            self.customScale.setVisible(this.isSelected());
            if (!this.isSelected()) {
                self.customScale.setValue({})
            }
            self.fireEvent(BI.ValueAxisBlockSetting.EVENT_CHANGE)
        });

        this.customScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.customScale.on(BI.CustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.ValueAxisBlockSetting.EVENT_CHANGE)
        });

        BI.createWidget({
            type: "bi.horizontal_adapt",
            element: this.element,
            columnSize: [80],
            cls: "single-line-settings",
            verticalAlign: "top",
            items: [{
                type: "bi.label",
                textHeight: 58,
                text: BI.i18nText("BI-Left_Value_Axis"),
                textAlign: "left",
                lgap: 5,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Num_Level"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.numberLevel]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.unit]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Format"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.axisStyle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.separator]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.isShowTitle, this.editTitle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showLable]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.label]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-name"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.lineColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.reversed]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showCustomScale]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.customScale]
                }], {
                    height: 58
                }),
                lgap: 10
            }]
        })
    },

    populate: function() {
        var wId = this.options.wId;

    },

    getValue: function() {
        return {

        }
    },

    setValue: function(v) {

    }

});
BI.ValueAxisBlockSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.value_axis_block_setting", BI.ValueAxisBlockSetting);