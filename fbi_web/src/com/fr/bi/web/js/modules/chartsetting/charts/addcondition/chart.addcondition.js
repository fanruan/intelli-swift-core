/**
 * Created by GameJian on 2016/7/1.
 */
BI.ChartAddCondition = BI.inherit (BI.Widget, {

    constant: {
        BUTTON_HEIGHT: 30,
        RADIO_WIDTH: 100,
        SINGLE_LINE_HEIGHT: 60,
        SINGLE_H_GAP: 10,
        SINGLE_V_GAP: 5,
        SINGLE_L_GAP: 10
    },

    _defaultConfig: function () {
        return BI.extend(BI.ChartAddCondition.superclass._defaultConfig.apply(this , arguments) , {
            baseCls: "bi-chart-add-condition"
        })
    },

    _init: function () {
        BI.ChartAddCondition.superclass._init.apply(this , arguments);
        var self = this, o = this.options;

        //刻度设置
        this.scale = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.CHART_SCALE_SETTING, {
                type: "bi.single_select_radio_item",
                width: this.constant.RADIO_WIDTH ,
                height: this.constant.BUTTON_HEIGHT
            }),
            layouts: [{
                type: "bi.horizontal_adapt",
                height: this.constant.BUTTON_HEIGHT
            }]
        });

        this.scale.setValue(BICst.SCALE_SETTING.AUTO);

        this.scale.on(BI.ButtonGroup.EVENT_CHANGE, function(v) {
            switch(v) {
                case BICst.SCALE_SETTING.AUTO:
                    self._setConditionVisible(false);
                    break;
                case BICst.SCALE_SETTING.CUSTOM:
                    self._setConditionVisible(true);
                    break;
            }
        });

        //添加条件button
        this.addConditionButton = BI.createWidget({
            type: "bi.button",
            text: "+" + BI.i18nText("BI-Add_Condition"),
            height: this.constant.BUTTON_HEIGHT,
            handler: function () {
                self._addNewCondition();
            }
        });

        //添加条件选择框
        this.conditions = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems([{
                range: {
                    min: 0,
                    max: 100,
                    closemin: false,
                    clasemax: false
                },
                color: "#09ABE9",
                cid: BI.UUID()
            } , {
                range: {
                    min: 100,
                    max: 200,
                    closemin: true,
                    clasemax: false
                },
                color: "#09ABE9",
                cid: BI.UUID()
            } , {
                range: {
                    min: 200,
                    max: 300,
                    closemin: true,
                    clasemax: false
                },
                color: "#09ABE9",
                cid: BI.UUID()
            }], {
                type: "bi.target_style_condition_item",
                onRemoveCondition: function(id) {
                    self._removeCondition(id)
                }
            }),
            layouts: [{
                type: "bi.vertical",
                hgap: this.constant.SINGLE_H_GAP,
                vgap: this.constant.SINGLE_V_GAP
            }]
        });

        self._setConditionVisible(false);

        var combo = BI.createWidget({
            type: "bi.left",
            items:  BI.createItems([{
                type: "bi.center_adapt",
                items: [this.scale]
            } , {
                type: "bi.center_adapt",
                items: [this.addConditionButton]
            } , {
                type: "bi.center_adapt",
                height:"",
                items: [this.conditions]
            }] , {
                height: this.constant.SINGLE_LINE_HEIGHT
            }),
            lgap: this.constant.SINGLE_L_GAP
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [combo],
            hgap: this.constant.SINGLE_H_GAP
        })
    },

    _setConditionVisible: function (v) {
        var self = this;
        self.addConditionButton.setVisible(v);
        self.conditions.setVisible(v);
    },

    _addNewCondition : function () {
        var self = this;
        var allConditions = this.conditions.getAllButtons();

        var nextMin = 0;
        if( allConditions.length > 0) {
            nextMin = BI.parseInt(allConditions[allConditions.length - 1].getValue().range.max) || 0;
        }

        this.conditions.addItems([{
            type: "bi.target_style_condition_item",
            range: {
                min: nextMin,
                max: nextMin + 100,
                closemin: false,
                clasemax: false
            },
            color: "#09ABE9",
            cid: BI.UUID(),
            onRemoveCondition: function () {
                self._removeCondition()
            }
        }]);

        if(this.conditions.length !==1) {
            self.conditions[self.conditions.length - 1].setSmallIntervalEnable();
        }
    },

    _removeCondition : function (id) {
        var allConditions = this.conditions.getAllButtons();
        var index = 0;

        BI.some(allConditions , function ( i , con ) {
            if(con.getValue().cid === id) {
                index = i;
                return true;
            }
        });

        this.conditions.removeItemAt(index)
    }

});
BI.ChartAddCondition.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_add_condition" , BI.ChartAddCondition);