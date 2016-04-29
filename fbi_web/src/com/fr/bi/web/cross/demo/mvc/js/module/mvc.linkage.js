LinkageView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(LinkageView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-linkage-view"
        })
    },

    _init: function () {
        LinkageView.superclass._init.apply(this, arguments);
    },

    _createTab: function (wId) {
        var name = this.getName();
        var layer = BI.Layers.make(name, this.$vessel);
        var linkage = BI.createWidget({
            type: "bi.linkage",
            wId: wId,
            element: layer
        });
        linkage.on(BI.Linkage.EVENT_CONFIRM, function () {
            BI.Layers.remove(name);
            var values = linkage.getValue();
            var widgets = Data.SharingPool.get("widgets");
            widgets[wId].linkages = values;
            Data.SharingPool.put("widgets", widgets);
        });
        linkage.populate();
        BI.Layers.show(name);
    },

    _render: function (vessel) {
        var self = this;
        var combo = BI.createWidget({
            type: "bi.single_select_combo",
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_NONE,
            width: 200,
            height: 30,
            text: "联动",
            items: BI.map(BI.Utils.getAllWidgetIDs(), function (i, wid) {
                return {
                    text: BI.Utils.getWidgetNameByID(wid),
                    value: wid
                }
            })
        });
        combo.on(BI.SingleSelectCombo.EVENT_CHANGE, function (val) {
            self._createTab(val);
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: combo,
                left: 50,
                top: 30
            }]
        })
    }
});

LinkageModel = BI.inherit(BI.Model, {});