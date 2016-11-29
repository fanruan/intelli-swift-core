/**
 * 数据标签详细设置combo
 * Created by AstronautOO7 on 2016/11/24.
 */
BI.DataLabelDetailedSettingCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DataLabelDetailedSettingCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-data-label-detailed-setting",
            width: 50
        })
    },

    _init: function () {
        BI.DataLabelDetailedSettingCombo.superclass._init.apply(this, arguments);

        BI.createWidget({
            type: "bi.combo",
            element: this.element,
            width: this.options.width,
            el: {
                type: "bi.detailed_setting_trigger"
            },
            popup: {
                el: this._getPopup(),
                minWidth: 350,
                stopPropagation: false
            }
        })
    },

    _getPopup: function () {
        var self = this, o = this.options;
        var wType = BI.Utils.getWidgetTypeByID(o.wId);
        //力学气泡没有数据标签的详细设置
        switch (wType) {
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                this.popup = BI.createWidget({
                    type: "bi.axis_data_label_detailed_setting_popup"
                });
                break;
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.ACCUMULATE_RADAR:
            case BICst.WIDGET.RADAR:
                this.popup = BI.createWidget({
                    type: "bi.area_data_label_detailed_setting_popup"
                });
                break;
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.PIE:
                this.popup = BI.createWidget({
                    type: "bi.pie_data_label_detailed_setting_popup"
                });
                break;
            case BICst.WIDGET.DASHBOARD:
                this.popup = BI.createWidget({
                    type: "bi.dashboard_data_label_detailed_setting_popup"
                });
                break;
            case BICst.WIDGET.BUBBLE:
                this.popup = BI.createWidget({
                    type: "bi.bubble_data_label_detailed_setting_popup"
                });
                break;
            case BICst.WIDGET.SCATTER:
                this.popup = BI.createWidget({
                    type: "bi.scatter_data_label_detailed_setting_popup"
                });
                break;
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
                this.popup = BI.createWidget({
                    type: "bi.map_data_label_detailed_setting_popup"
                });
                break;
        }

        this.popup.on(BI.AxisDataLabelDetailedSettingPopup.EVENT_CHANGE, function () {
            self.fireEvent(BI.DataLabelDetailedSettingCombo.EVENT_CHANGE)
        });

        return this.popup;
    },

    setValue: function (v) {
        this.popup.setValue(v)
    },

    getValue: function () {
        return this.popup.getValue()
    }
});
BI.DataLabelDetailedSettingCombo.EVENT_CHANGE = 'EVENT_CHANGE';
$.shortcut("bi.data_label_detailed_setting_combo", BI.DataLabelDetailedSettingCombo);