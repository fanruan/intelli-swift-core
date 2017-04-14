/**
 * Created by Young's on 2017/4/5.
 */
BI.AnalysisDynamicTabButton = BI.inherit(BI.Widget, {
    props: {
        extraCls: "bi-sheet-tab-dynamic-button",
        items: [{
            text: "sheet1"
        }],
        height: 30
    },

    render: function () {
        var self = this, o = this.options;
        this.tab = BI.createWidget({
            type: "bi.button_group",
            tagName: "ul",
            height: o.height,
            items: [],
            layouts: [{
                type: "bi.inline",
                scrollable: false,
                items: [{
                    el: {
                        type: "bi.horizontal",
                        tagName: "li"
                    }
                }]
            }]
        });
        this.addSheetButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Add_sheet"),
            title: BI.i18nText("BI-Add_sheet")
        });
        this.addSheetButton.on(BI.Button.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.AnalysisDynamicTabButton.ADD_SHEET, v)
        });


        this.mergeSheetButton = BI.createWidget({
            type: "bi.button",
            level: 'ignore',
            text: BI.i18nText("BI-merge_sheet"),
            title: BI.i18nText("BI-merge_sheet")
        });

        this.mergeSheetButton.setWarningTitle(BI.i18nText("BI-Need_Two_Tables_Or_Above_For_Merge"))

        this.mergeSheetButton.on(BI.Button.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.AnalysisDynamicTabButton.MERGE_SHEET, v)
        });

        this.scrollLeft = BI.createWidget({
            type: "bi.icon_button",
            cls: "pre-page-font bi-icon-button-scroll",
            invisible: true
        });
        this.scrollLeft.on(BI.IconButton.EVENT_CHANGE, function () {
            self.controller.scrollLeft();
        });

        this.scrollRight = BI.createWidget({
            type: "bi.icon_button",
            cls: "next-page-font bi-icon-button-scroll",
            invisible: true
        });
        this.scrollRight.on(BI.IconButton.EVENT_CHANGE, function () {
            self.controller.scrollRight();
        });

        BI.createWidget({
            type: "bi.left",
            height: o.height,
            element: this,
            items: [{
                type: "bi.horizontal",
                height: o.height,
                scrollx: false,
                cls: "bi-sheet-tab-dynamic-horizontal",
                items: [this.tab,
                    {
                        type: "bi.center_adapt",
                        items: [this.scrollLeft],
                        height: o.height
                    },
                    {
                        type: "bi.center_adapt",
                        items: [this.scrollRight],
                        height: o.height
                    },
                    {
                        type: "bi.center_adapt",
                        items: [this.addSheetButton],
                        height: o.height,
                        cls: "bi-dynamic-tab-button"
                    }, {
                        type: "bi.center_adapt",
                        items: [this.mergeSheetButton],
                        height: o.height,
                        cls: "bi-dynamic-tab-button"
                    }
                ]
            }]
        });
        BI.Resizers.add(this.getName(), function(e){
            self.resize();
            self.scrollSelectedVisible();
        })
    },

    _needScroll : function (visibleWidth, buttonWidth) {
        var currentLeft = this.tab.element[0].scrollLeft;
        return (visibleWidth > currentLeft && visibleWidth - currentLeft > buttonWidth) ||
            (visibleWidth < currentLeft)
    },

    _getTotalWidth : function () {
        var totalWidth = this.element.outerWidth();
        totalWidth -= this.addSheetButton.element.outerWidth();
        totalWidth -= this.mergeSheetButton.element.outerWidth();
        //两个小按钮和空隙固定90
        totalWidth -= 50;
        return totalWidth;
    },

    scrollSelectedVisible : function () {
        var value = this.tab.getValue()[0];
        var visibleWidth = this._calculateButtonsWith(function(item){
            if(item.getValue() === value){
                return true;
            }
        })
        var buttonWidth = this._getTotalWidth();
        var scrollWidth = visibleWidth - buttonWidth/2;
        if(this._needScroll(visibleWidth, buttonWidth)) {
            this._scrollTo(scrollWidth)
        }
    },

    setMergeEnable : function (enable) {
        this.mergeSheetButton.setEnable(enable);
    },

    _calculateButtonsWith : function(fn) {
        var buttonWidth = 0;
        var self = this;
        BI.some(this.tab.getAllButtons(), function(idx, item){
            buttonWidth += item.element.outerWidth();
            if(!(BI.isUndefined(fn)) && fn.apply(self, [item])){
                return true;
            }
        });
        if(BI.isUndefined(fn)){
            this.buttonWidth = buttonWidth;
        }
        return buttonWidth;
    },

    scrollLeft : function() {
        this._scrollTo(this.tab.element[0].scrollLeft - this.scrollSection)
    },

    _dealWithScrollButtonState:function (){
        var self = this;
        if(self.tab.element[0].scrollLeft === 0){
            self.scrollLeft.setEnable(false);
        } else {
            self.scrollLeft.setEnable(true);
        }
        if(BI.isUndefined(this.buttonWidth)){
            self._calculateButtonsWith();
        }
        var ulWidth = self.tab.element.outerWidth();
        var maxLeft = this.buttonWidth - ulWidth;
        if(self.tab.element[0].scrollLeft === maxLeft){
            self.scrollRight.setEnable(false);
        } else {
            self.scrollRight.setEnable(true);
        }
    },

    scrollRight : function() {
        this._scrollTo(this.tab.element[0].scrollLeft + this.scrollSection)
    },

    scrollToEnd : function (){
        if(BI.isUndefined(this.buttonWidth)){
            this._calculateButtonsWith();
        }
        this._scrollTo(this.buttonWidth)
    },

    _scrollTo : function (value) {
        var self = this;
        this.tab.element.animate({
            scrollLeft: value
        },  200, null, function(e){
            self._dealWithScrollButtonState();
        })
    },

    resize : function (){
        var totalWidth = this._getTotalWidth();
        var buttonWidth = this._calculateButtonsWith();
        var width = buttonWidth;
        var showScrollButton = false;
        if(buttonWidth > totalWidth){
            width = totalWidth;
            showScrollButton = true;
        }
        this.scrollLeft.setVisible(showScrollButton);
        this.scrollRight.setVisible(showScrollButton);
        this.tab.element.width(width);
        this._dealWithScrollButtonState();
        this.scrollSection = width * 2/3;
    },

    getButton: function (v) {
        return BI.find(this.tab.getAllButtons(), function (idx, item) {
            return item.getValue() === v;
        })
    },

    empty : function () {
        this.tab.empty();
    }
});
BI.AnalysisDynamicTabButton.ADD_SHEET = "ADD_SHEET";
BI.AnalysisDynamicTabButton.MERGE_SHEET = "MERGE_SHEET";
BI.shortcut("bi.analysis_dynamic_tab_button", BI.AnalysisDynamicTabButton);