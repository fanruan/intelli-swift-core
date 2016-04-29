/**
 * Created by Young's on 2016/4/26.
 */
BI.UpdatePreviewPaneModel = BI.inherit(FR.OB, {
    _init: function(){
        BI.UpdatePreviewPaneModel.superclass._init.apply(this, arguments);
        this.table = this.options.table;
    },

    getTableName: function(){
        return this.table.table_name;
    },

    getAllFieldsArray: function(){
        var fields = [];
        BI.each(this.table.fields, function(i, fs){
            BI.each(fs, function(j, field){
                fields.push(field.field_name);
            });
        });
        return fields;
    },

    getLackFields: function(fieldNames) {
        var lackFields = [];
        BI.each(this.getAllFieldsArray(), function(i, fieldName) {
            if(!fieldNames.contains(fieldName)) {
                lackFields.push(fieldName);
            }
        });
        return lackFields;
    },

    getExtraFields: function(fieldNames) {
        var extraFields = [];
        var fields = this.getAllFieldsArray();
        BI.each(fieldNames, function(i, fieldName){
            if(!fields.contains(fieldName)) {
                extraFields.push(fieldName);
            }
        });
        return extraFields;
    }
});