/**
 * Created by Young's on 2016/3/14.
 */
BI.ExcelUploadModel = BI.inherit(FR.OB, {
    _init: function(){
        BI.ExcelUploadModel.superclass._init.apply(this, arguments);
        this.fullFileName = this.options.fullFileName;
        this.file = {};
        this.fileName = "";
        this.fields = [];
        this.previewData = [];
        this.isNewArray = [];
    },

    initData: function(callback){
        var self = this;
        if(BI.isNotNull(this.fullFileName)){
            var mask = BI.createWidget({
                type: "bi.loading_mask",
                masker: BICst.BODY_ELEMENT,
                text: BI.i18nText("BI-Loading")
            });
            BI.Utils.getExcelDataByFileName(this.fullFileName, function(data){
                self.fields = data.fields;
                self.previewData = data.data;
                callback();
                mask.destroy();
            })
        }
    },

    setFile: function(file, callback){
        var self = this;
        this.file = file;
        this.fileName = file.filename;
        this.fullFileName = file.attach_id + this.fileName;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.saveFileGetExcelData(file.attach_id, function(data){
            //对比前一次fields
            var fields = data.fields;
            if(BI.isNotEmptyArray(self.fields)){
                self.isNewArray = [];
                BI.each(fields, function(i, fs){
                    BI.each(fs, function(j, field){
                        if(self._getFieldNamesArray().contains(field.field_name)){
                            self.isNewArray.push(false);
                        } else {
                            self.isNewArray.push(true);
                        }
                    });
                })
            }
            self.fields = fields;
            self.previewData = data.data;
            callback();
            mask.destroy();
        })
    },

    getTableName: function(){
        return this.fileName;
    },

    getIsNewArray: function(){
        return this.isNewArray;
    },

    getFields: function(){
        return this.fields;
    },

    getPreviewData: function(){
        return this.previewData;
    },

    getFile: function(){
        return BI.deepClone(this.file);
    },

    getFileName: function(){
        return this.file.filename;
    },

    getFullFileName: function(){
        return this.fullFileName;
    },

    getPreviewHeader: function(){
        var header = [];
        BI.each(this.fields, function(i, fs){
            BI.each(fs, function(j, field){
                header.push({
                    text: field.field_name
                })
            });
        });
        return [header];
    },

    getPreviewItems: function(){
        var items = [];
        BI.each(this.previewData, function(row, rowData){
            var rowItems = [];
            BI.each(rowData, function(i, v){
                rowItems.push({
                    text: v
                })
            });
            items.push(rowItems);
        });
        return items;
    },

    setFieldType: function(data){
        BI.each(this.fields, function(i, fs){
            BI.each(fs, function(j, field){
                if(field.field_name === data.fieldName){
                    field.field_type = data.fieldType;
                    switch (data.fieldType) {
                        case BICst.COLUMN.STRING:
                            field.class_type = BICst.CLASS.STRING;
                            break;
                        case BICst.COLUMN.DATE:
                            field.class_type = BICst.CLASS.DATE;
                            break;
                        case BICst.COLUMN.NUMBER:
                            field.class_type = BICst.CLASS.DOUBLE;
                            break;
                    }
                }
            });
        });
    },

    _getFieldNamesArray: function(){
        var fieldNamesArray = [];
        BI.each(this.fields, function(i, fs){
            BI.each(fs, function(j, field){
                fieldNamesArray.push(field.field_name);
            })
        });
        return fieldNamesArray;
    }
});