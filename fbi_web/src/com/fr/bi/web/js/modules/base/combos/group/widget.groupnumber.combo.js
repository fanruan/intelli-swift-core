/**
 * @class BI.GroupNumberCombo
 * @extend BI.Widget
 */
BI.GroupNumberCombo = BI.inherit(BI.Widget, {

    _defaultItems:function(){
        return [
            [{
                text: BI.i18nText("BI-Same_Value_A_Group"),
                value: BICst.STATISTICS_GROUP_NUMBER_COMBO.GROUP_BY_VALUE
            },{
                text: BI.i18nText("BI-Grouping_setting"),
                value: BICst.STATISTICS_GROUP_NUMBER_COMBO.GROUP_SETTING
            }],
            [{
                text: BI.i18nText("BI-Remove"),
                value: BICst.STATISTICS_GROUP_NUMBER_COMBO.DELETE
            }]
        ];
    },

    _defaultConfig: function(){
        return BI.extend(BI.GroupNumberCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-group-number-combo"
        })
    },

    _init: function(){
        BI.GroupNumberCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            height: 25,
            iconCls: "detail-dimension-set-font",
            items:this._defaultItems()
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function(v){
            self.fireEvent(BI.GroupNumberCombo.EVENT_CHANGE, v);
        });

        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW, function(){
            var selectedValue = self._createValueByGroup(o.dimension.group);
            this.setValue([selectedValue]);
        });
    },

    _assertGroup:function(val){
        val || (val = {});
        val.type || (val.type = BICst.GROUP.AUTO_GROUP);
        return val;
    },

    _createValueByGroup: function(group){
        group = this._assertGroup(group);

        var groupValue = {};

        switch(group.type){
            case BICst.GROUP.ID_GROUP:
                groupValue.value = BICst.STATISTICS_GROUP_NUMBER_COMBO.GROUP_BY_VALUE;
                break;
            case BICst.GROUP.AUTO_GROUP:
            case BICst.GROUP.CUSTOM_NUMBER_GROUP:
                groupValue.value = BICst.STATISTICS_GROUP_NUMBER_COMBO.GROUP_SETTING;
                break;
        }
        return groupValue;
    },

    getValue:function(){
        return this.combo.getValue();
    }
});

BI.GroupNumberCombo.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.group_number_combo", BI.GroupNumberCombo);