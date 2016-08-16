/**
 * @class BI.FilterPane
 * @extend BI.Widget
 * 过滤面板
 */
BI.DataLabelFilterPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DataLabelFilterPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-filter-pane",
            expander: {},
            items: [],
            itemsCreator: BI.emptyFn
        })
    },

    _init: function () {
        BI.DataLabelFilterPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.tree = BI.createWidget({
            type: "bi.custom_tree",
            element: this.element,
            expander: BI.extend({
                type: "bi.data_label_filter_expander",
                el: {},
                popup: {
                    type: "bi.custom_tree"
                }
            }, o.expander),
            el: {
                type: "bi.button_map",
                chooseType: BI.ButtonGroup.CHOOSE_TYPE_DEFAULT,
                layouts: [{
                    type: "bi.vertical"
                }]
            }
        });

        this.tree.on(BI.Controller.EVENT_CHANGE, function (type) {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
            if (type === BI.Events.CLICK) {
                self.fireEvent(BI.DataLabelFilterPane.EVENT_CHANGE, [].slice.call(arguments, 1));
            }
        });

        if (BI.isNotEmptyArray(o.items)) {
            this.populate(o.items);
        }
    },

    populate: function (items) {
        this.tree.populate.apply(this.tree, arguments);
    },

    getValue: function () {
        return this.tree.getValue();
    }
});
BI.DataLabelFilterPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.data_label_filter_pane", BI.FilterPane);