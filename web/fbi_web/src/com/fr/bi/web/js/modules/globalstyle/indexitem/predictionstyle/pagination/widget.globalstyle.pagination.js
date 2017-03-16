/**
 * Created by guy on 2016/9/22.
 */
BI.GlobalStylePagination = BI.inherit(BI.Widget, {
    _const: {
        pageCount: 6
    },

    _defaultConfig: function () {
        return BI.extend(BI.GlobalStylePagination.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-global-style-pagination"
        })
    },

    _init: function () {
        BI.GlobalStylePagination.superclass._init.apply(this, arguments);
        var self = this;
        this.button_group = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            items: [],
            layouts: [{
                type: "bi.float_center_adapt",
                items: [{
                    type: "bi.horizontal"
                }]
            }]
        });
        this.button_group.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStylePagination.EVENT_CHANGE);
        })
    },

    getValue: function () {
        return this.button_group.getValue()[0];
    },

    setValue: function (v) {
        this.button_group.setValue(v);
    },

    populate: function (customStyles) {
        var icons = [];
        var pages = Math.floor((customStyles.length + BI.size(BICst.GLOBAL_PREDICTION_STYLE) - 1) / this._const.pageCount);
        for (var i = 0; i < pages + 1; i++) {
            icons.push({
                type: "bi.global_style_pagination_icon",
                value: i + 1
            });
        }
        this.button_group.populate(icons);
    }
});
BI.GlobalStylePagination.EVENT_CHANGE = "BI.GlobalStylePagination.EVENT_CHANGE";
$.shortcut("bi.global_style_pagination", BI.GlobalStylePagination);