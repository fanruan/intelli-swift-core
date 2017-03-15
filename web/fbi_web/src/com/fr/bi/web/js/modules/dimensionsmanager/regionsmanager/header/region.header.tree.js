/**
 * Created by GUY on 2016/3/17.
 * @class BI.TreeRegionHeader
 * @extends BI.RegionHeader
 */
BI.TreeRegionHeader = BI.inherit(BI.RegionHeader, {

    _defaultConfig: function () {
        return BI.extend(BI.TreeRegionHeader.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-tree-region-header",
            titleName: "",
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        });
    },

    _init: function () {
        BI.TreeRegionHeader.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.relationButton = BI.createWidget({
            type: "bi.text_button",
            textHeight: 30,
            value: BI.i18nText("BI-Field_Relation_Setting")
        });

        this.relationButton.on(BI.TextButton.EVENT_CHANGE, function () {
            self.popup = BI.createWidget({
                type: "bi.field_relation_setting_with_preview_popup",
                wId: o.wId
            });
            self.popup.on(BI.FieldRelationSettingWithPreviewPopup.EVENT_CHANGE, function () {
                BI.each(BI.Utils.getAllDimensionIDs(o.wId), function (i, dId) {
                    self._setDimensionRelation(dId, {
                        relationItem: self.popup.getValue()
                    })
                });


            });
            BI.Popovers.remove("relation_set");
            BI.Popovers.create("relation_set", self.popup).open("relation_set");
        });


        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.right",
                    hgap: 10,
                    items: [{
                        el: this.relationButton
                    }],
                    height: 25
                },
                top: 0,
                left: 0,
                right: 0
            }]
        })
    },

    _setDimensionRelation: function (dId, options) {
        var self = this, o = this.options;
        o.dimensionCreator(dId, o.viewType, options);
    },

    populate: function(){
        var o = this.options;
        if(!checkRelationValid()){
            this.relationButton.setEnable(false);
            this.relationButton.setTitle(BI.i18nText("BI-Fields_Relation_Only"));
        }else{
            this.relationButton.setEnable(true);
            this.relationButton.setTitle("");
        }

        function checkRelationValid(){
            var tableIds = BI.map(BI.Utils.getAllDimDimensionIDs(o.wId), function(idx, dId){
                return BI.Utils.getTableIDByDimensionID(dId);
            });
            var commonTableIds = BI.Utils.getCommonForeignTablesByTableIDs(tableIds);
            if(commonTableIds.length < 2){
                return BI.isNotNull(BI.find(tableIds, function(idx, primaryTid){
                    return BI.find(commonTableIds, function(idx, foreignTid){
                        return BI.Utils.getPathsFromTableAToTableB(primaryTid, foreignTid).length > 1;
                    })
                }));
            }
            return true;
        }
    }
});
$.shortcut("bi.tree_region_header", BI.TreeRegionHeader);