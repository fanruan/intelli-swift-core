/**
 * Created by zcf on 2016/9/14.
 */
BI.StyleSetManager = BI.inherit(FR.OB, {
    _init: function () {
        BI.StyleSetManager.superclass._init.apply(this, arguments);
    },

    _getBackgroundValue: function (gs, name) {
        var result = "";
        if (gs[name]["type"] == 1) {
            result = gs[name]["value"];
            return result
        }
        if (gs[name]["type"] == 2) {
            result = "url(" + FR.servletURL + "?op=fr_bi&cmd=get_uploaded_image&image_id=" + gs[name]["value"] + ")";
            return result
        }
    },

    setStyle: function (id, objects) {
        if (BI.isNotNull(objects)) {
            var setLoad = new BI.StyleLoaderManager;
            var result = "";
            BI.each(objects, function (cls, object) {
                result += cls + "{";
                BI.each(object, function (name, value) {
                    result += name + ":" + value + ";"
                });
                result += "} ";
            });
            setLoad.removeStyle(id);
            setLoad.loadStyle(id, result);
        }
    },
    setThemeStyle: function (gs) {
        gs = gs || {};
        if (BI.isNotNull(gs.predictionValue)) {
            var style = gs.predictionValue.currentStyle;
            if (style == BICst.GLOBALPREDICTIONSTYLE.DEFAULT) {
                $("body").removeClass("bi-theme-default bi-theme-dark bi-theme-light").addClass("bi-theme-default");
            }
            if (style == BICst.GLOBALPREDICTIONSTYLE.ONE) {
                $("body").removeClass("bi-theme-default bi-theme-dark bi-theme-light").addClass("bi-theme-dark");
            }
            if (style == BICst.GLOBALPREDICTIONSTYLE.TWO) {
                $("body").removeClass("bi-theme-default bi-theme-dark bi-theme-light").addClass("bi-theme-light");
            }
        }
    },
    setGlobalStyle: function (id, globalStyle) {
        if (BI.isNotNull(globalStyle)) {
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
            // var GStyle = {
            //     ".fit-dashboard ": {"background": mainBackground},
            //     ".bi-dashboard-widget ": {"background": widgetBackground},
            //     ".bi-dashboard-widget .shelter-editor-text": {"background": titleBackground},
            //     ".bi-dashboard-widget .shelter-editor-text .bi-text": titleFont
            // };

            //this.setStyle(GStyle);
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
    }
});