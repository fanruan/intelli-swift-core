/**
 * @class BI.RelationSetGroup
 * @extend BI.Widget
 * 表间关系设置：N-1、1-1、1-N 按钮组
 */
BI.RelationSetGroup = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.RelationSetGroup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-relation-set-group"
        })
    },

    _init: function(){
        BI.RelationSetGroup.superclass._init.apply(this, arguments);
        var self = this;
        var groups = [{
            text: "1:N",
            value: BICst.RELATION_TYPE.ONE_TO_N
        }, {
            text: "N:1",
            value: BICst.RELATION_TYPE.N_TO_ONE
        }, {
            text: "1:1",
            value: BICst.RELATION_TYPE.ONE_TO_ONE
        }];
        this.relationGroup = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            items: BI.createItems(groups, {
                type: "bi.single_select_item",
                textAlign: "center",
                width: 80,
                height: 25
            }),
            layouts: [{
                type: "bi.vertical",
                hgap: 10,
                vgap: 5
            }]
        });
        this.relationGroup.setValue(this.options.relationType);
        this.relationGroup.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        })
    },

    getValue: function(){
        return this.relationGroup.getValue();
    }
});
$.shortcut("bi.relation_set_group", BI.RelationSetGroup);