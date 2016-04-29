/**
 * 一个区域
 *
 * Created by GUY on 2016/3/17.
 * @class BI.TargetRegion
 * @extends BI.AbstractRegion
 */
BI.TargetRegion = BI.inherit(BI.AbstractRegion, {

    _defaultConfig: function () {
        var conf = BI.TargetRegion.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            extraCls: 'bi-target-region',
            regionType: BICst.REGION.TARGET1
        })
    },

    _init: function () {
        BI.TargetRegion.superclass._init.apply(this, arguments);
        var o = this.options, self = this;
        this.calculateAddButton = BI.createWidget({
            type: "bi.text_button",
            textHeight: 30,
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

    _getRegionConnect: function () {
        return "targets-container";
    },

    _createDimension: function (dId, options) {
        var self = this, o = this.options;
        options || (options = {});
        var dim = o.dimensionCreator(dId, this.options.regionType, options);
        var container = BI.createWidget({
            type: "bi.absolute",
            cls: "target-container",
            data: {
                dId: dId
            },
            height: 25,
            items: [{
                el: dim,
                left: 0,
                top: 0,
                right: 0,
                bottom: 0
            }]
        });
        return container;
    },

    _setCalculateTarget: function (dId, options) {
        var o = this.options;
        var dim = o.dimensionCreator(dId, this.options.regionType, options);
        var container = BI.createWidget({
            type: "bi.absolute",
            cls: "target-container",
            data: {
                dId: dId
            },
            height: 25,
            items: [{
                el: dim,
                left: 0,
                top: 0,
                right: 0,
                bottom: 0
            }]
        });
        this.store[dId] = container;
        this.center.addItem(this.store[dId]);
    },

    getValue: function () {
        var result = [];
        var dimensions = $(".target-container", this.center.element);
        BI.each(dimensions, function (i, dom) {
            result.push($(dom).data("dId"));
        });
        return result;
    }
});
$.shortcut("bi.target_region", BI.TargetRegion);