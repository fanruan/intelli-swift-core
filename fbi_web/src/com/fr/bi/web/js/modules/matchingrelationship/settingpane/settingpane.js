/**
 * 设置过关系的region
 *
 * @class BI.SetRelationPane
 * @extends BI.Widget
 */

BI.SetRelationPane = BI.inherit(BI.Widget, {

    constants: {
        titleGap: 10,
        buttonHeight: 30,
        buttonGap: 10,
        titleHeight: 50,
        labelWidth: 45,
        labelHeight: 45,
        gap: 30
    },

    _defaultConfig: function () {
        return BI.extend(BI.SetRelationPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-set-relation-pane",
            tableName: "",
            targetIds: [],
            dimensionId: ""
        });
    },

    _init: function () {
        BI.SetRelationPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.targetLine = BI.createWidget({
            type: "bi.target_label_control"
        });

        this.dimensiontreeCombo = BI.createWidget({
            type: "bi.dimension_tree_combo",
            dId: o.dimensionId
        });

        this.dimensiontreeCombo.on(BI.DimensionTreeCombo.EVENT_CHANGE, function(){
            var fieldId = this.getValue()[0];
            self._checkDimensionAndTargetRelation(BI.Utils.getTableIdByFieldID(fieldId));
            self.pathChooser.populate({
                dimensionFieldId: fieldId,
                targetIds: o.targetIds
            });
        });

        this.pathChooser = BI.createWidget({
            type: "bi.multi_path_chooser",
            height: 200
        });

        this.emptyItem = BI.createWidget({
            type: "bi.default",
            items: [],
            height: 30
        });

        BI.createWidget({
            type: "bi.vtape",
            hgap: this.constants.titleGap,
            element: this.element,
            items: [{
                el: this.targetLine,
                height: 30
            }, {
                el: {
                    type: "bi.htape",
                    items: [{
                        el: {
                            type: "bi.label",
                            textAlign: "left",
                            text: BI.i18nText("BI-Dimension"),
                            height: this.constants.buttonHeight,
                            cls: "setting-tip-label"
                        },
                        width: this.constants.labelWidth
                    }, this.dimensiontreeCombo]
                },
                height: 30
            }, {
                el: this.emptyItem,
                height: 30
            }, {
                el: {
                    type: "bi.left",
                    items: [{
                        type: "bi.label",
                        cls: "setting-tip-label",
                        text: BI.i18nText("BI-Please_Select_Path_Between_Target_And_Dimension"),
                        height: this.constants.labelHeight,
                        rgap: 15
                    }, {
                        type: "bi.icon_button",
                        height: this.constants.labelHeight,
                        cls: "path-set-doubt"
                    }]
                },
                height: 45
            }, {
                type: "bi.vertical",
                cls: "select-path-region",
                scrolly: false,
                scrollable: true,
                vgap: 10,
                items: [this.pathChooser]
            }]
        });
    },

    _checkDimensionAndTargetRelation: function (tId) {
        var o = this.options;
        var tIds = BI.Utils.getForeignRelationTablesByTableID(o.targetIds[0]);
        var contains = BI.contains(tIds, tId);
        if(!!contains){
            if(!this.selectCombineTableCombo){
                this.selectCombineTableCombo = BI.createWidget({
                    type: "bi.text_icon_combo",
                    height: 30
                });
                this.emptyItem.addItem(this.selectCombineTableCombo);
            }else{
                var tables = BI.Utils.getFirstCommonPrimaryTablesByTableIDs([tId, BI.Utils.getTableIDByDimensionID(o.targetIds[0])]);
                var items = BI.map(tables, function(idx, tId){
                    return {
                        text: BI.Utils.getTableNameByID(tId),
                        value: tId
                    };
                });
                this.selectCombineTableCombo.populate(items);
            }
        }
        this.emptyItem && this.emptyItem.setVisible(contains);
    },

    populate: function(items){
        var  o = this.options;
        o.targetIds = items;
        this.targetLine.populate(o.targetIds);
        this.dimensiontreeCombo.populate(o.targetIds);
        this.pathChooser.populate({
            dimensionFieldId: BI.Utils.getFieldIDByDimensionID(o.dimensionId),
            targetIds: o.targetIds
        });
        this._checkDimensionAndTargetRelation(BI.Utils.getTableIDByDimensionID(o.targetIds[0]));
    },

    setValue: function(v){
        var o = this.options;
        if(BI.isEmpty(v)){
            return;
        }
        this.dimensiontreeCombo.setValue(v._src.field_id);
        this.pathChooser.populate({
            dimensionFieldId: v._src.field_id,
            targetIds: o.targetIds
        });
        this.pathChooser.setValue(v.target_relation);
    },

    getValue: function(){
        return {
            _src: {
                field_id: this.dimensiontreeCombo.getValue()[0]
            },
            target_relation: this.pathChooser.getValue()
        }
    }
});
BI.SetRelationPane.EVENT_DESTROY = "SetRelationPane.EVENT_DESTROY";
BI.SetRelationPane.EVENT_SET_RELATION = "SetRelationPane.EVENT_SET_RELATION";
$.shortcut('bi.set_relation_pane', BI.SetRelationPane);