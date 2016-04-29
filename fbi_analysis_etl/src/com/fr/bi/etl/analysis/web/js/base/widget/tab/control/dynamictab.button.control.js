BI.DynamactabButtonColltroller = BI.inherit(BI.Controller, {

    _constant : {
        defaultScrollSection:768
    },

    _defaultConfig: function () {
        return BI.extend(BI.DynamactabButtonColltroller.superclass._defaultConfig.apply(this, arguments), {
            items:[]
        });
    },

    _init: function () {
        BI.DynamactabButtonColltroller.superclass._init.apply(this, arguments);
        var o = this.options;
        this.scrollSection = this._constant.defaultScrollSection;
        var self = this;
    },

    setWidget : function (widget){
        this.widget = widget;
        var self = this;
        BI.Resizers.add(widget.getName(), function(e){
            self.resize();
            self.scrollSelectedVisible();
        })
    },

    scrollSelectedVisible : function () {
        var value = this.widget.tab.getValue()[0];
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

    _needScroll : function (visibleWidth, buttonWidth) {
        var currentLeft = this.widget.tab.element[0].scrollLeft;
        return (visibleWidth > currentLeft && visibleWidth - currentLeft > buttonWidth) ||
            (visibleWidth < currentLeft)
    },

    _getTotalWidth : function () {
        var totalWidth = this.widget.element.outerWidth();
        totalWidth -= this.widget.addSheetButton.element.outerWidth();
        totalWidth -= this.widget.mergeSheetButton.element.outerWidth();
        //两个小按钮和空隙固定90
        totalWidth -= 50;
        return totalWidth;
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
        this.widget.scrollLeft.setVisible(showScrollButton);
        this.widget.scrollRight.setVisible(showScrollButton);
        this.widget.tab.element.width(width);
        this._dealWithScrollButtonState();
        this.scrollSection = width * 2/3;
    },

    setMergeEnable : function (enable) {
        this.widget.mergeSheetButton.setEnable(enable);
    },

    _calculateButtonsWith : function(fn) {
        var buttonWidth = 0;
        var self = this;
        BI.some(this.widget.tab.getAllButtons(), function(idx, item){
            buttonWidth += item.element.outerWidth();
            if(!(BI.isUndefined(fn)) && fn.apply(self, [item])){
                return true;
            }
        })
        if(BI.isUndefined(fn)){
            this.buttonWidth = buttonWidth;
        }
        return buttonWidth;
    },

    scrollLeft : function() {
        this._scrollTo(this.widget.tab.element[0].scrollLeft - this.scrollSection)
    },

    _dealWithScrollButtonState:function (){
        var self = this;
        if(self.widget.tab.element[0].scrollLeft === 0){
            self.widget.scrollLeft.setEnable(false);
        } else {
            self.widget.scrollLeft.setEnable(true);
        }
        if(BI.isUndefined(this.buttonWidth)){
            self._calculateButtonsWith();
        }
        var ulWidth = self.widget.tab.element.outerWidth();
        var maxLeft = this.buttonWidth - ulWidth;
        if(self.widget.tab.element[0].scrollLeft === maxLeft){
            self.widget.scrollRight.setEnable(false);
        } else {
            self.widget.scrollRight.setEnable(true);
        }
    },

    scrollRight : function() {
        this._scrollTo(this.widget.tab.element[0].scrollLeft + this.scrollSection)
    },

    scrollToEnd : function (){
        if(BI.isUndefined(this.buttonWidth)){
            this._calculateButtonsWith();
        }
        this._scrollTo(this.buttonWidth)
    },

    _scrollTo : function (value) {
        var self = this;
        this.widget.tab.element.animate({
            scrollLeft: value
        },  200, null, function(e){
            self._dealWithScrollButtonState();
        })
    }

})