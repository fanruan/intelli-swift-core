/**
 * Created by roy on 15/11/10.
 */
BI.CustomSortPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CustomSortPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-sort-pane"
        })
    },

    _init: function () {
        var self = this, o = this.options;
        BI.CustomSortPane.superclass._init.apply(this, arguments);
        var id = o.dId;
        var all = 0;

        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: self.element,
            text: BI.i18nText("BI-Loading")
        });

        this.loader = BI.createWidget({
            type: "bi.list_loader",
            height: "100%",
            width: "100%",
            el: {
                layouts: [
                    {
                        type: "bi.vertical",
                        vgap: 20
                    }
                ]
            },
            itemsCreator: function (options, populate) {
                BI.Utils.getDataByDimensionID(id, function (allDate) {
                    mask.destroy();
                    all = allDate;
                    populate(self._createItems(allDate))
                })
            },
            hasNext: function (option) {
                return option.count < all;
            }
        });

        this.loader.element.sortable({
            containment: this.element,
            items: ".bi-custom-sort-item",
            cursor: "drag",
            tolerance: "intersect",
            placeholder: {
                element: function ($currentItem) {
                    var holder = BI.createWidget({
                        type: "bi.label",
                        cls: "bi-sortable-holder",
                        height: 20
                    });
                    return holder.element;
                },
                update: function () {

                }
            },
            stop: function (event, ui) {
            }
        });

        BI.createWidget({
            type: "bi.default",
            element: this.element,
            items: [self.loader]
        })

    },

    _createItems: function (items) {
        return BI.map(items, function (i, value) {
            return {
                type: "bi.text_icon_item",
                cls: "sortable-font bi-custom-sort-item",
                shadow: true,
                isShadowShowingOnSelected: true,
                attributes: {
                    itemvalue: value
                },
                text: value,
                height: 20,
                value: value
            }
        });
    },

    populate: function (items) {
        var self = this, o = this.options;
        var id = o.dId;
        BI.Utils.getDataByDimensionID(id, function (allDate) {
            var initItems = self._createItems(allDate);
            self.loader.populate(initItems);
        })
    },

    getValue: function () {
        var o = this.options;
        var result = {};
        result.type = BICst.SORT.CUSTOM;
        result.details = this.loader.element.sortable("toArray", {attribute: "itemvalue"});
        result.sort_target = o.dId;
        return result;
    }
});

$.shortcut("bi.custom_sort_pane", BI.CustomSortPane);