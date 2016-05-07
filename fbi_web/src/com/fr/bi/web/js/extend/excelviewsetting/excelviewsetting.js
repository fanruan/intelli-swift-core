/**
 * ExcelViewSetting
 *
 * Created by GUY on 2016/4/7.
 * @class BI.ExcelViewSetting
 * @extends BI.Widget
 */
BI.ExcelViewSetting = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ExcelViewSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-excel-view-setting"
        });
    },

    _init: function () {
        BI.ExcelViewSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.ExcelViewSettingModel({
            table: o.table,
            view: o.view
        });
        var tree = this._createSettingTree();
        var excel = this._createExcel();
        var clear = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Clear_Configuration"),
            height: 28,
            handler: function () {
                self.model.clearRowCol();
                self.populate();
            }
        });
        var cancel = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Cancel"),
            height: 28
        });
        cancel.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.ExcelViewSetting.EVENT_CANCEL);
        });

        var save = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Save"),
            height: 28
        });
        save.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.ExcelViewSetting.EVENT_SAVE, {
                name: self.model.getExcelName(),
                excel: self.model.getExcelData(),
                positions: self.tree.getMarkedFields()
            });
        });

        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                west: {
                    el: {
                        type: "bi.absolute",
                        items: [{
                            el: tree,
                            left: 20,
                            top: 20,
                            right: 20,
                            bottom: 0
                        }]
                    },
                    width: 280
                },
                center: {
                    el: {
                        type: "bi.absolute",
                        items: [{
                            el: excel,
                            top: 20,
                            left: 0,
                            right: 20,
                            bottom: 0
                        }]
                    }
                },
                south: {
                    el: {
                        type: "bi.absolute",
                        items: [{
                            el: {
                                type: "bi.left_right_vertical_adapt",
                                lrgap: 10,
                                rlgap: 10,
                                items: {
                                    left: [cancel],
                                    right: [clear, save]
                                }
                            },
                            left: 20,
                            top: 0,
                            right: 20,
                            bottom: 0
                        }]
                    },
                    height: 60
                }
            }
        });
    },

    _createSettingTree: function () {
        var self = this;
        this.tree = BI.createWidget({
            type: "bi.excel_view_setting_tree",
            tables: this.model.getTables(),
            clearOneCell: function(fieldId) {
                self.model.clearOneCell(fieldId);
                self.populate();
            }
        });

        this.tree.setValue(this.model.getNextField());

        return BI.createWidget({
            type: "bi.vtape",
            cls: "excel-view-setting-tree-pane",
            items: [{
                type: "bi.label",
                cls: "excel-view-setting-tree-title",
                textAlign: BI.HorizontalAlign.Left,
                hgap: 10,
                height: 50,
                text: BI.i18nText("BI-Set_Excel")
            }, {
                type: "bi.absolute",
                items: [{
                    el: this.tree,
                    left: 0,
                    right: 0,
                    top: 10,
                    bottom: 10
                }]
            }]
        });
    },

    _createExcel: function () {
        var self = this;
        var toolbar = this._createExcelToolbar();
        this.excel = BI.createWidget({
            type: "bi.excel_view_setting_excel",
            all_fields: this.model.getAllFields()
        });
        this.excel.populate(this.model.getExcelData());
        this.excel.setValue(this.model.getPositions());

        this.excel.on(BI.ExcelViewSettingExcel.EVENT_CHANGE, function (row, col) {
            var ids = self.tree.getValue();
            if (BI.isNotEmptyArray(ids)) {
                var id = ids[0];
                self.model.setRowColOnField(id, row, col);
                self.populate();
                self.tree.setValue(self.model.getNextField(id) || []);
            }
        });

        return BI.createWidget({
            type: "bi.vtape",
            cls: "excel-view-setting-excel-pane",
            items: [{
                el: toolbar,
                height: 50
            }, {
                el: {
                    type: "bi.absolute",
                    items: [{
                        el: this.excel,
                        top: 10,
                        right: 10,
                        bottom: 10,
                        left: 10
                    }]
                }
            }]
        })
    },

    _createExcelToolbar: function () {
        var self = this;
        this.uploadButton = BI.createWidget({
            type: "bi.upload_excel_button",
            text: BI.i18nText("BI-Upload_Data"),
            progressEL: this.excel,
            width: 120,
            height: 28
        });
        this.uploadButton.on(BI.UploadExcelButton.EVENT_AFTER_UPLOAD, function(files){
            self.model.setFile(files[files.length - 1], function(){
                self._refreshAfterUpload();
            });
        });

        this.excelName = BI.createWidget({
            type: "bi.label",
            height: 30,
            text: this.model.getExcelName(),
            cls: "excel-view-setting-excel-toolbar-title"
        });

        return BI.createWidget({
            type: "bi.absolute",
            cls: "excel-view-setting-excel-toolbar",
            items: [{
                el: {
                    type: "bi.left_right_vertical_adapt",
                    lrgap: 15,
                    rlgap: 15,
                    items: {
                        left: [{
                            type: "bi.label",
                            text: "excel" + BI.i18nText("BI-Table") + ":",
                            height: 30,
                            cls: "excel-view-setting-excel-toolbar-title"
                        }, this.excelName, this.uploadButton]
                    }
                },
                top: 0,
                bottom: 0,
                left: 10,
                right: 10
            }]
        })
    },

    _refreshAfterUpload: function(){
        this.excelName.setText(this.model.getExcelName());
        this.uploadButton.setText(BI.i18nText("BI-Excel_Reupload"));
        this.excel.populate(this.model.getExcelData());
        this.populate();
    },

    populate: function(){
        this.tree.populate(this.model.getTables());
        this.excel.setValue(this.model.getPositions());
    },

    getValue: function () {
        return this.model.getTables();
    }
});
BI.ExcelViewSetting.EVENT_CANCEL = "EVENT_CANCEL";
BI.ExcelViewSetting.EVENT_SAVE = "EVENT_SAVE";
$.shortcut('bi.excel_view_setting', BI.ExcelViewSetting);