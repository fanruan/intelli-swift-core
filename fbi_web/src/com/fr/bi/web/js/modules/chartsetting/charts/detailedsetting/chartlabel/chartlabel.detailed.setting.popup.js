/**
 * 图的轴标签
 * Created by AstronautOO7 on 2016/10/11.
 */
BI.ChartLabelDetailedSettingPopup = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.ChartLabelDetailedSettingPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-label-detailed-setting-popup"
        })
    },

    _init: function() {
        BI.ChartLabelDetailedSettingPopup.superclass._init.apply(this, arguments);
        var self = this;

        //文本方向
        this.textDirection = BI.createWidget({
            type: "bi.sign_editor",
            width: 80,
            height: 26,
            cls: "chart-label-setting-input",
            allowBlank: false,
            value: "0",
            errorText: BI.i18nText("BI-Please_Enter_Number_From_To_To", -90, 90),
            validationChecker: function (v) {
                return BI.isInteger(v) && v >= -90 && v <= 90;
            }
        });
        this.textDirection.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        var textDirectionWrapper = this._createWrapper(BI.i18nText("BI-Text_Direction"), this.textDirection);

        //字体设置
        this.textStyle = BI.createWidget({
            type: "bi.data_label_text_toolbar",
            cls: "chart-label-setting-input",
            width: 230
        });
        this.textStyle.on(BI.DataLabelTextToolBar.EVENT_CHANGE, function () {
            self.fireEvent(BI.ChartLabelDetailedSettingPopup.EVENT_CHANGE)
        });
        var textStyleWrapper = this._createWrapper(BI.i18nText("BI-Set_Font"), this.textStyle);

        this.centerItems = BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [
                textDirectionWrapper,
                textStyleWrapper
            ],
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
BI.ChartLabelDetailedSettingPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_label_detailed_setting_popup", BI.ChartLabelDetailedSettingPopup);