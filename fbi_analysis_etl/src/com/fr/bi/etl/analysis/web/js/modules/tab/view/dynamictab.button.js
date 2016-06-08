BI.DynamicTabButton = FR.extend(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DynamicTabButton.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-sheet-tab-dynamic-button",
            items:[{
                text: "sheet1"
            }],
            height:30
        })
    },


    _initController : function (){
        this.controller = new BI.DynamactabButtonColltroller(this.options);
        this.controller.setWidget(this);
    },

    _init: function () {
        BI.DynamicTabButton.superclass._init.apply(this, arguments);
        this._initController();
        var o = this.options;
        this.tab = BI.createWidget({
            type: "bi.button_group",
            tagName:"ul",
            height: o.height,
            items: [],
            layouts: [{
                type: "bi.inline",
                scrollable : false,
                items: [{
                    el: {
                        type: "bi.horizontal",
                        tagName:"li"
                    }
                }]
            }]
        })

        var self = this;
        this.addSheetButton = BI.createWidget({
            type:"bi.button",
            level: 'ignore',
            text: BI.i18nText("BI-add_sheet"),
            title: BI.i18nText("BI-add_sheet")
        })
        this.addSheetButton.on(BI.Button.EVENT_CHANGE, function(v){
            //self.controller.addNewSheet();
            self.fireEvent(BI.DynamicTabButton.ADD_SHEET, v)
        })



        this.mergeSheetButton = BI.createWidget({
            type:"bi.button",
            level: 'ignore',
            text: BI.i18nText("BI-merge_sheet"),
            title: BI.i18nText("BI-merge_sheet")
        })

        this.mergeSheetButton.setWarningTitle(BI.i18nText("BI-Need_Two_Tables_Or_Above_For_Merge"))

        this.mergeSheetButton.on(BI.Button.EVENT_CHANGE, function(v){
            self.fireEvent(BI.DynamicTabButton.MERGE_SHEET, v)
        })

        this.scrollLeft = BI.createWidget({
            type:"bi.icon_button",
            cls:"pre-page-font bi-icon-button-scroll",
            invisible:true
        })

        this.scrollLeft.on(BI.IconButton.EVENT_CHANGE, function(){
            self.controller.scrollLeft();
        })
        this.scrollRight = BI.createWidget({
            type:"bi.icon_button",
            cls:"next-page-font bi-icon-button-scroll",
            invisible:true
        })
        this.scrollRight.on(BI.IconButton.EVENT_CHANGE, function(){
            self.controller.scrollRight();
        })

        BI.createWidget({
            type:"bi.left",
            height: o.height,
            element:this.element,
            items:[{
                type:"bi.horizontal",
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
                        type:"bi.center_adapt",
                        items:[this.addSheetButton],
                        height: o.height,
                        cls:"bi-dynamic-tab-button"
                    }, {
                        type:"bi.center_adapt",
                        items:[this.mergeSheetButton],
                        height: o.height,
                        cls:"bi-dynamic-tab-button"
                    }
                ]
            }]
        })
    },

    getButton : function (v) {
       return   BI.find( this.tab.getAllButtons(), function (idx, item) {
            return item.getValue() === v;
       })
    },

    empty : function () {
        this.tab.empty()
    }
})

BI.DynamicTabButton.ADD_SHEET = "ADD_SHEET";
BI.DynamicTabButton.MERGE_SHEET = "MERGE_SHEET";


$.shortcut("bi.dynamic_tab_button", BI.DynamicTabButton);