/**
 * Created by zcf on 2016/12/26.
 */
BI.ShowTargetRegion = BI.inherit(BI.ShowAbstractRegion, {
    _defaultConfig: function () {
        return BI.extend(BI.ShowTargetRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-show-target-region",
            dimensionCreator: BI.emptyFn,
            containment: false,
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        })
    },

    _getDimensionClass: function () {
        return "target-container";
    },

    _getDimensionContainerClass: function () {
        return "targets-container";
    },

    _getSortableHelperClass: function () {
        return "target-sortable-helper";
    },

    _dropDataFilter: function (data) {
        var self = this, o = this.options;
        data = BI.filter(data, function (i, dimension) {
            return BI.Utils.isTargetType(dimension.type);
        });
        BI.each(data, function (i, dimension) {
            if (!self._checkFilter(dimension.filter_value)) {
                delete dimension.filter_value;
            }
        });
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
            return !(BI.contains(BI.values(BICst.DIMENSION_FILTER_NUMBER), filterType) || BI.contains(BI.values(BICst.DIMENSION_FILTER_STRING), filter) ||
            BI.contains(BI.values(BICst.DIMENSION_FILTER_DATE), filterType));
        }
    },

    _fieldDragStart: function () {
        var onlyCounter = !BI.some(this.dimensions, function (i, dimension) {
            return BI.Utils.isTargetType(dimension.type);
        });
        if (onlyCounter) {
            this._showForbiddenMask();
        }
    },

    setVisible: function (enable) {
        if (enable) {
            this.element.css({"height": "100%"});
        } else {
            this.element.css({"height": "0%"});
        }
        this.center.element.sortable("refreshPositions");
    },

    getViewType: function () {
        return this.options.viewType;
    }
});
$.shortcut("bi.show_target_region", BI.ShowTargetRegion);