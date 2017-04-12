BI.AnalysisETLMergeSheetPopover = BI.inherit(BI.BarPopoverSection, {

    _constants : {
        NORTH_HEIGHT:50
    },

    _defaultConfig : function () {
        return BI.extend(BI.AnalysisETLMergeSheetPopover.superclass._defaultConfig.apply(this, arguments), {
            extraCls:"bi-analysis-etl-operator-merge-choose-sheet",
            items:[{text:"sheet1", value:1},{text:"sheet2", value:2},{text:"sheet3", value:3}],
            value:[],
            selectCount : 2
        })
    },

    rebuildNorth : function (north) {
        var self = this, o = this.options;
        BI.createWidget({
            type: "bi.label",
            element: north,
            text:  BI.i18nText('BI-Choose_table'),
            textAlign: "left",
            height : self._constants.NORTH_HEIGHT
        });
        return true
    },

    rebuildCenter : function (center) {
        var self = this, o = this.options;
        this.vertical = BI.createWidget({
            type:"bi.vertical",
            items:[]
        })
        BI.createWidget({
            type:"bi.vtape",
            element:center,
            lgap:20,
            rgap:20,
            items:[{
                el:{
                    type:"bi.center_adapt",
                    items:[{
                        type:"bi.label",
                        cls: o.extraCls + "-content",
                        text: BI.i18nText('BI-Choose_Sheet_For_Merge'),
                        textAlign:"left"
                    }]
                },
                height:55
            },{
                el: this.vertical
            }]
        })
        this.items = [];
        BI.each(o.items, function (idx, item) {
            var button = BI.createWidget(BI.extend({
                type:"bi.analysis_etl_choose_sheet_button"
            }, item))
            self.items.push(button);
            button.on(this.BI.Controller.EVENT_CHANGE, function () {
                self.chooseLastTwoSheet(button.getValue(), self);
            })
        })
        this.vertical.populate(this.items);
        self.selectDefaultSheet(self);
    },

    end: function(){
        this.fireEvent(BI.AnalysisETLMergeSheetPopover.EVENT_CHANGE, this.getValue());
    },

    //原controller
    selectDefaultSheet : function () {
        if(this.value.length === 0) {
            var o = this.options;
            var self = this;
            BI.each(BI.range(0, o.selectCount), function (idx) {
                if(BI.isNotNull(self.items[idx])){
                    self.value.push(self.items[idx].getValue())
                }
            })
        }
        this._refreshSelected();
    },

    _refreshSelected : function () {
        var self = this;
        var o = this.options;
        while(this.value.length > o.selectCount) {
            this.value.shift();
        }
        BI.each(this.items, function (idx, item) {
            item.setSelected(BI.indexOf(self.value, item.getValue()) > -1)
        })
    },

    getValue : function () {
        return this.value;
    },

    chooseLastTwoSheet : function (value) {
        if(BI.indexOf(this.value, value) < 0) {
            this.value.push(value);
        }
        this._refreshSelected();
    }


});
BI.AnalysisETLMergeSheetPopover.EVENT_CHANGE="event_change";
BI.shortcut("bi.analysis_etl_choose_sheet_popover", BI.AnalysisETLMergeSheetPopover)