Copy2GroupComboView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(Copy2GroupComboView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-copy2group-combo bi-mvc-layout"
        })
    },

    _init: function () {
        Copy2GroupComboView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;
        var items = [{
            text: "分组1", value: "分组1", title: "分组1", py: "fz1"
        }, {
            text: "分组2", value: "分组2", title: "分组2", py: "fz2"
        }, {
            text: "分组3", value: "分组3", title: "分组3", py: "fz3"
        }, {
            text: "分组4", value: "分组4", title: "分组4", py: "fz4"
        }, {
            text: "分组5", value: "分组5", title: "分组5", py: "fz5"
        }, {
            text: "分组6", value: "分组6", title: "分组6", py: "fz6"
        }, {
            text: "分组7", value: "分组7", title: "分组7", py: "fz7"
        }, {
            text: "分组8", value: "分组8", title: "分组8", py: "fz8"
        }];
        var combo = BI.createWidget({
            type: "bi.copy2group_combo"
        });

        combo.on(BI.Copy2GroupCombo.EVENT_BEFORE_POPUPVIEW, function () {
            combo.populate(items);
        });
        combo.on(BI.Copy2GroupCombo.EVENT_CONFIRM, function () {
            BI.Msg.toast(JSON.stringify(combo.getValue()));
        });
        combo.on(BI.Copy2GroupCombo.EVENT_CLICK_BUTTON, function () {
            BI.each(items, function (i, item) {
                item.selected = false;
            })
            items.push({
                selected: true,
                text: combo.getTargetValue(),
                value: combo.getTargetValue(),
                py: combo.getTargetValue()
            });
            combo.populate(items);
            combo.setValue(["分组8"]);
            combo.scrollToBottom();
        });


        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 30,
            vgap: 20,
            items: [{
                type: "bi.copy2group_add_button"
            }, {
                type: "bi.copy2group_bar"
            }, combo]
        });
    }
});

Copy2GroupComboModel = BI.inherit(BI.Model, {});