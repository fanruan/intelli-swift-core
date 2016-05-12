/**
 * @class BI.AbstractDimensionCombo
 * @extend BI.AbstractDimensionTargetCombo
 * 字段类型date
 */
BI.AbstractDimensionCombo = BI.inherit(BI.AbstractDimensionTargetCombo, {

    defaultItems: function () {

    },

    typeConfig: function(){

    },

    _assertSort: function (val) {

    },

    _assertGroup:function(val){

    },

    _defaultConfig: function(){
        return BI.extend(BI.AbstractDimensionCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-abstract-dimension-combo"
        })
    },

    _init: function(){
        BI.AbstractDimensionCombo.superclass._init.apply(this, arguments);
    },

    _rebuildItems:function(){
        var items = this.defaultItems();
        var o = this.options, result = this._positionAscAndDesc(items);
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
        ascend.children.push({text: BI.Utils.getDimensionNameByID(o.dId), value: o.dId});
        descend.children.push({text: BI.Utils.getDimensionNameByID(o.dId), value: o.dId});

        if(BI.isNull(this.widgetId)){
            this.widgetId = BI.Utils.getWidgetIDByDimensionID(o.dId);
        }
        var targetIds = BI.Utils.getAllTargetDimensionIDs(this.widgetId);
        var targets = [];
        BI.each(targetIds,function(idx,tid){
            targets.push({id: tid, value: BI.Utils.getDimensionNameByID(tid)});
        });

        BI.each(targets,function(idx,target){
            ascend.children.push({text: target.value, value: target.id});
            descend.children.push({text: target.value, value: target.id});
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
        var sort = BI.Utils.getDimensionSortByID(o.dId);
        var group = BI.Utils.getDimensionGroupByID(o.dId);
        sort = this._assertSort(sort);
        group = this._assertGroup(group);

        var result = {};

        var sortValue = {},groupValue = {};

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
        result.sort = sortValue;
        result.group = groupValue;
        return [result.sort, result.group];
    }

});