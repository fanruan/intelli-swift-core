/**
 * 多路径设置Expander
 *
 * Created by GUY on 2015/12/31.
 * @class BI.MultiRelationExpander
 * @extends BI.Widget
 */

BI.MultiRelationExpander = BI.inherit(BI.Single, {

    _defaultConfig: function () {
        return BI.extend(BI.MultiRelationExpander.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-multi-relation-expander",
            relationsArray: []
        });
    },

    _init: function () {
        BI.MultiRelationExpander.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var title = BI.createWidget({
            type: "bi.label",
            cls: "multi-relation-expander-title",
            height: 40,
            textAlign: BI.HorizontalAlign.Left,
            text: this._createText()
        });

        this.button_group = BI.createWidget({
            type: "bi.button_group",
            items: this._createItems(),
            chooseType: BI.Selection.Multi,
            behaviors: {
                redmark: function () {
                    return true
                }
            },
            layouts: [{
                type: "bi.vertical",
                vgap: 5
            }]
        });
        this.button_group.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiRelationExpander.EVENT_CHANGE, arguments);
        });

        this.button_group.on(BI.Controller.EVENT_CHANGE, function (type, value, obj) {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments)
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            hgap: 20,
            items: [title, this.button_group]
        })
    },

    _createItems: function () {
        return BI.map(this.options.relationsArray, function (i, relations) {
            return {
                type: "bi.multi_relation_item",
                relations: relations
            }
        });
    },

    _getTableName: function (field) {
        return BI.Utils.getTableNameByFieldId4Conf(field.field_id);
    },

    _createText: function () {
        var o = this.options;
        var text = "";
        return this._getTableName(BI.last(o.relationsArray[0]).foreignKey)
            + "——" + this._getTableName(BI.first(o.relationsArray[0]).primaryKey);
    },

    doRedMark: function (keyword) {
        this.button_group.doBehavior(keyword);
    },

    setValue: function (v) {
        this.button_group.setValue(v);
    },

    getValue: function () {
        return this.button_group.getValue();
    },

    getNotSelectedValue: function () {
        return this.button_group.getNotSelectedValue();
    }


});
BI.MultiRelationExpander.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.multi_relation_expander', BI.MultiRelationExpander);