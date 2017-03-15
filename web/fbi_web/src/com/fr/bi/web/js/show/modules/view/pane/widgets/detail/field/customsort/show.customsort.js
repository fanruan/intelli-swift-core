/**
 * Created by roy on 15/11/10.
 */
BIShow.CustomSortView = BI.inherit(BI.BarFloatSection, {
    _defaultConfig: function () {
        return BI.extend(BIShow.CustomSortView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-sort-view"
        })
    },
    _init: function () {
        BIShow.CustomSortView.superclass._init.apply(this, arguments);
    },

    end: function () {
        var value = this.customsort.getValue();
        this.model.set(value || {});
    },


    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Drag_To_Sort"),
            height: 50,
            textAlign: "left",
            lgap: 10
        });
        return true;
    },

    rebuildCenter: function (center) {
        var id = this.model.get("id");
        this.customsort = BI.createWidget({
            element: center,
            dId: id,
            type: "bi.custom_sort_pane"
        })
    },


    refresh: function () {
        this.customsort.populate();
    }


});