/**
 * Created by roy on 16/3/10.
 */
BI.DetailTablePathItem = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePathItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "detail-table-multi-relation-item",
            height: 30,
            relations: [],
            //primaryFieldId: ""
            dId:""
        })
    },

    _init: function () {
        var self = this, o = this.options;
        BI.DetailTablePathItem.superclass._init.apply(this, arguments);
        this.pathLabel = BI.createWidget({
            type: "bi.label",
            lgap: 10,
            element: this.element,
            textHeight: o.height,
            textAlign: "left"
        });
        this.populate();

    },

    _createLabelValue: function (relations, dId) {
        var labelValue = "";
        BI.backAny(relations, function (i, rel) {
            var foreignTableId = BI.Utils.getTableIdByFieldID(BI.Utils.getForeignIdFromRelation(rel));
            if (i === 0) {
                labelValue = labelValue + " " + BI.Utils.getFieldNameByID(BI.Utils.getForeignIdFromRelation(rel));
                labelValue = labelValue + " (" + BI.Utils.getTableNameByID(foreignTableId) + ")";
                labelValue = labelValue + " > ";
                return
            }
            labelValue = labelValue + BI.Utils.getFieldNameByID(BI.Utils.getForeignIdFromRelation(rel));
            labelValue = labelValue + " (" + BI.Utils.getTableNameByID(foreignTableId) + ")";
            labelValue = labelValue + " >";

        });
        labelValue = labelValue + BI.Utils.getDimensionNameByID(dId);
        var tableId = BI.Utils.getTableIDByDimensionID(dId);
        labelValue = labelValue + " (" + BI.Utils.getTableNameByID(tableId) + ")";
        return labelValue;
    },

    setValue: function (relations, dId) {
        var self = this, o = this.options;
        o.relations = relations;
        o.dId = dId;
        this.pathLabel.setValue(self._createLabelValue(relations, dId))
    },

    getValue: function () {
        var o = this.options;
        return {
            relations: o.relations,
            primaryFieldName: o.primaryFieldName
        }
    },
    populate: function () {
        var o = this.options;
        this.setValue(o.relations, o.dId);
    }
});
$.shortcut("bi.detail_table_path_item", BI.DetailTablePathItem);