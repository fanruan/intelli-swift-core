/**
 * 数据点提示详细设置
 * Created by AstronautOO7 on 2016/10/12.
 */
BI.TooltipDetailedSettingPopup = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.TooltipDetailedSettingPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detailed-setting bi-tooltip-detailed-setting-popup"
        })
    },

    _init: function() {
        BI.TooltipDetailedSettingPopup.superclass._init.apply(this, arguments);
        var self = this;

        //显示所有指标
        this.showTargets = BI.createWidget({
            type: "bi.multi_select_item",
            width: 200,
            value: BI.i18nText("BI-Show_All_Targets_In_Same_Category")
        });

        this.showTargets.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.TooltipDetailedSettingPopup.EVENT_CHANGE)
        });

        //分类名
        this.categoryName = BI.createWidget({
            type: "bi.multi_select_item",
            width: 75,
            value: BI.i18nText("BI-Category_Name")
        });

        this.categoryName.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.TooltipDetailedSettingPopup.EVENT_CHANGE)
        });

        //系列名
        this.seriesName = BI.createWidget({
            type: "bi.multi_select_item",
            width: 75,
            value: BI.i18nText("BI-Series_Name")
        });

        this.seriesName.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.TooltipDetailedSettingPopup.EVENT_CHANGE)
        });

        //值
        this.showValue = BI.createWidget({
            type: "bi.multi_select_item",
            width: 50,
            value: BI.i18nText("BI-Value")
        });

        this.showValue.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.TooltipDetailedSettingPopup.EVENT_CHANGE)
        });

        //百分比
        this.showPercentage = BI.createWidget({
            type: "bi.multi_select_item",
            width: 75,
            value: BI.i18nText("BI-Percentage")
        });

        this.showPercentage.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.TooltipDetailedSettingPopup.EVENT_CHANGE)
        });

        var show = BI.createWidget({
            type: "bi.left",
            items: [this.categoryName, this.seriesName, this.showValue, this.showPercentage]
        });

        var showWrapper = this._createWrapper(BI.i18nText("BI-Show_Content"), show);

        //字体设置
        this.textStyle = BI.createWidget({
            type: "bi.data_label_text_toolbar",
            cls: "detailed-setting-popup",
            width: 230
        });
        this.textStyle.on(BI.DataLabelTextToolBar.EVENT_CHANGE, function () {
            self.fireEvent(BI.TooltipDetailedSettingPopup.EVENT_CHANGE)
        });
        var textStyleWrapper = this._createWrapper(BI.i18nText("BI-Set_Font"), this.textStyle);

        this.centerItems = BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: BI.createItems([
                this.showTargets,
                showWrapper,
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
        }
    },

    setValue: function(v) {
        v || (v = {});
    }

});
BI.TooltipDetailedSettingPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.tooltip_detailed_setting_popup", BI.TooltipDetailedSettingPopup);