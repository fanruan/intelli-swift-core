/**
 * @class BI.SelectSingleTableField
 * @extend BI.Widget
 * 选择表中单个字段(不提供搜索)
 */
BI.GroupSelectFieldsItemGroup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.GroupSelectFieldsItemGroup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "",
            fields: []
        })
    },

    _init: function () {
        BI.GroupSelectFieldsItemGroup.superclass._init.apply(this, arguments);
        var self = this,o = this.options;
        this.info = this._getDataByFields(o.fields);

        this.button_group = BI.createWidget({
            type:"bi.button_group",
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_SINGLE,
            layouts:[{
                type:"bi.vertical"
            }]
        });
        this.button_group.populate(this.info);
        this.button_group.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.SelectSingleTableField.EVENT_CHANGE);
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [ {
                    type:"bi.layout",
                    height:5
                },
                this.button_group
            ]
        });
    },

    _getDataByFields: function (fields) {
        var self = this;
        return BI.map(fields, function(idx, field){
            var fname = field["field_name"];
            return {
                type: "bi.group_select_fields_item",
                cls: "group-select-button",
                fieldType: field["field_type"],
                text: fname,
                value: fname,
                drag: self._createDrag(fname)
            };
        });


    },

    _createDrag:function(fieldName){
        var self = this, o = this.options;
        return {
            cursor: BICst.cursorUrl,
            cursorAt: {left:5, top:5},
            revert: "invalid",
            drag: function (e, ui) {
                ui.helper.css({
                    left: ui.position.left,
                    top: ui.position.top,
                    cursor: BICst.cursorUrl
                });
            },
            helper: function(){
                var text = fieldName;
                var field = self.getValue()[0];
                var data = BI.find(o.fields, function(idx,fieldInfo){
                    return fieldInfo.field_name == field;
                });
                var help = BI.createWidget({
                    type: "bi.helper",
                    data: {field: data},
                    text:text
                });
                BI.createWidget({
                    type:"bi.default",
                    element: "body",
                    items:[help]
                });
                return help.element;
            }
        };
    },

    populate:function(items){
        this.options.fields = items;
        this.info = this._getDataByFields(items);
        this.button_group.populate(this.info);
    },

    getValue:function(){
        return this.button_group.getValue();
    }

});
$.shortcut("bi.group_select_fields_item_group", BI.GroupSelectFieldsItemGroup);