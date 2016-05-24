/**
 * 可以编辑文本的多选列表项
 * @class BI.ConvertMultiSelectItem
 * @extends BI.BasicButton
 */

BI.ConvertMultiSelectItem = BI.inherit(BI.Widget, {

    constants: {
        newValuePos: 1,
        initialValuePos: 0
    },

    _defaultConfig: function() {
        return BI.extend(BI.ConvertMultiSelectItem.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-convert-multi-select-item",
            height: 25
        })
    },
    _init : function() {
        BI.ConvertMultiSelectItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.checkbox = BI.createWidget({
            type: "bi.checkbox",
            selected: this.options.selected
        });
        this.stateEditor = BI.createWidget({
            type: "bi.sign_initial_editor",
            cls: "sign-initial-editor",
            text: o.value,
            height: o.height,
            hgap: o.hgap,
            rgap: o.rgap
        });
        this.checkbox.on(BI.Controller.EVENT_CHANGE, function(type){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        this.stateEditor.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        BI.createWidget({
            type: "bi.default",
            element: this.element,
            tagName: "li",
            height: o.height,
            items: [{
                type: "bi.border",
                height: o.height,
                items: {
                    west: {
                        el: {
                            type: "bi.center_adapt",
                            items: [this.checkbox]
                        },
                        width: 36
                    },
                    center: {
                        el: this.stateEditor
                    }
                }
            }]
        });
    },

    setValue: function(v){
        var self = this;
        BI.any(v, function(idx, vobj){
            var newValue = vobj.value[self.constants.newValuePos], initialValue = vobj.value[self.constants.initialValuePos];
            if(initialValue + "" === self.options.value + ""){
                self.checkbox.setSelected(true);
                self.stateEditor.setValue({
                    value: newValue,
                    text: initialValue
                });
                return true;
            }else{
                self.checkbox.setSelected(false);
            }
        });
    },

    getValue: function(){
        var value = this.stateEditor.getValue();
        var arr = [];
        arr.push(value.text);
        arr.push(value.value === value.text ? "" : value.value);
        return {
            value: arr,
            selected: this.checkbox.isSelected()
        };
    }
});

BI.ConvertMultiSelectItem.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.convert_multi_select_item", BI.ConvertMultiSelectItem);