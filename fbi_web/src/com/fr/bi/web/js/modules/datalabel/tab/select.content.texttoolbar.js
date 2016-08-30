/**
 * Created by fay on 2016/8/24.
 */
BI.TextToolbarContentSelect = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TextToolbarContentSelect.superclass._defaultConfig.apply(this, arguments), {});
    },

    _init: function () {
        BI.TextToolbarContentSelect.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var label = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Show_Labels")
        });
        var items = o.items || [];
        this.items = BI.createWidgets(items, {
            type: "bi.multi_select_item",
            width: 100,
            warningTitle: BI.i18nText("BI-At_Least_One_Value")
        });
        BI.each(this.items, function (i, item) {
            item.setSelected(true);
            item.on(BI.Controller.EVENT_CHANGE, function () {
                self._checkState();
                self.fireEvent(BI.TextToolbarContentSelect.EVENT_CHANGE, arguments);
            })
        });
        this.select = BI.createWidget({
            type: "bi.horizontal",
            items: this.items
        });
        BI.createWidget({
            type: "bi.horizontal",
            element: this.element,
            items: [label, this.select],
            width: 500,
            tgap: 5,
            lgap: 2
        });
    },

    _checkState: function () {
        var value = this.getValue();
        var count = BI.countBy(value, function (i, num) {
            return num ? 'true' : 'false';
        });
        if (count['true'] === 1) {
            BI.each(this.items, function (i, item) {
                if (item.isSelected()) {
                    item.setEnable(false);
                }
            });
        } else {
            BI.each(this.items, function (i, item) {
                item.setEnable(true);
            });
        }
    },

    setValue: function (v) {
        v || (v = []);
        var self = this;
        if (v.length <= self.items.length) {
            BI.each(v, function (i, value) {
                self.items[i].setSelected(value);
            });
        }
        this._checkState();
    },

    getValue: function () {
        var result = [];
        BI.each(this.items, function (i, item) {
            result.push(item.isSelected());
        });
        return result;
    }
});
BI.TextToolbarContentSelect.EVENT_CHANGE = "BI.TextToolbarContentSelect.EVENT_CHANGE";
$.shortcut('bi.text_tool_bar_content_select', BI.TextToolbarContentSelect);