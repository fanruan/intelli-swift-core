/**
 * Created by 小灰灰 on 2016/4/6.
 */
BI.ETLGroupSortableList = BI.inherit(BI.Widget, {
    _init: function () {
        BI.ETLGroupSortableList.superclass._init.apply(this, arguments);
        var self = this;
        self.list = BI.createWidget({
            type : 'bi.vertical',
            element : self.element
        });
        self.list.element.sortable( {
            containment: self.list.element,
            items: ".bi-multi-select-item",
            cursor: "drag",
            tolerance: "pointer",
            placeholder: {
                element: function ($currentItem) {
                    var holder = BI.createWidget({
                        type: "bi.label",
                        cls: "ui-sortable-place-holder",
                        height: 20
                    });
                    return holder.element;
                },
                update: function () {

                }
            },
            start: function (event, ui) {
            },
            stop: function (event, ui) {
            },
            over: function (event, ui) {

            }
        });
    },

    populate: function (items) {
        var self = this;
        BI.each(items, function (i, item) {
            self.list.addItem(BI.createWidget({
                type : 'bi.multi_select_item',
                text : item.text
            }))
        })
    },

    getValue : function () {
        
    },
    
    setValue : function () {
        
    }

});
BI.ETLGroupSortableList.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.etl_group_sortable_list", BI.ETLGroupSortableList);