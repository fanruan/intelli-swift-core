

BI.AnalysisETLMergeSheetPopoverController = BI.inherit(BI.Controller, {


    _defaultConfig : function () {
        return BI.extend(BI.AnalysisETLMergeSheetPopoverController.superclass._defaultConfig.apply(this, arguments), {
            selectCount : 2,
            value: null
        })
    },

    _init : function () {
        BI.AnalysisETLMergeSheetPopoverController.superclass._init.apply(this, arguments)
        this.value = this.options.value || [];
    },

    selectDefaultSheet : function (widget) {
        if(this.value.length === 0) {
            var o = this.options;
            var self = this;
            BI.each(BI.range(0, o.selectCount), function (idx) {
                if(BI.isNotNull(widget.items[idx])){
                    self.value.push(widget.items[idx].getValue())
                }
            })
        }
        this._refreshSelected(widget);
    },
    
    _refreshSelected : function (widget) {
        var self = this;
        var o = this.options;
        while(this.value.length > o.selectCount) {
            this.value.shift();
        }
        BI.each(widget.items, function (idx, item) {
            item.setSelected(BI.indexOf(self.value, item.getValue()) > -1)
        })
    },

    getValue : function () {
       return this.value;
    },

    chooseLastTwoSheet : function (value, widget) {
        if(BI.indexOf(this.value, value) < 0) {
            this.value.push(value);
        }
        this._refreshSelected(widget);
    }

});