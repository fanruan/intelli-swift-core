/**
 * @class BI.PackageSearcherResultPane
 * @extend BI.Widget
 * 业务包搜索结果面板
 */
BI.PackageSearcherResultPane = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.PackageSearcherResultPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-package-searcher-result-pane"
        })
    },

    _init: function(){
        BI.PackageSearcherResultPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.resultPane = BI.createWidget({
            type: "bi.searcher_view",
            searcher: {
                type: "bi.package_tables_list_pane"
            }
        });
        this.resultPane.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.resultPane,
                top: 0,
                left: 0,
                right: 0,
                bottom: 0
            }]
        })
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

    populate: function (searchResult, matchResult, keyword) {
        //不去构造matcher了
        arguments[0] = matchResult.concat(searchResult);
        arguments[1] = [];
        this.resultPane.populate.apply(this.resultPane, arguments);
    }
});

$.shortcut("bi.package_searcher_result_pane", BI.PackageSearcherResultPane);