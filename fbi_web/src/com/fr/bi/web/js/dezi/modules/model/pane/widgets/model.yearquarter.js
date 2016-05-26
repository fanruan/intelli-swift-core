BIDezi.YearQuarterWidgetModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.YearQuarterWidgetModel.superclass._defaultConfig.apply(this), {
            name: "",
            bounds: {},
            type: BICst.WIDGET.QUARTER,
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
                    value: this.get("value")
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
        BIDezi.YearQuarterWidgetModel.superclass._init.apply(this, arguments);
    }
});