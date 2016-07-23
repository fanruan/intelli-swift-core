/**
 * 交互行为布局
 *
 *
 * Created by GUY on 2016/7/23.
 * @class BI.InteractiveArrangement
 * @extends BI.Widget
 */
BI.InteractiveArrangement = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.InteractiveArrangement.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-interactive-arrangement",
            resizable: true,
            isNeedReLayout: true,
            isNeedResizeContainer: true,
            layoutType: BI.Arrangement.LAYOUT_TYPE.FREE,
            items: []
        });
    },

    _init: function () {
        BI.InteractiveArrangement.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.arrangement = BI.createWidget({
            type: "bi.adaptive_arrangement",
            element: this.element,
            resizable: o.resizable,
            isNeedReLayout: o.isNeedReLayout,
            isNeedResizeContainer: o.isNeedResizeContainer,
            layoutType: o.layoutType,
            items: o.items
        });
        this.arrangement.on(BI.AdaptiveArrangement.EVENT_RESIZE, function () {
            self.fireEvent(BI.InteractiveArrangement.EVENT_RESIZE, arguments);
        });

        this.tags = [];

    },

    _isEqual: function (num1, num2) {
        return this.arrangement._isEqual(num1, num2);
    },

    _getScrollOffset: function () {
        return this.arrangement._getScrollOffset();
    },

    _positionAt: function (position, regions) {
        var self = this;
        regions = regions || this.getAllRegions();
        var left = [], center = [], right = [], top = [], middle = [], bottom = [];
        BI.each(regions, function (i, region) {
            var client = self._getRegionClientPosition(region.id);
            if (Math.abs(client.left - position.left) <= 10) {
                left.push(region);
            }
            if (Math.abs(client.left + client.width / 2 - position.left) <= 10) {
                center.push(region);
            }
            if (Math.abs(client.left + client.width - position.left) <= 10) {
                right.push(region);
            }
            if (Math.abs(client.top - position.top) <= 10) {
                top.push(region);
            }
            if (Math.abs(client.top + client.height / 2 - position.top) <= 10) {
                middle.push(region);
            }
            if (Math.abs(client.top + client.height - position.top) <= 10) {
                bottom.push(region);
            }
        });
        return {
            left: left,
            center: center,
            right: right,
            top: top,
            middle: middle,
            bottom: bottom
        }
    },

    _getRegionClientPosition: function (name) {
        var region = this.getRegionByName(name);
        var offset = this.arrangement._getScrollOffset();
        return {
            top: region.top - offset.top,
            left: region.left - offset.left,
            width: region.width,
            height: region.height,
            id: region.id
        }
    },

    _drawOneTag: function (start, end) {
        var s = BI.createWidget({
            type: "bi.icon_button",
            //invisible: true,
            width: 13,
            height: 13,
            cls: "drag-tag-font interactive-arrangement-dragtag-icon"
        });
        var e = BI.createWidget({
            type: "bi.icon_button",
            //invisible: true,
            width: 13,
            height: 13,
            cls: "drag-tag-font interactive-arrangement-dragtag-icon"
        });
        if (this._isEqual(start.left, end.left)) {
            var line = BI.createWidget({
                type: "bi.layout",
                //invisible: true,
                cls: "interactive-arrangement-dragtag-line",
                width: 1,
                height: Math.abs(start.top - end.top)
            });
        } else {
            var line = BI.createWidget({
                type: "bi.layout",
                //invisible: true,
                cls: "interactive-arrangement-dragtag-line",
                height: 1,
                width: Math.abs(start.left - end.left)
            });
        }
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: s,
                left: start.left - 6,
                top: start.top - 6
            }, {
                el: e,
                left: end.left - 6,
                top: end.top - 6
            }, {
                el: line,
                left: Math.min(start.left, end.left),
                top: Math.min(start.top, end.top)
            }]
        });
        this.tags.push(s);
        this.tags.push(e);
        this.tags.push(line);
    },

    _removeTags: function () {

    },

    _generateTags: function () {

    },

    getDirectRelativeRegions: function (name, direction) {
        return this.arrangement.getDirectRelativeRegions(name, direction);
    },

    addRegion: function (region, position) {
        return this.arrangement.addRegion(region, position);
    },

    deleteRegion: function (name) {
        return this.arrangement.deleteRegion(name);
    },

    setRegionSize: function (name, size) {
        return this.arrangement.setRegionSize(name, size);
    },

    setPosition: function (position, size) {
        var self = this;
        //switch (this.getLayoutType()) {
        //    case BI.Arrangement.LAYOUT_TYPE.ADAPTIVE:
        //        break;
        //    case BI.Arrangement.LAYOUT_TYPE.FREE:
        //        var topleft = this._positionAt(position);
        //        BI.each(topleft, function (direction, rs) {
        //            switch (direction) {
        //                case "left":
        //                    var l;
        //                    if (rs.length > 0) {
        //                        l = rs[0].left;
        //                    }
        //                    BI.each(rs, function (i, region) {
        //                        if (self._isEqual(region.left, l)) {
        //                            var position = self._getRegionClientPosition(region.id);
        //                            var topPoint = {
        //                                top: position.top + region.height / 2,
        //                                left: position.left
        //                            };
        //                            self._drawOneTag(topPoint, {
        //                                left: position.left,
        //                                top: position.top + size.height / 2
        //                            })
        //                        }
        //                    });
        //                    break;
        //                case "right":
        //                    break;
        //                case "center":
        //                    break;
        //                case "top":
        //                    break;
        //                case "middle":
        //                    break;
        //                case "bottom":
        //                    break;
        //            }
        //        });
        //        break;
        //}
        var at = this.arrangement.setPosition(position, size);
        return at;
    },

    setRegionPosition: function (name, position) {
        return this.arrangement.setRegionPosition(name, position);
    },

    resize: function () {
        return this.arrangement.resize();
    },

    relayout: function () {
        return this.arrangement.relayout();
    },

    setLayoutType: function (type) {
        this.arrangement.setLayoutType(type);
    },

    getLayoutType: function () {
        return this.arrangement.getLayoutType();
    },

    getHelper: function () {
        return this.arrangement.getHelper();
    },

    getRegionByName: function (name) {
        return this.arrangement.getRegionByName(name);
    },

    getAllRegions: function () {
        return this.arrangement.getAllRegions();
    },

    revoke: function () {
        return this.arrangement.revoke();
    },

    populate: function (items) {
        var self = this;
        this.arrangement.populate(items);
    }
});
BI.InteractiveArrangement.EVENT_RESIZE = "InteractiveArrangement.EVENT_RESIZE";
$.shortcut('bi.interactive_arrangement', BI.InteractiveArrangement);