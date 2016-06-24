/**
 * create by young
 */
BI.AuthoritySelectData = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.AuthoritySelectData.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-authority-select-data",
            height: 30,
            dId: ""
        });
    },

    _init: function () {
        BI.AuthoritySelectData.superclass._init.apply(this, arguments);
        var self = this, packageStructure = BI.Utils.getAllGroupedPackagesTree();
        BI.Utils.getAllPackages(function(packs){
            self.packs = packs;
            var ids = BI.Utils.getAllPackageIDs4Conf();
            self.searcher.setPackage(ids[0]);
        });
        this.searcher = BI.createWidget({
            type: "bi.select_data_searcher",
            element: this.element,
            packages: packageStructure,
            itemsCreator: function (op, populate) {
                if (BI.isKey(op.searchType) && BI.isKey(op.keyword)) {
                    self._getSearchResult(op.searchType, op.keyword, op.packageId, function (result) {
                        populate(result.finded, result.matched);
                    });
                    return;
                }
                if (!op.node) {//根节点， 根据业务包找所有的表
                    populate(self._getTablesStructureByPackId(op.packageId));
                    return;
                }
                if (BI.isNotNull(op.node.isParent)) {
                    populate(self._getFieldsStructureByTableId(op.node.id));
                }
            }
        });
        this.searcher.on(BI.SelectDataSearcher.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.AuthoritySelectData.EVENT_CONFIRM, arguments);
        });
    },

    _getSearchResult: function (type, keyword, packageId, callback) {
        var self = this;
        var searchResult = [], matchResult = [];
        var translations = Data.SharingPool.get("translations");
        //选择了所有数据
        if (type & BI.SelectDataSearchSegment.SECTION_ALL) {
            var packages = BI.Utils.getAllPackageIDs4Conf();
        } else {
            var packages = [packageId];
        }
        //选择了表
        if (type & BI.SelectDataSearchSegment.SECTION_TABLE) {
            var result = [];
            BI.each(packages, function (i, pid) {
                var items = self._getTablesStructureByPackId(pid);
                result.push(BI.Func.getSearchResult(items, keyword));
            });
            BI.each(result, function (i, sch) {
                searchResult = searchResult.concat(sch.finded);
                matchResult = matchResult.concat(sch.matched);
            })
        } else {
            var result = [], map = [];
            BI.each(packages, function (i, pId) {
                var tables = BI.Utils.getTableIDsOfPackageID4Conf(pId);
                var items = [];
                BI.each(tables, function (i, tid) {
                    items = items.concat(self._getFieldsStructureByTableId(tid));
                });
                result.push(BI.Func.getSearchResult(items, keyword));
            });
            BI.each(result, function (i, sch) {
                BI.each(sch.finded, function (j, finded) {
                    if (!map[finded.pId]) {
                        searchResult.push({
                            id: finded.pId,
                            type: "bi.select_data_level0_node",
                            text: translations[finded.pId],
                            value: finded.pId,
                            isParent: true,
                            open: true
                        });
                        map[finded.pId] = true;
                    }
                });
                searchResult = searchResult.concat(sch.finded);
                matchResult = matchResult.concat(sch.matched);
            })
        }
        callback({finded: searchResult, matched: matchResult});
    },

    _getTablesStructureByPackId: function (pId) {
        var self = this;
        var tablesStructure = [];
        var translations = Data.SharingPool.get("translations");
        var tables = self.packs[pId];
        BI.each(tables, function(id, table){
            tablesStructure.push({
                id: id,
                type: "bi.select_data_level0_node",
                text: translations[id],
                value: id,
                isParent: true,
                open: false,
                title: translations[id]
            });
        });
        return tablesStructure;
    },

    _getFieldsStructureByTableId: function (tableId) {
        var fieldStructure = [];
        BI.some(this.packs, function(pId, pack) {
            return BI.some(pack, function (tId, fields) {
                if (tableId === tId) {
                    BI.each(fields, function (i, field) {
                        fieldStructure.push({
                            id: field.id,
                            pId: tableId,
                            type: "bi.select_data_level0_item",
                            fieldType: field.field_type,
                            text: field.field_name,
                            value: field
                        })
                    });
                }
            });
        });
        return fieldStructure;
    }
});
BI.AuthoritySelectData.EVENT_CONFIRM = "AuthoritySelectData.EVENT_CONFIRM";
$.shortcut('bi.authority_select_data', BI.AuthoritySelectData);