/**
 * 表格样式详细设置
 * Created by AstronautOO7 on 2016/10/10.
 */
BI.TableDetailedSettingPopup = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.TableDetailedSettingPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detailed-setting bi-table-detailed-setting-popup"
        })
    },

    _init: function() {
        BI.TableDetailedSettingPopup.superclass._init.apply(this, arguments);
        var self = this;

        //字段名
        this.tableName = BI.createWidget({
            type: "bi.data_label_text_toolbar",
            cls: "detailed-setting-popup",
            width: 230
        });
        this.tableName.on(BI.DataLabelTextToolBar.EVENT_CHANGE, function () {
            self.fireEvent(BI.TableDetailedSettingPopup.EVENT_CHANGE)
        });
        var tableNameWrapper = this._createWrapper(BI.i18nText("BI-Field_Name"), this.tableName);

        //字段名颜色
        this.nameColor = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.nameColor.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.TableDetailedSettingPopup.EVENT_CHANGE);
        });

        var nameColorWrapper = this._createComboWrapper(this.nameColor);

        //字段值
        this.tableValue = BI.createWidget({
            type: "bi.data_label_text_toolbar",
            cls: "detailed-setting-popup",
            width: 230
        });
        this.tableValue.on(BI.DataLabelTextToolBar.EVENT_CHANGE, function () {
            self.fireEvent(BI.TableDetailedSettingPopup.EVENT_CHANGE)
        });
        var tableValueWrapper = this._createWrapper(BI.i18nText("BI-Field_Value"), this.tableValue);

        //背景色间隔
        this.bgColorInterval = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-BGColor_Interval"),
            width: 120
        });

        this.bgColorInterval.on(BI.Controller.EVENT_CHANGE, function() {
            self.intervalValueColor.setVisible(this.isSelected());
            self.fireEvent(BI.TableDetailedSettingPopup.EVENT_CHANGE)
        });

        var intervalWrapper = this._createComboWrapper(this.bgColorInterval);

        //字段值颜色
        this.valueColor = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.valueColor.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.TableDetailedSettingPopup.EVENT_CHANGE);
        });

        var valueColorWrapper = this._createComboWrapper(this.valueColor);

        //字段值间隔色
        this.intervalValueColor = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.intervalValueColor.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.TableDetailedSettingPopup.EVENT_CHANGE);
        });
        var intervalValueColorWrapper = this._createComboWrapper(this.intervalValueColor);

        //表格线
        this.tableLine = BI.createWidget({
            type: "bi.color_chooser",
            width: 28,
            height: 28
        });

        this.tableLine.on(BI.ColorChooser.EVENT_CHANGE, function() {
             self.fireEvent(BI.TableDetailedSettingPopup.EVENT_CHANGE)
        });

        var tableLineWrapper = this._createWrapper(BI.i18nText("BI-Table_Line"), this.tableLine);

        this.centerItems = BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [
                tableNameWrapper,
                nameColorWrapper,
                tableValueWrapper,
                intervalWrapper,
                valueColorWrapper,
                intervalValueColorWrapper,
                tableLineWrapper
            ],
            hgap: 5
        });

        this.intervalValueColor.setVisible(this.bgColorInterval.isSelected())
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

    _createComboWrapper: function (widget) {
        return {
            type: "bi.left",
            items: [widget],
            left: 55,
            vgap: 5
        }
    },

    getValue: function() {
        return {
            tableNameStyle: this.tableName.getValue(),
            tableNameColor: this.nameColor.getValue(),
            tableValueStyle: this.tableValue.getValue(),
            bGColorInterval: this.bgColorInterval.isSelected(),
            tableValueColor: this.valueColor.getValue(),
            tableValueIntervalColor: this.intervalValueColor.getValue(),
            tableLine: this.tableLine.getValue()
        }
    },

    setValue: function(v) {
        v || (v = {});
        this.tableName.setValue(v.tableNameStyle);
        this.nameColor.setValue(v.tableNameColor);
        this.tableValue.setValue(v.tableValueStyle);
        this.bgColorInterval.setSelected(v.bGColorInterval);
        this.valueColor.setValue(v.tableValueColor);
        this.intervalValueColor.setValue(v.tableValueIntervalColor);
        this.tableLine.setValue(v.tableLine)
    }

});
BI.TableDetailedSettingPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.table_detailed_setting_popup", BI.TableDetailedSettingPopup);