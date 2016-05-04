/**
 * 多对多选择路径
 *
 * @class BI.MultiMatchMultiPathChooser
 * @extends BI.Widget
 */

BI.MultiMatchMultiPathChooser = BI.inherit(BI.Widget, {

    constants: {
        NoPath: 0,
        OnePath: 1,
        MorePath: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.MultiMatchMultiPathChooser.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-multi-match-multi-path-chooser"
        });
    },

    _init: function () {
        BI.MultiMatchMultiPathChooser.superclass._init.apply(this, arguments);
        var self = this;
        this.pathChooser = BI.createWidget({
            type: "bi.direction_path_chooser"
        });
        this.pathChooser.on(BI.DirectionPathChooser.EVENT_CHANGE, function () {
            self.path = self._packageValueByValue(this.getValue());
        });
        this.lpath = [];
        this.rpath = [];
        this.pathValueMap = {};
        this.pathRelationMap = {};

        BI.createWidget({
            type: "bi.vertical",
            hgap: 10,
            scrolly: false,
            element: this.element,
            items: [{
                type: "bi.horizontal",
                scrollx: false,
                items: [this.pathChooser]
            }]
        });
    },

    _checkPathOfOneTable: function(value){
        value = value || [];
        if(value.length === 1){
            var pTId = BI.Utils.getTableIdByFieldID(value[0].primaryKey.field_id);
            var fTId = BI.Utils.getTableIdByFieldID(value[0].foreignKey.field_id);
            if(pTId === fTId){
                return [];
            }
        }
        return value;
    },

    _createRightRegionPath: function (combineFieldId, dimensionFieldId) {
        var self = this;
        var ptId = BI.Utils.getTableIdByFieldID(dimensionFieldId);
        var paths = BI.Utils.getPathsFromFieldAToFieldB(combineFieldId, dimensionFieldId);
        if(ptId === BI.Utils.getTableIdByFieldID(combineFieldId)){
            this.rpath = paths[0];
            return [];
        }
        if(paths.length === 1){
            this.rpath = paths[0];
        }
        return BI.map(paths, function (idx, path) {
            var p = [], pId = BI.UUID();
            BI.each(path, function (id, relation) {
                var foreignId = BI.Utils.getForeignIdFromRelation(relation);
                if (id === 0) {
                    var primaryId = BI.Utils.getPrimaryIdFromRelation(relation);
                    p.push({
                        region: BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(primaryId)),
                        text: BI.Utils.getFieldNameByID(primaryId),
                        value: primaryId,
                        direction: -1
                    });
                }
                p.push({
                    region: BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(foreignId)),
                    text: BI.Utils.getFieldNameByID(foreignId),
                    value: foreignId,
                    direction: -1
                });
            });
            self.pathValueMap[pId] = BI.pluck(p, "value");
            self.pathRelationMap[pId] = path;
            return p;
        });
    },

    _createLeftRegionPath: function (combineFieldId, targetFieldId) {
        var self = this;
        var ptId = BI.Utils.getTableIdByFieldID(targetFieldId);
        var paths = BI.Utils.getPathsFromFieldAToFieldB(combineFieldId, targetFieldId);
        if(ptId === BI.Utils.getTableIdByFieldID(combineFieldId)){
            this.lpath = paths[0];
            return [];
        }
        if(paths.length === 1){
            this.lpath = paths[0];
        }
        return BI.map(paths, function (idx, path) {
            var p = [], pId = BI.UUID();
            BI.backEach(path, function (id, relation) {
                var foreignId = BI.Utils.getForeignIdFromRelation(relation);
                p.push({
                    region: BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(foreignId)),
                    text: BI.Utils.getFieldNameByID(foreignId),
                    value: foreignId
                });
                if (id === 0) {
                    var primaryId = BI.Utils.getPrimaryIdFromRelation(relation);
                    p.push({
                        region: BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(primaryId)),
                        text: BI.Utils.getFieldNameByID(primaryId),
                        value: primaryId,
                        direction: -1
                    });
                }
            });
            self.pathValueMap[pId] = BI.pluck(p, "value");
            self.pathRelationMap[pId] = path;
            return p;
        });
    },

    _createRegionPathsByItems: function(items){
        this.options.dimensionFieldId = items.dimensionFieldId;
        this.options.combineTableId = items.combineTableId;
        var combineFieldId = BI.Utils.getFieldIDsOfTableID(items.combineTableId)[0];
        var lregion = this._createLeftRegionPath(combineFieldId, BI.Utils.getFieldIDByDimensionID(items.targetIds[0]));
        var rregion = this._createRightRegionPath(combineFieldId, items.dimensionFieldId);
        var newRegion = [];
        if(BI.isEmptyArray(lregion)){
            return rregion;
        }
        BI.each(lregion, function(idx, lg){
            BI.each(rregion, function(id, rg){
                rg.splice(0, 1);
                newRegion.push(BI.concat(lg, rg));
            })
        });
        return newRegion;
    },

    _packageValueByValue: function (value) {
        var self = this, o = this.options;
        var combineIndex = 0;
        BI.find(value, function(idx, val){
            combineIndex = idx;
            return BI.Utils.getTableIdByFieldID(val) === o.combineTableId;
        });
        var lvalue = BI.first(value, combineIndex + 1);
        var rvalue = BI.rest(combineIndex);
        var lkey = null, rkey = null;
        BI.any(BI.keys(this.pathValueMap), function (idx, key) {
            if(BI.isEqual(lvalue, self.pathValueMap[key])){
                lkey = key;
            }
            if(BI.isEqual(rvalue, self.pathValueMap[key])){
                rkey = key;
            }
            return BI.isNotNull(lkey) && BI.isNotNull(rkey);
        });
        var result = {};
        result.lpath = this.pathRelationMap[lkey] || this.lpath;
        result.rpath = this.pathRelationMap[rkey] || this.rpath;
        return result;
    },

    _unpackValueByValue: function (value) {
        var v = [], self = this;
        var lvalue = this._checkPathOfOneTable(value.lpath);
        var rvalue = this._checkPathOfOneTable(value.rpath);
        BI.backEach(lvalue, function (idx, val) {
            v.push(BI.Utils.getForeignIdFromRelation(val));
            if (idx === 0) {
                v.push(BI.Utils.getPrimaryIdFromRelation(val));
            }
        });
        BI.each(rvalue, function (idx, val) {
            v.push(BI.Utils.getForeignIdFromRelation(val));
        });
        return v;
    },

    populate: function (items) {
        this.path = [];
        this.pathRelationMap = {};
        this.pathValueMap = {};
        items = this._createRegionPathsByItems(items);
        this.pathChooser.populate(items);
        if(BI.size(this.pathValueMap) > 1){
            this.pathChooser.setValue();
        }
    },

    _assertValue: function (v) {
        v = v || {};
        v.lpath = v.lpath || [];
        v.rpath = v.rpath || [];
        return v;
    },

    setValue: function (v) {
        //lpath, rpath
        v = this._assertValue(v);
        this.lpath = v.lpath;
        this.lpath = v.rpath;
        this.pathChooser.setValue(this._unpackValueByValue(v));
    },

    getValue: function () {
        //lpath, rpath
        return {
            lpath: this.lpath,
            rpath: this.rpath
        };
    }
});
$.shortcut('bi.multi_match_multi_path_chooser', BI.MultiMatchMultiPathChooser);