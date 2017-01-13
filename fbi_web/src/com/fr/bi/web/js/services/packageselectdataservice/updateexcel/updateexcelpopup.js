/**
 * Created by zcf on 2017/1/9.
 */
BI.UpdateExcelPopup = BI.inherit(BI.Widget, {
    _constant: {
        LABEL_HEIGHT: 25,
        LABEL_WIDTH: 80,
        UPDATE_BUTTON_WIDTH: 110,
        UPDATE_BUTTON_HEIGHT: 30,
        UPDATE_BAR_HEIGHT: 70,
        HELP_COMBO_WIDTH: 50,

        FIELDS_LABEL_HEIGHT: 40,

        BOTTOM_BUTTON_HEIGHT: 30,
        BOTTOM_BUTTON_WIDTH: 90,
        BOTTOM_BUTTON_BAR_HEIGHT: 60
    },

    _defaultConfig: function () {
        return BI.extend(BI.UpdateExcelPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-update-excel-popup",
            tableId: ""
        })
    },

    _init: function () {
        BI.UpdateExcelPopup.superclass._init.apply(this, arguments);
    },

    _createPopup: function () {
        var self = this, o = this.options, c = this._constant;

        if (!this.model) {
            this.model = new BI.UpdateExcelModel({
                tableId: o.tableId
            });
        }

        if (!this.popup) {
            var updateExcelBar = this._createUpdateExcelBar();
            var tableFieldMassage = this._createTableFieldsMassage();
            var bottomButton = this._createBottomButton();

            var wrapper = BI.createWidget({
                type: "bi.vtape",
                items: [{
                    el: updateExcelBar,
                    height: c.UPDATE_BAR_HEIGHT
                }, {
                    el: tableFieldMassage,
                    height: "fill"
                }, {
                    el: bottomButton,
                    height: c.BOTTOM_BUTTON_BAR_HEIGHT
                }]
            });
            this.popup = BI.createWidget({
                type: "bi.absolute",
                element: this.element,
                items: [{
                    el: wrapper,
                    top: 0,
                    bottom: 0,
                    left: 20,
                    right: 20
                }]
            })
        }
    },

    _createUpdateExcelBar: function () {
        var self = this, c = this._constant;

        var nameLabel = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            text: BI.i18nText("BI-Upload_Information"),
            height: c.LABEL_HEIGHT
        });
        this.excelName = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            text: this.model.getExcelName(),
            height: c.LABEL_HEIGHT
        });
        var nameWrapper = BI.createWidget({
            type: "bi.htape",
            height: c.LABEL_HEIGHT,
            items: [{
                width: c.LABEL_WIDTH,
                el: nameLabel
            }, {
                width: "fill",
                el: this.excelName
            }]
        });

        var dateLabel = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            text: BI.i18nText("BI-Upload_Date"),
            height: c.LABEL_HEIGHT
        });
        this.updateDate = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            text: this.model.getUpdateDate(),
            height: c.LABEL_HEIGHT
        });
        var dateWrapper = BI.createWidget({
            type: "bi.htape",
            height: c.LABEL_HEIGHT,
            items: [{
                width: c.LABEL_WIDTH,
                el: dateLabel
            }, {
                width: "fill",
                el: this.updateDate
            }]
        });
        var emptyDiv = BI.createWidget({
            type: "bi.layout",
            height: 20
        });
        var excelMessage = BI.createWidget({
            type: "bi.vertical",
            cls: "label-text",
            items: [emptyDiv, nameWrapper, dateWrapper]
        });

        var updateButton = BI.createWidget({
            type: "bi.upload_excel_button",
            text: BI.i18nText("BI-Excel_Reupload"),
            width: c.UPDATE_BUTTON_WIDTH,
            height: c.UPDATE_BUTTON_HEIGHT
        });
        updateButton.on(BI.UploadExcelButton.EVENT_AFTER_UPLOAD, function (files) {
            self.model.setFile(files[files.length - 1], function () {
                self.mask = BI.createWidget({
                    type: "bi.loading_mask",
                    masker: self.element,
                    container: self.element,
                    text: BI.i18nText("BI-Loading")
                });
            }, function () {
                if (self.model.getValidation() === false) {
                    BI.Msg.toast(BI.i18nText("BI-Excel_Modify_Fail"), "warning");
                } else {
                    self._refreshAfterUpload();
                }
            }, function () {
                self.mask.destroy()
            });
        });

        var helpButton = BI.createWidget({
            type: "bi.excel_tip_combo",
            cls: "excel-help-combo"
        });

        return BI.createWidget({
            type: "bi.htape",
            cls: "excel-update-bar",
            items: [{
                width: "fill",
                el: excelMessage
            }, {
                width: c.UPDATE_BUTTON_WIDTH,
                el: {
                    type: "bi.center_adapt",
                    items: [updateButton],
                    height: c.UPDATE_BAR_HEIGHT
                }
            }, {
                width: c.HELP_COMBO_WIDTH,
                el: {
                    type: "bi.center_adapt",
                    items: [helpButton],
                    height: c.UPDATE_BAR_HEIGHT
                }
            }]
        })
    },

    _createTableFieldsMassage: function () {
        var c = this._constant;

        var label = BI.createWidget({
            type: "bi.label",
            cls: "label-text",
            text: BI.i18nText("BI-Excel_Table_Fields"),
            textAlign: "left",
            height: c.FIELDS_LABEL_HEIGHT
        });
        this.fieldsTableWrapper = BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: this._createFieldsTable(),
                top: 0,
                bottom: 0,
                left: 0,
                right: 0
            }]
        });
        return BI.createWidget({
            type: "bi.vtape",
            items: [{
                el: label,
                height: c.FIELDS_LABEL_HEIGHT
            }, {
                el: this.fieldsTableWrapper,
                height: "fill"
            }]
        })
    },

    _createBottomButton: function () {
        var self = this, c = this._constant;

        var confirm = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-OK"),
            height: c.BOTTOM_BUTTON_HEIGHT,
            width: c.BOTTOM_BUTTON_WIDTH
        });
        confirm.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.UpdateExcelPopup.EVENT_CONFIRM);
        });

        var cancel = BI.createWidget({
            type: "bi.button",
            level: 'ignore',
            text: BI.i18nText("BI-Cancel"),
            height: c.BOTTOM_BUTTON_HEIGHT,
            width: c.BOTTOM_BUTTON_WIDTH
        });
        cancel.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.UpdateExcelPopup.EVENT_CANCEL);
        });
        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            items: {
                left: [cancel],
                right: [confirm]
            }
        })
    },

    _createFieldsTable: function () {
        return BI.createWidget({
            type: "bi.update_excel_field_table",
            fields: this.model.getFields()
        });
    },

    _refreshAfterUpload: function () {
        this.excelName.setText(this.model.getExcelName());
        this.updateDate.setText(this.model.getUpdateDate());
        BI.Msg.toast(BI.i18nText("BI-Excel_Pass_Verification"), "success");
    },

    _destroyMask: function () {
        if (this.mask) {
            this.mask.destroy();
        }
    },

    getExcelFullName: function () {
        return this.model.getFullFileName();
    },

    getIsAllowUpdate: function () {
        return this.model.getValidation();
    },

    populate: function () {
        this._createPopup();
        this._destroyMask();
    }
});
BI.UpdateExcelPopup.EVENT_CONFIRM = "BI.UpdateExcelPopup.EVENT_CONFIRM";
BI.UpdateExcelPopup.EVENT_CANCEL = "BI.UpdateExcelPopup.EVENT_CANCEL";
$.shortcut("bi.update_excel_popup", BI.UpdateExcelPopup);