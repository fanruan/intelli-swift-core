/**
 * Created by windy on 2016/12/20.
 */
BI.DynamicGroupTabButtonManager = BI.inherit(BI.Widget, {

    _const: {
        MERGE_ADD_WIDTH: 245
    },

    _defaultConfig: function () {
        return BI.extend(BI.DynamicGroupTabButtonManager.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-dynamic-group-tab-button-manager",
            items:[],
            height:30
        })
    },

    _init: function () {
        BI.DynamicGroupTabButtonManager.superclass._init.apply(this, arguments);
        var o = this.options;
        this.tab = BI.createWidget({
            type: "bi.button_group",
            height: o.height,
            items: [],
            layouts: [{
                type: "bi.horizontal",
                scrollable : false,
                scrollx: false
            }]
        })

        this.tab.on(BI.ButtonGroup.EVENT_CHANGE, function(value, obj){
            self.fireEvent(BI.DynamicGroupTabButtonManager.EVENT_CHANGE, arguments);
        });

        var self = this;
        this.addSheetButton = BI.createWidget({
            type:"bi.button",
            level: 'ignore',
            text: BI.i18nText("BI-add_sheet"),
            title: BI.i18nText("BI-add_sheet")
        })
        this.addSheetButton.on(BI.Button.EVENT_CHANGE, function(){
            var sId = BI.UUID();
            self.addItems([self._getItemById(sId)]);
            self.tab.setValue(sId);
            self.fireEvent(BI.DynamicGroupTabButtonManager.ADD_SHEET, sId);
        })

        this.mergeSheetButton = BI.createWidget({
            type:"bi.button",
            level: 'ignore',
            text: BI.i18nText("BI-merge_sheet"),
            title: BI.i18nText("BI-merge_sheet")
        })

        this.mergeSheetButton.setWarningTitle(BI.i18nText("BI-Need_Two_Tables_Or_Above_For_Merge"))

        this.mergeSheetButton.on(BI.Button.EVENT_CHANGE, function(v){
            self.fireEvent(BI.DynamicGroupTabButtonManager.MERGE_SHEET, v)
        })

        this.scrollLeft = BI.createWidget({
            type:"bi.icon_button",
            cls:"pre-page-font bi-icon-button-scroll",
            invisible:true
        })

        this.scrollLeft.on(BI.IconButton.EVENT_CHANGE, function(){
            self._scrollLeft();
        })
        this.scrollRight = BI.createWidget({
            type:"bi.icon_button",
            cls:"next-page-font bi-icon-button-scroll",
            invisible:true
        })
        this.scrollRight.on(BI.IconButton.EVENT_CHANGE, function(){
            self._scrollRight();
        })

        BI.Resizers.add(this.getName(), function(e){
            self.resize();
        })

        BI.createWidget({
            type:"bi.left",
            element:this.element,
            items:[{
                type:"bi.horizontal",
                tgap: -1,
                height: o.height,
                scrollx:false,
                cls:"bi-sheet-tab-dynamic-horizontal",
                items:[this.tab,
                    {
                        type:"bi.center_adapt",
                        items:[this.scrollLeft],
                        height: o.height
                    },
                    {
                        type:"bi.center_adapt",
                        items:[this.scrollRight],
                        height: o.height
                    },
                    {
                        type:"bi.vertical_adapt",
                        items:[this.addSheetButton],
                        height: o.height,
                        lgap: 10
                    }, {
                        type:"bi.vertical_adapt",
                        items:[this.mergeSheetButton],
                        height: o.height,
                        lgap: 10
                    }
                ]
            }]
        })
    },

    _getItemById: function(sId){
        var self = this;
        var item = BI.createWidget(self._createItem(sId)) ;
        item.on(BI.DynamicGroupTabSheetButton.EVENT_COPY, function (sheetId) {
            var newId = BI.UUID();
            self.addItems([self._getItemById(newId)]);
            self.fireEvent(BI.DynamicGroupTabButtonManager.EVENT_COPY, sheetId, newId);
        });
        item.on(BI.DynamicGroupTabSheetButton.EVENT_RENAME, function (sheetId) {
            self.fireEvent(BI.DynamicGroupTabButtonManager.EVENT_RENAME, sheetId);
        });
        item.on(BI.DynamicGroupTabSheetButton.EVENT_DELETE, function (sheetId) {
            self.tab.removeItems(sheetId);
            self.mergeSheetButton.setEnable(self.getAllButtons().length > 1);
            self.resize();
            self.fireEvent(BI.DynamicGroupTabButtonManager.EVENT_DELETE, sheetId);
        });
        return item;
    },

    _createItem: function(id){
        return {
            type: "bi.dynamic_group_tab_sheet_button",
            height: 29,
            width: 90,
            value : id,
            text : "Sheet" + this.tab.getAllButtons().length
        }
    },

    _scrollLeft : function() {
        this._scrollTo(this.tab.element[0].scrollLeft - this.scrollSection)
    },

    _scrollRight : function() {
        this._scrollTo(this.tab.element[0].scrollLeft + this.scrollSection)
    },

    _getTotalWidth : function () {
        var totalWidth = this.element.outerWidth();
        totalWidth -= this._const.MERGE_ADD_WIDTH;
        return totalWidth;
    },

    _calculateButtonsWith : function(fn) {
        var buttonWidth = 0;
        var self = this;
        BI.some(this.tab.getAllButtons(), function(idx, item){
            buttonWidth += item.element.outerWidth();
            if(BI.isNotNull(fn) && fn.apply(self, [item])){
                return true;
            }
        })
        return buttonWidth;
    },

    _dealWithScrollButtonState:function (){
        var buttonWidth = this._calculateButtonsWith();
        if(this.tab.element[0].scrollLeft === 0){
            this.scrollLeft.setEnable(false);
        } else {
            this.scrollLeft.setEnable(true);
        }
        var ulWidth = this.tab.element.outerWidth();
        //可以滚动的最大距离
        var maxLeft = buttonWidth - ulWidth;
        if(this.tab.element[0].scrollLeft === maxLeft){
            this.scrollRight.setEnable(false);
        } else {
            this.scrollRight.setEnable(true);
        }
    },

    _needScroll : function (visibleWidth, buttonWidth) {
        var currentLeft = this.tab.element[0].scrollLeft;
        return (visibleWidth > currentLeft && visibleWidth - currentLeft > buttonWidth) ||
            (visibleWidth < currentLeft)
    },

    _scrollTo : function (value) {
        var self = this;
        BI.delay(function () {
            self.tab.element.scrollLeft(value);
            self._dealWithScrollButtonState();
        }, 30);
    },

    _scrollToEnd : function (){
        this._scrollTo(this._calculateButtonsWith())
    },

    resize : function (){
        //获取当前所有可使用的宽度，不包含添加和合并和导航按钮以及之间的空隙
        var totalWidth = this._getTotalWidth();
        //所有button的宽度
        var buttonWidth = this._calculateButtonsWith();
        var width = buttonWidth;
        var showScrollButton = false;
        if(buttonWidth > totalWidth){
            width = totalWidth;
            showScrollButton = true;
        }
        this.scrollLeft.setVisible(showScrollButton);
        this.scrollRight.setVisible(showScrollButton);
        //这边动态改变buttongroup的宽度，因为最大宽度是变的
        this.tab.element.width(width);
        this._dealWithScrollButtonState();
        this.scrollSection = width * 2/3;
        this.scrollSelectedVisible();
    },

    scrollSelectedVisible: function () {
        var value = this.tab.getValue()[0];
        //从index 0到当前选中的tab的所有button的宽度
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

    getAllButtons: function(){
        return this.tab.getAllButtons.apply(this.tab, arguments);
    },

    addItems: function(items){
        this.tab.addItems.apply(this.tab, arguments);
        this.resize();
        this._scrollToEnd();
        this.mergeSheetButton.setEnable(this.getAllButtons().length > 1);
    },

    getValue: function(){
        this.tab.getValue.apply(this.tab, arguments);
    },

    setValue: function(v){
        this.tab.setValue.apply(this.tab, arguments);
    },

    populate: function(){
        this.tab.populate.apply(this.tab, arguments);
        this.resize();
        this.mergeSheetButton.setEnable(this.getAllButtons().length > 1);
    }
})

BI.DynamicGroupTabButtonManager.EVENT_CHANGE = "EVENT_CHANGE";
BI.DynamicGroupTabButtonManager.ADD_SHEET = "ADD_SHEET";
BI.DynamicGroupTabButtonManager.MERGE_SHEET = "MERGE_SHEET";
BI.DynamicGroupTabButtonManager.EVENT_COPY = "EVENT_COPY";
BI.DynamicGroupTabButtonManager.EVENT_RENAME = "EVENT_RENAME";
BI.DynamicGroupTabButtonManager.EVENT_DELETE = "EVENT_DELETE";
$.shortcut("bi.dynamic_group_tab_button_manager", BI.DynamicGroupTabButtonManager);