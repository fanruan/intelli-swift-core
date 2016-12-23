/**
 * Created by zcf on 2016/12/21.
 */
BIShow.TreeListModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.TreeListModel.superclass._defaultConfig.apply(this, arguments), {
            name: "",
            bounds: {},
            type: BICst.WIDGET.TREE_LIST,
            dimensions: {},
            view: {},
            value: {}
        })
    },

    _init: function () {
        BIShow.TreeListModel.superclass._init.apply(this, arguments);
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
        var self = this;
        if (this.has("changeSort")) {
            var dimensions = this.get("dimensions");
            BI.each(dimensions, function (id, dimension) {
                var sort = self.get("changeSort");
                dimension.sort = {type: sort.type};
            });
            this.set("dimensions", dimensions);
            return true;
        }
        return false;
    }
});