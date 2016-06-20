/**
 * created by young
 *  ά�ȡ�������ϵ����򡢽��򡢹���������
 */
BI.SortFilterDimensionCombo = BI.inherit(BI.Widget, {

    constant: {
        icon_asc: "table-ascending-no-filter-font",
        icon_des: "table-descending-no-filter-font",
        icon_asc_filter: "table-ascending-filter-font",
        icon_des_filter: "table-descending-filter-font",
        icon_filter: "table-no-sort-filter-font",
        icon_no_sort_no_filter: "table-no-sort-no-filter-font"
    },

    _defaultConfig: function () {
        return BI.extend(BI.SortFilterDimensionCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-sort-filter-combo"
        })
    },

    _init: function () {
        BI.SortFilterDimensionCombo.superclass._init.apply(this, arguments);
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
                    text: this.isFirstDimensionBydId(dId) ? BI.i18nText("BI-Ascend") : BI.i18nText("BI-Asc_Group"),
                    value: BICst.SORT.ASC,
                    cls: "dot-e-font"
                }, {
                    text: this.isFirstDimensionBydId(dId) ? BI.i18nText("BI-Descend") : BI.i18nText("BI-Des_Group"),
                    value: BICst.SORT.DESC,
                    cls: "dot-e-font"
                }],
                [{
                    text: BI.i18nText("BI-Filter_Number_Summary"),
                    value: BI.SortFilterDimensionCombo.FILTER_ITEM,
                    cls: "dot-e-font"
                }]
            ]
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.SortFilterDimensionCombo.EVENT_CHANGE, arguments);
        });
        var sort = BI.Utils.getDimensionSortByID(dId), filter = BI.Utils.getDimensionFilterValueByID(dId);
        if (BI.isEmptyObject(sort)) {
            //默认排序方式
            var sortType = BICst.SORT.ASC;
            var fieldType = BI.Utils.getFieldTypeByDimensionID(dId);
            if (fieldType === BICst.COLUMN.NUMBER) {
                sortType = BICst.SORT.CUSTOM;
            }
            sort = {type: sortType};
        }
        var value = [], triggerIcon = this.constant.icon_asc;
        switch (sort.type) {
            case BICst.SORT.ASC:
                triggerIcon = BI.isNotEmptyObject(filter) ? this.constant.icon_asc_filter : this.constant.icon_asc;
                break;
            case BICst.SORT.DESC:
                triggerIcon = BI.isNotEmptyObject(filter) ? this.constant.icon_des_filter : this.constant.icon_des;
                break;
            default :
                triggerIcon = BI.isNotEmptyObject(filter) ? this.constant.icon_filter : this.constant.icon_no_sort_no_filter;
                break;
        }
        trigger.setIcon(triggerIcon);
        value.push({value: sort.type});
        if (BI.isNotEmptyObject(filter)) {
            value.push({value: BI.SortFilterDimensionCombo.FILTER_ITEM});
        }
        this.combo.setValue(value);
    },

    /**
     * �Ƿ��ǵ�һ��ά��
     * @param dId
     */
    isFirstDimensionBydId: function (dId) {
        var wId = BI.Utils.getWidgetIDByDimensionID(dId);
        var dims = BI.Utils.getAllDimDimensionIDs(wId);
        return dims[0] === dId;
    },

    setValue: function (v) {
        this.combo.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue();
    }
});
BI.extend(BI.SortFilterDimensionCombo, {
    FILTER_ITEM: -1
});
BI.SortFilterDimensionCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.sort_filter_dimension_combo", BI.SortFilterDimensionCombo);