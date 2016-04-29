/**
 * @class BI.JoinTypeGroup
 * @extend BI.Widget
 * 合并方式组
 */
BI.JoinTypeGroup = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.JoinTypeGroup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-join-type-group"
        })
    },

    _init: function(){
        BI.JoinTypeGroup.superclass._init.apply(this, arguments);
        var self = this;
        this.group = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            items: BI.createItems(BI.JoinTypeGroup.JOIN_TYPE_GROUP, {
                type: "bi.join_type_button"
            }),
            layouts: [{
                type: "bi.left",
                hgap: 2
            }]
        });
        this.group.setValue(BICst.ETL_JOIN_STYLE.LEFT_JOIN);
        this.group.on(BI.ButtonGroup.EVENT_CHANGE, function(){
            self.fireEvent(BI.JoinTypeGroup.EVENT_CHANGE);
        })
    },

    getValue: function(){
        return this.group.getValue();
    },

    setValue: function(v){
        this.group.setValue(v);
    }
});
BI.extend(BI.JoinTypeGroup, {
    JOIN_TYPE_GROUP: [{
        iconCls: "inner-join-icon", text: BI.i18nText("BI-Inner_Join"), value: BICst.ETL_JOIN_STYLE.INNER_JOIN
    }, {
        iconCls: "left-join-icon", text: BI.i18nText("BI-Left_Join"), value: BICst.ETL_JOIN_STYLE.LEFT_JOIN
    }, {
        iconCls: "right-join-icon", text: BI.i18nText("BI-Right_Join"), value: BICst.ETL_JOIN_STYLE.RIGHT_JOIN
    }, {
        iconCls: "outer-join-icon", text: BI.i18nText("BI-Outer_Join"), value: BICst.ETL_JOIN_STYLE.OUTER_JOIN
    }]

});
BI.JoinTypeGroup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.join_type_group", BI.JoinTypeGroup);