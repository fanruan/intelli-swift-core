/**
 * @class BI.ETLTablePreviewCenter
 * @extend BI.Pane
 */
BI.ETLTablePreviewCenter = BI.inherit(BI.Pane, {
    _defaultConfig: function(){
        return BI.extend(BI.ETLTablePreviewCenter.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl-table-preview-center"
        })
    },

    _init: function(){
        BI.ETLTablePreviewCenter.superclass._init.apply(this, arguments);
        var self = this;
        var wrapper = BI.createWidget({
            type: "bi.absolute",
            cls: "etl-preview-table",
            element: this.element
        });
        this.loading();
        BI.Utils.getPreviewDataByTableAndFields(this.options.table, [], function(data){
            self.loaded();
            var item = self._createTableItems(data);
            var tableView = BI.createWidget({
                type: "bi.preview_table",
                items: item.items,
                header: item.header
            });
            wrapper.addItem({
                el: tableView,
                top: 0,
                left: 0,
                right: 0,
                bottom: 0
            });
        });
    },

    _createTableItems: function(data){
        var self = this;
        var fields = data.fields, values = data.value;
        var header = [], items = [];
        BI.each(fields, function(i, field){
            header.push({
                text: field
            });
        });
        var fieldTypes = [];
        BI.each(this.options.table.fields, function(i, fs){
            BI.each(fs, function(j, field){
                fieldTypes.push(field.field_type);
            });
        });
        //后台的数据是按照列放进去的
        BI.each(values, function(i, value){
            var isDate = fieldTypes[i] === BICst.COLUMN.DATE;
            BI.each(value, function(j, v){
                if(BI.isNotNull(items[j])){
                    items[j].push({text: isDate === true ? self._formatDate(v) : v});
                } else {
                    items.push([{text: isDate === true ? self._formatDate(v) : v}]);
                }
            });
        });
        return {
            header: [header],
            items: items
        }
    },

    _formatDate: function(d){
        var date = new Date(BI.parseInt(d));
        return date.print("%Y/%X/%d %H:%M:%S")
    }
});
$.shortcut("bi.etl_table_preview_center", BI.ETLTablePreviewCenter);