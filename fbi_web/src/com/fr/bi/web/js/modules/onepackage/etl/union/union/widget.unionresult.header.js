/**
 * @class BI.UnionResultHeader
 * @extend BI.Widget
 * union合并结果的输出字段名称
 */
BI.UnionResultHeader = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.UnionResultHeader.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-merge-result-header"
        })
    },

    _init: function () {
        BI.UnionResultHeader.superclass._init.apply(this, arguments);
        this.table = BI.createWidget({
            type: "bi.table_view",
            element: this.element,
            items: []
        });
        this.populate(this.options.mergeResult);
    },

    _checkName: function (index, name) {
        var valid = true;
        BI.some(this.mergeResult, function (i, names) {
            if (i !== index && name === names[0]) {
                valid = false;
                return true;
            }
        });
        return valid;
    },

    populate: function (mergeResult) {
        var self = this;
        this.mergeResult = mergeResult;
        var items = [];
        BI.each(this.mergeResult, function (i, name) {
            var initValue = "";
            BI.each(name, function (k, n) {
                if (k > 0 && n !== "") {
                    initValue += (initValue === "") ? n : ("/" + n);
                }
            });
            var title = name[0] === initValue ? name[0] : (name[0] + "(" + initValue + ")");
            var nameEditor = BI.createWidget({
                type: "bi.sign_initial_editor",
                allowBlank: false,
                text: initValue,
                title: title,
                width: 100,
                errorText: function (v) {
                    if (v === "") {
                        return BI.i18nText("BI-Field_Name_Cannot_Be_Null");
                    }
                    return BI.i18nText("BI-Can_Not_Have_Rename_Fields");
                },
                validationChecker: function (v) {
                    return self._checkName(i, v);
                }
            });
            nameEditor.setValue({value: name[0]});
            nameEditor.on(BI.SignInitialEditor.EVENT_CHANGE, function () {
                var nameValue = nameEditor.getValue();
                self.mergeResult[i][0] = nameValue.value;
                nameEditor.setTitle(nameValue.value === initValue ? nameValue.value : (nameValue.value + "(" + initValue + ")"));
                self.fireEvent(BI.UnionResultHeader.EVENT_CHANGE, self.mergeResult);
            });
            var editorIcon = BI.createWidget({
                type: "bi.icon_button",
                cls: "edit-set-font",
                width: 20,
                height: 30,
                handler: function () {
                    nameEditor.focus();
                }
            });
            var tablesCount = 0, index = 0;
            BI.each(name, function (j, n) {
                if (j > 0 && n !== "") {
                    tablesCount++;
                    index = j;
                }
            });
            items.push({
                type: "bi.left_right_vertical_adapt",
                items: {
                    left: [nameEditor],
                    right: [editorIcon]
                },
                cls: tablesCount === 1 ? ("table-color" + (index - 1) % 5) : "result-table",
                height: "100%"
            });
        });
        this.table.populate([items]);
    }
});
BI.UnionResultHeader.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.union_result_header", BI.UnionResultHeader);