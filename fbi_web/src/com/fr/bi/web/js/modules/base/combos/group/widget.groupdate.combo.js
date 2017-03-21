/**
 * @class BI.GroupDateCombo
 * @extend BI.Widget
 */
BI.GroupDateCombo = BI.inherit(BI.Widget, {

    _defaultItems:function(){
        return [
            [{
                text: BI.i18nText("BI-Year_Fen"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.YEAR,
                cls: "dot-ha-font"
            }, {
                text: BI.i18nText("BI-Basic_Quarter"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.QUARTER,
                cls: "dot-ha-font"
            }, {
                text: BI.i18nText("BI-Month_Fen"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.MONTH,
                cls: "dot-ha-font"
            }, {
                text: BI.i18nText("BI-Week_XingQi"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.WEEK,
                cls: "dot-ha-font"
            },{
                text: BI.i18nText("BI-Day_Ri"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.D,
                cls: "dot-ha-font"
            },{
                el: {
                    text: BI.i18nText("BI-More_Group"),
                    value: BICst.STATISTICS_GROUP_DATE_COMBO.MORE_GROUP,
                    cls: "dot-ha-font"
                },
                children: [{
                    text: BI.i18nText("BI-Week_Count"),
                    value: BICst.STATISTICS_GROUP_DATE_COMBO.WEEK_COUNT,
                    cls: "dot-ha-font"
                }, {
                    text: BI.i18nText("BI-Hour_Sin"),
                    value: BICst.STATISTICS_GROUP_DATE_COMBO.HOUR,
                    cls: "dot-ha-font"
                }, {
                    text: BI.i18nText("BI-Basic_Minute"),
                    value: BICst.STATISTICS_GROUP_DATE_COMBO.MINUTE,
                    cls: "dot-ha-font"
                }, {
                    text: BI.i18nText("BI-Basic_Seconds"),
                    value: BICst.STATISTICS_GROUP_DATE_COMBO.SECOND,
                    cls: "dot-ha-font"
                },{
                    text: BI.i18nText("BI-Year_Quarter"),
                    value: BICst.STATISTICS_GROUP_DATE_COMBO.YS,
                    cls: "dot-ha-font"
                }, {
                    text: BI.i18nText("BI-Year_Month"),
                    value: BICst.STATISTICS_GROUP_DATE_COMBO.YM,
                    cls: "dot-ha-font"
                }, {
                    text: BI.i18nText("BI-Year_Week"),
                    value: BICst.STATISTICS_GROUP_DATE_COMBO.YW,
                    cls: "dot-ha-font"
                }, {
                    text: BI.i18nText("BI-YMD_Date"),
                    value: BICst.STATISTICS_GROUP_DATE_COMBO.DATE,
                    cls: "dot-ha-font"
                }, {
                    text: BI.i18nText("BI-Basic_YMDH"),
                    value: BICst.STATISTICS_GROUP_DATE_COMBO.YMDH,
                    cls: "dot-ha-font"
                }, {
                    text: BI.i18nText("BI-Basic_YMDHM"),
                    value: BICst.STATISTICS_GROUP_DATE_COMBO.YMDHM,
                    cls: "dot-ha-font"
                }, {
                    text: BI.i18nText("BI-Detail_Date"),
                    value: BICst.STATISTICS_GROUP_DATE_COMBO.YMDHMS,
                    cls: "dot-ha-font"
                }]
            }],
            [{
                text: BI.i18nText("BI-Basic_Rename"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.RENAME,
                cls: "widget-combo-rename-edit-font"

            }, {
                text: BI.i18nText("BI-Basic_Remove"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.DELETE,
                cls: "delete-h-font"
            }]
        ];
    },

    _defaultConfig: function(){
        return BI.extend(BI.GroupDateCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-group-date-combo"
        })
    },

    _init: function(){
        BI.GroupDateCombo.superclass._init.apply(this, arguments);

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
            self.fireEvent(BI.GroupDateCombo.EVENT_CHANGE, v);
        });

        this.combo.on(BI.DownListCombo.EVENT_SON_VALUE_CHANGE,function(v, father){
            self.fireEvent(BI.GroupDateCombo.EVENT_CHANGE, v, father);
        });

        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW, function(){
            var selectedValue = self._createValueByGroup(o.dimension.group);
            this.setValue([selectedValue]);
        });
    },

    _assertGroup:function(val){
        val || (val = {});
        val.type || (val.type = BICst.GROUP.Y);
        return val;
    },

    _createValueByGroup: function(group){
        group = this._assertGroup(group);

        var groupValue = {};

        switch(group.type){
            case BICst.GROUP.Y:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.YEAR;
                break;
            case BICst.GROUP.S:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.QUARTER;
                break;
            case BICst.GROUP.M:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.MONTH;
                break;
            case BICst.GROUP.W:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.WEEK;
                break;
            case BICst.GROUP.D:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.D;
                break;
            case BICst.GROUP.YMD:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.MORE_GROUP;
                groupValue.childValue = BICst.STATISTICS_GROUP_DATE_COMBO.DATE;
                break;
            case BICst.GROUP.WEEK_COUNT:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.MORE_GROUP;
                groupValue.childValue = BICst.STATISTICS_GROUP_DATE_COMBO.WEEK_COUNT;
                break;
            case BICst.GROUP.HOUR:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.MORE_GROUP;
                groupValue.childValue = BICst.STATISTICS_GROUP_DATE_COMBO.HOUR;
                break;
            case BICst.GROUP.MINUTE:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.MORE_GROUP;
                groupValue.childValue = BICst.STATISTICS_GROUP_DATE_COMBO.MINUTE;
                break;
            case BICst.GROUP.SECOND:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.MORE_GROUP;
                groupValue.childValue = BICst.STATISTICS_GROUP_DATE_COMBO.SECOND;
                break;
            case BICst.GROUP.YS:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.MORE_GROUP;
                groupValue.childValue = BICst.STATISTICS_GROUP_DATE_COMBO.YS;
                break;
            case BICst.GROUP.YM:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.MORE_GROUP;
                groupValue.childValue = BICst.STATISTICS_GROUP_DATE_COMBO.YM;
                break;
            case BICst.GROUP.YW:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.MORE_GROUP;
                groupValue.childValue = BICst.STATISTICS_GROUP_DATE_COMBO.YW;
                break;
            case BICst.GROUP.YMDH:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.MORE_GROUP;
                groupValue.childValue = BICst.STATISTICS_GROUP_DATE_COMBO.YMDH;
                break;
            case BICst.GROUP.YMDHM:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.MORE_GROUP;
                groupValue.childValue = BICst.STATISTICS_GROUP_DATE_COMBO.YMDHM;
                break;
            case BICst.GROUP.YMDHMS:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.MORE_GROUP;
                groupValue.childValue = BICst.STATISTICS_GROUP_DATE_COMBO.YMDHMS;
                break;
        }
        return groupValue;
    },

    getValue:function(){
        this.combo.getValue();
    }
});

BI.GroupDateCombo.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.group_date_combo", BI.GroupDateCombo);