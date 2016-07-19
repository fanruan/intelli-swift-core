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
        var self = this;

        BI.Utils.getAllPackages(function (packs) {
            self.packs = packs;
            var ids = BI.Utils.getAllPackageIDs4Conf();
            if (BI.isEmptyArray(ids)) {
                ids = [BI.Utils.getCurrentPackageId4Conf()]
            }
            self.selectDataPane.setPackage(ids[0]);
        });

        this.selectDataPane = BI.createWidget({
            type: "bi.package_select_data_service",
            element: this.element,
            showRelativeTables: false,
            showExcelView: false,
            showDateGroup: false,
            packageCreator: function () {
                return BI.Utils.getAllGroupedPackagesTree();
            },
            tablesCreator: function (packageId) {
                return self._getTablesStructureByPackId(packageId);
            },
            fieldsCreator: function (tableId, isRelation) {
                return self._getFieldsStructureByTableId(tableId);
            }
        });
        this.selectDataPane.on(BI.PackageSelectDataService.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.AuthoritySelectData.EVENT_CONFIRM, arguments);
        });
    },

    _getTablesStructureByPackId: function (pId) {
        var self = this;
        var tablesStructure = [];
        var translations = Data.SharingPool.get("translations");
        var tables = self.packs[pId];
        BI.each(tables, function (id, table) {
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
        BI.some(this.packs, function (pId, pack) {
            return BI.some(pack, function (tId, fields) {
                if (tableId === tId) {
                    BI.each(fields, function (i, field) {
                        fieldStructure.push({
                            id: field.id,
                            pId: tableId,
                            type: "bi.select_data_level0_item",
                            fieldType: field.field_type,
                            text: field.field_name,
                            value: field.id
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