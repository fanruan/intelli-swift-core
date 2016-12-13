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
                BI.each(self.getValue(), function (i, dId) {
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
});
$.shortcut("bi.detail_region_header", BI.DetailRegionHeader);