BIShow.StringWidgetModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.StringWidgetModel.superclass._defaultConfig.apply(this), {
            name: "",
            bounds: {},
            type: BICst.WIDGET.STRING,
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
        if (BI.has(changed, "dimensions")) {
            this.tmp({
                detail: {
                    name: this.get("name"),
                    dimensions: changed.dimensions,
                    view: this.get("view"),
                    type: this.get("type"),
                    filter_value: this.get("filter_value")
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
        if (this.has("changeSort")) {
            var dimensions = this.get("dimensions");
            var key = BI.keys(dimensions)[0];
            if (BI.isNotNull(key)) {
                var sort = this.get("changeSort");
                dimensions[key].sort = {type: sort.type, target_id: key};
                this.set("dimensions", dimensions);
            }
            return true;
        }
        return false;
    },

    _init: function () {
        BIShow.StringWidgetModel.superclass._init.apply(this, arguments);
    }
});
