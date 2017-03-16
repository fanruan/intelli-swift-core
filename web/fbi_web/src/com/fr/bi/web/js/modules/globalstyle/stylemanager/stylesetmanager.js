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

        var style = {
            ".bi-fit-4show ": {"background": mainBackground},
            ".bi-fit ": {"background": mainBackground},

            ".bi-dashboard-widget ": {"background": widgetBackground}
        };
        this.setStyle(this.constant.GLOBAL_STYLE, style);
    }
});