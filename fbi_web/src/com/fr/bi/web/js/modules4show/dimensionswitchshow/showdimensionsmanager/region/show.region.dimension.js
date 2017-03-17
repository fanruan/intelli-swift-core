/**
 * Created by zcf on 2016/12/26.
 */
BI.ShowDimensionRegion = BI.inherit(BI.ShowAbstractRegion, {
    _defaultConfig: function () {
        return BI.extend(BI.ShowDimensionRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-show-dimension-region",
            dimensionCreator: BI.emptyFn,
            containment: false,
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        })
    },

    _getDimensionClass: function () {
        return "dimension-container";
    },

    _getDimensionContainerClass: function () {
        return "dimensions-container";
    },

    _getSortableHelperClass: function () {
        return "dimension-sortable-helper";
    },

    _dropDataFilter: function (data) {
        var self = this, o = this.options;
        data = BI.filter(data, function (i, dimension) {
            return BI.Utils.isDimensionType(dimension.type);
        });
        BI.each(data, function (i, dimension) {
            if (!self._checkFilter(dimension.filter_value)) {
                delete dimension.filter_value;
            }
        })
        return data;
    },

    _checkFilter: function (filters) {
        var self = this;
        var filter = filters || {};
        var filterType = filter.filter_type, filterValue = filter.filter_value;
        if (filterType === BICst.FILTER_TYPE.AND || filterType === BICst.FILTER_TYPE.OR) {
            return BI.any(filterValue, function (i, value) {
                return self._checkFilter(value);
            });
        } else {
            return !(BI.contains(BI.values(BICst.TARGET_FILTER_NUMBER), filterType) || BI.contains(BI.values(BICst.TARGET_FILTER_STRING), filter) ||
            BI.contains(BI.values(BICst.FILTER_DATE, filterType)));
        }
    },

    setVisible: function (enable) {
        if (enable) {
            this.element.css({"height": "100%"});
        } else {
            this.element.css({"height": "0px"});
        }
        this.center.element.sortable("refreshPositions");
    },

    getViewType: function () {
        return this.options.viewType;
    }
});
$.shortcut("bi.show_dimension_region", BI.ShowDimensionRegion);
