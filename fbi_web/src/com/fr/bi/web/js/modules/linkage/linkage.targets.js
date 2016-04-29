/**
 * 联动指标面板
 * 从某个组件到某个组件的联动
 *
 * Created by GUY on 2016/3/14.
 * @class BI.LinkageTargets
 * @extends BI.Widget
 */
BI.LinkageTargets = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.LinkageTargets.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-targets-linkage",
            model: null,
            from: "",
            to: ""
        });
    },

    _init: function () {
        BI.LinkageTargets.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var items = [];
        var linkages = o.model.getLinkages(o.from);
        BI.each(linkages, function (j, linkage) {
            if (linkage.to === o.to) {
                items.push(self._createOneLinkage(linkage.from))
            }
        });
        this.button_group = BI.createWidget({
            type: "bi.button_tree",
            element: this.element,
            items: items,
            layouts: [{
                type: "bi.vertical",
                hgap: 20,
                tgap: 10
            }]
        });
    },

    _createOneLinkage: function (from) {
        var self = this, o = this.options;
        var linkage = BI.createWidget({
            type: "bi.linkage_target",
            from: from,
            to: o.to
        });
        linkage.on(BI.LinkageTarget.EVENT_DELETE, function () {
            self.button_group.removeItems(this.getValue());
            self.fireEvent(BI.LinkageTargets.EVENT_DELETE, from);
        });

        return linkage;
    },

    addOneLinkage: function (from) {
        var o = this.options;
        var values = this.button_group.getValue();
        if (BI.deepContains(values, {
                from: from,
                to: o.to
            })) {
            return;
        }
        var linkage = this._createOneLinkage(from);
        this.button_group.addItems([linkage]);
        this.fireEvent(BI.LinkageTargets.EVENT_ADD, from);
    },

    getValue: function () {
        return this.button_group.getValue();
    }
});
BI.LinkageTargets.EVENT_ADD = "LinkageTargets.EVENT_ADD";
BI.LinkageTargets.EVENT_DELETE = "LinkageTargets.EVENT_DELETE";
$.shortcut('bi.linkage_targets', BI.LinkageTargets);