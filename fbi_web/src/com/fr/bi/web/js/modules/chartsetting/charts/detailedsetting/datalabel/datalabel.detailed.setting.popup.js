/**
 * 数据标签详细设置
 * Created by AstronautOO7 on 2016/11/24.
 */
BI.DataLabelDetailedSettingPopup = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.DataLabelDetailedSettingPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detailed-setting bi-data-label-detailed-setting-popup"
        })
    },

    _init: function() {
        BI.DataLabelDetailedSettingPopup.superclass._init.apply(this, arguments);
        var self = this;

        //分类名
        this.categoryName = BI.createWidget({
            type: "bi.multi_select_item",
            width: 75,
            value: BI.i18nText("BI-Category_Name")
        });

        this.categoryName.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.DataLabelDetailedSettingPopup.EVENT_CHANGE)
        });

        //系列名
        this.seriesName = BI.createWidget({
            type: "bi.multi_select_item",
            width: 75,
            value: BI.i18nText("BI-Series_Name")
        });

        this.seriesName.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.DataLabelDetailedSettingPopup.EVENT_CHANGE)
        });

        //值
        this.showValue = BI.createWidget({
            type: "bi.multi_select_item",
            width: 50,
            value: BI.i18nText("BI-Value")
        });

        this.showValue.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.DataLabelDetailedSettingPopup.EVENT_CHANGE)
        });

        //百分比
        this.showPercentage = BI.createWidget({
            type: "bi.multi_select_item",
            width: 75,
            value: BI.i18nText("BI-Percentage")
        });

        this.showPercentage.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.DataLabelDetailedSettingPopup.EVENT_CHANGE)
        });

        var show = BI.createWidget({
            type: "bi.left",
            items: [this.categoryName, this.seriesName, this.showValue, this.showPercentage]
        });

        var showWrapper = this._createWrapper(BI.i18nText("BI-Show_Content"), show);

        //显示位置
        this.position = BI.createWidget({
            type: 'bi.button_group',
            items: BI.createItems(BICst.DATA_LABEL_POSITION, {
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
            self.fireEvent(BI.DataLabelDetailedSettingPopup.EVENT_CHANGE)
        });

        var positionWrapper = this._createWrapper(BI.i18nText("BI-Show_Position"), this.position);

        //字体设置
        this.textStyle = BI.createWidget({
            type: "bi.data_label_text_toolbar",
            cls: "detailed-setting-popup",
            width: 230
        });
        this.textStyle.on(BI.DataLabelTextToolBar.EVENT_CHANGE, function () {
            self.fireEvent(BI.DataLabelDetailedSettingPopup.EVENT_CHANGE)
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
            showCategoryName: this.categoryName.isSelected(),
            showSeriesName: this.seriesName.isSelected(),
            showValue: this.showValue.isSelected(),
            showPercentage: this.showPercentage.isSelected(),
            position: this.position.getValue()[0],
            textStyle: this.textStyle.getValue(),
        }
    },

    setValue: function(v) {
        v || (v = {});
        this.categoryName.setSelected(v.showCategoryName);
        this.seriesName.setSelected(v.showSeriesName);
        this.showValue.setSelected(v.showValue);
        this.showPercentage.setSelected(v.showPercentage);
        this.position.setValue(v.position);
        this.textStyle.setValue(v.textStyle)
    }

});
BI.DataLabelDetailedSettingPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.data_label_detailed_setting_popup", BI.DataLabelDetailedSettingPopup);