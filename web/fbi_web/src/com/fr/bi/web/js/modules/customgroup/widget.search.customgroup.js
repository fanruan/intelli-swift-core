/**
 * Created by roy on 15/10/22.
 */
BI.CustomGroupSearch = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CustomGroupSearch.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-group-search"
        })
    },
    _init: function () {
        var self = this, o = this.options;
        BI.CustomGroupSearch.superclass._init.apply(this, arguments);
        this.searchEditor = BI.createWidget({
            type: "bi.search_editor",
            cls: "bi-custom-group-search-editor",
            width: 445
        })

        this.searcher = BI.createWidget({
            type: "bi.searcher",
            el: self.searchEditor,
            onSearch: o.onSearch,
            adapter: o.adapter,
            popup: {
                type: "bi.custom_group_searcher_view",
                searcher: o.popup,
                tipText:BI.i18nText("BI-No_Select")
            },
            isAutoSearch: false,
            isAutoSync: false
        })


        this.toolbar = BI.createWidget({
            type: "bi.multi_select_bar",
            text: BI.i18nText("BI-Select_All_Search_Results"),
            height: 30,
            width: 110,
            disabled: true
        })




        BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            element: this.element,
            rhgap: 5,
            items: {
                left: [self.searcher],
                right: [self.toolbar]
            }
        })
    },

    stopSearch: function () {
        this.searcher.stopSearch();
    },


    setValue: function (v) {
        this.searchEditor.setValue(v);
    },

    getValue: function () {
        return this.searchEditor.getValue();
    }
});

$.shortcut("bi.custom_group_search", BI.CustomGroupSearch);