/**
 * Created by 小灰灰 on 2016/4/6.
 */
BI.ETLGroupSortableList = BI.inherit(BI.Widget, {
    _init: function () {
        BI.ETLGroupSortableList.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        self.list = BI.createWidget({
            type: "bi.sort_list",
            element: this.element,
            itemsCreator: function (op, callback) {
                callback(BI.createItems(o.items, {
                    type : 'bi.multi_select_item'
                }));
            },
            el: {
                chooseType: BI.Selection.Multi,
                layouts: [{
                    type: "bi.vertical",
                    scrolly: true
                }]
            },
        });
        self.list.on(BI.SortList.EVENT_CHANGE, function () {
            self.fireEvent(BI.ETLGroupSortableList.EVENT_CHANGE);
        })
    },

    getValue : function () {
        var value = [];
        var sorted = this.list.getSortedValues();
        var values = this.list.getValue();
        BI.each(sorted, function(i, item){
            if (BI.indexOf(values, item) != -1){
                value.push(item);
            }
        })
        return value;
    }

});
BI.ETLGroupSortableList.EVENT_CHANGE = "ETL_GROUP_EVENT_CHANGE";
$.shortcut("bi.etl_group_sortable_list", BI.ETLGroupSortableList);