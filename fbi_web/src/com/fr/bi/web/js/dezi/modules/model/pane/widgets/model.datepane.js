/**
 * Created by zcf on 12/22/2016.
 */
BIDezi.DatePaneModel=BI.inherit(BI.Model,{
    _defaultConfig: function () {
        return BI.extend(BIDezi.DatePaneModel.superclass._defaultConfig.apply(this), {
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
        if (this.has("expand")) {
            this.get("expand");
            return true;
        }
        if (this.has("layout")) {
            this.get("layout");
            return true;
        }
        return false;
    },

    _init: function () {
        BIDezi.DatePaneModel.superclass._init.apply(this, arguments);
    }
});