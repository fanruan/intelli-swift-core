/**
 * Created by Young's on 2016/3/14.
 */
BI.ETLExcel = BI.inherit(BI.ETL, {

    _constant: {
        EXCEL_LAYER: "__excel_layer__"
    },

    _init: function(){
        BI.ETLExcel.superclass._init.apply(this, arguments);
    },

    _populate: function(){
        BI.ETLExcel.superclass._populate.apply(this, arguments);
        var self = this;
        var allTables = this.model.getAllTables();
        if(allTables.length !== 1){
            return;
        }
        var finalTable = allTables[0][0];
        if(finalTable.connection_name !== BICst.CONNECTION.EXCEL_CONNECTION){
            return;
        }
        var reUploadExcel = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Update_Excel"),
            width: 110,
            height: 26
        });
        reUploadExcel.on(BI.Button.EVENT_CHANGE, function(){
            BI.Layers.remove(self._constant.EXCEL_LAYER);
            var excelUpload = BI.createWidget({
                type: "bi.excel_upload",
                element: BI.Layers.create(self._constant.EXCEL_LAYER),
                full_file_name: finalTable.full_file_name
            });
            BI.Layers.show(self._constant.EXCEL_LAYER);
            excelUpload.on(BI.ExcelUpload.EVENT_CANCEL, function(){
                BI.Layers.remove(self._constant.EXCEL_LAYER);
            });
            excelUpload.on(BI.ExcelUpload.EVENT_SAVE, function(data){
                BI.Layers.remove(self._constant.EXCEL_LAYER);
                if(BI.isNull(data.table_name) || data.table_name === "") {
                    data.table_name = finalTable.table_name;
                }
                self.model.setFields(data.fields);
                self.model.saveTableById(finalTable.id, data);
                self._populate();
            });
        });
        this.tableNameWrapper.addItem({
            el: reUploadExcel,
            top: 0,
            right: 0
        });
    }
});
$.shortcut("bi.etl_excel", BI.ETLExcel);