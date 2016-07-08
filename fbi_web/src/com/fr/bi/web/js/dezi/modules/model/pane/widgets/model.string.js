BIDezi.StringWidgetModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.StringWidgetModel.superclass._defaultConfig.apply(this), {
            name: "",
            bounds: {},
            type: BICst.WIDGET.STRING,
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
        if (this.has("changeSort")) {
            var dimensions = this.get("dimensions");
            var key = BI.keys(dimensions)[0];
            if (BI.isNotNull(key)) {
                var sort = this.get("changeSort");
                dimensions[key].sort = {type: sort.type, target_id: key};

                var value = BI.Func.getSearchResult(this.get("value").value);
                value = BI.concat(value.matched, value.finded);
                if (sort.type === BICst.SORT.DESC) {
                    value = value.reverse();
                }
                var v = this.get("value");
                v.value = value;
                this.set({"dimensions": dimensions, "value": v});
            }
            return true;
        }
        return false;
    },

    _init: function () {
        BIDezi.StringWidgetModel.superclass._init.apply(this, arguments);
    }
});