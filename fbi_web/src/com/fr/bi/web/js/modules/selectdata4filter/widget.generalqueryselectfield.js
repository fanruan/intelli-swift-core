/**
 * Created by Young's on 2016/5/9.
 */
BI.GeneralQuerySelectField = BI.inherit(BI.Widget, {

    _defaultConfig: function(){
        return BI.extend(BI.GeneralQuerySelectField.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-filter-select-field",
            field_id: ""
        })
    },

    _init: function(){
        BI.GeneralQuerySelectField.superclass._init.apply(this, arguments);
        var self = this;
        this.searcher = BI.createWidget({
            type: "bi.simple_select_data_searcher",
            element: this.element,
            itemsCreator: function (op, populate) {
                if (BI.isNotNull(op.keyword) && BI.isNotNull(op.searchType)) {
                    var result = self._getSearchResult(op.searchType, op.keyword);
                    populate(result.finded, result.matched);
                }else{
                    if (!op.node) {//根节点， 根据指标找所有的相关表
                        populate(self._getTablesStructureByFieldId(self.options.field_id));
                        return;
                    }
                    if (BI.isNotNull(op.node.isParent)) {
                        populate(self._getFieldsStructureByTableId(op.node.id));
                    }
                }
            }
        });
        this.searcher.on(BI.SimpleSelectDataSearcher.EVENT_CHANGE, function () {
            self.fireEvent(BI.GeneralQuerySelectField.EVENT_CHANGE, self.searcher.getValue());
        });
        this.searcher.on(BI.SimpleSelectDataSearcher.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.GeneralQuerySelectField.EVENT_CLICK_ITEM, arguments);
        });
        this.searcher.populate();
    },

    /**
     * 搜索结果
     * @param type
     * @param keyword
     * @param fId
     * @returns {{finded: Array, matched: Array}}
     * @private
     */
    _getSearchResult: function (type, keyword) {
        var self = this, o = this.options;
        var searchResult = [], matchResult = [];

        //选择了表
        if (type & BI.SelectDataSearchSegment.SECTION_TABLE) {
            var result = BI.Func.getSearchResult(self._getTablesStructureByFieldId(this.options.field_id), keyword);
            searchResult = result.finded;
            matchResult = result.matched;
            //表字段收起来
            BI.each(searchResult, function (i, field) {
                if (BI.isNotNull(field.open)) {
                    field.open = false;
                }
            });
        } else {
            var map = [];
            var tables = this._getTablesStructureByFieldId(o.field_id);
            var items = [];
            BI.each(tables, function (i, table) {
                items = items.concat(self._getFieldsStructureByTableId(table.id));
            });
            var result = BI.Func.getSearchResult(items, keyword);
            BI.each(result.matched.concat(result.finded), function (j, finded) {
                if (!map[finded.pId]) {
                    searchResult.push({
                        id: finded.pId,
                        type: "bi.select_data_level0_node",
                        text: BI.Utils.getTableNameByID(finded.pId),
                        title: BI.Utils.getTableNameByID(finded.pId),
                        value: finded.pId,
                        isParent: true,
                        open: true
                    });
                    map[finded.pId] = true;
                }
            });
            searchResult = searchResult.concat(result.matched.concat(result.finded));
            matchResult = matchResult.concat(result.matched);
        }
        return {
            finded: searchResult,
            matched: matchResult
        }
    },

    /**
     * 本表，所有主表和所有从表
     * @param fId
     * @returns {Array}
     * @private
     */
    _getTablesStructureByFieldId: function(fId){
        var tablesStructure = [];
        this.currentTableId = BI.Utils.getTableIdByFieldID(fId);
        this.primaryRelationTableIds = BI.Utils.getPrimaryRelationTablesByTableID(this.currentTableId);
        //this.foreignRelationTableIds = BI.Utils.getForeignRelationTablesByTableID(this.currentTableId);
        tablesStructure.push({
            id: this.currentTableId,
            type: "bi.select_data_level0_node",
            text: BI.Utils.getTableNameByID(this.currentTableId),
            title: BI.Utils.getTableNameByID(this.currentTableId),
            value: this.currentTableId,
            isParent: true,
            open: false
        });
        BI.each(this.primaryRelationTableIds, function (i, tId) {
            tablesStructure.push({
                id: tId,
                type: "bi.select_data_level0_node",
                text: BI.Utils.getTableNameByID(tId),
                title: BI.Utils.getTableNameByID(tId),
                value: tId,
                isParent: true,
                open: false
            });
        });
        //BI.each(this.foreignRelationTableIds, function (i, tId) {
        //    tablesStructure.push({
        //        id: tId,
        //        type: "bi.select_data_level0_node",
        //        text: BI.Utils.getTableNameByID(tId),
        //        title: BI.Utils.getTableNameByID(tId),
        //        value: tId,
        //        isParent: true,
        //        open: false
        //    });
        //});
        return tablesStructure;
    },

    /**
     * 单击展开某个表
     * @param tId
     * @returns {Array}
     * @private
     */
    _getFieldsStructureByTableId: function (tId) {
        var fieldStructure = [];
        var self = this;
        //count, string, number, date
        BI.each(BI.Utils.getSortedFieldIdsOfOneTableByTableId(tId), function (i, fid) {
            if(BI.Utils.getFieldIsUsableByID(fid) === false){
                return;
            }
            //从表-只需要数值类字段和记录数
            //if(BI.contains(self.foreignRelationTableIds, tId) && (BI.Utils.getFieldTypeByID(fid) === BICst.COLUMN.STRING || BI.Utils.getFieldTypeByID(fid) === BICst.COLUMN.DATE)){
            //    return;
            //}
            //主表-不需要记录数
            if(BI.contains(self.primaryRelationTableIds, tId) && (BI.Utils.getFieldTypeByID(fid) === BICst.COLUMN.COUNTER)){
                return;
            }
            var fieldName = BI.Utils.getFieldNameByID(fid);
            fieldStructure.push({
                id: fid,
                pId: tId,
                type: "bi.select_data_level0_item",
                fieldType: BI.Utils.getFieldTypeByID(fid),
                text: fieldName,
                title: fieldName,
                value: fid
            })
        });
        return fieldStructure;
    }
});
BI.GeneralQuerySelectField.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
BI.GeneralQuerySelectField.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.general_query_select_field", BI.GeneralQuerySelectField);