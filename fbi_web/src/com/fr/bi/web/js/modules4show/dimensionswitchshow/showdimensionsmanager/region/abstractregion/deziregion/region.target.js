/**
 * Created by GUY on 2016/3/16.
 * @class BI.TargetRegion
 * @extends BI.AbstractRegion
 */
BI.TargetRegion = BI.inherit(BI.ShowAbstractRegion, {
    _defaultConfig: function () {
        return BI.extend(BI.TargetRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-region",
            dimensionCreator: BI.emptyFn,
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        });
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
        BI.each(data, function(i, dimension){
            if(!self._checkFilter(dimension.filter_value)){
                delete dimension.filter_value;
            }
        })
        return data;
    },

    _checkFilter: function(filters){
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
});
$.shortcut("bi.target_region", BI.TargetRegion);