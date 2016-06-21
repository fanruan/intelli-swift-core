/**
 * @class BI.AbstractDimensionCombo
 * @extend BI.AbstractDimensionTargetCombo
 * 字段类型date
 */
BI.AbstractDimensionCombo = BI.inherit(BI.AbstractDimensionTargetCombo, {

    constants: {
        SORT: 0,
        GROUP: 1,
        POSITION: 2
    },

    defaultItems: function () {

    },

    typeConfig: function(){

    },

    _assertSort: function (val) {

    },

    _assertGroup:function(val){

    },

    _assertAddress: function () {

    },

    _defaultConfig: function(){
        return BI.extend(BI.AbstractDimensionCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-abstract-dimension-combo"
        })
    },

    _init: function(){
        BI.AbstractDimensionCombo.superclass._init.apply(this, arguments);
    },

    rebuildItemsForGISMAP: function () {
        var items = this.defaultItems();
        items[0] = [{
            text: BI.i18nText("BI-Address"),
            value: this.typeConfig().POSITION_BY_ADDRESS,
            cls: "dot-e-font"
        }, {
            el: {
                text: BI.i18nText("BI-Lng_Lat"),
                value: this.typeConfig().POSITION_BY_LNG_LAT,
                iconCls1: ""
            },
            children: [{
                text: BI.i18nText("BI-Lng_First"),
                value: BICst.GIS_POSITION_TYPE.LNG_FIRST,
                cls: "dot-e-font"
            },{
                text: BI.i18nText("BI-Lat_First"),
                value: BICst.GIS_POSITION_TYPE.LAT_FIRST,
                cls: "dot-e-font"
            }]
        }];
        var o = this.options;
        var lngLat = items[0][1];

        var selectedValues = this._createValue();

        switch (selectedValues[this.constants.POSITION].value) {
            case this.typeConfig().POSITION_BY_LNG_LAT :
                var text = BI.Utils.getDimensionPositionByID(o.dId).type === BICst.GIS_POSITION_TYPE.LNG_FIRST ? BI.i18nText("BI-Lng_First") : BI.i18nText("BI-Lat_First");
                this._changeElText(lngLat.el,BI.i18nText("BI-Lng_Lat") + "(" + text +")");
                break;
            default :
                this._changeElText(lngLat.el,BI.i18nText("BI-Lng_Lat"));
                break;
        }

        if(items.length > 0 && BI.isNotNull(items[items.length - 1][0])){
            var fieldId = BI.Utils.getFieldIDByDimensionID(o.dId);
            var fieldName = BI.Utils.getFieldNameByID(fieldId);
            var tableName = BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(fieldId));
            items[items.length - 1][0].text = items[items.length - 1][0].title = BI.i18nText("BI-Dimension_From") + ": " + tableName + "."  + fieldName;
        }
        return items;
    },

    _rebuildItems:function(){
        var o = this.options;
        if(BI.Utils.getWidgetTypeByID(BI.Utils.getWidgetIDByDimensionID(o.dId)) === BICst.WIDGET.GIS_MAP){
            return this.rebuildItemsForGISMAP();
        }
        var items = this.defaultItems();
        var result = this._positionAscAndDesc(items);
        var ascend = result.ascend,descend = result.descend;

        var selectedValues = this._createValue();

        switch (selectedValues[0].value) {         //先更新老子的文本
            case this.typeConfig().ASCEND :
                this._changeElText(ascend.el,BI.has(selectedValues[0], "childValue") ? BI.i18nText("BI-Ascend") + "(" + BI.Utils.getDimensionNameByID(selectedValues[0].childValue) + ")" : BI.i18nText("BI-Ascend"));
                this._changeElText(descend.el,BI.i18nText("BI-Descend"));
                break;
            case this.typeConfig().DESCEND :
                this._changeElText(ascend.el,BI.i18nText("BI-Ascend"));
                this._changeElText(descend.el,BI.has(selectedValues[0], "childValue") ? BI.i18nText("BI-Descend") + "(" + BI.Utils.getDimensionNameByID(selectedValues[0].childValue) + ")" : BI.i18nText("BI-Descend"));
                break;
            default :
                this._changeElText(ascend.el,BI.i18nText("BI-Ascend"));
                this._changeElText(descend.el,BI.i18nText("BI-Descend"));
                break;
        }
        ascend.children = [];                //再重置儿子的属性
        descend.children = [];
        ascend.children.push({text: BI.Utils.getDimensionNameByID(o.dId), value: o.dId, cls: "dot-e-font"});
        descend.children.push({text: BI.Utils.getDimensionNameByID(o.dId), value: o.dId, cls: "dot-e-font"});

        if(BI.isNull(this.widgetId)){
            this.widgetId = BI.Utils.getWidgetIDByDimensionID(o.dId);
        }
        var targetIds = BI.Utils.getAllTargetDimensionIDs(this.widgetId);
        var targets = [];
        BI.each(targetIds,function(idx,tid){
            targets.push({id: tid, value: BI.Utils.getDimensionNameByID(tid)});
        });

        BI.each(targets,function(idx,target){
            ascend.children.push({text: target.value, value: target.id, cls: "dot-e-font"});
            descend.children.push({text: target.value, value: target.id, cls: "dot-e-font"});
        });

        if(items.length > 0 && BI.isNotNull(items[items.length - 1][0])){
            var fieldId = BI.Utils.getFieldIDByDimensionID(o.dId);
            var fieldName = BI.Utils.getFieldNameByID(fieldId);
            var tableName = BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(fieldId));
            items[items.length - 1][0].text = items[items.length - 1][0].title = BI.i18nText("BI-Dimension_From") + ": " + tableName + "."  + fieldName;
        }
        return items;
    },

    _changeElText:function(el,text){
        el.text = text;
        el.title = text;
    },

    _positionAscAndDesc:function(items){
        var ascend = {},descend = {};
        var findCount = 0;
        BI.any(items,function(idx,item){
            BI.any(item,function(idx, it){
                var itE = BI.stripEL(it);
                if(itE.text === BI.i18nText("BI-Ascend")){
                    ascend = it;
                    findCount++;
                }
                if(itE.text === BI.i18nText("BI-Descend")){
                    descend = it;
                    findCount++;
                }
            });
            if(findCount === 2){
                return true;
            }
        });
        return {ascend: ascend, descend: descend};
    },

    _createValue: function () {
        var o = this.options;
        var address = BI.Utils.getDimensionPositionByID(o.dId);
        var sort = BI.Utils.getDimensionSortByID(o.dId);
        var group = BI.Utils.getDimensionGroupByID(o.dId);
        sort = this._assertSort(sort);
        group = this._assertGroup(group);
        address = this._assertAddress(address);

        var result = {};

        var sortValue = {},groupValue = {}, addressValue = {};

        switch (sort.type){
            case BICst.SORT.ASC:
                sortValue.value = this.typeConfig().ASCEND;
                sortValue.childValue = sort.sort_target;
                break;
            case BICst.SORT.DESC:
                sortValue.value = this.typeConfig().DESCEND;
                sortValue.childValue = sort.sort_target;
                break;
            case BICst.SORT.NONE:
                sortValue.value = this.typeConfig().NOT_SORT;
                break;
            case BICst.SORT.CUSTOM:
                sortValue.value = this.typeConfig().SORT_BY_CUSTOM;
                break;
            default :
                break;
        }

        switch(group.type){
            case BICst.GROUP.ID_GROUP:
                groupValue.value = this.typeConfig().GROUP_BY_VALUE;
                break;
            case BICst.GROUP.AUTO_GROUP:
            case BICst.GROUP.CUSTOM_NUMBER_GROUP:
                groupValue.value = this.typeConfig().GROUP_SETTING;
                break;
            case BICst.GROUP.CUSTOM_GROUP:
                groupValue.value = this.typeConfig().GROUP_BY_CUSTOM;
                break;
            case BICst.GROUP.Y:
                groupValue.value = this.typeConfig().YEAR;
                break;
            case BICst.GROUP.M:
                groupValue.value = this.typeConfig().MONTH;
                break;
            case BICst.GROUP.W:
                groupValue.value = this.typeConfig().WEEK;
                break;
            case BICst.GROUP.YMD:
                groupValue.value = this.typeConfig().DATE;
                break;
            case BICst.GROUP.S:
                groupValue.value = this.typeConfig().QUARTER;
                break;
        }

        switch(address.type){
            case BICst.GIS_POSITION_TYPE.ADDRESS:
                addressValue.value = this.typeConfig().POSITION_BY_ADDRESS;
                break;
            case BICst.GIS_POSITION_TYPE.LNG_FIRST:
                addressValue.value = this.typeConfig().POSITION_BY_LNG_LAT;
                addressValue.childValue =  BICst.GIS_POSITION_TYPE.LNG_FIRST;
                break;
            case BICst.GIS_POSITION_TYPE.LAT_FIRST:
                addressValue.value = this.typeConfig().POSITION_BY_LNG_LAT;
                addressValue.childValue = BICst.GIS_POSITION_TYPE.LAT_FIRST;
                break;
        }
        result.sort = sortValue;
        result.group = groupValue;
        result.position = addressValue;
        return [result.sort, result.group, result.position];
    }

});