/**
 * Created by Young's on 2016/4/7.
 */
BI.DrillFilterItem = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.DrillFilterItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-drill-filter-item"
        })
    },

    _init: function(){
        BI.DrillFilterItem.superclass._init.apply(this, arguments);
        this.wrapper = BI.createWidget({
            type: "bi.left",
            element: this.element,
            hgap: 5,
            vgap: 5
        })
    },

    _isEmptyDrillById: function(wId){
        var drills = BI.Utils.getDrillByID(wId);
        var isEmpty = true;
        BI.some(drills, function(id, drill){
            if( BI.isNotEmptyArray(drill)) {
                isEmpty = false;
                return true;
            }
        });
        return isEmpty;
    },

    _parseClicked4Group: function (dId, v) {
        var group = BI.Utils.getDimensionGroupByID(dId);
        var fieldType = BI.Utils.getFieldTypeByDimensionID(dId);
        var clicked = v;

        if (BI.isNotNull(group)) {
            if(fieldType === BICst.COLUMN.STRING) {
                var details = group.details,
                    ungroup2Other = group.ungroup2Other,
                    ungroup2OtherName = group.ungroup2OtherName;
                if (ungroup2Other === BICst.CUSTOM_GROUP.UNGROUP2OTHER.SELECTED &&
                    v === BICst.UNGROUP_TO_OTHER) {
                    clicked = ungroup2OtherName;
                }
                BI.some(details, function (i, detail) {
                    if (detail.id === v) {
                        clicked = detail.value;
                        return true;
                    }
                });
            } else if(fieldType === BICst.COLUMN.NUMBER) {
                var groupValue = group.group_value, groupType = group.type;
                if(groupType === BICst.GROUP.CUSTOM_NUMBER_GROUP) {
                    var groupNodes = groupValue.group_nodes, useOther = groupValue.use_other;
                    if(v === BICst.UNGROUP_TO_OTHER) {
                        clicked = useOther;
                    }
                    BI.some(groupNodes, function (i, node) {
                        if(node.id === v) {
                            clicked = node.group_name;
                            return true;
                        }
                    });
                }
            }
        }
        return clicked;
    },

    _formatDate: function (d) {
        if (BI.isNull(d) || !BI.isNumeric(d)) {
            return d || "";
        }
        var date = new Date(BI.parseInt(d));
        return date.print("%Y-%X-%d")
    },

    _parseValue: function(dId, value) {
        var text = this._parseClicked4Group(dId, value);
        //日期需要format
        if (BI.Utils.getFieldTypeByDimensionID(dId) === BICst.COLUMN.DATE &&
            BI.Utils.getDimensionGroupByID(dId).type === BICst.GROUP.YMD) {
            text = this._formatDate(text);
        }
        return text;
    },

    populate: function(){
        var self = this, wId = this.options.wId;
        this.wrapper.empty();
        if(this._isEmptyDrillById(wId)) {
            return;
        }
        var drills = BI.Utils.getDrillByID(wId);
        this.wrapper.addItem({
            type: "bi.label",
            text: BI.i18nText("BI-Drill"),
            height: 30
        });
        BI.each(drills, function(dId, values){          //第一层放的是下钻开始的dId
            BI.each(values, function(i, vs){            //第二层放的是下钻的所有节点，按数组顺序
                BI.each(vs.values, function(i, value){         //第三层放的是某层下钻节点的过滤值（用数组存是因为，钻取的时候需要带上前面所有的值）
                    self.wrapper.addItem({
                        type: "bi.label",
                        cls: "drill-filter",
                        text: BI.Utils.getDimensionNameByID(value.dId) + "=" + self._parseValue(value.dId, value.value[0]),
                        height: 30,
                        hgap: 5
                    });
                });
            })
        })
    }
});
$.shortcut("bi.drill_filter_item", BI.DrillFilterItem);