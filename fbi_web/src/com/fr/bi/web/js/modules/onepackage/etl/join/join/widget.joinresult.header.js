/**
 * @class BI.JoinResultHeader
 * @extend BI.Widget
 * join 结果
 */
BI.JoinResultHeader = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.JoinResultHeader.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-merge-result-header"
        })
    },

    _init: function(){
        BI.JoinResultHeader.superclass._init.apply(this, arguments);
        this.table = BI.createWidget({
            type: "bi.table_view",
            element: this.element,
            items: []
        });
        this.populate(this.options.mergeResult);
    },

    _checkName: function(index, name){
        var valid = true;
        BI.some(this.mergeResult, function(i, names){
            if(i !== index && name === names.name){
                valid = false;
                return true;
            }
        });
        return valid;
    },

    populate: function(mergeResult) {
        var self = this;
        this.mergeResult = mergeResult;
        var items = [];
        BI.each(this.mergeResult, function(i, name){
            var title = name.name === name.column_name ? name.name : (name.name + "(" + name.column_name + ")");
            var nameEditor = BI.createWidget({
                type: "bi.sign_initial_editor",
                allowBlank: false,
                text: name.column_name,
                title: title,
                width: 100,
                validationChecker: function(v){
                    return self._checkName(i, v);
                },
                errorText: BI.i18nText("BI-Can_Not_Have_Rename_Fields")
            });
            nameEditor.setValue({value: name.name});
            nameEditor.on(BI.SignInitialEditor.EVENT_CHANGE, function(){
                var nameValue = nameEditor.getValue();
                self.mergeResult[i].name = nameValue.value;
                nameEditor.setTitle(nameValue.value === name.column_name ? nameValue.value : (nameValue.value + "(" + name.column_name + ")"));
                self.fireEvent(BI.JoinResultHeader.EVENT_CHANGE, self.mergeResult);
            });
            var editorIcon = BI.createWidget({
                type: "bi.icon_button",
                cls: "edit-set-font",
                width: 20,
                height: 30,
                handler: function(){
                    nameEditor.focus();
                }
            });
            items.push({
                type: "bi.left_right_vertical_adapt",
                items: {
                    left: [nameEditor],
                    right: [editorIcon]
                },
                cls: name.isLeft === true ? "table-color0" : "table-color1",
                height: "100%"
            });
        });
        this.table.populate([items]);
    }
});
BI.JoinResultHeader.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.join_result_header", BI.JoinResultHeader);