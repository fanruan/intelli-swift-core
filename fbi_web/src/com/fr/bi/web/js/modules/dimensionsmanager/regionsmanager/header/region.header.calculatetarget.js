/**
 * Created by GUY on 2016/3/17.
 * @class BI.CalculateTargetRegionHeader
 * @extends BI.RegionHeader
 */
BI.CalculateTargetRegionHeader = BI.inherit(BI.RegionHeader, {

    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetRegionHeader.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-calculate-target-region-header",
            titleName: "",
            wId: "",
            viewType: BICst.REGION.TARGET1
        });
    },

    _init: function () {
        BI.CalculateTargetRegionHeader.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.calculateAddButton = BI.createWidget({
            type: "bi.text_button",
            height: 25,
            disabled: BI.isEmptyArray(BI.Utils.getAllTargetDimensionIDs(this.options.wId)),
            warningTitle: BI.i18nText("BI-There_Is_No_Target_for_Contruct_Calculate_Target"),
            value: BI.i18nText("BI-Add_Cal_Target")
        });

        this.calculateAddButton.on(BI.TextButton.EVENT_CHANGE, function () {
            var dId = BI.UUID();
            self.calpopup = BI.createWidget({
                type: "bi.calculate_target_popup_summary",
                wId: o.wId,
                targetId: dId
            });
            self.calpopup.on(BI.CalculateTargetPopupSummary.EVENT_CHANGE, function () {
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

                    }],
                    height: 25
                },
                top: 0,
                left: 0,
                right: 0
            }]
        })
    },

    _setCalculateTarget: function (dId, options) {
        var o = this.options;
        o.dimensionCreator(dId, this.options.viewType, options);
    },
});
$.shortcut("bi.calculate_target_region_header", BI.CalculateTargetRegionHeader);