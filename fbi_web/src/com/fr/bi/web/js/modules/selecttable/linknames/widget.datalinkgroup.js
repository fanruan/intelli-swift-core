/**
 * @class BI.DataLinkGroup
 * @extend BI.Widget
 * 数据连接名 组
 */
BI.DataLinkGroup = BI.inherit(BI.Widget, {

    constants: {
        TOP_GAP: 20,
        LEFT_GAP: 20,
        RIGHT_GAP: 20,
        NAV_TITLE_HEIGHT: 50,
        NAV_BUTTON_HEIGHT: 30,
        NAV_BUTTON_LEFT_GAP: 20,
        GROUPS_GAP: 10
    },

    _defaultConfig: function () {
        return BI.extend(BI.DataLinkGroup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-data-link-group",
            width: 280
        })
    },

    _init: function () {
        BI.DataLinkGroup.superclass._init.apply(this, arguments);
        var self = this, items = this.options.items;
        var dataLinks = items.dataLinks, packages = items.packages, etl = items.etl;
        var arr = dataLinks.concat(packages);
        arr = arr.concat(etl);
        this.group = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(arr, {
                type: "bi.text_button",
                cls: "nav-button",
                textAlign: "left",
                height: this.constants.NAV_BUTTON_HEIGHT,
                lgap: this.constants.LEFT_GAP
            })
        });
        this.group.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        var container = BI.createWidget({
            type: "bi.vertical",
            cls: "data-link-groups",
            items: [{
                type: "bi.left",
                cls: "nav-title",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Data_Connection"),
                    height: this.constants.NAV_TITLE_HEIGHT,
                    hgap: this.constants.GROUPS_GAP
                }]
            }],
            vgap: this.constants.GROUPS_GAP,
            hgap: this.constants.GROUPS_GAP
        });
        container.addItems(this.group.getAllButtons().slice(0, dataLinks.length));
        container.addItem(BI.createWidget({
            type: "bi.left",
            cls: "nav-title",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Package"),
                height: this.constants.NAV_TITLE_HEIGHT,
                hgap: this.constants.GROUPS_GAP
            }]
        }));
        container.addItems(this.group.getAllButtons().slice(dataLinks.length, dataLinks.length + packages.length));
        if (etl.length > 0) {
            container.addItem(BI.createWidget({
                type: "bi.left",
                cls: "nav-title",
                items: [{
                    type: "bi.label",
                    text: "ETL",
                    height: this.constants.NAV_TITLE_HEIGHT,
                    hgap: this.constants.GROUPS_GAP
                }]
            }));
            container.addItems(this.group.getAllButtons().slice(dataLinks.length + packages.length, dataLinks.length + packages.length + etl.length));
        }

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: container,
                top: this.constants.TOP_GAP,
                left: this.constants.LEFT_GAP,
                right: this.constants.RIGHT_GAP,
                bottom: 0
            }]
        })
    },

    getValue: function () {
        return this.group.getValue();
    },

    setValue: function (v) {
        this.group.setValue(v);
    }
});
BI.DataLinkGroup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.data_link_group", BI.DataLinkGroup);