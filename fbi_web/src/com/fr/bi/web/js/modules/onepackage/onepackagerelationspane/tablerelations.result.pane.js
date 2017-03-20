/**
 * 关联视图搜索结果面板
 * Created by Young's on 2017/2/20.
 */
BI.PackageTableRelationResultPane = BI.inherit(BI.PackageTableRelationsPane, {
    _defaultConfig: function () {
        return BI.extend(BI.PackageTableRelationResultPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-package-table-relation-result-pane"
        });
    },

    _init: function () {
        BI.PackageTableRelationResultPane.superclass._init.apply(this, arguments);
        this.resultPane = BI.createWidget({
            type: "bi.absolute",
            element: this.element
        });
    },

    startSearch: function () {
        this.options.onStartSearch.apply(this, arguments);
    },

    stopSearch: function () {
        this.options.onStopSearch.apply(this, arguments);
    },

    empty: function () {
        this.resultPane.empty();
    },

    populate: function (items, tableIds, keyword) {
        var resultItems = [];
        this.resultPane.empty();
        BI.each(items, function (i, item) {
            var foreign = item.foreign || {}, primary = item.primary || {};
            var res = BI.Func.getSearchResult([foreign, primary], keyword, "regionText");
            var foundRes = res.finded;
            var matchIds = [];
            BI.each(foundRes, function (i, find) {
                if (tableIds.contains(find.region)) {
                    matchIds.push(find.region);
                }
            });
            if (matchIds.length > 0) {
                if (matchIds.contains(foreign.region)) {
                    foreign.keyword = keyword;
                }
                if (matchIds.contains(primary.region)) {
                    primary.keyword = keyword;
                }
                resultItems.push(item);
            }
        });
        if (resultItems.length === 0) {
            this.resultPane.addItem({
                el: {
                    type: "bi.label",
                    cls: "no-result-comment",
                    textAlign: "center",
                    text: BI.i18nText("BI-No_Select")
                },
                top: 10,
                left: 0,
                right: 0
            });
            return;
        }
        var resultPane = BI.createWidget({
            type: "bi.package_table_relations_pane",
            element: this.element
        });
        resultPane.refresh(resultItems);
    }
});
$.shortcut("bi.package_table_relation_result_pane", BI.PackageTableRelationResultPane);