/**
 * Created by Young's on 2016/6/1.
 */
BI.ReportHangoutPathChooser = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function () {
        return BI.extend(BI.ReportHangoutPathChooser.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-report-hangout-path-chooser"
        });
    },

    _init: function () {
        BI.ReportHangoutPathChooser.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Hangout_Now"),
            height: 50,
            textAlign: "left",
            hgap: 10
        })
    },

    rebuildCenter: function (center) {
        var self = this, o = this.options;
        this.pathCombo = BI.createWidget({
            type: "bi.multilayer_select_tree_combo",
            cls: "all-reports",
            width: 220,
            height: 30
        });
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: center,
            text: BI.i18nText("BI-Loading")
        });
        BI.requestAsync("fs_entry", "getfolder", {}, function (data) {
            var items = self._formatItems(data);
            self.pathCombo.populate(items);
            mask.destroy();
        });

        this.reportName = BI.createWidget({
            type: "bi.sign_editor",
            cls: "report-name",
            value: o.reportName,
            height: 30,
            width: 220,
            allowBlank: false
        });

        this.description = BI.createWidget({
            type: "bi.textarea_editor",
            cls: "report-description",
            width: 220,
            height: 100
        });

        BI.createWidget({
            type: "bi.vertical",
            element: center,
            cls: "bi-hangout-report",
            items: [{
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Path"),
                    height: 30,
                    width: 90,
                    textAlign: "left",
                    cls: "config-name"
                }, this.pathCombo]
            }, {
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Name_Title"),
                    height: 30,
                    width: 90,
                    textAlign: "left",
                    cls: "config-name"
                }, this.reportName]
            }, {
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Describe"),
                    height: 30,
                    width: 90,
                    textAlign: "left",
                    cls: "config-name"
                }, this.description]
            }],
            hgap: 10,
            vgap: 10
        })
    },

    _formatItems: function (data) {
        var self = this;
        var items = [];
        BI.each(data, function (i, item) {
            if (BI.isNotNull(item.ChildNodes)) {
                items = items.concat(self._formatItems(item.ChildNodes));
            }
            if (item.id !== "0-1") {
                items.push({
                    id: item.id,
                    pId: item.parentId,
                    text: item.text,
                    value: item.value,
                    open: true
                })
            }
        });
        return items;
    },

    rebuildSouth: function (south) {
        var self = this;
        var sure = BI.createWidget({
            type: 'bi.button',
            text: BI.i18nText("BI-Sure"),
            height: 30,
            handler: function (v) {
                self.end();
            }
        });
        var cancel = BI.createWidget({
            type: 'bi.button',
            text: BI.i18nText("BI-Cancel"),
            height: 30,
            level: 'ignore',
            handler: function (v) {
                self.close(v);
            }
        });
        BI.createWidget({
            type: 'bi.right_vertical_adapt',
            element: south,
            hgap: 5,
            items: [cancel, sure]
        });
    },

    getValue: function () {
        return {
            parentId: this.pathCombo.getValue()[0],
            text: this.reportName.getValue(),
            description: this.description.getValue()
        };
    },

    end: function () {
        if (BI.isNull(this.pathCombo.getValue()) || this.pathCombo.getValue().length === 0) {
            BI.Msg.toast(BI.i18nText("BI-Please_Select_Path"), "warning");
            return;
        }
        this.fireEvent(BI.ReportHangoutPathChooser.EVENT_SAVE);
        this.close();
    }

});
BI.ReportHangoutPathChooser.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.report_hangout_path_chooser", BI.ReportHangoutPathChooser);