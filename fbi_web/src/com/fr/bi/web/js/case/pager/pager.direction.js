/**
 * 显示页码的分页控件
 *
 * Created by GUY on 2016/6/30.
 * @class BI.DirectionPager
 * @extends BI.Widget
 */
BI.DirectionPager = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DirectionPager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-direction-pager",
            width: 106,
            height: 25,
            horizontal: {
                pages: false, //总页数
                curr: 1, //初始化当前页， pages为数字时可用

                hasPrev: BI.emptyFn,
                hasNext: BI.emptyFn,
                firstPage: 1,
                lastPage: BI.emptyFn
            },
            vertical: {
                pages: false, //总页数
                curr: 1, //初始化当前页， pages为数字时可用

                hasPrev: BI.emptyFn,
                hasNext: BI.emptyFn,
                firstPage: 1,
                lastPage: BI.emptyFn
            }
        })
    },
    _init: function () {
        BI.DirectionPager.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var v = o.vertical, h = o.horizontal;
        this.currentPage = [v.curr, h.curr];
        this._createVPager();
        this._createHPager();
        BI.createWidget({
            type: "bi.absolute",
            scrollable: false,
            element: this.element,
            items: [{
                el: this.vpager,
                top: 0,
                left: -20
            }, {
                el: this.vlabel,
                top: 0,
                left: 16
            }, {
                el: this.hpager,
                top: 0,
                right: -20
            }, {
                el: this.hlabel,
                top: 0,
                right: 16
            }]
        });
    },

    _createVPager: function () {
        var self = this, o = this.options;
        var v = o.vertical;
        this.vlabel = BI.createWidget({
            type: "bi.label",
            width: 20,
            height: o.height,
            value: v.curr
        });
        this.vpager = BI.createWidget({
            type: "bi.pager",
            width: 72,
            layouts: [{
                type: "bi.horizontal",
                lgap: 20,
                vgap: 1
            }],

            dynamicShow: false,
            pages: v.pages,
            curr: v.curr,
            groups: 0,

            first: false,
            last: false,
            prev: {
                type: "bi.icon_button",
                value: "prev",
                title: BI.i18nText("BI-Up_Page"),
                warningTitle: BI.i18nText("BI-Current_Is_First_Page"),
                height: o.height - 2,
                cls: "direction-pager-prev column-pre-page-h-font"
            },
            next: {
                type: "bi.icon_button",
                value: "next",
                title: BI.i18nText("BI-Down_Page"),
                warningTitle: BI.i18nText("BI-Current_Is_Last_Page"),
                height: o.height - 2,
                cls: "direction-pager-next column-next-page-h-font"
            },

            hasPrev: v.hasPrev,
            hasNext: v.hasNext,
            firstPage: v.firstPage,
            lastPage: v.lastPage
        });

        this.vpager.on(BI.Pager.EVENT_CHANGE, function () {
            if (self.getVPage() !== self.vpager.getCurrentPage()) {
                self.currentPage[0] = self.vpager.getCurrentPage();
                self.fireEvent(BI.DirectionPager.EVENT_CHANGE);
            }
        });
        this.vpager.on(BI.Pager.EVENT_AFTER_POPULATE, function () {
            self.vlabel.setValue(this.getCurrentPage());
        });
    },

    _createHPager: function () {
        var self = this, o = this.options;
        var h = o.vertical;
        this.hlabel = BI.createWidget({
            type: "bi.label",
            width: 20,
            height: o.height,
            value: h.curr
        });
        this.hpager = BI.createWidget({
            type: "bi.pager",
            width: 72,
            layouts: [{
                type: "bi.horizontal",
                rgap: 20,
                vgap: 1
            }],

            dynamicShow: false,
            pages: h.pages,
            curr: h.curr,
            groups: 0,

            first: false,
            last: false,
            prev: {
                type: "bi.icon_button",
                value: "prev",
                title: BI.i18nText("BI-Left_Page"),
                warningTitle: BI.i18nText("BI-Current_Is_First_Page"),
                height: o.height - 2,
                cls: "direction-pager-prev row-pre-page-h-font"
            },
            next: {
                type: "bi.icon_button",
                value: "next",
                title: BI.i18nText("BI-Right_Page"),
                warningTitle: BI.i18nText("BI-Current_Is_Last_Page"),
                height: o.height - 2,
                cls: "direction-pager-next row-next-page-h-font"
            },

            hasPrev: h.hasPrev,
            hasNext: h.hasNext,
            firstPage: h.firstPage,
            lastPage: h.lastPage
        });

        this.hpager.on(BI.Pager.EVENT_CHANGE, function () {
            if (self.getHPage() !== self.hpager.getCurrentPage()) {
                self.currentPage[1] = self.hpager.getCurrentPage();
                self.fireEvent(BI.DirectionPager.EVENT_CHANGE);
            }
        });
        this.hpager.on(BI.Pager.EVENT_AFTER_POPULATE, function () {
            self.hlabel.setValue(this.getCurrentPage());
        });
    },

    getVPage: function () {
        return this.currentPage[0];
    },

    getHPage: function () {
        return this.currentPage[1];
    },

    setVPage: function (v) {
        this.vpager.setValue(v);
    },

    setHPage: function (v) {
        this.hpager.setValue(v);
    },

    populate: function () {
        this.vpager.populate();
        this.hpager.populate();
    }
});
BI.DirectionPager.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.direction_pager", BI.DirectionPager);