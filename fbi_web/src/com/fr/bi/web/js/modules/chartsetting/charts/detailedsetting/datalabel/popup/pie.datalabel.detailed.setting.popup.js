/**
 * 饼图和圆环图数据标签的popup
 * Created by AstronautOO7 on 2016/11/28.
 */
BI.PieDataLabelDetailedSettingPopup = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.PieDataLabelDetailedSettingPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detailed-setting bi-pie-data-label-detailed-setting-popup"
        })
    },

    _init: function() {
        BI.PieDataLabelDetailedSettingPopup.superclass._init.apply(this, arguments);
        var self = this;

        //分类名
        this.showCategoryName = BI.createWidget({
            type: "bi.multi_select_item",
            width: 75,
            value: BI.i18nText("BI-Category_Name")
        });

        this.showCategoryName.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.PieDataLabelDetailedSettingPopup.EVENT_CHANGE)
        });

        //系列名
        this.showSeriesName = BI.createWidget({
            type: "bi.multi_select_item",
            width: 75,
            value: BI.i18nText("BI-Series_Name")
        });

        this.showSeriesName.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.PieDataLabelDetailedSettingPopup.EVENT_CHANGE)
        });

        //值
        this.showValue = BI.createWidget({
            type: "bi.multi_select_item",
            width: 50,
            value: BI.i18nText("BI-Value")
        });

        this.showValue.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.PieDataLabelDetailedSettingPopup.EVENT_CHANGE)
        });

        //百分比
        this.showPercentage = BI.createWidget({
            type: "bi.multi_select_item",
            width: 75,
            value: BI.i18nText("BI-Percentage")
        });

        this.showPercentage.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.PieDataLabelDetailedSettingPopup.EVENT_CHANGE)
        });

        var show = BI.createWidget({
            type: "bi.left",
            items: [this.showCategoryName, this.showSeriesName, this.showValue, this.showPercentage]
        });

        var showWrapper = this._createWrapper(BI.i18nText("BI-Show_Content"), show);

        //显示位置
        this.position = BI.createWidget({
            type: 'bi.button_group',
            items: BI.createItems(BICst.PIE_DATA_LABEL_POSITION, {
                type: 'bi.icon_button',
                extraCls: 'data-label-position-icon',
                width: 30,
                height: 30,
            }),
            layouts: [{
                type: 'bi.left',
                lgap: 5
            }]
        });

        this.position.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.showTractionLine.setVisible(this.getValue()[0] === BICst.DATA_LABEL.POSITION_OUTER);
            self.fireEvent(BI.PieDataLabelDetailedSettingPopup.EVENT_CHANGE)
        });

        this.showTractionLine = BI.createWidget({
            type: "bi.multi_select_item",
            width: 100,
            value: BI.i18nText("BI-Show_Traction_Line")
        });

        this.showTractionLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.PieDataLabelDetailedSettingPopup.EVENT_CHANGE)
        });

        var positionAndLine = BI.createWidget({
            type: 'bi.left',
            items: [this.position, this.showTractionLine]
        });

        var positionWrapper = this._createWrapper(BI.i18nText("BI-Show_Position"), positionAndLine);

        //字体设置
        this.textStyle = BI.createWidget({
            type: "bi.data_label_text_toolbar",
            cls: "detailed-setting-popup",
            width: 230
        });
        this.textStyle.on(BI.DataLabelTextToolBar.EVENT_CHANGE, function () {
            self.fireEvent(BI.PieDataLabelDetailedSettingPopup.EVENT_CHANGE)
        });
        var textStyleWrapper = this._createWrapper(BI.i18nText("BI-Set_Font"), this.textStyle);

        this.centerItems = BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: BI.createItems([
                showWrapper,
                positionWrapper,
                textStyleWrapper
            ]),
            hgap: 5
        });
    },

    _createWrapper: function (name, widget) {
        return {
            type: "bi.left",
            items: [{
                type: "bi.label",
                text: name + ":",
                textAlign: "left",
                height: 30,
                width: 60
            }, widget],
            vgap: 5
        }
    },

    getValue: function() {
        return {
            showCategoryName: this.showCategoryName.isSelected(),
            showSeriesName: this.showSeriesName.isSelected(),
            showValue: this.showValue.isSelected(),
            showPercentage: this.showPercentage.isSelected(),
            position: this.position.getValue()[0],
            showTractionLine: this.showTractionLine.isSelected(),
            textStyle: this.textStyle.getValue(),
        }
    },

    setValue: function(v) {
        v || (v = {});
        this.showCategoryName.setSelected(v.showCategoryName);
        this.showSeriesName.setSelected(v.showSeriesName);
        this.showValue.setSelected(v.showValue);
        this.showPercentage.setSelected(v.showPercentage);
        this.position.setValue(v.position ||  BICst.DATA_LABEL.POSITION_OUTER);
        this.showTractionLine.setSelected(v.showTractionLine);
        this.showTractionLine.setVisible(v.position === BICst.DATA_LABEL.POSITION_OUTER);
        this.textStyle.setValue(v.textStyle);
    }

});
BI.PieDataLabelDetailedSettingPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.pie_data_label_detailed_setting_popup", BI.PieDataLabelDetailedSettingPopup);