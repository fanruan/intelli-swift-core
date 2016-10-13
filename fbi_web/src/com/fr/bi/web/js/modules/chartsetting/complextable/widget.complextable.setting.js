/**
 * Created by Young's on 2016/9/20.
 */
BI.ComplexTableSetting = BI.inherit(BI.Widget, {

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
        return BI.extend(BI.ComplexTableSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cross-table-setting"
        })
    },

    _init: function () {
        BI.ComplexTableSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.widgetSetting = BI.createWidget({
            type: "bi.widget_block_setting",
            wId: o.wId
        });

        this.widgetSetting.on(BI.WidgetBlockSetting.EVENT_CHANGE, function() {
            self.fireEvent(BI.ComplexTableSetting.EVENT_CHANGE)
        });

        //类型——横向、纵向展开
        this.tableFormGroup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.TABLE_FORM_GROUP, {
                type: "bi.icon_button",
                extraCls: "table-form-font table-style-font",
                width: this.constant.BUTTON_WIDTH,
                height: this.constant.BUTTON_HEIGHT,
                iconWidth: this.constant.ICON_WIDTH,
                iconHeight: this.constant.ICON_HEIGHT
            }),
            layouts: [{
                type: "bi.vertical_adapt",
                height: this.constant.SINGLE_LINE_HEIGHT
            }]
        });
        this.tableFormGroup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.ComplexTableSetting.EVENT_CHANGE);
        });
        //主题颜色
        this.colorSelector = BI.createWidget({
            type: "bi.color_chooser",
            width: this.constant.BUTTON_HEIGHT,
            height: this.constant.BUTTON_HEIGHT
        });
        this.colorSelector.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.ComplexTableSetting.EVENT_CHANGE);
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
                type: "bi.vertical_adapt",
                height: this.constant.SINGLE_LINE_HEIGHT
            }]
        });
        this.tableSyleGroup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.ComplexTableSetting.EVENT_CHANGE);
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
            type: "bi.left",
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Table_Sheet_Style"),
                cls: "line-title",
                height: this.constant.SINGLE_LINE_HEIGHT
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Type"),
                cls: "attr-names",
                height: this.constant.SINGLE_LINE_HEIGHT
            }, this.tableFormGroup, {
                type: "bi.label",
                text: BI.i18nText("BI-Theme_Color"),
                cls: "attr-names",
                height: this.constant.SINGLE_LINE_HEIGHT
            }, {
                type: "bi.vertical_adapt",
                items: [this.colorSelector],
                height: this.constant.SINGLE_LINE_HEIGHT
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Table_Style"),
                cls: "attr-names",
                height: this.constant.SINGLE_LINE_HEIGHT
            }, this.tableSyleGroup, {
                type: "bi.vertical_adapt",
                items: [this.customTableStyle],
                cls: "attr-names",
                height: this.constant.SINGLE_LINE_HEIGHT
            }, {
                type: "bi.vertical_adapt",
                items: [this.tableStyleSetting],
                height: this.constant.SINGLE_LINE_HEIGHT
            }],
            hgap: this.constant.SIMPLE_H_GAP
        });

        //显示行汇总
        this.showRowTotal = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Total_Row"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.showRowTotal.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.ComplexTableSetting.EVENT_CHANGE);
        });
        //显示列汇总
        this.showColTotal = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Total_Col"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.showColTotal.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.ComplexTableSetting.EVENT_CHANGE);
        });
        //展开所有行表头节点
        this.openRowNode = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Open_All_Row_Header_Node"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.openRowNode.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.ComplexTableSetting.EVENT_CHANGE);
        });
        //展开所有列表头节点
        this.openColNode = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Open_All_Col_Header_Node"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.openColNode.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.ComplexTableSetting.EVENT_CHANGE);
        });
        //单页最大行数
        this.maxRow = BI.createWidget({
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
        this.maxRow.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.ComplexTableSetting.EVENT_CHANGE);
        });
        //单页最大列数
        this.maxCol = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "max-row-input",
            errorText: BI.i18nText("BI-Please_Enter_Number_1_To_100"),
            validationChecker: function (v) {
                return BI.isInteger(v) && v > 0 && v <= 100;
            }
        });
        this.maxCol.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.ComplexTableSetting.EVENT_CHANGE);
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
            items: BI.createItems([{
                type: "bi.label",
                text: BI.i18nText("BI-Element_Show"),
                cls: "line-title"
            }, {
                type: "bi.vertical_adapt",
                items: [this.showRowTotal]
            }, {
                type: "bi.vertical_adapt",
                items: [this.showColTotal]
            }, {
                type: "bi.vertical_adapt",
                items: [this.openRowNode]
            }, {
                type: "bi.vertical_adapt",
                items: [this.openColNode]
            }, {
                type: "bi.vertical_adapt",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Page_Max_Row"),
                    cls: "attr-names",
                    height: this.constant.SINGLE_LINE_HEIGHT
                }, {
                    type: "bi.center_adapt",
                    items: [this.maxRow],
                    width: this.constant.EDITOR_WIDTH,
                    height: this.constant.SINGLE_LINE_HEIGHT
                }],
                lgap: 5
            }, {
                type: "bi.vertical_adapt",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Page_Max_Col"),
                    cls: "attr-names",
                    height: this.constant.SINGLE_LINE_HEIGHT
                }, {
                    type: "bi.center_adapt",
                    items: [this.maxCol],
                    width: this.constant.EDITOR_WIDTH,
                    height: this.constant.SINGLE_LINE_HEIGHT
                }],
                lgap: 5
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
                lgap: 5
            }], {
                height: this.constant.SINGLE_LINE_HEIGHT
            }),
            hgap: this.constant.SIMPLE_H_GAP
        });

        //冻结维度
        this.freezeDim = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Freeze_Table_Dimensions"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.freezeDim.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.ComplexTableSetting.EVENT_CHANGE);
        });
        //联动传递指标过滤条件
        this.transferFilter = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Bind_Target_Condition"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.transferFilter.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.ComplexTableSetting.EVENT_CHANGE);
        });
        var otherAttr = BI.createWidget({
            type: "bi.left",
            cls: "single-line-settings",
            items: BI.createItems([{
                type: "bi.label",
                text: BI.i18nText("BI-Interactive_Attr"),
                cls: "line-title"
            }, {
                type: "bi.vertical_adapt",
                items: [this.freezeDim]
            }, {
                type: "bi.vertical_adapt",
                items: [this.transferFilter]
            }], {
                height: this.constant.SINGLE_LINE_HEIGHT
            }),
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
        this.tableFormGroup.setValue(BI.Utils.getWSTableFormByID(wId));
        this.colorSelector.setValue(BI.Utils.getWSThemeColorByID(wId));
        this.tableSyleGroup.setValue(BI.Utils.getWSTableStyleByID(wId));
        this.showRowTotal.setSelected(BI.Utils.getWSShowRowTotalByID(wId));
        this.showColTotal.setSelected(BI.Utils.getWSShowColTotalByID(wId));
        this.openRowNode.setSelected(BI.Utils.getWSOpenRowNodeByID(wId));
        this.openColNode.setSelected(BI.Utils.getWSOpenColNodeByID(wId));
        this.maxRow.setValue(BI.Utils.getWSMaxRowByID(wId));
        this.maxCol.setValue(BI.Utils.getWSMaxColByID(wId));
        this.freezeDim.setSelected(BI.Utils.getWSFreezeDimByID(wId));
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
    },

    getValue: function () {
        return {
            widget_setting: this.widgetSetting.getValue(),
            table_form: this.tableFormGroup.getValue()[0],
            theme_color: this.colorSelector.getValue(),
            table_style: this.tableSyleGroup.getValue()[0],
            show_row_total: this.showRowTotal.isSelected(),
            show_col_total: this.showColTotal.isSelected(),
            open_row_node: this.openRowNode.isSelected(),
            open_col_node: this.openColNode.isSelected(),
            max_row: this.maxRow.getValue(),
            max_col: this.maxCol.getValue(),
            freeze_dim: this.freezeDim.isSelected(),
            transfer_filter: this.transferFilter.isSelected()
        }
    },

    setValue: function (v) {
        this.widgetSetting.setValue(v.widget_setting);
        this.tableFormGroup.setValue(v.table_form);
        this.colorSelector.setValue(v.theme_color);
        this.tableSyleGroup.setValue(v.table_style);
        this.showRowTotal.setSelected(v.show_row_total);
        this.showColTotal.setSelected(v.show_col_total);
        this.openRowNode.setSelected(v.open_row_node);
        this.openColNode.setSelected(v.open_col_node);
        this.maxRow.setValue(v.max_row);
        this.maxCol.setValue(v.max_col);
        this.freezeDim.setSelected(v.freeze_dim);
        this.transferFilter.setSelected(v.transfer_filter);
    }
});
BI.ComplexTableSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.complex_table_setting", BI.ComplexTableSetting);