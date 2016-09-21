/**
 * Created by zcf on 2016/9/14.
 */
BI.StyleSetManager = BI.inherit(FR.OB, {
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
        switch (gs.theme) {
            case BI.THEME_DARK:
                $("body").removeClass().addClass(BI.THEME_DARK);
                break;
            case BI.THEME_LIGHT:
                $("body").removeClass().addClass(BI.THEME_LIGHT);
                break;
            default:
                $("body").removeClass().addClass(BI.THEME_DETAULT);
                break;
        }
    },

    setGlobalStyle: function (id, globalStyle) {
        globalStyle || (globalStyle = {});
        if (BI.isNotNull(globalStyle.mainBackground)) {
            var mainBackground = this._getBackgroundValue(globalStyle, "mainBackground");
        }
        if (BI.isNotNull(globalStyle.widgetBackground)) {
            var widgetBackground = this._getBackgroundValue(globalStyle, "widgetBackground")
        }
        if (BI.isNotNull(globalStyle.titleBackground)) {
            var titleBackground = this._getBackgroundValue(globalStyle, "titleBackground")
        }
        if (BI.isNotNull(globalStyle.titleFont)) {
            var titleFont = globalStyle.titleFont
        }

        if (BI.isNotNull(globalStyle.controlTheme)) {
            var color = globalStyle.controlTheme;
            var border = " 1px solid " + color;
            var rgb = BI.DOM.hex2rgb(color);
            var json = BI.DOM.rgb2json(rgb);
            json.a = 0.2;
            var rgba = BI.DOM.json2rgba(json);
            var style = {
                ".bi-fit-4show ": {"background": mainBackground},
                ".bi-fit ": {"background": mainBackground},
                ".bi-dashboard-widget ": {"background": widgetBackground},
                ".bi-dashboard-widget .shelter-editor-text": {"background": titleBackground},
                ".bi-dashboard-widget .shelter-editor-text .bi-text": titleFont,

                ".bi-control-widget .bi-list-view .list-view-outer": {"border": border},
                ".bi-control-widget .bi-multi-select-trigger": {"border": border},
                ".bi-control-widget .bi-multi-select-combo .multi-select-trigger-icon-button": {"border-left": border},
                ".bi-control-widget .bi-multi-select-check-selected-button": {"color": color},
                ".bi-control-widget .bi-list-view .list-view-toolbar": {"border-top": border, "color": color},
                ".bi-control-widget .bi-list-view .list-view-toolbar > .center-element": {"border-left": border},
                ".bi-control-widget .bi-list-view .list-view-toolbar > .first-element ": {"border-left": "none"},
                ".bi-control-widget .bi-basic-button .bi-button-mask": {"background-color": color},
                ".bi-control-widget .bi-multi-select-check-pane .multi-select-check-selected": {"color": color},
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
                ".bi-control-widget .bi-list-item-active.active, ": {
                    "color": color,
                    "background-color": rgba
                },
                ".bi-control-widget .bi-list-item-active:hover": {"background-color": rgba},
                ".bi-control-widget .bi-list-item-select:active": {
                    "color": color,
                    "background-color": rgba
                },
                ".bi-control-widget .bi-multi-tree-combo .multi-select-trigger-icon-button": {"border-left": border},
                ".bi-control-widget .bi-multi-tree-check-selected-button .trigger-check-selected": {"color": color},
                ".bi-control-widget .bi-multi-tree-check-pane .multi-tree-check-selected": {"color": color},
                ".bi-control-widget .bi-year-trigger": {"border": border},
                ".bi-control-widget .bi-trigger .bi-trigger-icon-button": {"border-left": border},
                ".bi-control-widget .bi-year-popup .year-popup-navigation": {"border-top": border},
                ".bi-control-widget .bi-year-popup .year-popup-navigation > .center-element": {"border-left": border},
                ".bi-control-widget .bi-year-popup .year-popup-navigation > .first-element": {"border-left": "none"},
                ".bi-control-widget .bi-month-trigger": {"border": border},
                ".bi-control-widget .bi-quarter-trigger": {"border": border},
                ".bi-control-widget .bi-multidate-combo": {"border": border},
                ".bi-control-widget .bi-multidate-popup .bi-multidate-popup-item.active": {"background-color": color},
                ".bi-control-widget .bi-multidate-popup .bi-multidate-popup-button": {
                    "color": color,
                    "border-top": border
                },
                ".bi-control-widget .bi-multidate-popup .bi-multidate-popup-label": {
                    "color": color,
                    "border-left": border,
                    "border-right": border,
                    "border-top": border
                },
                ".bi-control-widget .bi-filter-pane": {"border-top": border, "border-left": border},
                ".bi-control-widget .bi-filter-item": {"border-right": border, "border-bottom": border},
                ".bi-control-widget .bi-select-text-trigger": {"border": border},
                ".bi-control-widget .bi-filter-expander": {"border-right": "0px", "border-bottom": "0px"},
                ".bi-control-widget .bi-filter-expander > table > tbody > tr > td.first-element": {
                    "border-right": border + " !important",
                    "border-bottom": border + " !important"
                }
            };
            this.setStyle(id, style);
        }
    }
});