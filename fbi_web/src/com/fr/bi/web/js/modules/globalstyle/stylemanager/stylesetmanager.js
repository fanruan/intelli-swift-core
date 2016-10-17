/**
 * Created by zcf on 2016/9/14.
 */
BI.StyleSetManager = BI.inherit(FR.OB, {

    constant: {
        GLOBAL_STYLE: "__global_style__"
    },

    _init: function () {
        BI.StyleSetManager.superclass._init.apply(this, arguments);
    },

    _getBackgroundValue: function (gs, name) {
        if (!gs[name]) {
            return "";
        }
        switch (gs[name].type) {
            case BICst.BACKGROUND_TYPE.COLOR:
                return gs[name].value;
            case BICst.BACKGROUND_TYPE.IMAGE:
                return "url(" + FR.servletURL + "?op=fr_bi&cmd=get_uploaded_image&image_id=" + gs[name]["value"] + ")";
        }
        return "";
    },

    setStyle: function (id, objects) {
        var result = "";
        BI.each(objects, function (cls, object) {
            result += cls + "{";
            BI.each(object, function (name, value) {
                result += name + ":" + value + ";"
            });
            result += "} ";
        });
        BI.StyleLoaders.removeStyle(id).loadStyle(id, result);
    },

    setThemeStyle: function (gs) {
        gs = gs || {};
        $("body").removeClass(BICst.THEME_DEFAULT).removeClass(BICst.THEME_DARK).removeClass(BICst.THEME_LIGHT);
        switch (gs.theme) {
            case BICst.THEME_DARK:
                $("body").addClass(BICst.THEME_DARK);
                break;
            case BICst.THEME_LIGHT:
                $("body").addClass(BICst.THEME_LIGHT);
                break;
            default:
                $("body").addClass(BICst.THEME_DEFAULT);
                break;
        }
    },

    setGlobalStyle: function (globalStyle) {
        globalStyle || (globalStyle = {});
        var mainBackground = this._getBackgroundValue(globalStyle, "mainBackground");
        var widgetBackground = this._getBackgroundValue(globalStyle, "widgetBackground");
        var titleBackground = this._getBackgroundValue(globalStyle, "titleBackground");
        var titleFont = globalStyle.titleFont;

        var color = globalStyle.controlTheme || "";
        if(color) {
            var border = " 1px solid " + color;
            var rgb = BI.DOM.hex2rgb(color);
            var json = BI.DOM.rgb2json(rgb);
            json.a = 0.2;
            var rgba = BI.DOM.json2rgba(json);
        } else {
            var border = "";
            var rgba = "";
        }

        var style = {
            ".bi-fit-4show ": {"background": mainBackground},
            ".bi-fit ": {"background": mainBackground},

            ".bi-dashboard-widget ": {"background": widgetBackground},
            ".bi-dashboard-widget .dashboard-widget-title": BI.extend({"background": titleBackground}, titleFont),
            // ".bi-control-widget .dashboard-widget-title": {"background": "inherit"},


            //控件通用规则
            ".bi-dashboard-widget .bi-combo .bi-text-trigger": {"border": border},
            ".bi-dashboard-widget .bi-trigger-icon-button": {"border-left": border},
            ".bi-dashboard-widget .bi-trigger-icon-button .b-font:before": {"color": color},

            ".bi-dashboard-widget .bi-list-view .list-view-outer": {"border": border},
            ".bi-dashboard-widget .bi-list-view .list-view-toolbar": {"border-top": border},
            ".bi-dashboard-widget .bi-list-view .list-view-toolbar > .center-element": {"border-left": border},
            ".bi-dashboard-widget .bi-list-view .list-view-toolbar > .first-element ": {"border-left": "none"},
            ".bi-dashboard-widget .bi-button .bi-button-mask": {"background-color": color},


            //文本控件trigger
            ".bi-control-widget .bi-multi-select-combo .bi-multi-select-trigger": {"border": border},
            ".bi-control-widget .bi-multi-select-combo .multi-select-trigger-icon-button": {"border-left": border},
            ".bi-control-widget .bi-multi-select-combo .b-font:before": {"color": color},
            ".bi-control-widget .bi-multi-select-combo .bi-multi-select-check-selected-button": {"color": color},

            //数值控件
            ".bi-control-widget .bi-numerical-interval .numerical-interval-small-editor": {
                "border-top": border,
                "border-bottom": border,
                "border-left": border
            },
            ".bi-control-widget .bi-numerical-interval .numerical-interval-small-combo": {"border": border},
            ".bi-control-widget .bi-numerical-interval .numerical-interval-big-combo": {"border": border},
            ".bi-control-widget .bi-numerical-interval .numerical-interval-big-editor": {
                "border-top": border,
                "border-bottom": border,
                "border-right": border
            },

            //树控件
            ".bi-control-widget .bi-multi-tree-combo .bi-multi-select-trigger": {"border": border},
            ".bi-control-widget .bi-multi-tree-combo .multi-select-trigger-icon-button": {"border-left": border},
            ".bi-control-widget .bi-multi-tree-combo .b-font:before": {"color": color},
            ".bi-control-widget .bi-multi-tree-combo .bi-multi-tree-check-selected-button .trigger-check-selected": {"color": color},


            //年份控件
            ".bi-control-widget .bi-year-combo .bi-year-trigger": {"border": border},
            ".bi-control-widget .bi-year-combo .bi-year-trigger .bi-trigger-icon-button": {"border-left": border},
            ".bi-control-widget .bi-year-combo .bi-year-popup .year-popup-navigation": {"border-top": border},
            ".bi-control-widget .bi-year-combo .bi-year-popup .year-popup-navigation > .center-element": {"border-left": border},
            ".bi-control-widget .bi-year-combo .bi-year-popup .year-popup-navigation > .first-element": {"border-left": "none"},

            //月控件
            ".bi-control-widget .bi-month-trigger": {"border": border},

            //季度控件
            ".bi-control-widget .bi-quarter-trigger": {"border": border},

            //日期控件
            ".bi-control-widget .bi-multidate-combo .bi-combo .bi-trigger": {"border": border},
            ".bi-control-widget .bi-multidate-combo .bi-multidate-popup .bi-multidate-popup-item.active": {"background-color": color},
            ".bi-control-widget .bi-multidate-combo .bi-multidate-popup .bi-multidate-popup-item:active": {"background-color": color},
            ".bi-control-widget .bi-multidate-combo .bi-multidate-popup .bi-multidate-popup-button": {
                "border-top": border
            },
            ".bi-control-widget .bi-multidate-combo .bi-multidate-popup .bi-multidate-popup-label": {
                "border-left": border,
                "border-right": border,
                "border-top": border
            },

            ".bi-control-widget bi-calendar .bi-list-item-active.active, ": {
                "color": color,
                "background-color": rgba
            },
            ".bi-control-widget bi-calendar .bi-list-item-active:hover": {"background-color": rgba},
            ".bi-control-widget bi-calendar .bi-list-item-select:active": {
                "color": color,
                "background-color": rgba
            },

            //通用查询
            ".bi-control-widget .bi-filter-pane": {"border-top": border, "border-left": border},
            ".bi-control-widget .bi-filter-pane .bi-filter-item": {"border-right": border, "border-bottom": border},
            ".bi-control-widget .bi-filter-pane .bi-filter-expander": {"border-right": "0px", "border-bottom": "0px"},
            ".bi-control-widget .bi-filter-pane .bi-filter-expander > table > tbody > tr > td.first-element": {
                "border-right": border + " !important",
                "border-bottom": border + " !important"
            },
            ".bi-control-widget .bi-filter-pane .condition-operator-input": {"border": border},
        };
        this.setStyle(this.constant.GLOBAL_STYLE, style);
    }
});