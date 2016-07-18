/**
 * Created by Young's on 2016/3/14.
 */
BI.ExcelUpload = BI.inherit(BI.Widget, {

    constants: {
        EXCEL_UPLOAD_NORTH_HEIGHT: 40,
        EXCEL_UPLOAD_SOUTH_HEIGHT: 60,
        EXCEL_UPLOAD_WEST_WIDTH: 580,
        EXCEL_UPLOAD_BUTTON_HEIGHT: 28,
        EXCEL_UPLOAD_BUTTON_GAP: 20,
        EXCEL_UPLOAD_NAME_WIDTH: 50,
        EXCEL_UPLOAD_GAP: 20,
        EXCEL_UPLOAD_PANE_GAP: 10,
        EXCEL_UPLOAD_EMPTY_TIP_HEIGHT: 200,
        EXCEL_UPLOAD_TOOL_WRAPPER: 160,
        EXCEL_UPLOAD_BUTTON_WIDTH: 110,
        PREVIEW_EMPTY: 0,
        PREVIEW_PANE: 1
    },

    _defaultConfig: function(){
        return BI.extend(BI.ExcelUpload.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-excel-upload"
        })
    },

    _init: function(){
        BI.ExcelUpload.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.ExcelUploadModel({
            fullFileName: o.full_file_name
        });
        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                west: {
                    el: this._createWest(),
                    width: this.constants.EXCEL_UPLOAD_WEST_WIDTH
                },
                center: this._createCenter(),
                south: {
                    el: this._createSouth(),
                    height: this.constants.EXCEL_UPLOAD_SOUTH_HEIGHT
                }
            }
        });
        this.model.initData(function(){
            self._refreshAfterUpload();
        })
    },

    _createWest: function(){
        var self = this;
        this.excelName = BI.createWidget({
            type: "bi.label",
            cls: "excel-name",
            height: this.constants.EXCEL_UPLOAD_NORTH_HEIGHT
        });
        this.uploadButton = BI.createWidget({
            type: "bi.upload_excel_button",
            text: BI.i18nText("BI-Upload_Data"),
            width: this.constants.EXCEL_UPLOAD_BUTTON_WIDTH,
            height: this.constants.EXCEL_UPLOAD_BUTTON_HEIGHT
        });
        this.uploadButton.on(BI.UploadExcelButton.EVENT_AFTER_UPLOAD, function(files){
            self.model.setFile(files[files.length - 1], function(){
                self._refreshAfterUpload();
            });
        });
        var tip = BI.createWidget({
            type: "bi.excel_tip_combo"
        });
        this.excelFieldsWrapper = BI.createWidget({
            type: "bi.absolute"
        });
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: {
                    type: "bi.vtape",
                    cls: "upload-excel-pane",
                    items: [{
                        el: {
                            type: "bi.left",
                            cls: "upload-north",
                            items: [{
                                type: "bi.label",
                                text: BI.i18nText("BI-Excelset_Setting"),
                                height: this.constants.EXCEL_UPLOAD_NORTH_HEIGHT
                            }]
                        },
                        height: this.constants.EXCEL_UPLOAD_NORTH_HEIGHT
                    }, {
                        el: {
                            type: "bi.left_right_vertical_adapt",
                            cls: "upload-tool",
                            items: {
                                left: [{
                                    type: "bi.label",
                                    cls: "upload-info",
                                    text: BI.i18nText("BI-Upload_Information"),
                                    height: this.constants.EXCEL_UPLOAD_NORTH_HEIGHT
                                }, this.excelName],
                                right: [{
                                    type: "bi.center_adapt",
                                    items: [this.uploadButton, tip],
                                    width: this.constants.EXCEL_UPLOAD_TOOL_WRAPPER,
                                    height: this.constants.EXCEL_UPLOAD_NORTH_HEIGHT
                                }]
                            }
                        },
                        height: this.constants.EXCEL_UPLOAD_NORTH_HEIGHT
                    }, {
                        el: this.excelFieldsWrapper,
                        height: "fill"
                    }],
                    hgap: this.constants.EXCEL_UPLOAD_PANE_GAP
                },
                top: this.constants.EXCEL_UPLOAD_GAP,
                left: this.constants.EXCEL_UPLOAD_GAP,
                right: this.constants.EXCEL_UPLOAD_GAP,
                bottom: 0
            }]
        })
    },

    _createCenter: function(){
        var self = this;
        this.previewWrapper = BI.createWidget({
            type: "bi.preview_table",
            rowSize: 25
        });
        this.previewTab = BI.createWidget({
            type: "bi.tab",
            direction: "custom",
            tab: {},
            cardCreator: function(v){
                switch (v){
                    case self.constants.PREVIEW_EMPTY:
                        return {
                            type: "bi.label",
                            cls: "preview-empty-tip",
                            text: BI.i18nText("BI-Please_Upload_Excel_Data"),
                            textAlign: "center",
                            height: self.constants.EXCEL_UPLOAD_EMPTY_TIP_HEIGHT
                        };
                    case self.constants.PREVIEW_PANE:
                        return self.previewWrapper;
                }
            }
        });
        this.previewTab.setSelect(this.constants.PREVIEW_EMPTY);
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: {
                    type: "bi.vtape",
                    cls: "upload-excel-preview",
                    items: [{
                        el: {
                            type: "bi.left",
                            cls: "preview-north",
                            items: [{
                                type: "bi.label",
                                text: BI.i18nText("BI-Table_Data"),
                                height: this.constants.EXCEL_UPLOAD_NORTH_HEIGHT
                            }]
                        },
                        height: this.constants.EXCEL_UPLOAD_NORTH_HEIGHT
                    }, {
                        el: this.previewTab,
                        height: "fill"
                    }],
                    hgap: this.constants.EXCEL_UPLOAD_GAP
                },
                top: this.constants.EXCEL_UPLOAD_GAP,
                left: 0,
                bottom: 0,
                right: this.constants.EXCEL_UPLOAD_GAP
            }]
        })
    },

    _createSouth: function(){
        var self = this;
        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "excel-upload-south",
            items: {
                left: [{
                    type: "bi.button",
                    level: "ignore",
                    text: BI.i18nText("BI-Cancel"),
                    height: this.constants.EXCEL_UPLOAD_BUTTON_HEIGHT,
                    handler: function () {
                        self.fireEvent(BI.ExcelUpload.EVENT_CANCEL);
                    }
                }],
                right: [{
                    type: "bi.button",
                    level: "common",
                    text: BI.i18nText("BI-Save"),
                    title: BI.i18nText("BI-Save"),
                    height: this.constants.EXCEL_UPLOAD_BUTTON_HEIGHT,
                    handler: function(){
                        self.fireEvent(BI.ExcelUpload.EVENT_SAVE, self.getValue());
                    }
                }]
            },
            lhgap: this.constants.EXCEL_UPLOAD_BUTTON_GAP,
            rhgap: this.constants.EXCEL_UPLOAD_BUTTON_GAP
        })
    },

    _refreshAfterUpload: function(){
        var self = this;
        this.excelName.setText(this.model.getFileName());
        this.excelFieldsWrapper.empty();
        if(BI.isEmptyArray(this.model.getFields())) {
            this.previewTab.setSelect(this.constants.PREVIEW_EMPTY);
            return;
        }
        this.uploadButton.setText(BI.i18nText("BI-Excel_Reupload"));
        var excelFieldSet = BI.createWidget({
            type: "bi.excel_field_set",
            fields: this.model.getFields(),
            isNewArray: this.model.getIsNewArray()
        });
        excelFieldSet.on(BI.ExcelFieldSet.EVENT_CHANGE, function(data){
            self.model.setFieldType(data);
        });
        this.excelFieldsWrapper.addItem({
            el: excelFieldSet,
            top: 0,
            left: 0,
            right: 0,
            bottom: 0
        });
        this.previewTab.setSelect(this.constants.PREVIEW_PANE);
        this.previewWrapper.populate(this.model.getPreviewItems(), this.model.getPreviewHeader());
    },

    //获取excel信息
    getValue: function(){
        return {
            connection_name: BICst.CONNECTION.EXCEL_CONNECTION,
            table_name: this.model.getTableName(),
            full_file_name: this.model.getFullFileName(),
            fields: this.model.getFields()
        }
    }
});
BI.ExcelUpload.EVENT_CANCEL = "EVENT_CANCEL";
BI.ExcelUpload.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.excel_upload", BI.ExcelUpload);
