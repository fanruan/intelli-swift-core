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
                        text: BI.Utils.getDimensionNameByID(value.dId) + "=" + value.value[0],
                        height: 30,
                        hgap: 5
                    });
                });
            })
        })
    }
});
$.shortcut("bi.drill_filter_item", BI.DrillFilterItem);