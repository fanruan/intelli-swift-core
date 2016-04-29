/**
 * 维度管理器Model
 *
 * Created by GUY on 2016/3/17.
 * @class BI.DimensionsManagerModel
 * @extends BI.Widget
 */
BI.DimensionsManagerModel = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DimensionsManagerModel.superclass._defaultConfig.apply(this, arguments), {
            wId: ""
        });
    },

    _init: function () {
        BI.DimensionsManagerModel.superclass._init.apply(this, arguments);
        this.viewMap = {};
        this.populate();
    },

    _join: function(newType){
        var self = this;
        this.viewMap[newType] = this.viewMap[newType] || {};
        //向viewMap中添加新添加的dimension
        BI.each(this.viewMap[this.type], function(regionId, dIds){
            BI.each(dIds, function(idx, dId){
                var find = BI.find(self.viewMap[newType], function(regionId, ds){
                    return BI.contains(ds, dId);
                });
                if(BI.isNull(find)){
                    if(regionId < BICst.REGION.TARGET1){
                        self.viewMap[newType][BICst.REGION.DIMENSION1] = self.viewMap[newType][BICst.REGION.DIMENSION1] || [];
                        self.viewMap[newType][BICst.REGION.DIMENSION1].push(dId);
                    }else{
                        self.viewMap[newType][BICst.REGION.TARGET1] = self.viewMap[newType][BICst.REGION.TARGET1] || [];
                        self.viewMap[newType][BICst.REGION.TARGET1].push(dId);
                    }
                }
            })
        });
        //从viewMap中删除已被删除的dimension
        BI.each(this.viewMap[newType], function(regionId, dIds){
            BI.each(dIds, function(idx, dId){
                var result = BI.find(self.viewMap[self.type], function(regionId, ds){
                    return BI.contains(ds, dId);
                });
                if(BI.isNull(result)){
                    BI.remove(dIds, dId);
                }
            })
        });
    },

    setType: function (type) {
        this._join(type);
        this.type = type;
    },

    getType: function () {
        return this.type;
    },

    setViews: function (view) {
        this.viewMap[this.type] = view;
    },

    getViews: function () {
        return this.viewMap[this.type];
    },

    getValue: function () {
        return {
            type: this.type,
            view: this.viewMap[this.type] || []
        }
    },

    populate: function () {
        var o = this.options;
        this.type = BI.Utils.getWidgetTypeByID(o.wId);
        this.viewMap[this.type] = BI.Utils.getWidgetViewByID(o.wId);
    }
});