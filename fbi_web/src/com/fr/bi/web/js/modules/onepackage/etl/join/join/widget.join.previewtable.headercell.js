/**
 * Created by Young's on 2016/7/9.
 */
BI.JoinPreviewTableHeaderCell = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.JoinPreviewTableHeaderCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-join-preview-table-header-cell"
        })
    },

    _init: function () {
        BI.JoinPreviewTableHeaderCell.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var merge = o.merge;
        var oFields = "";
        if(merge.length > 1) {
            oFields = "(" + merge.join("/") + ")";
        }
        var nameLabel = BI.createWidget({
            type: "bi.text_button",
            textAlign: "left",
            text: o.text + oFields,
            value: o.text + oFields,
            title: function() {
                return nameLabel.getValue();
            },
            height: 30
        });
        var nameEditor = BI.createWidget({
            type: "bi.sign_editor",
            value: o.value,
            height: 30,
            validationChecker: o.validationChecker,
            quitChecker: o.quitChecker,
            allowBlank: false,
            errorText: function (v) {
                if (v === "") {
                    return BI.i18nText("BI-Field_Name_Cannot_Be_Null");
                }
                return BI.i18nText("BI-Field_Name_Already_Exist");
            }
        });
        BI.createWidget({
            type: "bi.absolute",
            element: nameLabel.element,
            items: [{
                el: nameEditor,
                left: 6,
                right: 0,
                top: 0,
                bottom: 0
            }]
        });

        var showEditor = function () {
            nameLabel.setValue("");
            nameEditor.setVisible(true);
            nameEditor.focus();
        };
        nameLabel.on(BI.TextButton.EVENT_CHANGE, function () {
            showEditor();
        });
        nameEditor.on(BI.SignEditor.EVENT_CONFIRM, function () {
            nameEditor.setVisible(false);
            nameLabel.setValue(nameEditor.getValue() + oFields);
            o.onRenameField(this.getValue());
        });
        nameEditor.setVisible(false);
        var editorIcon = BI.createWidget({
            type: "bi.icon_button",
            cls: "edit-set-font",
            width: 30,
            height: o.height,
            handler: function () {
                showEditor();
            }
        });
        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: nameLabel
            }, {
                el: editorIcon,
                width: 30
            }]
        });
    }
});
$.shortcut("bi.join_preview_table_header_cell", BI.JoinPreviewTableHeaderCell);