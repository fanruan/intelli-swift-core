/**
 * style加载管理器
 *
 * Created by GUY on 2015/9/7.
 * @class
 */
BI.StyleLoaderManager = BI.inherit(FR.OB, {
    _defaultConfig: function () {
        return BI.extend(BI.StyleLoaderManager.superclass._defaultConfig.apply(this, arguments), {});
    },

    _init: function () {
        BI.StyleLoaderManager.superclass._init.apply(this, arguments);
        this.stylesManager = {};
    },

    loadStyle: function (name, styleString) {
        var docEl = document.documentElement;
        var styleEl = document.createElement('style');
        var firstElementChild = docEl.firstElementChild || docEl.children[0];   //兼容IE8
        firstElementChild.appendChild(styleEl);
        $('<style type="text/css">' + styleString + '</style>').appendTo('head');
        this.stylesManager[name] = styleEl;

        return this;
    },

    get: function (name) {
        return this.stylesManager[name];
    },

    has: function (name) {
        return this.stylesManager[name] != null;
    },

    removeStyle: function (name) {
        if (!this.has(name)) {
            return this;
        }
        this.stylesManager[name].parentNode.removeChild(this.stylesManager[name]);
        delete this.stylesManager[name];
        return this;
    }
});