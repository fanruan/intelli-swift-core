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
        var fields = data.fields, values = data.value;
        var header = [], items = [];
        BI.each(fields, function(i, field){
            header.push({
                text: field
            });
        });
        //后台的数据是按照列放进去的
        BI.each(values, function(i, value){
            BI.each(value, function(j, v){
                if(BI.isNotNull(items[j])){
                    items[j].push({text: v});
                } else {
                    items.push([{text: v}]);
                }
            });
        });
        return {
            header: [header],
            items: items
        }
    }
});
$.shortcut("bi.etl_table_preview_center", BI.ETLTablePreviewCenter);