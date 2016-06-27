/**
 * Created by roy on 16/6/25.
 */
BI.SortFilterDetailCombo = BI.inherit(BI.Widget, {
    constant: {
        icon_asc: "table-ascending-no-filter-font",
        icon_des: "table-descending-no-filter-font",
        icon_asc_filter: "table-ascending-filter-font",
        icon_des_filter: "table-descending-filter-font",
        icon_filter: "table-no-sort-filter-font",
        icon_no_sort_no_filter: "table-no-sort-no-filter-font"
    },

    _defaultConfig: function () {
        return BI.extend(BI.SortFilterDetailCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-sort-filter-combo"
        })
    },

    _init: function () {
        BI.SortFilterDetailCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var dId = o.dId;

        var trigger = BI.createWidget({
            type: "bi.icon_change_button",
            forceNotSelected: true,
            height: 25,
            width: 25
        });
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            el: trigger,
            items: [
                [{
                    text: BI.i18nText("BI-Ascend"),
                    value: BICst.SORT.ASC,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Descend"),
                    value: BICst.SORT.DESC,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Unsorted"),
                    value: BICst.SORT.NONE,
                    cls: "dot-e-font"
                }],
                [{
                    text: BI.i18nText("BI-Filter"),
                    value: BI.SortFilterDetailCombo.FILTER_ITEM,
                    cls: "dot-e-font"
                }]
            ]
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.SortFilterDetailCombo.EVENT_CHANGE, arguments);
        });
        var targetFilters = BI.Utils.getWidgetFilterValueByID(BI.Utils.getWidgetIDByDimensionID(dId));
        var sort = BI.Utils.getDimensionSortByID(dId), filter = targetFilters[dId] || null;
        var value = [], triggerIcon = this.constant.icon_asc;
        var sortType = sort.target_id === dId ? sort.type : BICst.SORT.NONE;
        switch (sortType) {
            case BICst.SORT.ASC:
                triggerIcon = BI.isNotNull(filter) ? this.constant.icon_asc_filter : this.constant.icon_asc;
                break;
            case BICst.SORT.DESC:
                triggerIcon = BI.isNotNull(filter) ? this.constant.icon_des_filter : this.constant.icon_des;
                break;
            default :
                triggerIcon = BI.isNotNull(filter) ? this.constant.icon_filter : this.constant.icon_no_sort_no_filter;
                break;
        }
        value.push({value: sortType});
        if (BI.isNotNull(filter)) {
            value.push({value: BI.SortFilterDetailCombo.FILTER_ITEM});
        }
        trigger.setIcon(triggerIcon);
        this.combo.setValue(value);
    },

    setValue: function (v) {
        this.combo.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue();
    }
});
BI.extend(BI.SortFilterDetailCombo, {
    FILTER_ITEM: -1
});
BI.SortFilterDetailCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.sort_filter_detail_combo", BI.SortFilterDetailCombo);