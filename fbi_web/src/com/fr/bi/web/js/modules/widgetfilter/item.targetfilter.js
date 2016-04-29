/**
 * Created by Young's on 2016/4/7.
 */
BI.TargetFilterItem = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.TargetFilterItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-filter-item"
        })
    },

    _init: function(){
        BI.TargetFilterItem.superclass._init.apply(this, arguments);
        var o = this.options;
        var tId = o.tId, filter = o.filter;
        var relation = "", value = "";
        switch (filter.filter_type) {
            case BICst.TARGET_FILTER_NUMBER.EQUAL_TO:
                relation = BI.i18nText("BI-Equal")
                value = filter.filter_value;
                break;
            case BICst.TARGET_FILTER_NUMBER.NOT_EQUAL_TO:
                relation = BI.i18nText("BI-Not_Equal_To");
                value = filter.filter_value;
                break;
            case BICst.TARGET_FILTER_NUMBER.BELONG_VALUE:
                relation = BI.i18nText("BI-In");
                value = this._getRangeText(filter.filter_value);
                break;
            case BICst.TARGET_FILTER_NUMBER.NOT_BELONG_VALUE:
                relation = BI.i18nText("BI-Not_In");
                value = this._getRangeText(filter.filter_value);
                break;
            case BICst.TARGET_FILTER_NUMBER.IS_NULL:
                relation = BI.i18nText("BI-Is_Null");
                break;
            case BICst.TARGET_FILTER_NUMBER.NOT_NULL:
                relation = BI.i18nText("BI-Not_Null");
                break;
        }
        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [{
                type: "bi.label",
                text: BI.Utils.getDimensionNameByID(tId),
                cls: "target-name",
                height: 30
            }, {
                type: "bi.label",
                text: relation,
                height: 30,
                cls: ""
            }, {
                type: "bi.label",
                text: value,
                height: 30,
                cls: ""
            }],
            hgap: 10,
            vgap: 5
        })
    },

    _getRangeText: function( filterValue) {
        var text = "";
        var closemin = filterValue.closemin, closemax = filterValue.closemax, min = filterValue.min, max = filterValue.max;
        if(BI.isNotNull(min) && BI.isNotEmptyString(min) && BI.isNotNull(max) && BI.isNotEmptyString(max)) {
            text = min + (closemin ? "<=" : "<") + BI.i18nText("BI-Value") + (closemax ? "<=" : "<") + max;
        } else if(BI.isNotNull(min) && BI.isNotEmptyString(min)) {
            text = min + (closemin ? "<=" : "<") + BI.i18nText("BI-Value");
        } else if(BI.isNotNull(max) && BI.isNotEmptyString(max)) {
            text = BI.i18nText("BI-Value") + (closemax ? "<=" : "<") + max;
        } else {

        }
        return text;
    }
});
$.shortcut("bi.target_filter_item", BI.TargetFilterItem);