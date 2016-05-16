BIShow.DateRangeModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.DateRangeModel.superclass._defaultConfig.apply(this), {
            name: "",
            bounds: {},
            type: BICst.WIDGET.DATE,
            dimensions: {},
            view: {},
            filter_value: {}
        })
    },

    _static: function () {

    },

    change: function (changed) {
        if (BI.has(changed, "detail")) {
            this.set(this.get("detail"));
        }
        if (BI.has(changed, "filter_value")) {
            this.tmp({
                detail: {
                    name: this.get("name"),
                    dimensions: this.get("dimensions"),
                    view: this.get("view"),
                    type: this.get("type"),
                    filter_value: changed.filter_value
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
                filter_value: this.get("filter_value")
            }
        }, {
            silent: true
        });
    },

    local: function () {
        return false;
    },

    _init: function () {
        BIShow.DateRangeModel.superclass._init.apply(this, arguments);
    }
});
