/**
 * Created by GUY on 2016/4/27.
 * @class BI.SelectStringLevel0Item
 * @extends BI.Single
 */
BI.SelectStringLevel0Item = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectStringLevel0Item.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-select-string-level0-item bi-select-data-level0-item",
            wId: "",
            height: 25,
            hgap: 0,
            fieldType: BICst.COLUMN.STRING,
            warningTitle: BI.i18nText("BI-Field_Already_In_Dimension"),
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
        BI.SelectStringLevel0Item.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.button = BI.createWidget({
            type: "bi.blank_icon_text_item",
            trigger: "mousedown",
            cls: "select-data-level0-item-button " + this._getFieldClass(o.fieldType),
            blankWidth: 10,
            text: o.text,
            value: o.value,
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
            if (!self.isEnabled()) {
                return;
            }
            if (BI.Utils.getFieldTypeByID(o.value) === BICst.COLUMN.COUNTER) {
                return;
            }
            self.previewBtn.visible();
        }, function () {
            self.previewBtn.invisible();
        });
        //标蓝
        BI.Utils.isSrcUsedBySrcID(o.id) === true && this.doHighLight();
        BI.Broadcasts.on(o.id, function (v) {
            if (v === true) {
                self.doHighLight();
            } else {
                if (BI.Utils.isSrcUsedBySrcID(o.id) === false) {
                    self.unHighLight();
                }
            }
        });

        //灰化
        var enable = function () {
            var dIds = BI.Utils.getAllDimensionIDs(o.wId);
            var find = BI.any(dIds, function (i, dId) {
                var src = BI.Utils.getFieldIDByDimensionID(dId);
                if (src === o.id) {
                    self.setEnable(false);
                    return true;
                }
            });
            if (find === false) {
                self.setEnable(true);
            }
        };
        BI.Utils.isSrcUsedBySrcID(o.id) === true && enable();
        BI.Broadcasts.on(o.id, function () {
            enable();
        });
    },

    setEnable: function (v) {
        BI.SelectStringLevel0Item.superclass.setEnable.apply(this, arguments)
        this.button.setEnable(v);
        this.previewBtn.setEnable(v);
        this.button.element.draggable(v ? "enable" : "disable");
        if (!v) {
            this.setSelected(false);
        }
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

$.shortcut("bi.select_string_level0_item", BI.SelectStringLevel0Item);