/**
 * 多路径设置
 *
 * Created by GUY on 2015/12/31.
 * @class BI.MultiRelation
 * @extends BI.Widget
 */

BI.MultiRelation = BI.inherit(BI.Single, {

    _defaultConfig: function () {
        return BI.extend(BI.MultiRelation.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-multi-relation",
            relations: []
        });
    },

    _init: function () {
        BI.MultiRelation.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.button_group = BI.createWidget({
            type: "bi.button_tree",
            chooseType: BI.Selection.Multi,
            element: this.element,
            behaviors: {
                redmark: function () {
                    return true
                }
            },
            items: this._createItems(o.relations),
            layouts: [{
                type: "bi.vertical",
                tgap: 10
            }]
        });
        this.button_group.on(BI.ButtonTree.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiRelation.EVENT_CHANGE, arguments);
        });

        this.button_group.on(BI.Controller.EVENT_CHANGE, function (type, value, obj) {
            var multiPathValue = self.getValue();
            self.fireEvent(BI.Controller.EVENT_CHANGE, type, multiPathValue, obj);
        })
    },

    _createItems: function (relations) {
        return BI.map(relations, function (i, relationsArray) {
            return {
                type: "bi.multi_relation_expander",
                relationsArray: relationsArray
            }
        });
    },

    getItems: function () {
        return this.attr("relations");
    },

    setValue: function (v) {
        this.button_group.setValue(v);
    },

    getValue: function () {
        return this.button_group.getValue();
    },

    getNotSelectedValue: function () {
        return this.button_group.getNotSelectedValue();
    },

    populate: function (relations, availableRelationItem, keyword) {
        this.options.relations = relations;
        var items = this._createItems(relations);
        this.button_group.populate(items, keyword);
        this.setValue(availableRelationItem);
    },

    doBehavior: function () {
        this.button_group.doBehavior.apply(this.button_group, arguments);
    }
});
BI.MultiRelation.EVENT_CHANGE = "MultiRelation.EVENT_CHANGE";
$.shortcut('bi.multi_relation', BI.MultiRelation);