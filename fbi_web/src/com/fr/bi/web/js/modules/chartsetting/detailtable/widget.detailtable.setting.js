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

        this.widgetSetting = BI.createWidget({
            type: "bi.widget_block_setting",
            wId: o.wId
        });

        this.widgetSetting.on(BI.WidgetBlockSetting.EVENT_CHANGE, function() {
            self.fireEvent(BI.DetailTableSetting.EVENT_CHANGE)
        });

        //主题颜色
        this.colorSelector = BI.createWidget({
            type: "bi.color_chooser",
            width: this.constant.BUTTON_HEIGHT,
            height: this.constant.BUTTON_HEIGHT
        });
        this.colorSelector.on(BI.ColorChooser.EVENT_CHANGE, function () {
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
        this.customTableStyle = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Custom_Table_Style"),
            width: 135
        });

        this.customTableStyle.on(BI.Controller.EVENT_CHANGE, function() {
            self.tableStyleSetting.setVisible(this.isSelected());
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE)
        });

        //表格样式设置
        this.tableStyleSetting = BI.createWidget({
            type: "bi.table_detailed_setting_combo"
        });

        this.tableStyleSetting.on(BI.TableDetailedSettingCombo.EVENT_CHANGE, function() {
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE)
        });

        this.tableStyleSetting.setVisible(false);

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
                }, this.colorSelector, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Style"),
                    cls: "attr-names"
                }, this.tableSyleGroup, {
                    type: "bi.vertical_adapt",
                    items: [this.customTableStyle],
                    cls: "attr-names",
                    height: this.constant.SINGLE_LINE_HEIGHT
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.tableStyleSetting],
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
            items: [this.widgetSetting, tableStyle, show, otherAttr],
            hgap: 10
        })
    },

    populate: function () {
        var wId = this.options.wId;
        this.widgetSetting.populate();
        this.colorSelector.setValue(BI.Utils.getWSThemeColorByID(wId));
        this.tableSyleGroup.setValue(BI.Utils.getWSTableStyleByID(wId));
        this.showNumber.setSelected(BI.Utils.getWSShowNumberByID(wId));
        this.freezeFirstColumn.setSelected(BI.Utils.getWSFreezeFirstColumnById(wId));
    },

    getValue: function () {
        return {
            widget_setting: this.widgetSetting.getValue(),
            theme_color: this.colorSelector.getValue(),
            table_style: this.tableSyleGroup.getValue()[0],
            show_number: this.showNumber.isSelected(),
            freeze_first_column: this.freezeFirstColumn.isSelected()
        }
    },

    setValue: function (v) {
        this.widgetSetting.setValue(v.widget_setting);
        this.colorSelector.setValue(v.theme_color);
        this.tableSyleGroup.setValue(v.table_style);
        this.showNumber.setSelected(v.show_number);
        this.freezeFirstColumn.setSelected(v.freeze_first_column);
    }
});
BI.DetailTableSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_table_setting", BI.DetailTableSetting);