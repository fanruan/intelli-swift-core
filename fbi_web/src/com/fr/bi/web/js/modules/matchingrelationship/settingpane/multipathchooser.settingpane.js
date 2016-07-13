/**
 * 选择多路径
 *
 * @class BI.MultiPathChooser
 * @extends BI.Widget
 */

BI.MultiPathChooser = BI.inherit(BI.Widget, {

    constants: {
        NoPath: 0,
        OnePath: 1,
        MorePath: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.MultiPathChooser.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-multi-path-chooser"
        });
    },

    _init: function () {
        BI.MultiPathChooser.superclass._init.apply(this, arguments);
        var self = this;
        this.pathChooser = BI.createWidget({
            type: "bi.path_chooser"
        });
        this.pathChooser.on(BI.PathChooser.EVENT_CHANGE, function () {
            self.path = self._packageValueByValue(this.getValue());
        });
        this.path = [];
        this.pathValueMap = {};
        this.pathRelationMap = {};
        this.tipTab = BI.createWidget({
            direction: "custom",
            type: "bi.tab",
            defaultShowIndex: false,
            logic: {
                dynamic: true
            },
            cardCreator: BI.bind(this._createTabs, this)
        });
        BI.createWidget({
            type: "bi.vertical",
            hgap: 10,
            scrolly: false,
            element: this.element,
            items: [{
                type: "bi.horizontal",
                scrollx: false,
                items: [this.pathChooser]
            }, this.tipTab]
        });
    },

    _createTabs: function (v) {
        switch (v) {
            case this.constants.NoPath:
                return BI.createWidget({
                    type: "bi.label",
                    textAlign: "left",
                    lgap: 20,
                    text: BI.i18nText("BI-Tip_Dimension_Target_No_Relation_May_Need_Change"),
                    cls: "no-path-label"
                });
            case this.constants.OnePath:
                return BI.createWidget({
                    type: "bi.label",
                    textAlign: "left",
                    lgap: 20,
                    text: BI.i18nText("BI-Tip_Only_One_Path_Between_Dimension_Target"),
                    cls: "one-path-label"
                });
            case this.constants.MorePath:
                return BI.createWidget();
        }
    },

    _checkPathOfOneTable: function(value){
        //value = value || [];
        //if(value.length === 1){
        //    var pTId = BI.Utils.getTableIdByFieldID(value[0].primaryKey.field_id);
        //    var fTId = BI.Utils.getTableIdByFieldID(value[0].foreignKey.field_id);
        //    if(pTId === fTId){
        //        return [];
        //    }
        //}
        return value;
    },

    _createRegionPathsByItems: function(items){
        var self = this;
        var uuidMap = {}; //管理一下各条路径上的uuid
        this.options.dimensionFieldId = items.dimensionFieldId;
        var ptId = BI.Utils.getTableIdByFieldID(items.dimensionFieldId);
        var paths = BI.Utils.getPathsFromFieldAToFieldB(items.dimensionFieldId, BI.Utils.getFieldIDByDimensionID(items.targetIds[0]));
        if(paths.length === 1){
            this.path = paths[0];
        }
        return BI.map(paths, function (idx, path) {
            var p = [], pId = BI.UUID();
            BI.backEach(path, function (id, relation) {
                var foreignId = BI.Utils.getForeignIdFromRelation(relation);
                var primaryId = BI.Utils.getPrimaryIdFromRelation(relation);
                if(BI.Utils.getTableIdByFieldID(foreignId) === BI.Utils.getTableIdByFieldID(primaryId)){
                    var regionId = BI.UUID();
                    if(BI.has(uuidMap, foreignId)){
                        regionId = uuidMap[foreignId];
                    }else{
                        uuidMap[foreignId] = regionId;
                    }
                    p.push({
                        region: BI.UUID(),
                        //region: regionId,
                        regionText: BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(foreignId)),
                        text: BI.Utils.getFieldNameByID(foreignId),
                        value: foreignId
                    });
                }else{
                    p.push({
                        region: BI.UUID(),
                        regionText: BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(foreignId)),
                        text: BI.Utils.getFieldNameByID(foreignId),
                        value: foreignId
                    });
                }
                if (id === 0) {
                    p.push({
                        //region: BI.Utils.getTableNameByID(ptId),
                        region: BI.UUID(),
                        regionText: BI.Utils.getTableNameByID(ptId),
                        text: BI.Utils.getFieldNameByID(items.dimensionFieldId),
                        value: primaryId
                    });
                }
            });
            self.pathValueMap[pId] = BI.pluck(p, "value");
            self.pathRelationMap[pId] = path;
            return p;
        });
    },

    _packageValueByValue: function (value) {
        var self = this;
        var key = BI.find(BI.keys(this.pathValueMap), function (idx, key) {
            return BI.isEqual(value, self.pathValueMap[key]);
        });
        if (BI.isNull(key)) {
            return this.path;
        }else{
            return this.pathRelationMap[key];
        }
    },

    _unpackValueByValue: function (value) {
        //v:  [{primaryKey: , foreignKey: }, {primaryKey: , foreignKey:}, {primaryKey: , foreignKey:}]
        var self = this, val = this._checkPathOfOneTable(value);
        var key = null;
        BI.any(BI.keys(this.pathRelationMap), function (idx, k) {
            if(BI.isEqual(val, self.pathRelationMap[k])){
                key = k;
            }
            return BI.isNotNull(key);
        });
        return BI.isNotNull(key) ? this.pathValueMap[key] : [];
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
        //this.tipTab.setSelect(items.length > 1 ? this.constants.MorePath : (items.length === 1 ? this.constants.OnePath : this.constants.NoPath));
    },

    setValue: function (v) {
        this.path = v[0];
        this.pathChooser.setValue(this._unpackValueByValue(v[0]));
    },

    getValue: function () {
        return [this.path];
    }
});
$.shortcut('bi.multi_path_chooser', BI.MultiPathChooser);