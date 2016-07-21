/**
 *
 * Created by GUY on 2016/1/20.
 * @class BI.LoadingPane
 * @extends BI.Widget
 * @abstract
 */
BI.LoadingPane = BI.inherit(BI.Pane, {

    _defaultConfig: function () {
        var conf = BI.LoadingPane.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: conf.baseCls + " bi-loading-pane",
            onLoaded: BI.emptyFn
        })
    },

    _init: function () {
        BI.LoadingPane.superclass._init.apply(this, arguments);
    },

    loading: function () {
        if (!this._loading) {
            this._loading = BI.createWidget({
                type: "bi.loading_mask",
                masker: this,
                container: this.options.container || this,
                text: BI.i18nText("BI-Loading")
            });
        }
    },

    loaded: function () {
        this._loading.destroy();
        this._loading = null;
        this.options.onLoaded();
        this.fireEvent(BI.Pane.EVENT_LOADED);
    }
});