/**
 * 仪表盘数据标签的popup
 * Created by AstronautOO7 on 2016/11/28.
 */
BI.DashboardDataLabelDetailedSettingPopup = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.DashboardDataLabelDetailedSettingPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detailed-setting bi-dashboard-data-label-detailed-setting-popup"
        })
    },

    _init: function() {
        BI.DashboardDataLabelDetailedSettingPopup.superclass._init.apply(this, arguments);
        var self = this;

        //分类名
        this.showCategoryName = BI.createWidget({
            type: "bi.multi_select_item",
            width: 75,
            value: BI.i18nText("BI-Category_Name")
        });

        this.showCategoryName.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.AreaDataLabelDetailedSettingPopup.EVENT_CHANGE)
        });

        //值
        this.showValue = BI.createWidget({
            type: "bi.multi_select_item",
            width: 50,
            value: BI.i18nText("BI-Value")
        });

        this.showValue.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.DashboardDataLabelDetailedSettingPopup.EVENT_CHANGE)
        });

        //百分比
        this.showPercentage = BI.createWidget({
            type: "bi.multi_select_item",
            width: 75,
            value: BI.i18nText("BI-Percentage")
        });

        this.showPercentage.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.DashboardDataLabelDetailedSettingPopup.EVENT_CHANGE)
        });

        var show = BI.createWidget({
            type: "bi.left",
            items: [this.showCategoryName, this.showValue, this.showPercentage]
        });

        var showWrapper = this._createWrapper(BI.i18nText("BI-Show_Content"), show);

        //字体设置
        this.textStyle = BI.createWidget({
            type: "bi.data_label_text_toolbar",
            cls: "detailed-setting-popup",
            width: 230
        });
        this.textStyle.on(BI.DataLabelTextToolBar.EVENT_CHANGE, function () {
            self.fireEvent(BI.DashboardDataLabelDetailedSettingPopup.EVENT_CHANGE)
        });
        var textStyleWrapper = this._createWrapper(BI.i18nText("BI-Set_Font"), this.textStyle);

        this.centerItems = BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: BI.createItems([
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
            showCategoryName: this.showCategoryName.isSelected(),
            showValue: this.showValue.isSelected(),
            showPercentage: this.showPercentage.isSelected(),
            textStyle: this.textStyle.getValue(),
        }
    },

    setValue: function(v) {
        v || (v = {});
        this.showCategoryName.setSelected(v.showCategoryName);
        this.showValue.setSelected(v.showValue);
        this.showPercentage.setSelected(v.showPercentage);
        this.textStyle.setValue(v.textStyle)
    }

});
BI.DashboardDataLabelDetailedSettingPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.dashboard_data_label_detailed_setting_popup", BI.DashboardDataLabelDetailedSettingPopup);
