/**
 * Created by roy on 16/3/14.
 */
BI.DetailTablePathSettingSwitch = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePathSettingSwitch.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "detail-table-path-setting-switch",
            items: [{}, {}],
            showIndex: 0
        })
    },

    _init: function () {
        var self = this, o = this.options;
        BI.DetailTablePathSettingSwitch.superclass._init.apply(this, arguments);
        this._createItems();
        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: self.itemPanes
        });
        this._showPane();

    },

    _createItems: function () {
        var self = this, o = this.options;
        this.itemPanes = [];
        BI.each(o.items, function (i, item) {
            var itemPane = BI.createWidget(BI.extend({
                type: "bi.button",
                height: 30
            }, item));
            itemPane.setVisible(false);
            itemPane.on(BI.Button.EVENT_CHANGE, function () {
                o.showIndex++;
                if (o.showIndex === o.items.length) {
                    o.showIndex = 0;
                }
                self._showPane();
            });

            itemPane.on(BI.Controller.EVENT_CHANGE, function (type, value, obj) {
                self.fireEvent(BI.DetailTablePathSettingSwitch.EVENT_CHANGE, value);
            });
            self.itemPanes.push(itemPane);
        })
    },

    _showPane: function () {
        var self = this;
        var index = this.options.showIndex;
        BI.each(self.itemPanes, function (i, pane) {
            pane.setVisible(false);
        });
        self.itemPanes[index].setVisible(true);
    },

    getValue: function () {
        var self = this;
        return self.itemPanes[self.options.showIndex].getValue();
    },

    setValue: function (v) {
        var self = this;
        BI.find(self.itemPanes, function (i, pane) {
            if (v === pane.getValue()) {
                self.options.showIndex = i;
                return true
            }
        });
        this._showPane();
    }


});
BI.DetailTablePathSettingSwitch.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_table_path_setting_switch", BI.DetailTablePathSettingSwitch);