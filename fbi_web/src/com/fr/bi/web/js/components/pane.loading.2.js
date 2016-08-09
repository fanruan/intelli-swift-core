/**
 *
 * Created by GUY on 2016/7/20.
 * @class BI.LoadingPane2
 * @extends BI.Widget
 * @abstract
 */
BI.LoadingPane2 = BI.inherit(BI.Pane, {

    _defaultConfig: function () {
        var conf = BI.LoadingPane2.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: conf.baseCls + " bi-loading-pane",
            overlap: false,
            onLoaded: BI.emptyFn
        })
    },

    _init: function () {
        BI.LoadingPane2.superclass._init.apply(this, arguments);
    },

    loading: function () {
        if (!this._loading) {
            if (this.options.overlap === true) {
                this._loading = BI.createWidget({
                    type: "bi.loading_background",
                    backgroundCls: "loading-background-e50",
                    masker: this,
                    container: this.options.container || this
                });
            } else {
                this._loading = BI.createWidget({
                    type: "bi.layout",
                    cls: "bi-loading-mask loading-background-e50"
                });
                this._loading.element.css("zIndex", 1);
                BI.createWidget({
                    type: "bi.absolute",
                    element: this.element,
                    items: [{
                        el: this._loading,
                        left: 0,
                        right: 0,
                        top: 0,
                        bottom: 0
                    }]
                })
            }
        }
    },

    loaded: function () {
        this._loading.destroy();
        this._loading = null;
        this.options.onLoaded();
        this.fireEvent(BI.Pane.EVENT_LOADED);
    }
});