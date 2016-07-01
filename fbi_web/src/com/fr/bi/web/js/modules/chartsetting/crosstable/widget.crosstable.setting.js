/**
 * created by young
 * 交叉表的样式设置
 */
BI.CrossTableSetting = BI.inherit(BI.Widget, {

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
        return BI.extend(BI.CrossTableSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cross-table-setting"
        })
    },

    _init: function () {
        BI.CrossTableSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
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
            self.fireEvent(BI.CrossTableSetting.EVENT_CHANGE);
        });
        //主题颜色
        this.colorSelector = BI.createWidget({
            type: "bi.color_chooser",
            width: this.constant.BUTTON_HEIGHT,
            height: this.constant.BUTTON_HEIGHT
        });
        this.colorSelector.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.CrossTableSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.CrossTableSetting.EVENT_CHANGE);
        });
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
            }, this.tableSyleGroup],
            hgap: this.constant.SIMPLE_H_GAP
        });

        //显示序号
        this.showNumber = BI.createWidget({
            type: "bi.checkbox"
        });
        this.showNumber.on(BI.Checkbox.EVENT_CHANGE, function () {
            self.fireEvent(BI.CrossTableSetting.EVENT_CHANGE);
        });
        //显示行汇总
        this.showRowTotal = BI.createWidget({
            type: "bi.checkbox"
        });
        this.showRowTotal.on(BI.Checkbox.EVENT_CHANGE, function () {
            self.fireEvent(BI.CrossTableSetting.EVENT_CHANGE);
        });
        //显示列汇总
        this.showColTotal = BI.createWidget({
            type: "bi.checkbox"
        });
        this.showColTotal.on(BI.Checkbox.EVENT_CHANGE, function () {
            self.fireEvent(BI.CrossTableSetting.EVENT_CHANGE);
        });
        //展开所有行表头节点
        this.openRowNode = BI.createWidget({
            type: "bi.checkbox"
        });
        this.openRowNode.on(BI.Checkbox.EVENT_CHANGE, function () {
            self.fireEvent(BI.CrossTableSetting.EVENT_CHANGE);
        });
        //展开所有列表头节点
        this.openColNode = BI.createWidget({
            type: "bi.checkbox"
        });
        this.openColNode.on(BI.Checkbox.EVENT_CHANGE, function () {
            self.fireEvent(BI.CrossTableSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.CrossTableSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.CrossTableSetting.EVENT_CHANGE);
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
                items: [{
                    type: "bi.center_adapt",
                    items: [this.showNumber],
                    width: this.constant.CHECKBOX_WIDTH,
                    height: this.constant.SINGLE_LINE_HEIGHT
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Display_Sequence_Number"),
                    cls: "attr-names",
                    height: this.constant.SINGLE_LINE_HEIGHT
                }],
                lgap: this.constant.SIMPLE_L_GAP
            }, {
                type: "bi.vertical_adapt",
                items: [{
                    type: "bi.center_adapt",
                    items: [this.showRowTotal],
                    width: this.constant.CHECKBOX_WIDTH,
                    height: this.constant.SINGLE_LINE_HEIGHT
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Show_Total_Row"),
                    cls: "attr-names",
                    height: this.constant.SINGLE_LINE_HEIGHT
                }],
                lgap: this.constant.SIMPLE_L_GAP
            }, {
                type: "bi.vertical_adapt",
                items: [{
                    type: "bi.center_adapt",
                    items: [this.showColTotal],
                    width: this.constant.CHECKBOX_WIDTH,
                    height: this.constant.SINGLE_LINE_HEIGHT
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Show_Total_Col"),
                    cls: "attr-names",
                    height: this.constant.SINGLE_LINE_HEIGHT
                }],
                lgap: this.constant.SIMPLE_L_GAP
            }, {
                type: "bi.vertical_adapt",
                items: [{
                    type: "bi.center_adapt",
                    items: [this.openRowNode],
                    width: this.constant.CHECKBOX_WIDTH,
                    height: this.constant.SINGLE_LINE_HEIGHT
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Open_All_Row_Header_Node"),
                    cls: "attr-names",
                    height: this.constant.SINGLE_LINE_HEIGHT
                }],
                lgap: this.constant.SIMPLE_L_GAP
            }, {
                type: "bi.vertical_adapt",
                items: [{
                    type: "bi.center_adapt",
                    items: [this.openColNode],
                    width: this.constant.CHECKBOX_WIDTH,
                    height: this.constant.SINGLE_LINE_HEIGHT
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Open_All_Col_Header_Node"),
                    cls: "attr-names",
                    height: this.constant.SINGLE_LINE_HEIGHT
                }],
                lgap: this.constant.SIMPLE_L_GAP
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
            }],
            hgap: this.constant.SIMPLE_H_GAP
        });

        //冻结维度
        this.freezeDim = BI.createWidget({
            type: "bi.checkbox"
        });
        this.freezeDim.on(BI.Checkbox.EVENT_CHANGE, function () {
            self.fireEvent(BI.CrossTableSetting.EVENT_CHANGE);
        });
        //联动传递指标过滤条件
        this.transferFilter = BI.createWidget({
            type: "bi.checkbox"
        });
        this.transferFilter.on(BI.Checkbox.EVENT_CHANGE, function () {
            self.fireEvent(BI.CrossTableSetting.EVENT_CHANGE);
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
                type: "bi.left",
                items: [{
                    type: "bi.center_adapt",
                    items: [this.freezeDim],
                    width: this.constant.CHECKBOX_WIDTH,
                    height: this.constant.SINGLE_LINE_HEIGHT
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Freeze_Table_Dimensions"),
                    cls: "attr-names",
                    height: this.constant.SINGLE_LINE_HEIGHT
                }],
                lgap: this.constant.SIMPLE_L_GAP
            }, {
                type: "bi.left",
                items: [{
                    type: "bi.center_adapt",
                    items: [this.transferFilter],
                    width: this.constant.CHECKBOX_WIDTH,
                    height: this.constant.SINGLE_LINE_HEIGHT
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Bind_Target_Condition"),
                    cls: "attr-names",
                    height: this.constant.SINGLE_LINE_HEIGHT
                }],
                lgap: this.constant.SIMPLE_L_GAP
            }],
            hgap: this.constant.SIMPLE_H_GAP
        });
        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [tableStyle, show, otherAttr],
            hgap: 10
        })
    },

    populate: function () {
        var wId = this.options.wId;
        this.tableFormGroup.setValue(BI.Utils.getWSTableFormByID(wId));
        this.colorSelector.setValue(BI.Utils.getWSThemeColorByID(wId));
        this.tableSyleGroup.setValue(BI.Utils.getWSTableStyleByID(wId));
        this.showNumber.setSelected(BI.Utils.getWSShowNumberByID(wId));
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
            table_form: this.tableFormGroup.getValue()[0],
            theme_color: this.colorSelector.getValue(),
            table_style: this.tableSyleGroup.getValue()[0],
            show_number: this.showNumber.isSelected(),
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
        this.tableFormGroup.setValue(v.table_form);
        this.colorSelector.setValue(v.theme_color);
        this.tableSyleGroup.setValue(v.table_style);
        this.showNumber.setSelected(v.show_number);
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
BI.CrossTableSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.cross_table_setting", BI.CrossTableSetting);