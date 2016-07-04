/**
 * @class BI.DimensionTreeCombo
 * @extends BI.Widget
 */
BI.DimensionTreeCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DimensionTreeCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-tree-combo",
            height: 30,
            items: []
        });
    },

    _init: function () {
        BI.DimensionTreeCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.trigger = BI.createWidget({
            type: "bi.dimension_tree_trigger",
            height: o.height - 2,
            items: o.items
        });

        this.popup = BI.createWidget({
            type: "bi.dimension_tree_popup",
            items: o.items
        });

        this.combo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            adjustLength: 2,
            el: this.trigger,
            popup: {
                el: this.popup
            }
        });

        this.combo.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        this.popup.on(BI.SingleTreePopup.EVENT_CHANGE, function () {
            self.setValue(self.popup.getValue());
            self.combo.hideView();
            self.fireEvent(BI.DimensionTreeCombo.EVENT_CHANGE);
        });
    },

    _isSelfCircleTable: function (tableId) {
        return BI.Utils.getPathsFromTableAToTableB(tableId, tableId).length > 0;
    },

    _getSelfCircleFieldsByFieldId: function (fieldId, circleForeignIds) {
        var self = this, o = this.options;
        circleForeignIds || (circleForeignIds = []);
        return BI.map(circleForeignIds, function (i, f) {
            var fieldName = BI.Utils.getFieldNameByID(f) || "";
            return {
                id: f,
                pId: fieldId,
                text: fieldName,
                title: fieldName,
                value: f
            };
        });
    },

    _createItemsByTargetIds: function(targetIds){
        var o = this.options, self = this;
        if(BI.isEmpty(targetIds)){
            return [];
        }
        var targetId = targetIds[0];
        var tableIds = [BI.Utils.getTableIDByDimensionID(targetId)];
        tableIds = BI.concat(tableIds, BI.Utils.getPrimaryRelationTablesByTableID(tableIds[0]));
        var foreignTableIds = [];
        BI.each(tableIds, function(id, tableId){
            foreignTableIds = BI.concat(foreignTableIds, BI.Utils.getForeignRelationTablesByTableID(tableId));
        });
        tableIds = BI.concat(tableIds, foreignTableIds);
        return BI.map(BI.uniq(tableIds), function(idx, tId){
            var initialFieldIds = BI.Utils.getFieldIDsOfTableID(tId);
            var fieldIds = BI.filter(initialFieldIds, function(idx, ids){
                return BI.Utils.getFieldTypeByID(ids) === BI.Utils.getFieldTypeByDimensionID(o.dId);
            });
            if (this._isSelfCircleTable(tId)) {
                var pIds = [], fIds = [], map = {};
                var relations = BI.Utils.getPathsFromTableAToTableB(tId, tId);
                BI.each(relations, function (i, path) {
                    var pId = BI.Utils.getFirstRelationPrimaryIdFromRelations(path);
                    var fId = BI.Utils.getLastRelationForeignIdFromRelations(path);
                    pIds.push(pId);
                    if (!map[pId]) {
                        map[pId] = [];
                    }
                    map[pId].push(fId);
                    fIds.push(fId);
                });
                var newFields = [];
                BI.each(fieldIds, function (i, id) {
                    if (pIds.contains(id)) {
                    } else if (!fIds.contains(id)) {
                        newFields.push(id);
                    }
                });
            } else {
                newFields = fieldIds;
            }
            var node, pId;
            var dimensionTableId = BI.Utils.getTableIDByDimensionID(o.dId);
            //推荐表： 1.是维度所在表且维度与指标有路径；2.维度与指标无路径时是指标所在表
            if((BI.contains(initialFieldIds, BI.Utils.getFieldIDByDimensionID(o.dId)) && !BI.isEmpty(BI.Utils.getPathsFromTableAToTableB(dimensionTableId, tableIds[0])))
                || (BI.isEmpty(BI.Utils.getPathsFromTableAToTableB(dimensionTableId, tableIds[0])) && BI.contains(initialFieldIds, BI.Utils.getFieldIDByDimensionID(targetId)))){
                node = {
                    id: 0,
                    text: BI.Utils.getTableNameByID(tId) + "(" + BI.i18nText("BI-Recommend") + ")",
                    cls: "recommend-node",
                    value: 0,
                    isParent: true,
                    open: true,
                    title: BI.Utils.getTableNameByID(tId)
                };
                pId = 0;
            }else{
                node = {
                    id: idx + 1,
                    text: BI.Utils.getTableNameByID(tId),
                    value: idx + 1,
                    isParent: true,
                    open: false,
                    title: BI.Utils.getTableNameByID(tId)
                };
                pId = idx + 1;
            }
            var items = [];
            if(self._isSelfCircleTable(tId)){
                BI.each(fieldIds, function (i, fId) {
                    var id = fId;
                    if (pIds.contains(id)) {
                        var fieldName = BI.Utils.getFieldNameByID(id) || "";
                        items.push({
                            id: id,
                            pId: pId,
                            text: fieldName,
                            title: fieldName,
                            value: id
                        });
                        items = BI.concat(items, self._getSelfCircleFieldsByFieldId(id, map[id] || []))
                    }
                });
            }
            BI.each(fieldIds, function(id, fId){
                items.push({
                    pId: pId,
                    id: fId,
                    text: BI.Utils.getFieldNameByID(fId),
                    value: fId,
                    title: BI.Utils.getFieldNameByID(fId)
                });
            });
            items.push(node);
            return items;
        });
    },

    populate: function (items) {
        this.combo.populate(BI.sortBy(BI.flatten(this._createItemsByTargetIds(items)), "id"));
        this.combo.setValue(BI.Utils.getFieldIDByDimensionID(this.options.dId));
    },

    setValue: function (v) {
        v = BI.isArray(v) ? v : [v];
        this.trigger.setValue(v);
        this.popup.setValue(v);
    },

    getValue: function () {
        return this.popup.getValue();
    }
});

BI.DimensionTreeCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.dimension_tree_combo", BI.DimensionTreeCombo);