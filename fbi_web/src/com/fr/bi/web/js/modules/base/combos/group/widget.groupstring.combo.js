/**
 * @class BI.GroupStringCombo
 * @extend BI.Widget
 */
BI.GroupStringCombo = BI.inherit(BI.Widget, {

    _defaultItems:function(){
        return [
            [{
                text: BI.i18nText("BI-Same_Value_A_Group"),
                value: BICst.STATISTICS_GROUP_STRING_COMBO.GROUP_BY_VALUE,
                cls: "dot-ha-font"
            }, {
                text: BI.i18nText("BI-Custom_Grouping_Dot"),
                value: BICst.STATISTICS_GROUP_STRING_COMBO.GROUP_BY_CUSTOM,
                cls: "dot-ha-font"
            }],
            [{
                text: BI.i18nText("BI-Rename"),
                value: BICst.STATISTICS_GROUP_STRING_COMBO.RENAME
            },{
                text: BI.i18nText("BI-Remove"),
                value: BICst.STATISTICS_GROUP_STRING_COMBO.DELETE
            }]
        ];
    },

    _defaultConfig: function(){
        return BI.extend(BI.GroupStringCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-group-string-combo"
        })
    },

    _init: function(){
        BI.GroupStringCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            stopPropagation: true,
            height: 25,
            iconCls: "detail-dimension-set-font",
            items:this._defaultItems()
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function(v){
            self.fireEvent(BI.GroupStringCombo.EVENT_CHANGE, v);
        });

        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW, function(){
            var selectedValue = self._createValueByGroup(o.dimension.group);
            this.setValue([selectedValue]);
        });
    },

    _assertGroup:function(val){
        val || (val = {});
        val.type || (val.type = BICst.GROUP.ID_GROUP);
        return val;
    },

    _createValueByGroup: function(group){
        group = this._assertGroup(group);

        var groupValue = {};

        switch(group.type){
            case BICst.GROUP.ID_GROUP:
                groupValue.value = BICst.STATISTICS_GROUP_STRING_COMBO.GROUP_BY_VALUE;
                break;
            case BICst.GROUP.CUSTOM_GROUP:
                groupValue.value = BICst.STATISTICS_GROUP_STRING_COMBO.GROUP_BY_CUSTOM;
                break;
        }
        return groupValue;
    },

    getValue:function(){
        return this.combo.getValue();
    }
});

BI.GroupStringCombo.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.group_string_combo", BI.GroupStringCombo);