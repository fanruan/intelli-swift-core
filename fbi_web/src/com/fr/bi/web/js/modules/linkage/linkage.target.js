/**
 * 联动指标面板
 * 从某个指标到某个组件的联动
 *
 * Created by GUY on 2016/3/14.
 * @class BI.LinkageTarget
 * @extends BI.Widget
 */
BI.LinkageTarget = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.LinkageTarget.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-linkage-target",
            height: 30,
            from: "",
            to: ""
        });
    },

    _init: function () {
        BI.LinkageTarget.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var name = BI.Utils.getDimensionNameByID(o.from);
        var nameLabel = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            lgap: 10,
            height: 30,
            text: name
        });
        var del = BI.createWidget({
            type: "bi.icon_button",
            width: 14,
            cls: "search-close-h-font"
        });
        del.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.LinkageTarget.EVENT_DELETE, arguments);
        });
        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: nameLabel
            }, {
                el: {
                    type: "bi.center_adapt",
                    items: [del]
                },
                width: 30
            }]
        });
    },

    getValue: function () {
        return {
            from: this.options.from,
            to: this.options.to
        }
    }
});
BI.LinkageTarget.EVENT_DELETE = "LinkageTarget.EVENT_DELETE";
$.shortcut('bi.linkage_target', BI.LinkageTarget);