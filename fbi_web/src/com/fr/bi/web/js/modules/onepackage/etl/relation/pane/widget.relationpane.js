/**
 * @class BI.RelationPane
 * @extend BI.Widget
 * 关联关系面板
 */
BI.RelationPane = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.RelationPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-relation-pane"
        })
    },

    _init: function(){
        BI.RelationPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.RelationPaneModel({
            field_id: o.field_id,
            relations: o.relations,
            translations: o.translations,
            all_fields: o.all_fields
        });
        this._createRelationTree();
        var addRelationTable = BI.createWidget({
            type: "bi.button",
            text: "+" + BI.i18nText("BI-Linked_To_Other"),
            height: 30
        });
        addRelationTable.on(BI.Button.EVENT_CHANGE, function(){
            self._createSelectDataMask();
        });
        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [this.relationTree, {
                type: "bi.right",
                items: [addRelationTable],
                hgap: 10
            }]
        });
    },

    _drawSVGLine: function(){
        var treeValue = this.relationTree.getValue();
        if(treeValue.length === 0 || treeValue[0] === ""){
            return;
        }
        var svg = BI.createWidget({
            type: "bi.svg"
        });
        svg.element.css({"z-index": -1});
        var branchLength = treeValue.length;
        svg.path("M160," + branchLength*105/2 + "L180," + branchLength*105/2)
            .attr({stroke: "gray"});
        svg.path("M180," + branchLength*105/2 + "L180," + 105/2 +
            "M180," + branchLength*105/2 + "L" + "180," + 105*(2*branchLength - 1)/2)
            .attr({stroke: "gray"});
        var path = "";
        BI.each(treeValue, function(i, v){
            path = path + "M180," + (2*i + 1) * 105/2 + "L200," + (2*i + 1)*105/2 +
                    "M300," + (2*i + 1)*105/2 + "L340," + (2*i + 1)*105/2;
        });
        svg.path(path).attr({stroke: "gray"});
        BI.createWidget({
            type: "bi.absolute",
            element: this.relationTree,
            items: [{
                el: svg,
                top: 0,
                left: 0,
                right: 0,
                bottom: 0
            }]
        })
    },

    _createSelectDataMask: function(fieldId){
        var self = this, maskId = BI.UUID();
        var mask = BI.Maskers.make(maskId, BICst.BODY_ELEMENT);
        BI.Maskers.show(maskId);
        var selectDataMask = BI.createWidget({
            type: "bi.select_data_with_mask",
            element: mask,
            model: this.model,
            field_id: fieldId
        });
        selectDataMask.on(BI.SelectDataWithMask.EVENT_VALUE_CANCEL, function(){
            BI.Maskers.remove(maskId);
        });
        selectDataMask.on(BI.SelectDataWithMask.EVENT_CHANGE, function(v){
            selectDataMask.destroy();
            BI.Maskers.remove(maskId);
            var treeValue = self.relationTree.getValue();
            BI.isEmptyString(treeValue[0]) && (treeValue = []);
            if(BI.isNotNull(fieldId)) {
                BI.remove(treeValue, function(index, item){
                    return item.fieldId === fieldId;
                });
            }
            treeValue.push({
                fieldId: v.field_id,
                relationType: self.model.getRelationType(fieldId),
                translations: self.model.getTranslations()
            });
            self._refreshTree(treeValue);
        });
    },

    _createRelationTree: function(){
        var self = this;
        var relationIds = this.model.getRelationIds();
        var relationChildren = [];
        BI.each(relationIds, function(i, rId){
            relationChildren.push({
                fieldId: rId,
                relationType: self.model.getRelationType(rId),
                model: self.model
            });
        });
        this.relationTree = BI.createWidget({
            type: "bi.branch_tree",
            items: this._createBranchItems(relationChildren)
        });
        this._drawSVGLine();
        this.relationTree.on(BI.Controller.EVENT_CHANGE, function(type, clickType, fieldId){
            switch (clickType){
                case BI.RelationSettingTable.CLICK_GROUP:
                    break;
                case BI.RelationSettingTable.CLICK_TABLE:
                    self._createSelectDataMask(fieldId);
                    break;
                case BI.RelationSettingTable.CLICK_REMOVE:
                    var treeValue = self.relationTree.getValue();
                    self._refreshTree(treeValue);
                    break;
            }
        });
        this.model.setOldRelationValue(this.relationTree.getValue());
    },

    _createBranchItems: function(relationChildren){
        return [{
            el: {
                type: "bi.float_center_adapt",
                items: [{
                    type: "bi.relation_table_field_button",
                    table_name: this.model.getTableNameByFieldId(this.model.getFieldId()),
                    field_name: this.model.getFieldNameByFieldId(this.model.getFieldId()),
                    field_id: this.model.getFieldId()
                }],
                width: 180
            },
            children: BI.createItems(relationChildren, {
                type: "bi.relation_setting_table",
                model: this.model
            })
        }];
    },

    _refreshTree: function(relationChildren){
        if(BI.isNotEmptyArray(relationChildren)){
            var empty = true;
            BI.each(relationChildren, function(i, v){
                BI.isNotNull(v.relationType) && (empty = false);
            });
            //TODO 确定按钮状态
        }
        this.relationTree.populate(this._createBranchItems(relationChildren));
        this._drawSVGLine();
        this.model.setRelations(this.getValue());
    },

    getValue: function(){
        return this.model.getParsedRelation(this.relationTree.getValue());
    }
});
BI.RelationPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.relation_pane", BI.RelationPane);