BI.AnalysisETLOperatorGroupPaneModel = BI.inherit(BI.MVCModel, {


    _init: function () {
        BI.AnalysisETLOperatorGroupPaneModel.superclass._init.apply(this, arguments);
        var operator = this.get('operator') || {};
        this.set(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY, operator[BI.AnalysisETLOperatorGroupPaneModel.DIMKEY] || {});
        this.set(BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY, operator[BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY] || {});
        this._initDefault()
        this.changed = false;
    },

    _initDefault : function () {
        var dimension =  this.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY);

        if(BI.isEmptyObject(dimension)) {
            var self = this;
            var parent = this.get(ETLCst.PARENTS)[0];
            var fields = parent[ETLCst.FIELDS];
            BI.each(fields, function (idx, item) {
                if(item.field_type === BICst.COLUMN.NUMBER) {
                    self.addDimensionByField({fieldInfo: item,regionType: BICst.REGION.TARGET1})
                }
            })
        }
    },

    getDimension: function (id) {
        if (BI.isNull(id)) {
            return this.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY);
        }
        return this.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY)[id];
    },

    /**
     * 删除dimension
     * @param dId
     */
    deleteDimension: function (dId) {
        var view = this.get(BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY), dimensions = this.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY);
        delete dimensions[dId];
        BI.each(view, function (region, ids) {
            BI.any(ids, function (idx, id) {
                if (dId == id) {
                    ids.remove(id);
                    return true;
                }
            });
        });
        this.set(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY, dimensions)
        this.set(BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY, view)
        this.changed = true;
    },


    getDimensionUsedById: function (id) {
        var dimensions = this.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY);
        return dimensions[id].used;
    },

    setDimensionUsedById: function (id, used) {
        var dimensions = this.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY);
        dimensions[id].used = used;
        this.changed = true;
    },

    getDimensionNameById : function (id) {
        var dimensions = this.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY);
        return dimensions[id].name;
    },

    setDimensionNameById : function (id, name) {
        var dimensions = this.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY);
        dimensions[id].name = name;
        this.changed = true;
    },

    /**
     * 添加dimension
     * @param field
     */
    addDimensionByField: function (field) {
        var self = this;
        var view = this.get(BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY), dimensions = this.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY);
        var f = field;
        var field = f["fieldInfo"], regionType = f["regionType"];
        var id = BI.UUID();
        var type = field["field_type"];
        var getDimensionTypeByFieldType = function (fieldType) {
            switch (fieldType) {
                case BICst.COLUMN.STRING:
                    return BICst.TARGET_TYPE.STRING;
                case BICst.COLUMN.NUMBER:
                    return BICst.TARGET_TYPE.NUMBER;
                case BICst.COLUMN.DATE:
                    return BICst.TARGET_TYPE.DATE;
            }
        };
        dimensions[id] = {
            name: BI.Func.createDistinctName(dimensions, field["field_name"]),
            _src: {
                field_id: field.id,
                field_type: type,
                field_name: field["field_name"]
            },
            type: getDimensionTypeByFieldType(type),
            used: true
        };
        var assertTargetSummary = function (type) {
            switch (type) {
                case BICst.COLUMN.NUMBER:
                    return {
                        type: BICst.SUMMARY_TYPE.SUM
                    };
                case BICst.COLUMN.STRING:
                case BICst.COLUMN.DATE:
                    return {
                        type: BICst.SUMMARY_TYPE.COUNT
                    }
            }
        };
        var assertDimensionSummary = function (type) {
            switch (type) {
                case BICst.COLUMN.NUMBER:
                    return {
                        type: BICst.GROUP.ID_GROUP
                    };
                case BICst.COLUMN.STRING:
                    return {
                        type: BICst.GROUP.ID_GROUP
                    };
                case BICst.COLUMN.DATE:
                    return {
                        type: BICst.GROUP.M
                    };
            }
        };
        regionType == BICst.REGION.DIMENSION1 ? dimensions[id]["group"] = assertDimensionSummary(type) : dimensions[id]["group"] = assertTargetSummary(type);
        view[regionType] || (view[regionType] = []);
        view[regionType].push(id);
        this.set(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY, dimensions)
        this.set(BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY, view)
        this.changed = true;
        return id;
    },

    setSortBySortInfo: function (sorted) {
        var view = this.get(BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY);
        var dims = sorted.dimensions;
        var type = sorted.regionType;
        view[type] = dims;
        this.set(BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY, view)
        this.changed = true;
    },

    createFields : function () {
        var fields = [];
        var view = this.get(BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY);
        var dimensions = this.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY);
        var self = this;
        BI.each(view, function (idx, item) {
            BI.each(item, function (i, v) {
                var  dimension = dimensions[v];
                fields.push({
                    field_name : dimension["name"],
                    field_type: self._getFieldType(dimension, idx),
                });
            })
        })
        return fields;
    },

    _getFieldType : function (dimension, idx) {
        if(idx === BICst.REGION.TARGET1) {
            return BICst.COLUMN.NUMBER;
        } else {
            //TODO FIX其他类型等分组ok
            var src = dimension._src.field_type;
            switch (src) {
                case BICst.COLUMN.NUMBER: {
                    if(BI.isNotNull(dimension.group) && dimension.group.type === BICst.GROUP.ID_GROUP){
                        return BICst.COLUMN.NUMBER
                    }
                    break;
                }
                case BICst.COLUMN.DATE : {
                    if(BI.isNotNull(dimension.group) && dimension.group.type === BICst.GROUP.YMD){
                        return BICst.COLUMN.DATE
                    }
                    return BICst.COLUMN.NUMBER
                }
            }

            return BICst.COLUMN.STRING;
        }
    },
    
    update : function () {
        var v  = BI.AnalysisETLOperatorGroupPaneModel.superclass.update.apply(this, arguments);
        v.etlType = ETLCst.ETL_TYPE.GROUP_SUMMARY;
        v.fields = this.createFields();
        v.operator = {};
        v.operator[BI.AnalysisETLOperatorGroupPaneModel.DIMKEY] = v[BI.AnalysisETLOperatorGroupPaneModel.DIMKEY];
        v.operator[BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY] = v[BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY]
        return v;
    },

    getDimensionGroupById : function (id) {
        var dimensions = this.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY);
        return dimensions[id].group;
    },

    setDimensionGroupById : function (id, group) {
        var dimensions = this.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY);
        dimensions[id].group = group;
        this.changed = true;
    },

    isValid : function () {
        return !BI.isEmptyObject(this.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY))
    },

    getTextByType: function(id, groupOrSummary, fieldtype){
        var list = [];
        var obj = this.getDimensionGroupById(id);
        if(groupOrSummary ===  1){
            switch (fieldtype) {
                case BICst.COLUMN.STRING:
                    list = BICst.CONF_STATISTIC_STRING;
                    break;
                case BICst.COLUMN.NUMBER:
                    list = BICst.CONF_STATISTIC_NUMBER;
                    break;
                case BICst.COLUMN.DATE:
                    return BI.i18nText("BI-No_Repeat_Count");
            }
        }
        if(groupOrSummary ===  0){
            switch (fieldtype) {
                case BICst.COLUMN.STRING:
                    list = BICst.CONF_GROUP_STRING;
                    break;
                case BICst.COLUMN.NUMBER:
                    list = BICst.CONF_GROUP_NUMBER;
                    break ;
                case BICst.COLUMN.DATE:
                    list = BICst.CONF_GROUP_DATE;
                    break;
            }
        }
        var result = BI.find(list, function(idx, item){
            return item.value === obj.type || (item.value === BICst.GROUP.CUSTOM_NUMBER_GROUP && obj.type === BICst.GROUP.AUTO_GROUP);
        });
        result = result || {};
        return result.text;
    },
    
    isDefaultValue : function () {
        return !this.changed;
    }
});
BI.AnalysisETLOperatorGroupPaneModel.DIMKEY = "dimensions";
BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY = "view";