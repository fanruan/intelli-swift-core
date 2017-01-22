/**
 * @class BI.ETLTablePreview
 * @extend BI.BarPopoverSection
 * etl表数据预览
 */
BI.ETLTablePreview = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function () {
        return BI.extend(BI.ETLTablePreview.superclass._defaultConfig.apply(this, arguments), {});
    },

    _init: function () {
        BI.ETLTablePreview.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: this.options.name,
            textAlign: "left",
            height: 50
        })
    },

    rebuildCenter: function (center) {
        var table = this.options.table;
        BI.createWidget({
            type: "bi.etl_table_preview_center",
            table: table,
            element: center
        });
    },

    rebuildSouth: function (south) {
        var self = this;
        BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "bi-etl-table-preview-south",
            element: south,
            items: {
                left: [{
                    type: "bi.label",
                    cls: "preview-comment",
                    text: BI.i18nText("BI-Table_Data_Preview_Comment"),
                    height: 30
                }],
                right: [{
                    type: "bi.button",
                    level: "ignore",
                    text: BI.i18nText("BI-Close"),
                    height: 30,
                    handler: function () {
                        self.close();
                    }
                }]
            }

        })
    }

});
$.shortcut("bi.etl_table_preview", BI.ETLTablePreview);