/**
 * 图表值轴配置
 * Created by AstronautOO7 on 2016/10/12.
 */
BI.ValueAxisBlockSetting = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ValueAxisBlockSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-value-axis-block-setting bi-charts-setting",
            headText: BI.i18nText("BI-Left_Value_Axis"),
            reversed: true
        })
    },

    _init: function () {
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
            self.label.setVisible(this.isSelected());
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

        this.reversed.setVisible(o.reversed);

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
                text: o.headText,
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

    getValue: function () {
        return {
            number_level: this.numberLevel.getValue()[0],
            axis_unit: this.unit.getValue(),
            axis_style: this.axisStyle.getValue()[0],
            num_separator: this.separator.isSelected(),
            show_title: this.isShowTitle.isSelected(),
            axis_title: this.editTitle.getValue(),
            show_label: this.showLable.isSelected(),
            label_setting: this.label.getValue(),
            line_color: this.lineColor.getValue(),
            axis_reversed: this.reversed.isSelected(),
            show_custom_scale: this.showCustomScale.isSelected(),
            custom_scale: this.customScale.getValue()
        }
    },

    setValue: function (v) {
        this.editTitle.setVisible(v.show_title);
        this.customScale.setVisible(v.show_custom_scale);
        this.label.setVisible(v.show_label);

        this.numberLevel.setValue(v.number_level);
        this.unit.setValue(v.axis_unit);
        this.axisStyle.setValue(v.axis_style);
        this.separator.setSelected(v.num_separator);
        this.isShowTitle.setSelected(v.show_title);
        this.editTitle.setValue(v.axis_title);
        this.showLable.setSelected(v.show_label);
        this.label.setValue(v.label_setting);
        this.lineColor.setValue(v.line_color);
        this.reversed.setSelected(v.axis_reversed);
        this.showCustomScale.setSelected(v.show_custom_scale);
        this.customScale.setValue(v.custom_scale)
    }

});
BI.ValueAxisBlockSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.value_axis_block_setting", BI.ValueAxisBlockSetting);