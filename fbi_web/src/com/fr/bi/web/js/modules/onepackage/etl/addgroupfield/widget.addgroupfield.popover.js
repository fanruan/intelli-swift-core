/**
 * Created by roy on 16/3/16.
 */
BI.AddGroupFieldPopover = BI.inherit(BI.BarPopoverSection, {
    constants: {
        ETL_ADD_GROUP_FIELD_FLOAT_BOX_LABEL_NARROW_WIDTH: 50,
        ETL_ADD_GROUP_FIELD_FLOAT_BOX_LABEL_WIDE_WIDTH: 140,
        ETL_ADD_GROUP_FIELD_FLOAT_BOX_LABEL_WIDTH: 75,
        ETL_ADD_GROUP_FIELD_FLOAT_BOX_LABEL_HEIGHT: 30,
        ETL_ADD_GROUP_FIELD_FLOAT_BOX_GAP_TEN: 10,
        ETL_ADD_GROUP_FIELD_FLOAT_BOX_GAP_FIVE: 5,
        ETL_ADD_GROUP_FIELD_FLOAT_BOX_NORTH_HEIGHT: 50
    },
    _defaultConfig: function () {
        return BI.extend(BI.AddGroupFieldPopover.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl-add-group-field-popover"
        })
    },
    _init: function () {
        var o = this.options;
        BI.AddGroupFieldPopover.superclass._init.apply(this, arguments);
        this.model = new BI.AddGroupFieldPopoverModel({
            info: o.info
        });
    },

    end: function () {
        var groupValue = this.customgroup.getValue();
        var target_field_name = this.fieldPaneButton.getValue();
        var field_name = this.newFieldNameEditor.getValue();
        var data = {
            id: this.model.getID(),
            group: groupValue,
            table_infor: {
                target_field_name: target_field_name,
                field_name: field_name
            }
        };
        this.fireEvent(BI.AddGroupFieldPopover.EVENT_SAVE, data);
    },

    rebuildNorth: function (north) {
        this.northLabel = BI.createWidget({
            type: "bi.label",
            element: north,
            value: BI.i18nText("BI-Select_Base_Column_Of_New_Columns"),
            textAlign: "left",
            height: this.constants.ETL_ADD_GROUP_FIELD_FLOAT_BOX_NORTH_HEIGHT
        });
    },


    rebuildCenter: function (center) {
        var self = this;
        this.customgroup = BI.createWidget({
            type: "bi.etl_add_group_field_custom_group"
        });

        this.newFieldNameEditor = BI.createWidget({
            type: "bi.text_editor",
            tipType: "warning",
            quitChecker: function () {
                return false
            },
            allowBlank: true,
            errorText: BI.i18nText("BI-Group_Column_Duplicate"),
            validationChecker: function (v) {
                return !self.model.isDuplicate(v);
            }
        });
        this.newFieldNameEditor.on(BI.TextEditor.EVENT_VALID, function () {
            self._checkEnable();
        });

        this.newFieldNameEditor.on(BI.TextEditor.EVENT_ERROR, function () {
            self._checkEnable();
        });

        this.fieldPane = BI.createWidget({
            type: "bi.left",
            cls: "bi-etl-add-group-field-float-box-field-pane",
            hgap: this.constants.ETL_ADD_GROUP_FIELD_FLOAT_BOX_GAP_TEN
        });

        this.fieldPaneButton = BI.createWidget({
            type: "bi.text_button",
            textAlign: "left",
            height: this.constants.ETL_ADD_GROUP_FIELD_FLOAT_BOX_LABEL_HEIGHT
        });

        this.fieldPaneButton.on(BI.TextButton.EVENT_CHANGE, function () {
            self.fieldPane.setVisible(true);
            self.sure.setVisible(false);
            self.newFieldNameEditor.setValue("");
            self.northLabel.setValue(BI.i18nText("BI-Select_Base_Column_Of_New_Columns"));
        });

        var newFieldNameAndFieldPaneButton = BI.createWidget({
            type: "bi.htape",
            items: [
                {
                    type: "bi.label",
                    cls: "bi-etl-add-group-field-float-box-label",
                    value: BI.i18nText("BI-Column_Name"),
                    width: this.constants.ETL_ADD_GROUP_FIELD_FLOAT_BOX_LABEL_NARROW_WIDTH
                },
                {
                    el: this.newFieldNameEditor
                },
                {
                    el: {
                        type: "bi.htape",
                        items: [{
                            type: "bi.label",
                            cls: "bi-etl-add-group-field-float-box-label",
                            value: BI.i18nText("BI-Base_Column") + ":",
                            width: this.constants.ETL_ADD_GROUP_FIELD_FLOAT_BOX_LABEL_WIDTH
                        }, {
                            el: this.fieldPaneButton
                        }]
                    },
                    width: this.constants.ETL_ADD_GROUP_FIELD_FLOAT_BOX_LABEL_WIDE_WIDTH
                }
            ],
        });

        BI.createWidget({
            type: "bi.vtape",
            element: center,
            items: [
                {
                    el: newFieldNameAndFieldPaneButton,
                    height: this.constants.ETL_ADD_GROUP_FIELD_FLOAT_BOX_LABEL_HEIGHT
                },
                {
                    el: this.customgroup
                }
            ],
            bgap: this.constants.ETL_ADD_GROUP_FIELD_FLOAT_BOX_GAP_TEN
        });

        BI.createWidget({
            type: "bi.absolute",
            element: center,
            items: [{
                el: this.fieldPane,
                top: 0,
                left: 0,
                bottom: 0,
                right: 0
            }]
        })
    },

    _checkOepn2Change: function () {
        var id = this.model.getID();

        var fieldName = "";
        if (this.model.isOpen2Change()) {
            var table_infor = this.model.getNewGroupsData()[id].table_infor;
            var group = this.model.getNewGroupsData()[id].group;
            this.fieldPane.setVisible(false);
            this.sure.setVisible(true);
            fieldName = table_infor.target_field_name;
            var tables = this.model.getAllTables();
            this.customgroup.populate(tables[0] || tables, fieldName, group.details, group.ungroup2Other, group.ungroup2OtherName);
            this.newFieldNameEditor.setValue(table_infor.field_name);
            this.fieldPaneButton.setValue(fieldName);
            this.northLabel.setValue(BI.i18nText("BI-Add_Grouping_Column"));
        } else {
            this.fieldPane.setVisible(true);
            this.sure.setVisible(false);
        }
    },

    _createFieldItems: function (fields) {
        var buttons = [];
        var self = this;
        BI.each(fields, function (i, field) {
            var button = BI.createWidget({
                type: "bi.text_button",
                cls: "bi-etl-add-group-field-float-box-field-button",
                value: field.field_name,
                vgap: self.constants.ETL_ADD_GROUP_FIELD_FLOAT_BOX_GAP_FIVE,
                height: self.constants.ETL_ADD_GROUP_FIELD_FLOAT_BOX_LABEL_HEIGHT,
                id: field.id
            });
            button.on(BI.TextButton.EVENT_CHANGE, function (value) {
                var table = self.model.getAllTables();
                self.fieldPane.setVisible(false);
                self.sure.setVisible(true);
                self.customgroup.populate(table[0] || table, value);
                self.fieldPaneButton.setValue(value);
                self.northLabel.setValue(BI.i18nText("BI-Add_Grouping_Column"));
            });
            buttons.push(button);
        });
        return buttons
    },

    _checkEnable: function () {
        var fieldName = this.newFieldNameEditor.getValue();
        if (BI.isEmptyString(fieldName)) {
            this.sure.setWarningTitle(BI.i18nText("BI-Field_Name_Cannot_Be_Null"));
            this.sure.setEnable(false);
        } else if (!this.newFieldNameEditor.isValid()) {
            this.newFieldNameEditor.setWarningTitle(BI.i18nText("BI-Group_Column_Duplicate"));
            this.sure.setEnable(false);
            this.sure.setWarningTitle(BI.i18nText("BI-Group_Column_Duplicate"));
        } else {
            this.sure.setWarningTitle("");
            this.sure.setEnable(true);
        }
    },


    populate: function () {
        var self = this;
        this.model.initData(function () {
            var fields = self.model.getAllFields();
            var buttons = self._createFieldItems(fields);
            self.fieldPane.empty();
            self.fieldPane.populate(buttons);
            self._checkOepn2Change();
            self._checkEnable();
        })

    }

});
BI.AddGroupFieldPopover.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.add_group_field_popover", BI.AddGroupFieldPopover);