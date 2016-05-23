/**
 * Created by GUY on 2015/9/6.
 * @class BI.DetailSelectDataLevel1Item
 * @extends BI.Single
 */
BI.DetailSelectDataLevel1Item = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailSelectDataLevel1Item.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-detail-select-data-level1-item bi-select-data-level1-item",
            height: 25,
            fieldType: BICst.COLUMN.STRING,
            hgap: 0,
            lgap: 0,
            rgap: 35
        })
    },

    _getFieldClass: function (type) {
        switch (type) {
            case BICst.COLUMN.STRING:
                return "select-data-field-string-font";
            case BICst.COLUMN.NUMBER:
                return "select-data-field-number-font";
            case BICst.COLUMN.DATE:
                return "select-data-field-date-font";
            case BICst.COLUMN.COUNTER:
                return "select-data-field-number-font";
        }
    },

    _init: function () {
        BI.DetailSelectDataLevel1Item.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.button = BI.createWidget({
            type: "bi.blank_icon_text_item",
            trigger: "mousedown",
            cls: "select-date-level1-item-button " + this._getFieldClass(o.fieldType),
            text: o.text,
            value: o.value,
            blankWidth: 35,
            height: 25,
            textLgap: 10,
            textRgap: 5
        });
        this.button.on(BI.Controller.EVENT_CHANGE, function (type) {
            if (type === BI.Events.CLICK) {
                self.setSelected(self.isSelected());
            }
            self.fireEvent(BI.Controller.EVENT_CHANGE, BI.Events.CLICK, self.getValue(), self);
        });
        this.button.element.draggable(o.drag);

        this.previewBtn = BI.createWidget({
            type: "bi.text_button",
            text: BI.i18nText("BI-Preview"),
            title: BI.i18nText("BI-Preview")
        });
        this.previewBtn.doHighLight();
        this.previewBtn.on(BI.TextButton.EVENT_CHANGE, function () {
            BI.Popovers.create(self.getName(), BI.createWidget({
                type: "bi.detail_select_data_preview_section",
                text: o.text,
                value: o.value
            })).open(self.getName());
        });

        this.topLine = BI.createWidget({
            type: "bi.layout",
            height: 0,
            cls: "select-data-top-line"
        });
        this.bottomLine = BI.createWidget({
            type: "bi.layout",
            height: 0,
            cls: "select-data-bottom-line"
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.topLine,
                top: 0,
                left: o.lgap,
                right: o.rgap
            }, {
                el: this.bottomLine,
                bottom: 0,
                left: o.lgap,
                right: o.rgap
            }, {
                el: this.button,
                top: 0,
                left: o.lgap,
                right: o.rgap
            }, {
                el: {
                    type: "bi.center_adapt",
                    items: [this.previewBtn]
                },
                top: 0,
                right: 0,
                bottom: 0,
                width: o.rgap
            }]
        });
        this.topLine.invisible();
        this.bottomLine.invisible();

        this.previewBtn.invisible();
        this.element.hover(function () {
            if (BI.Utils.getFieldTypeByID(o.value.field_id || o.value) === BICst.COLUMN.COUNTER) {
                return;
            }
            self.previewBtn.visible();
        }, function () {
            self.previewBtn.invisible();
        });

        BI.Utils.isSrcUsedBySrcID(o.id) === true && this.doHighLight();
        BI.Broadcasts.on(BICst.BROADCAST.SRC_PREFIX + o.id, function (v) {
            if (v === true) {
                self.doHighLight();
            } else {
                if (BI.Utils.isSrcUsedBySrcID(o.id) === false) {
                    self.unHighLight();
                }
            }
            self.setSelected(false);
        });
    },

    isSelected: function () {
        return this.button.isSelected();
    },

    setSelected: function (b) {
        this.button.setSelected(b);
        if (!b) {
            this.topLine.invisible();
            this.bottomLine.invisible();
            this.element.removeClass("select-data-item-top");
            this.element.removeClass("select-data-item-bottom");
        }
    },

    setTopLineVisible: function () {
        this.topLine.visible();
        this.element.addClass("select-data-item-top");
    },

    setTopLineInVisible: function () {
        this.topLine.invisible();
        this.element.removeClass("select-data-item-top");
    },

    setBottomLineVisible: function () {
        this.bottomLine.visible();
        this.element.addClass("select-data-item-bottom");
    },

    setBottomLineInVisible: function () {
        this.bottomLine.invisible();
        this.element.removeClass("select-data-item-bottom");
    },

    doRedMark: function () {
        this.button.doRedMark.apply(this.button, arguments);
    },

    unRedMark: function () {
        this.button.unRedMark.apply(this.button, arguments);
    },

    doHighLight: function () {
        this.button.doHighLight.apply(this.button, arguments);
    },

    unHighLight: function () {
        this.button.unHighLight.apply(this.button, arguments);
    }
});

$.shortcut("bi.detail_select_data_level1_item", BI.DetailSelectDataLevel1Item);