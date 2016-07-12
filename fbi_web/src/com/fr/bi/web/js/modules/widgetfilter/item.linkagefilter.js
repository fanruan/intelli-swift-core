/**
 * Created by Young's on 2016/4/7.
 */
BI.LinkageFilterItem = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.LinkageFilterItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-linkage-filter-item"
        })
    },

    _init: function(){
        BI.LinkageFilterItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var tId = o.tId, linkFilter = o.filter;
        var wId = BI.Utils.getWidgetIDByDimensionID(tId);
        var items = [];
        BI.each(linkFilter, function(i, value){
            items.push(self._createSingleLinkageFilter(value.dId, value.value[0]));
        });
        var wrapper = BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [{
                type: "bi.center_adapt",
                cls: BI.Utils.getWidgetIconClsByWidgetId(wId),
                items: [{
                    type: "bi.icon",
                    width: 20,
                    height: 20
                }],
                width: 20,
                height: 30
            }, {
                type: "bi.label",
                text: BI.Utils.getWidgetNameByID(wId),
                height: 30
            }],
            hgap: 5,
            vgap: 5
        });
        wrapper.addItems(items);
    },

    _formatDate: function (d) {
        if (BI.isNull(d) || !BI.isNumeric(d)) {
            return d || "";
        }
        var date = new Date(BI.parseInt(d));
        return date.print("%Y-%X-%d")
    },

    _createSingleLinkageFilter: function(dId, value){
        var tId = this.options.tId;
        var onRemoveFilter = this.options.onRemoveFilter;
        var text = value;
        //日期需要format
        if (BI.Utils.getFieldTypeByDimensionID(dId) === BICst.COLUMN.DATE &&
            BI.Utils.getDimensionGroupByID(dId).type === BICst.GROUP.YMD) {
            text = this._formatDate(text);
        }
        var removeButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-ha-font",
            width: 20,
            height: 30
        });
        removeButton.on(BI.IconButton.EVENT_CHANGE, function(){
            onRemoveFilter(tId, dId);
        });
        return {
            type: "bi.left",
            cls: "single-filter",
            items: [{
                type: "bi.label",
                text: BI.Utils.getDimensionNameByID(dId) + "=" + text,
                height: 30
            }, removeButton],
            hgap: 2
        }
    }
});
BI.LinkageFilterItem.EVENT_REMOVE_FILTER = "EVENT_REMOVE_FILTER";
$.shortcut("bi.linkage_filter_item", BI.LinkageFilterItem);