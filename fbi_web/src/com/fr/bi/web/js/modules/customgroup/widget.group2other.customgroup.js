/**
 * Created by roy on 15/10/19.
 */
BI.CustomGroupGroup2Other = BI.inherit(BI.Widget, {
    _constant: {
        selected: 1
    },
    _defaultConfig: function () {
        return BI.extend(BI.CustomGroupGroup2Other.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "custom-group-bottom"
        })
    }
    ,
    _init: function () {
        var self = this, o = this.options;
        BI.CustomGroupGroup2Other.superclass._init.apply(this, arguments);
        this.checkbox = BI.createWidget({
            type: "bi.checkbox"
        });
        this.label = BI.createWidget({
            type: "bi.label",
            value: BI.i18nText("BI-Ungrouped_Value_To")
        });
        this.editor = BI.createWidget({
            type: "bi.editor",
            cls: "custom-group-editor",
            value: BI.i18nText("BI-Others"),
            validationChecker: o.validationChecker,
            width: 88,
            height: 28,
            disabled: true
        });

        this.checkbox.on(BI.Checkbox.EVENT_CHANGE, function () {
            if (self.checkbox.isSelected()) {
                self.editor.setEnable(true);
            } else {
                self.editor.setEnable(false);
            }
        });

        this.editor.on(BI.Editor.EVENT_CHANGE, function () {
            self.fireEvent(BI.CustomGroupGroup2Other.EVENT_CHANGE)
        });

        BI.createWidget({
            element: this.element,
            type: "bi.horizontal",
            rgap: 5,
            items: [
                self.checkbox,
                self.label,
                self.editor
            ]
        })
    },


    populate: function (ungroup2Other, ungroup2OtherName) {
        c = this._constant;
        if (ungroup2Other === BICst.CUSTOM_GROUP.UNGROUP2OTHER.SELECTED) {
            this.checkbox.setSelected(true);
            this.editor.setValue(ungroup2OtherName);
            this.editor.setEnable(true);
        } else {
            this.checkbox.setSelected(false);
            this.editor.setValue(BI.i18nText("BI-Others"));
            this.editor.setEnable(false);
        }
    }
    ,

    isSelected: function () {
        return this.checkbox.isSelected();
    }
    ,

    getValue: function () {
        return this.editor.getValue();
    }
    ,

    setValue: function (v) {
        this.editor.setValue(v);
    }


})
;
BI.CustomGroupGroup2Other.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.custom_group_group2other", BI.CustomGroupGroup2Other);