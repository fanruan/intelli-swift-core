/**
 * Created by fay on 2016/8/24.
 */
BI.TextToolbarContentSelect = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TextToolbarContentSelect.superclass._defaultConfig.apply(this, arguments), {});
    },
    _init: function () {
        BI.TextToolbarContentSelect.superclass._init.apply(this, arguments);
        var o = this.options;
        var label = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Show_Labels")
        });
        var items = o.items || [];
        this.select = BI.createWidget({
            type: "bi.horizontal",
            items: BI.createItems(items, {
                type: "bi.multi_select_item",
                width: 80
            })
        });
        BI.createWidget({
            type: "bi.horizontal",
            element: this.element,
            items: [label, this.select],
            width: 500,
            tgap: 5,
            lgap: 2
        });
    }
});
$.shortcut('bi.text_tool_bar_content_select', BI.TextToolbarContentSelect);