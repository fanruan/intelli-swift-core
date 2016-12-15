/**
 * 区域管理器
 *
 * Created by GUY on 2016/3/17.
 * @class BI.RegionsManager
 * @extends BI.Widget
 */
BI.RegionsManager = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.RegionsManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-regions-manager",
            dimensionCreator: BI.emptyFn,
            wId: ""
        });
    },

    _init: function () {
        BI.RegionsManager.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.wrappers = {};
    },

    getValue: function () {
        var views = {}, o = this.options;
        BI.each(this.wrappers, function (type, wrap) {
            BI.extend(views, wrap.getValue());
        });
        return {
            view: views
        };
    },

    populate: function () {
        var self = this, o = this.options;
        BI.each(this.wrappers, function (type, wrap) {
            wrap.populate();
        });
    }
});

BI.RegionsManager.EVENT_CHANGE = "RegionsManager.EVENT_CHANGE";
