/**
 * Created by roy on 16/2/15.
 */
BI.MultirelationItem = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.MultirelationItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-multi-relation-item",
            height: 30,
            relations: []
        })
    },

    _init: function () {
        BI.MultirelationItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.checkbox = BI.createWidget({
            type: "bi.checkbox"
        });

        this.checkbox.on(BI.Controller.EVENT_CHANGE, function (type, value, obj) {
            var value = self.getValue();
            self.fireEvent(BI.Controller.EVENT_CHANGE, type, value, self);
        });

        this.textGroup = BI.createWidget({
            type: "bi.button_group",
            behaviors: {
                redmark: function () {
                    return true
                }
            },
            items: self._createItems(o.relations),
            layouts: [{
                type: "bi.left"
            }]
        });

        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.center_adapt",
                    items: [this.checkbox]
                },
                width: 13
            }, {
                el: this.textGroup
            }]
        })
    },

    _createItems: function () {
        var self = this, o = this.options;
        var items = [];
        BI.backAny(o.relations, function (i, rel) {
            var widgetItem = {};
            var foreignFieldId = rel.foreignKey.field_id;
            var primaryFieldId = rel.primaryKey.field_id;
            if (i === 0) {
                widgetItem.type = "bi.multi_relation_table_field_item";
                widgetItem.fieldName = BI.Utils.getFieldNameByFieldId4Conf(foreignFieldId);
                widgetItem.tableName = BI.Utils.getTableNameByFieldId4Conf(foreignFieldId);
                items.push(BI.deepClone(widgetItem));
                items.push({
                    type: "bi.label",
                    value: "->"
                });
                widgetItem.type = "bi.multi_relation_table_field_item";
                widgetItem.fieldName = BI.Utils.getFieldNameByFieldId4Conf(primaryFieldId);
                widgetItem.tableName = BI.Utils.getTableNameByFieldId4Conf(primaryFieldId);
                items.push(BI.deepClone(widgetItem));
                return
            }
            widgetItem.type = "bi.multi_relation_table_field_item";
            widgetItem.fieldName = BI.Utils.getFieldNameByFieldId4Conf(foreignFieldId);
            widgetItem.tableName = BI.Utils.getTableNameByFieldId4Conf(foreignFieldId);
            items.push(BI.deepClone(widgetItem));
            items.push({
                type: "bi.label",
                value: "->"
            });

        });
        return items;
    },

    doBehavior: function () {
        this.textGroup.doBehavior.apply(this.textGroup, arguments);
    },

    getValue: function () {
        return this.options.relations;
    },

    setValue: function (v) {
        this.options.relations = v;
        this.textGroup.populate(this._createItems());
    },

    setSelected: function (b) {
        this.checkbox.setSelected(b);
    },

    isSelected: function () {
        return this.checkbox.isSelected();
    }


});
$.shortcut("bi.multi_relation_item", BI.MultirelationItem);