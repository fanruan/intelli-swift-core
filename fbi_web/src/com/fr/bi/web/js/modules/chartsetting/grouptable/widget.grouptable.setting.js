/**
 * created by young
 * 分组表的样式设置
 */
BI.GroupTableSetting = BI.inherit(BI.Widget, {

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

    _defaultConfig: function(){
        return BI.extend(BI.GroupTableSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-group-table-setting"
        })
    },

    _init: function(){
        BI.GroupTableSetting.superclass._init.apply(this, arguments);
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
                type: "bi.left"
            }]
        });
        this.tableFormGroup.on(BI.ButtonGroup.EVENT_CHANGE, function(){
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
        });
        //主题颜色
        this.colorSelector = BI.createWidget({
            type: "bi.color_chooser",
            width: this.constant.BUTTON_HEIGHT,
            height: this.constant.BUTTON_HEIGHT
        });
        this.colorSelector.on(BI.ColorChooser.EVENT_CHANGE, function(){
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
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
        this.tableSyleGroup.on(BI.ButtonGroup.EVENT_CHANGE, function(){
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
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
                    text: BI.i18nText("BI-Type"),
                    cls: "attr-names"
                }, this.tableFormGroup, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Theme_Color"),
                    cls: "attr-names"
                }, this.colorSelector, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Style"),
                    cls: "attr-names"
                }, this.tableSyleGroup]
            },
            height: this.constant.SINGLE_LINE_HEIGHT,
            lhgap: this.constant.SIMPLE_H_GAP
        });

        //显示序号
        this.showNumber = BI.createWidget({
            type: "bi.checkbox"
        });
        this.showNumber.on(BI.Checkbox.EVENT_CHANGE, function(){
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
        });
        //显示汇总
        this.showRowTotal = BI.createWidget({
            type: "bi.checkbox"
        });
        this.showRowTotal.on(BI.Checkbox.EVENT_CHANGE, function(){
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
        });
        //展开所有行表头节点
        this.openRowNode = BI.createWidget({
            type: "bi.checkbox"
        });
        this.openRowNode.on(BI.Checkbox.EVENT_CHANGE, function(){
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
        });
        //单页最大行数
        this.maxRow = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "max-row-input",
            errorText: BI.i18nText("BI-Please_Enter_Number_1_To_100"),
            validationChecker: function(v){
                var value = BI.parseInt(v);
                return value > 0 && value < 100;
            }
        });
        this.maxRow.on(BI.SignEditor.EVENT_CHANGE, function(){
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
        });
        var show = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Element_Show"),
                    cls: "line-title"
                }, {
                    type: "bi.left",
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
                    type: "bi.left",
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
                    type: "bi.left",
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
                    type: "bi.left",
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
                }]
            },
            height: this.constant.SINGLE_LINE_HEIGHT,
            lhgap: this.constant.SIMPLE_H_GAP
        });

        //冻结维度
        this.freezeDim = BI.createWidget({
            type: "bi.checkbox"
        });
        this.freezeDim.on(BI.Checkbox.EVENT_CHANGE, function(){
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
        });
        //联动传递指标过滤条件
        this.transferFilter = BI.createWidget({
            type: "bi.checkbox"
        });
        this.transferFilter.on(BI.Checkbox.EVENT_CHANGE, function(){
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
        });
        var otherAttr = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Interactive_Attr"),
                    cls: "line-title"
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
                }]
            },
            height: this.constant.SINGLE_LINE_HEIGHT,
            lhgap: this.constant.SIMPLE_H_GAP
        });
        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [tableStyle, show, otherAttr],
            hgap: 10
        })
    },

    getValue: function(){
        return {
            table_form: this.tableFormGroup.getValue()[0],
            theme_color: this.colorSelector.getValue(),
            table_style: this.tableSyleGroup.getValue()[0],
            show_number: this.showNumber.isSelected(),
            show_row_total: this.showRowTotal.isSelected(),
            open_row_node: this.openRowNode.isSelected(),
            max_row: this.maxRow.getValue(),
            freeze_dim: this.freezeDim.isSelected(),
            transfer_filter: this.transferFilter.isSelected()
        }
    },

    setValue: function(v){
        this.tableFormGroup.setValue(v.table_form);
        this.colorSelector.setValue(v.theme_color);
        this.tableSyleGroup.setValue(v.table_style);
        this.showNumber.setSelected(v.show_number);
        this.showRowTotal.setSelected(v.show_row_total);
        this.openRowNode.setSelected(v.open_row_node);
        this.maxRow.setValue(v.max_row);
        this.freezeDim.setSelected(v.freeze_dim);
        this.transferFilter.setSelected(v.transfer_filter);
    }
});
BI.GroupTableSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.group_table_setting", BI.GroupTableSetting);