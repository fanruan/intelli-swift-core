/**
 * Created by guy on 2016/9/22.
 */
BI.GlobalStylePaginationIcon = BI.inherit(BI.BasicButton, {
    _defaultConfig: function () {
        return BI.extend(BI.GlobalStylePaginationIcon.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-global-style-pagination-icon",
            forceSelected: true,
            width: 20,
            height: 20
        })
    },

    _init: function () {
        BI.GlobalStylePaginationIcon.superclass._init.apply(this, arguments);
        var self = this;
        this.icon = BI.createWidget({
            type: "bi.icon_button",
            cls: "page-pagination-font",
            element: this.element
        });
    },

    doClick: function () {
        BI.GlobalStylePaginationIcon.superclass.doClick.apply(this, arguments);
        if (this.isValid()) {
            this.fireEvent(BI.GlobalStylePaginationIcon.EVENT_CHANGE);
        }
    }
});
BI.GlobalStylePaginationIcon.EVENT_CHANGE = "BI.GlobalStylePaginationIcon.EVENT_CHANGE";
$.shortcut("bi.global_style_pagination_icon", BI.GlobalStylePaginationIcon);