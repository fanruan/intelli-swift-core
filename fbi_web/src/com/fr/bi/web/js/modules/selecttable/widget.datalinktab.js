/**
 * @class BI.DataLinksTab
 * @extend BI.Widget
 * 构造data link tab
 */
BI.DataLinksTab = BI.inherit(BI.Widget, {

    constants: {
        NAV_WIDTH: 200

    },

    _defaultConfig: function () {
        return BI.extend(BI.DataLinksTab.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-data-link-tab"
        })
    },

    _init: function () {
        BI.DataLinksTab.superclass._init.apply(this, arguments);
        this.sourceCards = [];
        this.packageCards = [];
        this.etlCards = [];
    },

    populate: function (linkNames) {
        var self = this, o = this.options;
        var dataLinks = [], packages = [], etl = [];
        //这边 value 拼了一下，不好
        BI.each(linkNames, function (i, name) {
            dataLinks.push({
                text: name,
                value: BICst.DATA_LINK.DATA_SOURCE + name
            })
        });
        BI.each(Data.SharingPool.get("packages"), function (id, pack) {
            packages.push({
                text: pack.name,
                value: BICst.DATA_LINK.PACKAGES + id
            })
        });
        if (BI.isNotEmptyArray(o.etl)) {
            etl.push({
                text: BI.i18nText("BI-Etl_Stream"),
                value: BICst.DATA_LINK.ETL + BI.i18nText("BI-Etl_Stream")
            })
        }
        var items = {
            dataLinks: dataLinks,
            packages: packages,
            etl: etl
        };
        var dataLinkGroup = BI.createWidget({
            type: "bi.data_link_group",
            items: items
        });

        this.tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            direction: "left",
            tab: dataLinkGroup,
            defaultShowIndex: false,
            cardCreator: function (v) {
                return self._createLinksCards(v);
            }
        });
        if(BI.isNotEmptyArray(items.etl)) {
            this.tab.setSelect(items.etl[0].value);
        } else if(BI.isNotEmptyArray(items.dataLinks)){
            this.tab.setSelect(items.dataLinks[0].value);
        } else if(BI.isNotEmptyArray(items.packages)) {
            this.tab.setSelect(items.packages[0].value);
        }
    },

    _createLinksCards: function (v) {
        if (v.indexOf(BICst.DATA_LINK.DATA_SOURCE) !== -1) {
            return this._createDataSourceCard(v.slice(BICst.DATA_LINK.DATA_SOURCE.length));
        } else if (v.indexOf(BICst.DATA_LINK.PACKAGES) !== -1) {
            return this._createPackagesCard(v.slice(BICst.DATA_LINK.PACKAGES.length));
        } else if (v.indexOf(BICst.DATA_LINK.ETL) !== -1) {
            return this._createETLCard(v.slice(BICst.DATA_LINK.ETL.length));
        }
    },

    _createDataSourceCard: function (connName) {
        var self = this;
        this.tablesTab = BI.createWidget({
            type: "bi.database_tables_main_pane",
            tables: this.options.tables
        });
        this.tablesTab.populate(connName);
        this.tablesTab.on(BI.DatabaseTablesMainPane.EVENT_CHANGE, function () {
            self.fireEvent(BI.DataLinksTab.EVENT_CHANGE);
        });
        this.sourceCards.push(this.tablesTab);
        return this.tablesTab;
    },

    _createPackagesCard: function (packId) {
        var self = this;
        var packagePane = BI.createWidget({
            type: "bi.package_table_pane",
            packId: packId,
            translations: this.options.translations
        });
        packagePane.on(BI.PackageTablePane.EVENT_CHANGE, function () {
            self.fireEvent(BI.DataLinksTab.EVENT_CHANGE);
        });
        this.packageCards.push(packagePane);
        return packagePane;
    },

    _createETLCard: function () {
        var self = this;
        var etlPane = BI.createWidget({
            type: "bi.select_table_etl_pane",
            etl: self.options.etl,
            currentId: self.options.currentId
        });
        etlPane.on(BI.SelectTableETLPane.EVENT_CHANGE, function () {
            self.fireEvent(BI.DataLinksTab.EVENT_CHANGE);
        });
        this.etlCards.push(etlPane);
        return etlPane;
    },

    getValue: function () {
        var sourceTables = [], packageTables = [], etlTables = [];
        BI.each(this.sourceCards, function (i, sTables) {
            sourceTables = sourceTables.concat(sTables.getValue());
        });
        BI.each(this.packageCards, function (i, pTables) {
            packageTables = packageTables.concat(pTables.getValue());
        });
        BI.each(this.etlCards, function (i, eTables) {
            etlTables = etlTables.concat(eTables.getValue());
        });
        return {
            sTables: sourceTables,
            pTables: packageTables,
            eTables: etlTables
        }
    }
});
BI.DataLinksTab.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.data_links_tab", BI.DataLinksTab);