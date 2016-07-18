/**
 * @class BI.ConfNumberIntervalCustomGroupTab
 * @extend BI.Widget
 */

BI.ConfNumberIntervalCustomGroupTab = BI.inherit(BI.Widget,{

    constants:{
        itemHeight:25,
        addButtonHeight:30,
        intervalHeight:40,
        editorWidth:155,
        tgap:10,
        hgap:10,
        textWidth:85
    },

    _defaultConfig:function(){
        return BI.extend(BI.ConfNumberIntervalCustomGroupTab.superclass._defaultConfig.apply(this,arguments),{
            baseCls:"bi-number-custom-group-tab",
            tab:{

            }
        });
    },

    _init:function(){
        BI.ConfNumberIntervalCustomGroupTab.superclass._init.apply(this,arguments);

        var self = this,o = this.options;

        this.tab = BI.createWidget({
            direction: "custom",
            type: "bi.tab",
            element: this.element,
            tab: o.tab,
            cardCreator: BI.bind(this._createTabs, this)
        });

        this.tab.on(BI.Tab.EVENT_CHANGE, function(){
            self._checkState();
        });

        this.tab.setSelect(BI.ConfNumberIntervalCustomGroupCombo.Type_Auto);
    },

    _checkState: function(){
        switch (this.tab.getSelect()) {
            case BI.ConfNumberIntervalCustomGroupCombo.Type_Custom:
                this.panel && (this.panel.isEmptyPanel() ? this.fireEvent(BI.ConfNumberIntervalCustomGroupTab.EVENT_EMPTY_GROUP) :
                    (this.panel.isValid() ? this.fireEvent(BI.ConfNumberIntervalCustomGroupTab.EVENT_VALID) : this.fireEvent(BI.ConfNumberIntervalCustomGroupTab.EVENT_ERROR)));
                break;
            case BI.ConfNumberIntervalCustomGroupCombo.Type_Auto:
                if(BI.isNull(this.space)){
                    this.space = this._checkInterval();
                    this.editor.setValue(this.space);
                }
                this.fireEvent(BI.ConfNumberIntervalCustomGroupTab.EVENT_VALID);
                break;
        }
    },

    _createPanel:function(){
        var self = this;
        this.panel = BI.createWidget({
            type:"bi.conf_number_custom_group_panel"
        });
        this.panel.on(BI.ConfNumberIntervalCustomGroupPanel.EVENT_ERROR,function(){
            self.fireEvent(BI.ConfNumberIntervalCustomGroupTab.EVENT_ERROR);
        });

        this.panel.on(BI.ConfNumberIntervalCustomGroupPanel.EVENT_VALID,function(){
            self.fireEvent(BI.ConfNumberIntervalCustomGroupTab.EVENT_VALID);
        });

        this.panel.on(BI.ConfNumberIntervalCustomGroupPanel.EVENT_EMPTY_GROUP,function(){
            self.fireEvent(BI.ConfNumberIntervalCustomGroupTab.EVENT_EMPTY_GROUP);
        });
        this.panel.populate(this._createItems());
    },

    _createTabs : function(v){
        var self = this;
        switch (v){
            case BI.ConfNumberIntervalCustomGroupCombo.Type_Custom:
                this._createPanel();
                this.other = BI.createWidget({
                    type:"bi.conf_number_custom_group_other"
                });
                return BI.createWidget({
                    type:"bi.absolute",
                    items:[{
                        el:{
                            type:"bi.vertical",
                            items:[this.panel,{
                                el:this.other,
                                tgap:this.constants.tgap
                            }]
                        },
                        top:0,
                        left:0,
                        right:0
                    }]
                });
            case BI.ConfNumberIntervalCustomGroupCombo.Type_Auto:
                this.editor = BI.createWidget({
                    type:"bi.editor",
                    cls:"group-tab-editor",
                    height:this.constants.addButtonHeight,
                    validationChecker: function (v) {
                        if (!BI.isNumeric(v) || BI.parseFloat(v) > self.max || BI.parseFloat(v) <= 0) {
                            return false;
                        }
                        return true;
                    }
                });
                this.editor.on(BI.Editor.EVENT_CONFIRM, function(){
                    self.space = this.getValue();
                });
                return BI.createWidget({
                    type:"bi.htape",
                    items:[{
                        el:{
                            type:"bi.label",
                            cls:"group-tab-label",
                            textAlign:"left",
                            height:this.constants.itemHeight,
                            text:BI.i18nText("BI-Interval_Span")
                        },
                        width:this.constants.textWidth
                    },this.editor]
                });
        }
    },

    _createItems:function(nodes){
        if(BI.isNull(nodes)){
            var current = this.min;
            var items = [];
            while(current + this.space < this.max){
                items.push({
                    type:"bi.conf_number_custom_group_item",
                    group_name:current + "~" + (current + this.space),
                    height:this.constants.intervalHeight,
                    min:current,
                    max:current + this.space,
                    closemin:true,
                    closemax:false
                });
                current += this.space;
            }
            if(current <= this.max){
                items.push({
                    type:"bi.conf_number_custom_group_item",
                    group_name:current + "~" + this.max,
                    height:this.constants.intervalHeight,
                    min:current,
                    max:this.max,
                    closemin:true,
                    closemax:true
                });
            }
            return items;
        }

        BI.each(nodes,function(idx,node){
            if(!BI.has(node,"max")){
                node["max"] = "";
            }
            if(!BI.has(node,"closemax")){
                if(idx === nodes.length - 1){
                    node["closemax"] = true;
                }else{
                    node["closemax"] = true;
                }
            }
            if(!BI.has(node,"min")){
                node["min"] = "";
            }
            if(!BI.has(node,"closemin")){
                node["closemin"] = true;
            }
        });
        return BI.createItems(nodes,{
            type:"bi.conf_number_custom_group_item",
            height:this.constants.intervalHeight
        });
    },

    _checkMagnifyCount: function (number) {
        var numText = number + "";
        var dotText = numText.split(".")[0];
        return dotText.length;
    },

    _checkInterval:function(){
        var self = this;
        var min = Math.abs(this.min) + "";
        var max = Math.abs(this.max) + "";
        var minCount = this._checkMagnifyCount(min);
        var maxCount = this._checkMagnifyCount(max);
        var count = minCount > maxCount ? minCount : maxCount;
        var magnify = 1;
        //缩小补零
        var s = "0.";
        while (count - minCount > 0) {
            s += "0";
            minCount++;
        }
        min = min.replace(".", "");
        min = s + min;
        s = "0.";
        while (count - maxCount > 0) {
            s += "0";
            maxCount++;
        }
        max = max.replace(".", "");
        max = s + max;

        //后面补零对齐
        var zeros = max.length - min.length;
        if(zeros > 0){
            while (zeros-- > 0) {
                min += "0";
            }
        }else{
            while (zeros++ < 0) {
                max += "0";
            }
        }
        //截零
        var i = max.length - 1, add = "0.";
        while (min[i] === "0" && max[i] === "0" && this.min != 0 && this.max != 0) {
            i--;
        }

        while (count-- > 0) {
            magnify *= 10;
        }

        //截位/截位+1
        min = this.min < 0 ? -(cutBig(min)) : cutSmall(min);
        max = this.max < 0 ? -(cutSmall(max)) : cutBig(max);

        //(max - min) / 5
        var genMin = min.mul(magnify);
        var genMax = max.mul(magnify);
        return BI.parseFloat(genMax.sub(genMin)).div(5);

        function cutSmall(val){
            return BI.parseFloat(val.substring(0, i));
        }

        function  cutBig(val){
            if(val[i] === "0"){
                return BI.parseFloat(val);
            }
            val = val.substring(0, i);
            var length = val.length - 2;
            while (--length > 0) {
                add += "0";
            }
            add += "1";
            if(val[i-1] === "."){
                return BI.parseFloat(val) + 1;
            }
            return BI.parseFloat(val) + BI.parseFloat(add);
        }
    },

    _populatePane: function(configs){
        var config = configs.group_value;
        switch (configs.type) {
            case BICst.NUMBER_INTERVAL_CUSTOM_GROUP_CUSTOM:
                this.tab.setSelect(BI.ConfNumberIntervalCustomGroupTab.Type_Group_Custom);
                this.panel && this.panel.populate(this._createItems(BI.isNull(config) ? [] : (BI.isNull(config.group_nodes) ? [] : config.group_nodes)));
                this.other && this.other.setValue(BI.isNull(config) ? config : config.use_other);
                break;
            case BICst.NUMBER_INTERVAL_CUSTOM_GROUP_AUTO:
            default :
                this.tab.setSelect(BI.ConfNumberIntervalCustomGroupTab.Type_Group_Auto);
                this.space = (BI.isNull(config) || BI.isNull(config.group_interval)) ? this._checkInterval() : BI.parseFloat(config.group_interval);
                this.editor && this.editor.setValue(this.space);
                this.panel && this.panel.populate(this._createItems());
                break;
        }
    },

    getValue:function(){
        var group = {};
        var value = group["group_value"] = {};
        switch (this.tab.getSelect()){
            case BI.ConfNumberIntervalCustomGroupCombo.Type_Custom:
                group["type"] = BI.ConfNumberIntervalCustomGroupTab.Type_Group_Custom;
                value["group_nodes"] = this.panel.getValue();
                if(this.other.isValid()){
                    value["use_other"] = this.other.getValue();
                }
                value["min"] = this.min;
                value["max"] = this.max;
                break;
            case BI.ConfNumberIntervalCustomGroupTab.Type_Group_Auto:
                group["type"] = BI.ConfNumberIntervalCustomGroupTab.Type_Group_Auto;
                value["group_interval"] = this.editor.getValue();
                value["min"] = this.min;
                value["max"] = this.max;
                break;
        }
        return group;
    },

    populate:function(configs){
        if(BI.isNull(configs)){
            return;
        }
        var self = this, o = this.options;
        if(BI.isNotNull(configs.group_value)){
            this.max = BI.parseInt(configs.group_value.max);
            this.min = BI.parseInt(configs.group_value.min);
            this._populatePane(configs);
            return;
        }
        o.model.getMinMaxValueForNumberCustomGroup(o.dId, function(res){
            self.max = BI.parseInt(res.max);
            self.min = BI.parseInt(res.min);
            self._populatePane(configs);
        });
    }
});

BI.extend(BI.ConfNumberIntervalCustomGroupTab,{
    Type_Group_Custom:BICst.NUMBER_INTERVAL_CUSTOM_GROUP_CUSTOM,
    Type_Group_Auto:BICst.NUMBER_INTERVAL_CUSTOM_GROUP_AUTO
});

BI.ConfNumberIntervalCustomGroupTab.EVENT_ERROR = "EVENT_ERROR";
BI.ConfNumberIntervalCustomGroupTab.EVENT_EMPTY_GROUP = "EVENT_EMPTY_GROUP";
BI.ConfNumberIntervalCustomGroupTab.EVENT_VALID = "EVENT_VALID";

$.shortcut("bi.conf_number_custom_group_tab",BI.ConfNumberIntervalCustomGroupTab);