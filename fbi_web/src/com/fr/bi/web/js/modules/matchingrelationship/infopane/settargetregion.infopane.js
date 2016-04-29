/**
 * 设置过关系的region
 *
 * @class BI.SetMatchingRelationTargetRegion
 * @extends BI.Widget
 */

BI.SetMatchingRelationTargetRegion = BI.inherit(BI.Widget, {

    constants: {
        titleGap: 10,
        buttonHeight: 30,
        buttonGap: 10,
        titleHeight: 50
    },

    _defaultConfig: function () {
        return BI.extend(BI.SetMatchingRelationTargetRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-set-match-relation-target-region",
            tableName: "",
            items: []
        });
    },

    _init: function () {
        BI.SetMatchingRelationTargetRegion.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.relationSettingButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Set_Relation"),
            height: this.constants.buttonHeight
        });
        this.relationSettingButton.setEnable(false);

        this.relationSettingButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.SetMatchingRelationTargetRegion.EVENT_SET_RELATION);
        });

        this.group = BI.createWidget({
            type: "bi.button_group",
            chooseType: BI.Selection.Multi,
            items: [],
            layouts: [{
                type: "bi.left",
                hgap: this.constants.buttonGap,
                vgap: this.constants.buttonGap
            }]
        });

        this.dimensionLabel = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            cls: "region-title-label",
            text: BI.i18nText("BI-Following_Targets_Correspond_To_Dimension_Field") + BI.Utils.getDimensionNameByID(o.dId),
            height: 25
        });

        this.group.on(BI.ButtonGroup.EVENT_CHANGE, function(){
            self._checkItemSelected();
        });

        this.group.on(BI.Controller.EVENT_CHANGE, function(){

        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                type: "bi.left_right_vertical_adapt",
                height: this.constants.titleHeight,
                cls: "target-region-title",
                lhgap: this.constants.titleGap,
                rhgap: this.constants.titleGap,
                items: {
                    left: [{
                        type: "bi.vertical",
                        items: [{
                            type: "bi.label",
                            textAlign: "left",
                            cls: "region-title-label",
                            text: o.tableName,
                            title: o.tableName,
                            height: 25
                        }, this.dimensionLabel]
                    }],
                    right: [this.relationSettingButton]
                }
            }, this.group]
        })
    },

    _checkItemSelected: function(){
        this.relationSettingButton.setEnable(!BI.isEmpty(this.group.getValue()));
    },

    _checkEmpty: function(){
        BI.size(this.group.getAllButtons()) === 0 && this.destroy();
    },

    _createItemsByItems: function(items){
        return BI.createItems(items, {
            type: "bi.text_button",
            cls: "target-region-button",
            hgap: 5,
            height: this.constants.buttonHeight
        });
    },

    removeItemsByValue: function(v){
        this.group.removeItems(v);
        this._checkEmpty();
        this._checkItemSelected()
    },

    populate: function(items){
        var o = this.options;
        o.items = items;
        this.group.populate(this._createItemsByItems(items));
    },

    addItems: function(items){
        this.group.addItems(this._createItemsByItems(items));
    },

    getValue: function(){
        return this.group.getValue();
    },

    destroy: function(){
        BI.SetMatchingRelationTargetRegion.superclass.destroy.apply(this, arguments);
        this.fireEvent(BI.SetMatchingRelationTargetRegion.EVENT_DESTROY);
    }
});
BI.SetMatchingRelationTargetRegion.EVENT_DESTROY = "SetMatchingRelationTargetRegion.EVENT_DESTROY";
BI.SetMatchingRelationTargetRegion.EVENT_SET_RELATION = "SetMatchingRelationTargetRegion.EVENT_SET_RELATION";
$.shortcut('bi.set_matching_relation_target_region', BI.SetMatchingRelationTargetRegion);