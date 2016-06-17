/**
 * 警戒线操作面板
 * @class BI.CordonPane
 * @extend BI.Widget
 */
BI.CordonPane = BI.inherit(BI.Widget, {

    constants:{
        bgap:10,
        addButtonHeight:30,
        iconTextButtonWidth:73,
        vgap: 10,
        hgap: 10,
        itemHeight: 40,
        initialMax: 100
    },

    _defaultConfig: function () {
        return BI.extend(BI.CordonPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cordon-pane",
            dId: ""
        })
    },

    _init: function () {
        BI.CordonPane.superclass._init.apply(this, arguments);

        var self = this, o = this.options;
        this.button = BI.createWidget({
            type:"bi.icon_text_item",
            cls:"group-add-font group-tab-button-text",
            height:this.constants.addButtonHeight,
            width:this.constants.iconTextButtonWidth,
            text:BI.i18nText("BI-Conditions_Setting")
        });

        this.button_group = BI.createWidget({
            type: "bi.list_pane",
            cls: "cordon-group",
            el: {
                type: "bi.button_group",
                chooseType: BI.ButtonGroup.CHOOSE_TYPE_NONE,
                layouts: [{
                    type: "bi.vertical"
                }]
            },
            items: o.items
        });

        this.button.on(BI.IconTextItem.EVENT_CHANGE,function(){
            if(self.button_group.isValid()){
                self.addItem();
                self.scrollToBottom();
            }
        });

        this.buttons = this.button_group.getAllButtons();

        return BI.createWidget({
            type: "bi.panel",
            element:this.element,
            title: BI.i18nText("BI-Cordon"),
            titleButtons: [this.button],
            el: this.button_group,
            logic:{
                dynamic: true
            }
        });

    },

    scrollToBottom: function () {
        var self = this;
        BI.delay(function () {
            self.button_group.element.scrollTop(BI.MAX);
        }, 30);
    },

    addItem: function(){
        this.button_group.addItems([{
            type: "bi.cordon_item",
            cordon_name: BI.i18nText("BI-Cordon") + (this.buttons.length + 1),
            height: this.constants.itemHeight
        }]);
        this.buttons = this.button_group.getAllButtons();
    },

    populate: function () {
        var self = this, o = this.options;
        var cordon = BI.Utils.getDimensionCordonByID(o.dId);
        var wId = BI.Utils.getWidgetIDByDimensionID(o.dId);
        var type = BI.Utils.getWidgetTypeByID(wId);
        var regionType = BI.Utils.getRegionTypeByDimensionID(o.dId);
        var numberLevel = BICst.TARGET_STYLE.NUM_LEVEL.NORMAL;
        this.magnify = 1;
        switch (type) {
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                if(regionType === BICst.REGION.TARGET1){
                    numberLevel = BI.Utils.getWSLeftYAxisNumLevelByID(wId);
                }
                if(regionType === BICst.REGION.TARGET2){
                    numberLevel = BI.Utils.getWSRightYAxisNumLevelByID(wId);
                }
                if(regionType === BICst.REGION.TARGET3){
                    numberLevel = BI.Utils.getWSRightYAxis2NumLevelByID(wId);
                }
                break;
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
                if(regionType === BICst.REGION.TARGET1 || regionType === BICst.REGION.TARGET2){
                    numberLevel = BI.Utils.getWSXAxisNumLevelByID(wId);
                }
                break;
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.BUBBLE:
                if(regionType === BICst.REGION.TARGET1){
                    numberLevel = BI.Utils.getWSLeftYAxisNumLevelByID(wId);
                }
                if(regionType === BICst.REGION.TARGET2){
                    numberLevel = BI.Utils.getWSXAxisNumLevelByID(wId);
                }
                break;
        }
        switch (numberLevel) {
            case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
            case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                this.magnify = 1;
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                this.magnify = 10000;
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                this.magnify = 1000000;
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                this.magnify = 100000000;
                break;
        }
        var items = [];
        BI.each(cordon, function(idx, cor){
            items.push({
                type: "bi.cordon_item",
                cordon_name: cor.cordon_name,
                cordon_value: BI.parseFloat(cor.cordon_value).div(self.magnify),
                cordon_number_level: numberLevel,
                cordon_color: cor.cordon_color,
                height: self.constants.itemHeight
            });
        });
        this.button_group.populate(items);
        this.buttons = this.button_group.getAllButtons();
    },

    getValue: function () {
        var self = this, group_nodes = [];
        BI.each(this.buttons, function (idx, item) {
            var value = item.getValue();
            group_nodes.push(BI.extend(value, {
                cordon_value: BI.parseFloat(value.cordon_value).mul(self.magnify)
            }));
        });
        return group_nodes;
    }

});
BI.CordonPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.cordon_pane", BI.CordonPane);