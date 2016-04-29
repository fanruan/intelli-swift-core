/**
 * 未设置匹配关系的region
 *
 * @class BI.UnSettingMatchingRelationTargetRegion
 * @extends BI.Widget
 */

BI.UnSettingMatchingRelationTargetRegion = BI.inherit(BI.Widget, {

    constants: {
        titleGap: 10,
        buttonHeight: 30,
        buttonGap: 10,
        titleHeight: 50
    },

    _defaultConfig: function () {
        return BI.extend(BI.UnSettingMatchingRelationTargetRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-unsetting-match-relation-target-region",
            tableName: "",
            items: []
        });
    },

    _init: function () {
        BI.UnSettingMatchingRelationTargetRegion.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.relationSettingButton = BI.createWidget({
            type: "bi.button",
            readonly: false,
            height: this.constants.buttonHeight
        });

        this.relationSettingButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.UnSettingMatchingRelationTargetRegion.EVENT_SET_RELATION);
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

        this.group.on(BI.ButtonGroup.EVENT_CHANGE, function(){
            self._checkItemSelected();
        });

        this.group.on(BI.Controller.EVENT_CHANGE, function(){
            self._checkItemSelected();
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
                            title: o.tableName,
                            text: o.tableName,
                            height: 25
                        }, {
                            type: "bi.label",
                            textAlign: "left",
                            cls: "region-title-label",
                            text: BI.i18nText("BI-Dimension_Not_Have_Definite_Relationship_With_Following_Targets"),
                            height: 25
                        }]
                    }],
                    right: [this.relationSettingButton]
                }
            }, this.group]
        })
    },

    _checkItemSelected: function(){
        var count = BI.size(this.group.getValue());
        this.relationSettingButton.setValue(BI.i18nText("BI-Build_Relation") + (count === 0 ? "" : "(" + count + ")"));
        this.relationSettingButton.setEnable(count !== 0);
    },

    _checkEmpty: function(){
        BI.size(this.group.getAllButtons()) === 0 && this.destroy();
    },

    _createItemsByItems: function(items){
        return BI.createItems(items, {
            type: "bi.text_button",
            cls: "target-region-button",
            selected: true,
            hgap: 5,
            height: this.constants.buttonHeight
        });
    },

    populate: function(items){
        this.options.items = items;
        this.group.populate(this._createItemsByItems(items));
        this._checkItemSelected();
        this._checkEmpty();
    },

    addItems: function(items){
        this.group.addItems(this._createItemsByItems(items));
        this._checkItemSelected();
    },

    removeItemsByValue: function(v){
        this.group.removeItems(v);
        this._checkEmpty();
        this._checkItemSelected()
    },

    getValue: function(){
        return this.group.getValue();
    },

    destroy: function(){
        BI.UnSettingMatchingRelationTargetRegion.superclass.destroy.apply(this, arguments);
        this.fireEvent(BI.UnSettingMatchingRelationTargetRegion.EVENT_DESTROY);
    }
});
BI.UnSettingMatchingRelationTargetRegion.EVENT_DESTROY = "UnSettingMatchingRelationTargetRegion.EVENT_DESTROY";
BI.UnSettingMatchingRelationTargetRegion.EVENT_SET_RELATION = "UnSettingMatchingRelationTargetRegion.EVENT_SET_RELATION";
$.shortcut('bi.unsetting_matching_relation_target_region', BI.UnSettingMatchingRelationTargetRegion);