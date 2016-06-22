/**
 * 联动
 *
 * Created by GUY on 2016/3/15.
 * @class BI.LinkageModel
 * @extends BI.Widget
 */
BI.LinkageModel = BI.inherit(FR.OB, {

    _defaultConfig: function () {
        return BI.extend(BI.LinkageModel.superclass._defaultConfig.apply(this, arguments), {
            wId: ""
        });
    },

    _init: function () {
        BI.LinkageModel.superclass._init.apply(this, arguments);
        this.reset();
    },

    reset: function () {
        var self = this;
        this.store = {};
        this.widgets = {};
        var ids = BI.Utils.getAllWidgetIDs();
        BI.each(ids, function (i, id) {
            self.widgets[id] = {
                linkages: BI.Utils.getWidgetLinkageByID(id)
            }
        });
    },

    _getWidgetLinkage: function (wId) {
        return this.widgets[wId].linkages;
    },

    _initDirectChildren: function (currentId) {
        var self = this, o = this.options;
        var directChildren = [];
        var linkages = this._getWidgetLinkage(currentId);
        BI.each(linkages, function (i, linkage) {
            if (!directChildren.contains(linkage.to)) {
                directChildren.push(linkage.to);
            }
        });
        return directChildren;
    },

    _initChildren: function (currentId) {
        var self = this, o = this.options;
        var queue = [currentId];
        var children = [];
        while (queue.length > 0) {
            var id = queue.shift();
            var linkages = this._getWidgetLinkage(id);
            BI.each(linkages, function (i, linkage) {
                if (!children.contains(linkage.to)) {
                    children.push(linkage.to);
                    queue.push(linkage.to);
                }
            });
        }
        return children;
    },

    _initDirectParents: function (currentId) {
        var self = this, o = this.options;
        var directParents = [];
        var ids = BI.Utils.getAllWidgetIDs();
        BI.each(ids, function (i, wId) {
            var linkages = self._getWidgetLinkage(wId);
            BI.each(linkages, function (j, linkage) {
                if (linkage.to === currentId && !directParents.contains(linkage.from)) {
                    directParents.push(linkage.from);
                }
            });
        });
        return directParents;
    },

    _initParents: function (currentId) {
        var self = this, o = this.options;
        var parents = [];
        var map = {};
        var ids = BI.Utils.getAllWidgetIDs();
        BI.each(ids, function (i, id) {
            map[id] = {};
        });
        BI.each(ids, function (i, wId) {
            var linkages = self._getWidgetLinkage(wId);
            BI.each(linkages, function (j, linkage) {
                map[linkage.to][wId] = true;
            });
        });

        var queue = [currentId];
        while (queue.length > 0) {
            var id = queue.shift();
            var ps = BI.keys(map[id]);
            parents = parents.concat(ps);
        }
        return parents;
    },

    _getWidgetRelations: function (currentId) {
        return this.store[currentId] = {
            directChildren: this._initDirectChildren(currentId),
            children: this._initChildren(currentId),
            directParents: this._initDirectParents(currentId),
            parents: this._initParents(currentId),
            current: currentId
        }
    },

    _isRelationsIntersect: function (relation1, relation2) {
        var all1 = relation1.children.concat(relation1.parents).concat(relation1.current);
        var all2 = relation2.children.concat(relation2.parents).concat(relation2.current);
        var intersection = BI.intersection(all1, all2);
        return intersection.length > 0;
    },

    getLinkages: function (wId) {
        return this.widgets[wId].linkages || [];
    },

    addLinkage: function (tId, wId) {
        var currentId = BI.Utils.getWidgetIDByDimensionID(tId);
        var linkages = this.widgets[currentId].linkages || [];
        linkages.push({
            from: tId,
            to: wId
        });
        this.widgets[currentId].linkages = linkages;
    },

    deleteLinkage: function (tId, wId) {
        var currentId = BI.Utils.getWidgetIDByDimensionID(tId);
        var linkages = this.widgets[currentId].linkages || [];
        BI.remove(linkages, function (i, linkage) {
            return BI.isEqual(linkage, {
                from: tId,
                to: wId
            })
        });
    },

    isWidgetCanLinkageTo: function (wId) {
        var o = this.options;
        if (wId === o.wId) {
            return false;
        }
        var wType = BI.Utils.getWidgetTypeByID(wId);
        if (BI.Utils.isControlWidgetByWidgetId(wId) ||
            wType === BICst.WIDGET.RESET ||
            wType === BICst.WIDGET.QUERY) {
            return false;
        }
        var currentRelations = this._getWidgetRelations(o.wId);
        if (currentRelations.directChildren.contains(wId)) {
            return true;
        }
        return !this._isRelationsIntersect(currentRelations, this._getWidgetRelations(wId));
    },

    getLinkedWidgetsByTargetId: function (tId) {
        var widgets = [];
        BI.each(this.widgets, function (wId, link) {
            BI.each(link.linkages, function (i, linkage) {
                if (linkage.from === tId) {
                    widgets.push(linkage.to);
                }
            })
        });
        return widgets;
    },

    getWidgetIconClsByWidgetId: function (wId) {
        var widgetType = BI.Utils.getWidgetTypeByID(wId);
        switch (widgetType) {
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.TABLE:
            case BICst.WIDGET.CROSS_TABLE:
            case BICst.WIDGET.COMPLEX_TABLE:
                return "chart-table-font";
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.FALL_AXIS:
                return "chart-axis-font";
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.COMPARE_BAR:
                return "chart-bar-font";
            case BICst.WIDGET.ACCUMULATE_BAR:
                return "chart-accumulate-bar-font";
            case BICst.WIDGET.PIE:
                return "chart-pie-font";
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
                return "chart-map-font";
            case BICst.WIDGET.DASHBOARD:
                return "chart-dashboard-font";
            case BICst.WIDGET.DONUT:
                return "chart-doughnut-font";
            case BICst.WIDGET.DETAIL:
                return "chart-detail-font";
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.FORCE_BUBBLE:
                return "chart-bubble-font";
            case BICst.WIDGET.SCATTER:
                return "chart-scatter-font";
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
                return "chart-radar-font";
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                return "";
        }
    }
});