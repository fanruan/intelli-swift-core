/**
 * 选取业务包表搜索结果面板
 * Created by Young's on 2016/10/24.
 */
BI.PackageTablesSearchResultPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.PackageSearcherResultPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-package-table-search-result-pane"
        })
    },

    _init: function () {
        BI.PackageSearcherResultPane.superclass._init.apply(this, arguments);
        this.resultPane = BI.createWidget({
            type: "bi.absolute",
            element: this.element
        });
    },

    empty: function () {
        this.resultPane.empty();
    },

    populate: function (searchResult, matchResult, keyword, selectedTables) {
        var self = this;
        this.empty();
        var tables = BI.createWidget({
            type: "bi.button_group",
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_MULTI,
            behaviors: {
                redmark: function () {
                    return true;
                }
            },
            layouts: [{
                type: "bi.left",
                scrollable: true,
                hgap: 10,
                vgap: 10
            }]
        });
        var items = BI.createItems(matchResult.concat(searchResult), {
            type: "bi.database_table",
            cls: "bi-table-ha-button"
        });
        tables.populate(items, keyword);
        tables.setValue(selectedTables);
        tables.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        this.resultPane.addItem({
            el: tables,
            top: 0,
            right: 0,
            bottom: 0,
            left: 0
        });
    }
});
$.shortcut("bi.package_table_search_result_pane", BI.PackageTablesSearchResultPane);