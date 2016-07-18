/**
 * 管理set和unsetting的region
 *
 * @class BI.RelationInfoPane
 * @extends BI.Widget
 *
 * map结构
 * targetMap: {
 *      targetId1: region1
 *      targetId2: region1
 *      targetId3: region3
 *      targetId4: region2
 * }
 * regionMap: {
 *      region1: rWidget1
 *      region2: rWidget2
 *      region3: rWidget3
 * }
 */

BI.RelationInfoPane = BI.inherit(BI.Widget, {

    constants: {
        UNSETTING_REGION: 0,
        SET_REGION: 1
    },

    _defaultConfig: function () {
        return BI.extend(BI.RelationInfoPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-relation-info-pane"
        });
    },

    _init: function () {
        BI.RelationInfoPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.regionMap = {};
        this.targetMap = {};  //存储targets所在region信息
        this.stored_paths = {};
        this.stored_value = {};
        this.operatorTargets = [];

        this.setRegionGroup = BI.createWidget({
            type: "bi.button_group",
            items: [],
            layouts: [{
                type: "bi.vertical",
                bgap: 10
            }]
        });
        this.unsettingRegionGroup = BI.createWidget({
            type: "bi.button_group",
            items: [],
            layouts: [{
                type: "bi.vertical",
                bgap: 10
            }]
        });
        this.layout = BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: []
        });
        this.layout.addItem(this.unsettingRegionGroup);
        this.layout.addItem(this.setRegionGroup);
    },

    _join: function(res, tId){
        var self = this, o = this.options;
        if(BI.isEmpty(res)){
            var dimensionMap = BI.Utils.getDimensionMapByDimensionID(o.dId);
            if(BI.isNotEmptyObject(dimensionMap)){
                BI.each(dimensionMap, function(tId, content){
                    self.stored_paths[tId] = [content.target_relation];
                    self.stored_value[tId] = content;
                });
            }
            return;
        }
        if(BI.contains(this.operatorTargets, tId)){
            this.stored_paths[tId] = [res.target_relation];
            this.stored_value[tId] = res;
        }
    },

    _getMD5ByPathAndDimensionFieldId: function(path, fId){
        var res = "";
        var getMD5Result = function(pa){
            var fArray = BI.pluck(pa, "foreignKey");
            var pArray = BI.pluck(pa, "primaryKey");
            var s = fId;
            BI.each(fArray, function(idx, item){
                s += item.field_id;
            });
            s += pArray[0].field_id;
            return s;
        };
        if(BI.isArray(path[0])){
            BI.each(path, function(idx, p){
                res += getMD5Result(p);
            })
        }else{
            res = getMD5Result(path);
        }
        return BI.MD5.hex_md5(res);
    },

    populate: function(res){
        var self = this, o = this.options;
        var wId = BI.Utils.getWidgetIDByDimensionID(o.dId);
        var pFId = BI.Utils.getFieldIDByDimensionID(o.dId);
        var dimSrc = BI.Utils.getDimensionSrcByID(o.dId);
        if(BI.has(dimSrc, "relation")){
            pFId = BI.Utils.getForeignIdFromRelation(dimSrc.relation);
        }
        var tIds = BI.Utils.getAllTargetDimensionIDs(wId);
        BI.each(tIds, function(idx, tId){
            var fFId = BI.Utils.getFieldIDByDimensionID(tId);
            //计算指标不参与
            if(BI.isNull(fFId)){
                return;
            }
            BI.isEmpty(self.stored_paths[tId]) && (self.stored_paths[tId] = BI.Utils.getPathsFromFieldAToFieldB(pFId, fFId));
            self._join(res, tId);
            if((BI.size(self.stored_paths[tId]) > 1 || BI.size(self.stored_paths[tId]) === 0)){
                self.addRegion(self.constants.UNSETTING_REGION, tId);
            }else{
                self.addRegion(self.constants.SET_REGION, tId, res);
            }
        });
    },

    addRegion: function(type, tId,  res){
        var self = this, o = this.options;
        var tableId = BI.Utils.getTableIDByDimensionID(tId);
        var region = null, tableName = BI.Utils.getTableNameByID(tableId);
        switch (type) {
            case this.constants.UNSETTING_REGION:
                if(BI.isEmpty(this.regionMap[tableId])){
                    region = BI.createWidget({
                        type: "bi.unsetting_matching_relation_target_region",
                        tableName: tableName
                    });
                    region.on(BI.UnSettingMatchingRelationTargetRegion.EVENT_SET_RELATION, function(){
                        var tIds = this.getValue();
                        var value = {};
                        BI.each(tIds, function (idx, tId) {
                            value[tId] = {};
                        });
                        self.operatorTargets = tIds;
                        self.fireEvent(BI.RelationInfoPane.EVENT_CLICK_RELATION_BUTTON, value);
                    });
                    region.on(BI.UnSettingMatchingRelationTargetRegion.EVENT_DESTROY, function(){
                        delete self.regionMap[tableId];
                    });
                    this.regionMap[tableId] = region;
                    this.unsettingRegionGroup.addItems([region]);
                }
                this.targetMap[tId] !== tableId && this.regionMap[tableId].addItems([{
                    text: BI.Utils.getDimensionNameByID(tId),
                    value: tId,
                    title: BI.Utils.getDimensionNameByID(tId)
                }]);
                this.targetMap[tId] = tableId;
                break;
            case this.constants.SET_REGION:
                var field_id = BI.Utils.getFieldIDByDimensionID(o.dId);
                if(BI.isNotNull(this.stored_value[tId])){
                    field_id = this.stored_value[tId]._src.field_id;
                }
                var regionKey = this._getMD5ByPathAndDimensionFieldId(this.stored_paths[tId][0], field_id);
                if(BI.isEmpty(this.regionMap[regionKey])){
                    var fId = BI.Utils.getFieldIDByDimensionID(o.dId);
                    if(BI.has(res, "_src")){
                        fId = res._src.field_id;
                    }
                    region = BI.createWidget({
                        type: "bi.set_matching_relation_target_region",
                        tableName: tableName,
                        fieldId: fId
                    });
                    region.on(BI.SetMatchingRelationTargetRegion.EVENT_SET_RELATION, function(){
                        var tIds = this.getValue();
                        var value = {};
                        BI.each(tIds, function (idx, tId) {
                            value[tId] = self.stored_value[tId];
                        });
                        self.operatorTargets = tIds;
                        self.fireEvent(BI.RelationInfoPane.EVENT_CLICK_RELATION_BUTTON, value);
                    });
                    region.on(BI.SetMatchingRelationTargetRegion.EVENT_DESTROY, function(){
                        delete self.regionMap[regionKey];
                    });
                    this.regionMap[regionKey] = region;
                    this.setRegionGroup.addItems([region]);
                }
                BI.isNotNull(this.targetMap[tId]) && this.targetMap[tId] !== regionKey && this.regionMap[this.targetMap[tId]].removeItemsByValue(tId);
                this.targetMap[tId] !== regionKey && this.regionMap[regionKey].addItems([{
                    text: BI.Utils.getDimensionNameByID(tId),
                    value: tId,
                    title: BI.Utils.getDimensionNameByID(tId)
                }]);
                this.targetMap[tId] = regionKey;
                break;
        }
    },

    getValue: function(){
        return this.stored_value;
    }
});
BI.RelationInfoPane.EVENT_CLICK_RELATION_BUTTON = "EVENT_CLICK_RELATION_BUTTON";
BI.RelationInfoPane.EVENT_CHANGE = "RelationInfoPane.EVENT_CHANGE";
$.shortcut('bi.relation_info_pane', BI.RelationInfoPane);