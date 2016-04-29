/**
 * 自定义滚动条
 *
 * Created by GUY on 2016/2/15.
 * @class BI.CustomScrollTableScrollBar
 * @extends BI.Widget
 */
BI.CustomScrollTableScrollBar = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CustomScrollTableScrollBar.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-scroll-table-scroll-bar",
            axis: BI.Axis.Vertical,
            tagName: "ul"
        });
    },

    _init: function () {
        BI.CustomScrollTableScrollBar.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.container = BI.createWidget({
            type: "bi.layout",
            tagName: "li",
            height: 1,
            width: 1
        });
        BI.createWidget({
            type: "bi.default",
            element: this.element,
            items: [this.container]
        });
        var config = {
            theme: "bi-default",
            scrollInertia: 0,
            scrollEasing: false,
            scrollButtons: {
                enable: true
            },
            callbacks: {
                onInit: false,
                onScrollStart: false,
                onScroll: false,
                onTotalScroll: function () {
                    self.fireEvent(BI.CustomScrollTableScrollBar.EVENT_SCROLL_BEHIND);
                },
                onTotalScrollBack: function () {
                    self.fireEvent(BI.CustomScrollTableScrollBar.EVENT_SCROLL_FRONT);
                },
                whileScrolling: function () {
                    self.fireEvent(BI.CustomScrollTableScrollBar.EVENT_SCROLL);
                },
                onTotalScrollOffset: 0,
                onTotalScrollBackOffset: 0,
                alwaysTriggerOffsets: true,
                onOverflowY: false,
                onOverflowX: false,
                onOverflowYNone: false,
                onOverflowXNone: false
            }
        };
        switch (o.axis) {
            case BI.Axis.Vertical:
                this.element.width(18);
                BI.defer(function () {
                    self.element.mCustomScrollbar(BI.extend(config, {
                        axis: "y"
                    }));
                });
                break;
            case BI.Axis.Horizontal:
                this.element.height(18);
                BI.defer(function () {
                    self.element.mCustomScrollbar(BI.extend(config, {
                        axis: "x"
                    }));
                });
                break;
        }
    },

    getScrollLeft: function () {
        return -1 * $(".mCSB_container", this.element).position().left;
    },

    getScrollTop: function () {
        return -1 * $(".mCSB_container", this.element).position().top;
    },

    getScrollHeight: function () {
        return this.container.element.height();
    },

    getScrollWidth: function () {
        return this.container.element.width();
    },

    setScrollLeft: function (scrollLeft) {
        this.element.mCustomScrollbar('scrollTo', scrollLeft);
    },

    setScrollTop: function (scrollTop) {
        this.element.mCustomScrollbar('scrollTo', scrollTop);
    },

    setScrollHeight: function (height) {
        this.container.element.height(height);
        $(".mCSB_container", this.element).height(height);
        this.element.mCustomScrollbar("update");
    },

    setScrollWidth: function (width) {
        this.container.element.width(width);
        $(".mCSB_container", this.element).width(width);
        this.element.mCustomScrollbar("update");
    },

    setScrollToLeft: function () {
        $(".mCSB_container", this.element).css("left", "0");
        $(".mCSB_scrollTools .mCSB_dragger").css("left", "0");
    },

    setScrollToTop: function () {
        $(".mCSB_container", this.element).css("top", "0");
        $(".mCSB_scrollTools .mCSB_dragger").css("top", "0");
    },

    scrollToTop: function () {
        this.element.mCustomScrollbar('scrollTo', 'top');
    },

    scrollToBottom: function () {
        this.element.mCustomScrollbar('scrollTo', 'bottom');
    },

    scrollToLeft: function () {
        this.element.mCustomScrollbar('scrollTo', 'left');
    },

    scrollToRight: function () {
        this.element.mCustomScrollbar('scrollTo', 'right');
    }
});
BI.CustomScrollTableScrollBar.EVENT_SCROLL = "CustomScrollTableScrollBar.EVENT_SCROLL";
BI.CustomScrollTableScrollBar.EVENT_SCROLL_BEHIND = "CustomScrollTableScrollBar.EVENT_SCROLL_BEHIND";
BI.CustomScrollTableScrollBar.EVENT_SCROLL_FRONT = "CustomScrollTableScrollBar.EVENT_SCROLL_FRONT";
$.shortcut('bi.custom_scroll_table_scroll_bar', BI.CustomScrollTableScrollBar);