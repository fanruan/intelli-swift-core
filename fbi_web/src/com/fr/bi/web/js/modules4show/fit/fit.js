/**
 * 自适应布局
 *
 * Created by GUY on 2016/6/28.
 * @class BI.Fit4Show
 * @extends BI.Widget
 */
BI.Fit4Show = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.Fit4Show.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-fit-4show",
            widgetCreator: BI.emptyFn
        });
    },

    _init: function () {
        BI.Fit4Show.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.arrangement = BI.createWidget({
            type: "bi.arrangement",
            element: this.element,
            isNeedReLayout: false
        });
        this.zIndex = 0;
        this.element.click(function (e) {
            BI.each(self.getAllRegions(), function (i, region) {
                if (!region.el.element.__isMouseInBounds__(e)) {
                    region.el.element.removeClass("selected");
                }
            });
        });

        BI.Resizers.add(this.getName(), function (e) {
            //只有window resize的时候才会触发事件
            if (BI.isWindow(e.target) && self.element.is(":visible")) {
                self.populate();
            }
        });
    },

    _initResizable: function (item) {
        var self = this, o = this.options;
        item.element.css("zIndex", ++this.zIndex);
        item.element.mousedown(function () {
            item.element.css("zIndex", ++self.zIndex);
            BI.each(self.getAllRegions(), function (i, region) {
                region.el.element.removeClass("selected");
            });
            item.element.addClass("selected");
        });
    },

    getDirectRelativeRegions: function (name, direction) {
        return this.arrangement.getDirectRelativeRegions(name, direction);
    },

    resize: function () {
        this.arrangement.resize();
    },

    relayout: function () {

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

    },

    _assertBounds: function () {
        var self = this;
        if (!this.layoutType) {
            this.layoutType = Data.SharingPool.get("layoutType");
            if (BI.isNull(this.layoutType)) {
                this.layoutType = BI.Arrangement.LAYOUT_TYPE.FREE;
            }
        }
        if (!this.bounds) {
            this.bounds = Data.SharingPool.cat("widgets");
        } else {
            //这里特别恶心， 自适应布局的时候高度又不能让它变化，但宽度又要resize
            var widgets = Data.SharingPool.cat("widgets");
            BI.each(widgets, function (i, widget) {
                self.bounds[i].bounds.left = widget.bounds.left;
                self.bounds[i].bounds.width = widget.bounds.width;
            });
        }
    },

    populate: function () {
        var self = this, o = this.options;

        this._assertBounds();
        var items = [];
        BI.each(this.bounds, function (wid, widget) {
            var w = o.widgetCreator(wid);
            var container = BI.createWidget({
                type: "bi.absolute",
                id: wid,
                items: [{
                    el: w,
                    left: 5,
                    right: 5,
                    top: 5,
                    bottom: 5
                }]
            });
            items.push({
                el: container,
                left: widget.bounds.left,
                top: widget.bounds.top,
                width: widget.bounds.width,
                height: widget.bounds.height
            });
        });
        BI.each(items, function (i, item) {
            self._initResizable(item.el);
        });
        this.arrangement.setLayoutType(this.layoutType);
        this.arrangement.populate(items);
        switch (this.layoutType) {
            case BI.Arrangement.LAYOUT_TYPE.ADAPTIVE:
                BI.nextTick(function () {
                    self.arrangement.resize();
                    self.fireEvent(BI.Fit4Show.EVENT_RESIZE);
                });
                break;
            case BI.Arrangement.LAYOUT_TYPE.FREE:
                break;
        }
    }
});
BI.Fit4Show.EVENT_RESIZE = "EVENT_RESIZE";
$.shortcut('bi.fit_4show', BI.Fit4Show);