BI.AnalysisOperatorButton = FR.extend(BI.Widget, {

    _defaultConfig: function() {
        var conf = BI.AnalysisOperatorButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-analysis-etl-operator-button-group",
            items : ETLCst.ANALYSIS_TABLE_OPERATOR,
            height:30,
            lock:false,
            handler : function(v){
                //alert(v.getValue())
            }
        })
    },

    _init : function() {
        BI.AnalysisOperatorButton.superclass._init.apply(this, arguments);
        var o = this.options;
        this._createButtons();
        var self =  this;
        BI.createWidget({
            type: "bi.inline",
            element:this.element,
            scrollable : false,
            height: o.height,
            items : this.buttons
        })
    },

    _createButtons : function() {
        this.buttons = [];
        var o = this.options;
        var self = this;
        BI.each( BI.createItems(o.items, {
                        type:"bi.button",
                        cls : "bi-analysis-etl-operator-button",
                        isShadowShowingOnSelected:true,
                        height: o.height
                    }), function(idx, item){
            var button = BI.createWidget(item);
            button.on(BI.Button.EVENT_CHANGE, function (v) {
                if(o.lock === true){
                    return;
                }
                self.setValue(v.getValue())
                self.fireEvent(BI.AnalysisOperatorButton.EVENT_OPERATOR_CHANGE, arguments)
            });
            self.buttons.push(button)
        })
        return this.buttons;
    },

    setValue : function(v){
        BI.each(this.buttons, function(idx, item){
            if(!BI.isUndefined(item.setSelected)){
                item.setSelected(v === item.getValue())
            }
        })
    },

    setEnable : function(v) {
        var o = this.options;
        o.lock = !v;
        BI.each(this.buttons, function(idx, item){
            //被选中的不改变状态
            if(!(!BI.isUndefined(item.isSelected) && item.isSelected())){
                item.setEnable(v)
            }
        })
    },

    isEnabled : function(){
        return !this.options.lock;
    },

    getValue : function() {
        var res = null;
        BI.some(this.buttons, function(idx, item){
            if(item.isEnabled() && !BI.isUndefined(item.isSelected) && item.isSelected()){
                res = item.getValue();
                return true;
            }
        })
        return res;
    },

    getElementByValue : function (v) {
        var res = null;
        BI.some(this.buttons, function(idx, item){
            if(item.getValue() === v){
                res = item;
                return true;
            }
        })
        return res;
    }

})
BI.AnalysisOperatorButton.EVENT_OPERATOR_CHANGE = "EVENT_OPERATOR_CHANGE";
$.shortcut("bi.analysis_operator_button", BI.AnalysisOperatorButton)