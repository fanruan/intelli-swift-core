/**
 * Created by zcf on 2017/1/9.
 */
BI.UpdateExcelPopup = BI.inherit(BI.Widget, {
    _constant: {
        LABEL_HEIGHT: 30,
        LABEL_WIDTH: 80
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
        var self = this, o = this.options;

        if (!this.model) {
            this.model = new BI.UpdateExcelModel({
                tableId: o.tableId
            });
        }

        if (!this.popup) {
            var updateExcelBar = this._createUpdateExcelBar();
            var tableFieldMassage = this._createTableFieldsMassage();
            var bottomButton = this._createBottomButton();

            this.popup = BI.createWidget({
                type: "bi.vtape",
                element: this.element,
                items: [{
                    el: updateExcelBar,
                    height: 60
                }, {
                    el: tableFieldMassage,
                    height: "fill"
                }, {
                    el: bottomButton,
                    height: 40
                }]
            })

        }
    },

    _createUpdateExcelBar: function () {
        var c = this._constant;

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

        var excelMessage = BI.createWidget({
            type: "bi.vertical",
            items: [nameWrapper, dateWrapper]
        });

        var updateButton = BI.createWidget({
            type: "bi.upload_excel_button",
            text: BI.i18nText("BI-Excel_Reupload"),
            width: 110,
            height: c.LABEL_HEIGHT
        });
        updateButton.on(BI.UploadExcelButton.EVENT_AFTER_UPLOAD, function (files) {
            self.model.setFile(files[files.length - 1], function (firstRowHasMerge) {
                if (firstRowHasMerge === true) {
                    // BI.Msg.alert(BI.i18nText("BI-Prompt"), BI.i18nText("BI-Excel_First_Row_Has_Merge_Cell"));
                } else {
                    self._refreshAfterUpload();
                }
            });
        });

        var helpButton = BI.createWidget({
            type: "bi.excel_tip_combo"
        });

        return BI.createWidget({
            type: "bi.htape",
            items: [{
                width: "fill",
                el: excelMessage
            }, {
                width: 110,
                el: updateButton
            }, {
                width: 50,
                el: helpButton
            }]
        })
    },

    _createTableFieldsMassage: function () {
        var label = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Excel_Table_Fields"),
            textAlign: "left",
            height: 30
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
                height: 30
            }, {
                el: this.fieldsTableWrapper,
                height: "fill"
            }]
        })
    },

    _createBottomButton: function () {
        var self = this;

        var confirm = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-OK"),
            height: 30,
            width: 90
        });
        confirm.on(BI.Button.EVENT_CHANGE, function () {

        });
        var cancel = BI.createWidget({
            type: "bi.button",
            level: 'ignore',
            text: BI.i18nText("BI-Cancel"),
            height: 30,
            width: 90
        });
        cancel.on(BI.Button.EVENT_CHANGE, function () {

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

    },

    populate: function () {
        this._createPopup();
    }
});
$.shortcut("bi.update_excel_popup", BI.UpdateExcelPopup);