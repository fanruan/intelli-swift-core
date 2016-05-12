/**
 * Created by roy on 16/4/13.
 */
BIDezi.TreeWidgetModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.TreeWidgetModel.superclass._defaultConfig.apply(this, arguments), {
            name: "",
            bounds: {},
            type: BICst.Widget.TREE,
            dimensions: {},
            view: {},
            value: {},
            settings: BICst.DEFAULT_CONTROL_SETTING
        })
    },

    _init: function () {
        BIDezi.TreeWidgetModel.superclass._init.apply(this, arguments);
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
                    settings: this.get("settings"),
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
                settings: this.get("settings"),
                value: this.get("value")
            }
        }, {
            silent: true
        });
    },
    local: function () {
        if(this.has("changeSort")){
            var dimensions = this.get("dimensions");
            var key = BI.keys(dimensions)[0];
            if(BI.isNotNull(key)){
                var sort = this.get("changeSort");
                dimensions[key].sort = {type: sort.type, target_id: key};
                this.set("dimensions", dimensions);
            }
            return true;
        }
        return false;
    }
});