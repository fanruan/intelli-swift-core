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
        this._createCombos();
        var self =  this;
        BI.createWidget({
            type: "bi.inline",
            element:this.element,
            height: o.height,
            items : this.combos
        })
    },

    _createCombos: function(){
        var self = this;
        this.combos = [];
        BI.each(this.buttons, function(idx, button) {
            var combo = BI.createWidget({
                type: "bi.bubble_combo",
                cls: "select-bubble-combo",
                height: 30,
                trigger: "",
                el: button,
                popup: {
                    type: "bi.bubble_bar_popup_view",
                    buttons: [{
                        value: BI.i18nText(BI.i18nText("BI-Sure")),
                        handler: function () {
                            combo.hideView();
                            self.fireEvent(BI.AnalysisOperatorButton.EVENT_OPERATOR_CHANGE, button, true);
                        }
                    }, {
                        value: BI.i18nText("BI-Cancel"),
                        level: "ignore",
                        handler: function () {
                            combo.hideView();
                        }
                    }],
                    el: {
                        type: "bi.vertical_adapt",
                        items: [{
                            type: "bi.label",
                            whiteSpace: "normal",
                            text: BI.i18nText("BI-Current_Operator_May_Cause_Problem_With_After_Confirm_To_Continue"),
                            cls: "delete-label",
                            textAlign: "left",
                            width: 300
                        }],
                        width: 300,
                        height: 100,
                        hgap: 20
                    },
                    maxHeight: 140,
                    minWidth: 340
                }
            });
            self.combos.push(combo);
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
                self.fireEvent(BI.AnalysisOperatorButton.EVENT_OPERATOR_CHANGE, v, false);
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

    setEnable : function(v, txt) {
        var o = this.options;
        o.lock = !v;
        BI.each(this.buttons, function(idx, item){
            //被选中的不改变状态
            if(!(!BI.isUndefined(item.isSelected) && item.isSelected())){
                item.setEnable(v)
                item.setWarningTitle(txt);
            }
        })
    },

    isEnabled : function(){
        return !this.options.lock;
    },

    getAllButtons: function(){
        return this.buttons;
    },

    getAllCombos: function(){
        return this.combos;
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