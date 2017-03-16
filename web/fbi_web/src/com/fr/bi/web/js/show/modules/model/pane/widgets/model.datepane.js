/**
 * Created by zcf on 12/22/2016.
 */
BIShow.DatePaneModel = BI.inherit(BI.Model, {

    _defaultConfig: function () {
        return BI.extend(BIShow.DatePaneModel.superclass._defaultConfig.apply(this), {
            name: "",
            bounds: {},
            type: BICst.WIDGET.DATE_PANE,
            dimensions: {},
            view: {},
            value: {}
        })
    },

    change: function (changed) {
        if (BI.has(changed, "detail")) {
            this.set(this.get("detail"));
        }
        if (BI.has(changed, "value")) {
            this.tmp({
                detail: {
                    name: this.get("name"),
                    dimensions: this.get("dimensions"),
                    view: this.get("view"),
                    type: this.get("type"),
                    value: changed.value
                }
            }, {
                silent: true
            });
        }
    },

    refresh: function () {
        this.tmp({
            detail: {
                name: this.get("name"),
                dimensions: this.get("dimensions"),
                view: this.get("view"),
                type: this.get("type"),
                value: this.get("value")
            }
        }, {
            silent: true
        });
    },

    local: function () {
        return false;
    },

    _init: function () {
        BIShow.DatePaneModel.superclass._init.apply(this, arguments);
    }
});