/**
 * Created by roy on 16/5/23.
 * 明细表样式
 */
BI.DetailTableSetting = BI.inherit(BI.Widget, {

    constant: {
        SINGLE_LINE_HEIGHT: 60,
        SIMPLE_H_GAP: 10,
        SIMPLE_L_GAP: 2,
        CHECKBOX_WIDTH: 16,
        EDITOR_WIDTH: 60,
        EDITOR_HEIGHT: 26,
        BUTTON_WIDTH: 40,
        BUTTON_HEIGHT: 30,
        ICON_WIDTH: 24,
        ICON_HEIGHT: 24
    },

    _defaultConfig: function () {
        return BI.extend(BI.DetailTableSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-group-table-setting"
        })
    },

    _init: function () {
        BI.DetailTableSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        //显示组件标题
        this.showName = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Chart_Title"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.showName.on(BI.Controller.EVENT_CHANGE, function () {
            self.widgetTitle.setVisible(this.isSelected());
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //组件标题
        this.widgetName = BI.createWidget({
            type: "bi.sign_editor",
            cls: "title-input",
            width: 120
        });

        this.widgetName.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //详细设置
        this.widgetNameStyle = BI.createWidget({
            type: "bi.show_title_detailed_setting_combo"
        });

        this.widgetNameStyle.on(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.widgetTitle = BI.createWidget({
            type: "bi.left",
            items: [this.widgetName, this.widgetNameStyle],
            hgap: this.constant.SIMPLE_H_GAP
        });

        //组件背景
        this.widgetBG = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBG.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        var widgetTitle = BI.createWidget({
            type: "bi.left",
            cls: "single-line-settings",
            items: BI.createItems([{
                type: "bi.vertical_adapt",
                items: [this.showName]
            }, {
                type: "bi.vertical_adapt",
                items: [this.widgetTitle]
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Widget_Background_Colour"),
                cls: "attr-names",
            }, {
                type: "bi.vertical_adapt",
                items: [this.widgetBG]
            }], {
                height: 58
            }),
            hgap: 10
        });

        //主题颜色
        this.themeColor = BI.createWidget({
            type: "bi.color_chooser",
            width: this.constant.BUTTON_HEIGHT,
            height: this.constant.BUTTON_HEIGHT
        });
        this.themeColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.DetailTableSetting.EVENT_CHANGE);
        });
        //风格——1、2、3
        this.tableSyleGroup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.TABLE_STYLE_GROUP, {
                type: "bi.icon_button",
                extraCls: "table-style-font",
                width: this.constant.BUTTON_WIDTH,
                height: this.constant.BUTTON_HEIGHT,
                iconWidth: this.constant.ICON_WIDTH,
                iconHeight: this.constant.ICON_HEIGHT
            }),
            layouts: [{
                type: "bi.left"
            }]
        });
        this.tableSyleGroup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.DetailTableSetting.EVENT_CHANGE);
        });

        //自定义表格样式
        this.isCustomTableStyle = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Custom_Table_Style"),
            width: 135
        });

        this.isCustomTableStyle.on(BI.Controller.EVENT_CHANGE, function() {
            self.customTableStyle.setVisible(this.isSelected());
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE)
        });

        //表格样式设置
        this.customTableStyle = BI.createWidget({
            type: "bi.table_detailed_setting_combo"
        });

        this.customTableStyle.on(BI.TableDetailedSettingCombo.EVENT_CHANGE, function() {
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE)
        });

        var tableStyle = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Sheet_Style"),
                    cls: "line-title"
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Theme_Color"),
                    cls: "attr-names"
                }, this.themeColor, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Style"),
                    cls: "attr-names"
                }, this.tableSyleGroup, {
                    type: "bi.vertical_adapt",
                    items: [this.isCustomTableStyle],
                    cls: "attr-names",
                    height: this.constant.SINGLE_LINE_HEIGHT
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.customTableStyle],
                    height: this.constant.SINGLE_LINE_HEIGHT
                }]
            },
            height: this.constant.SINGLE_LINE_HEIGHT,
            lhgap: this.constant.SIMPLE_H_GAP
        });

        //显示序号
        this.showNumber = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Display_Sequence_Number"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.showNumber.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.DetailTableSetting.EVENT_CHANGE);
        });

        //表格行高
        this.rowHeight = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "max-row-input",
            errorText: BI.i18nText("BI-Please_Enter_Number_1_To_100"),
            allowBlank: false,
            validationChecker: function (v) {
                return BI.isInteger(v) && v > 0 && v <= 100;
            }
        });
        this.rowHeight.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
        });

        var show = BI.createWidget({
            type: "bi.left",
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Element_Show"),
                cls: "line-title",
                height: this.constant.SINGLE_LINE_HEIGHT
            }, {
                type: "bi.vertical_adapt",
                items: [this.showNumber],
                height: this.constant.SINGLE_LINE_HEIGHT
            }, {
                type: "bi.vertical_adapt",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Row_Height"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rowHeight],
                    width: this.constant.EDITOR_WIDTH
                }],
                height: this.constant.SINGLE_LINE_HEIGHT,
                lgap: 5
            }],
            hgap: this.constant.SIMPLE_H_GAP
        });

        //冻结维度
        this.freezeFirstColumn = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Freeze_FIRST_COLUMN"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.freezeFirstColumn.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.DetailTableSetting.EVENT_CHANGE);
        });

        var otherAttr = BI.createWidget({
            type: "bi.left",
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Interactive_Attr"),
                cls: "line-title",
                height: this.constant.SINGLE_LINE_HEIGHT
            }, {
                type: "bi.vertical_adapt",
                items: [this.freezeFirstColumn],
                height: this.constant.SINGLE_LINE_HEIGHT
            }],
            hgap: this.constant.SIMPLE_H_GAP
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [widgetTitle, tableStyle, show, otherAttr],
            hgap: 10
        })
    },

    populate: function () {
        var wId = this.options.wId;
        this.showName.setSelected(BI.Utils.getWSShowNameByID(wId));
        this.widgetName.setValue(BI.Utils.getWidgetNameByID(wId));
        this.widgetNameStyle.setValue(BI.Utils.getWSTitleDetailSettingByID(wId));
        this.widgetTitle.setVisible(BI.Utils.getWSShowNameByID(wId));
        this.widgetBG.setValue(BI.Utils.getWSWidgetBGByID(wId));

        this.themeColor.setValue(BI.Utils.getWSThemeColorByID(wId));
        this.tableSyleGroup.setValue(BI.Utils.getWSTableStyleByID(wId));
        this.isCustomTableStyle.setSelected(BI.Utils.getWSIsCustomTableStyleByID(wId));
        this.customTableStyle.setValue(BI.Utils.getWSCustomTableStyleByID(wId));
        this.customTableStyle.setVisible(BI.Utils.getWSIsCustomTableStyleByID(wId));

        this.showNumber.setSelected(BI.Utils.getWSShowNumberByID(wId));
        this.rowHeight.setValue(BI.Utils.getWSRowHeightByID(wId));

        this.freezeFirstColumn.setSelected(BI.Utils.getWSFreezeFirstColumnById(wId));
    },

    getValue: function () {
        return {
            showName: this.showName.isSelected(),
            widgetName: this.widgetName.getValue(),
            widgetNameStyle: this.widgetNameStyle.getValue(),
            widgetBG: this.widgetBG.getValue(),

            themeColor: this.themeColor.getValue(),
            tableStyleGroup: this.tableSyleGroup.getValue()[0],
            isCustomTableStyle: this.isCustomTableStyle.isSelected(),
            customTableStyle: this.customTableStyle.getValue(),

            showNumber: this.showNumber.isSelected(),
            rowHeight: BI.parseFloat(this.rowHeight.getValue()),

            freezeFirstColumn: this.freezeFirstColumn.isSelected()
        }
    },
});
BI.DetailTableSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_table_setting", BI.DetailTableSetting);