/**
 * Created by GUY on 2016/3/17.
 * @class BI.DetailRegionHeader
 * @extends BI.RegionHeader
 */
BI.DetailRegionHeader = BI.inherit(BI.RegionHeader, {

    _defaultConfig: function () {
        return BI.extend(BI.DetailRegionHeader.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-detail-region-header",
            titleName: "",
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        });
    },

    _init: function () {
        BI.DetailRegionHeader.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.relationButton = BI.createWidget({
            type: "bi.text_button",
            textHeight: 30,
            value: BI.i18nText("BI-Field_Relation_Setting")
        });

        this.relationButton.on(BI.TextButton.EVENT_CHANGE, function () {
            self.popup = BI.createWidget({
                type: "bi.field_relation_setting_popup",
                wId: o.wId
            });
            self.popup.on(BI.FieldRelationSettingPopup.EVENT_CHANGE, function () {
                BI.each(BI.Utils.getAllDimensionIDs(o.wId), function (i, dId) {
                    self._setDimensionRelation(dId, {
                        relationItem: self.popup.getValue()
                    })
                });
            });
            BI.Popovers.remove("relation_set");
            BI.Popovers.create("relation_set", self.popup).open("relation_set");
        });

        this.calculateAddButton = BI.createWidget({
            type: "bi.text_button",
            textHeight: 30,
            value: BI.i18nText("BI-Add_Cal_Target")
        });

        this.calculateAddButton.on(BI.TextButton.EVENT_CHANGE, function () {
            var dId = BI.UUID();
            self.calpopup = BI.createWidget({
                type: "bi.calculate_target_popup_detail",
                wId: o.wId,
                targetId: dId
            });
            self.calpopup.on(BI.CalculateTargetPopupDetail.EVENT_CHANGE, function () {
                var targetValue = self.calpopup.getValue();
                self._setCalculateTarget(targetValue.dId, targetValue.data)

            });
            BI.Popovers.remove(dId + "calculate_set");
            BI.Popovers.create(dId + "calculate_set", self.calpopup).open(dId + "calculate_set");
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.right",
                    hgap: 10,
                    items: [{
                        el: this.calculateAddButton

                    }, {
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

    _setCalculateTarget: function (dId, options) {
        var o = this.options;
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

        if(!checkAddCalcTargetValid()){
            this.calculateAddButton.setEnable(false);
            this.calculateAddButton.setTitle(BI.i18nText("BI-There_Is_No_Target_for_Contruct_Calculate_Number_Target"));
        }else{
            this.calculateAddButton.setEnable(true);
            this.calculateAddButton.setTitle("");
        }

        //动画，先保留
        //if(hasMultiRelation()){
        //    this.relationButton.element.addClass("animated infinite pulse");
        //}else{
        //    this.relationButton.element.removeClass("animated infinite pulse");
        //}

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

        function checkAddCalcTargetValid(){
            var dimsAndTars = BI.Utils.getAllDimensionIDs(o.wId);
            return BI.isNotNull(BI.find(dimsAndTars, function(idx, dId){
                var targetType = BI.Utils.getDimensionTypeByID(dId);
                return targetType === BICst.TARGET_TYPE.NUMBER || targetType === BICst.TARGET_TYPE.FORMULA
            }));
        }

        function hasMultiRelation(){
            var tableIds = BI.map(BI.Utils.getAllDimDimensionIDs(o.wId), function(idx, dId){
                return BI.Utils.getTableIDByDimensionID(dId);
            });
            var commonTableIds = BI.Utils.getCommonForeignTablesByTableIDs(tableIds);
            if(commonTableIds.length > 1){
                return true;
            }else{
                return BI.isNotNull(BI.find(tableIds, function(idx, primaryTid){
                    return BI.find(commonTableIds, function(idx, foreignTid){
                        return BI.Utils.getPathsFromTableAToTableB(primaryTid, foreignTid).length > 1;
                    })
                }));
            }
        }
    }
});
$.shortcut("bi.detail_region_header", BI.DetailRegionHeader);