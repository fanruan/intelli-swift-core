/**
 * Created by zcf on 2016/8/30.
 */
BI.GlobalStyleIndexPredictionStyle = BI.inherit(BI.Widget, {
    _const: {
        pageCount: 6
    },

    _defaultConfig: function () {
        return BI.extend(BI.GlobalStyleIndexPredictionStyle.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-global-style-index-prediction-style"
        })
    },

    _init: function () {
        BI.GlobalStyleIndexPredictionStyle.superclass._init.apply(this, arguments);

        var o = this.options, self = this;
        this.customStyles = [];

        this.leftButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "page-left-font",
            height: 26,
            width: 14
        });
        this.leftButton.on(BI.IconButton.EVENT_CHANGE, function () {
            self._onPageChange(self.currentPage - 1);
        });

        this.rightButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "page-right-font",
            height: 26,
            width: 14
        });
        this.rightButton.on(BI.IconButton.EVENT_CHANGE, function () {
            self._onPageChange(self.currentPage + 1);
        });

        this.pagination = BI.createWidget({
            type: "bi.global_style_pagination"
        });
        this.pagination.on(BI.GlobalStylePagination.EVENT_CHANGE, function () {
            self._onPageChange(this.getValue());
        });

        this.centerButtonGroup = BI.createWidget({
            type: "bi.button_group",
            items: [],
            layouts: [{
                type: "bi.inline",
                lgap: 5,
                rgap: 5,
                tgap: 5,
                bgap: 5
            }]
        });
        this.centerButtonGroup.on(BI.ButtonGroup.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.GlobalStyleIndexPredictionStyle.EVENT_CHANGE)
        });

        var centerItems = BI.createWidget({
            type: "bi.htape",
            items: [{
                el: {
                    type: "bi.center_adapt",
                    items: [this.leftButton]
                },
                width: 29
            }, {
                el: this.centerButtonGroup
            }, {
                el: {
                    type: "bi.center_adapt",
                    items: [this.rightButton]
                },
                width: 29
            }]
        });

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.layout"
                },
                height: 9
            }, {
                el: centerItems
            }, {
                el: this.pagination,
                height: 20
            }]
        })
    },

    _createUserCustomStyleButton: function (style) {
        var self = this;
        var button = BI.createWidget({
            type: "bi.global_style_user_custom_button",
            cls: "button-shadow",
            text: style.name,
            value: style.style,
            cannotDelete: style.cannotDelete
        });
        button.on(BI.GlobalStyleUserCustomButton.EVENT_DELETE, function () {
            self.removeStyle(this.getValue());
            self.fireEvent(BI.GlobalStyleIndexPredictionStyle.EVENT_DELETE);
        });
        return button;
    },

    canAddMoreStyles: function () {
        return this.customStyles.length < 5;
    },

    addStyle: function (style) {
        if (this.canAddMoreStyles() && !BI.deepContains(this.customStyles, style) && !BI.deepContains(BICst.GLOBAL_PREDICTION_STYLE, style)) {
            this.customStyles.push(style);
            this._populate(this.customStyles);
            return true;
        }
        return false;
    },

    removeStyle: function (style) {
        if (BI.deepRemove(this.customStyles, style)) {
            if ((this.customStyles.length + BI.size(BICst.GLOBAL_PREDICTION_STYLE)) % this._const.pageCount === 0) {
                this.currentPage--;
                if (this.currentPage < 1) {
                    this.currentPage = 1;
                }
            }
            this._populate(this.customStyles);
        }
    },

    getValue: function () {
        return this.centerButtonGroup.getValue()[0];
    },

    setValue: function (v) {
        this.centerButtonGroup.setValue(v);
    },

    getStyles: function () {
        return this.customStyles;
    },

    _onPageChange: function (currentPage) {
        this.currentPage = currentPage;
        this._populate(this.customStyles);
    },

    populate: function () {
        this.currentPage = 1;
        this._populate();
    },

    _populate: function (customStyles) {
        var self = this;
        if (customStyles) {
            this.customStyles = customStyles;
        } else {
            var gs = BI.Utils.getGlobalStyle();
            this.customStyles = gs.predictionStyle || [];
        }
        var styles = [];
        BI.each(BICst.GLOBAL_PREDICTION_STYLE, function (name, style) {
            styles.push({
                name: BI.i18nText("BI-Prediction_Style") + (styles.length + 1),
                style: style,
                cannotDelete: true
            });
        });
        styles = styles.concat(BI.map(this.customStyles, function (i, style) {
            return {
                name: BI.i18nText("BI-Custom_Style") + (i + 1),
                style: style
            }
        }));
        this.centerButtonGroup.populate(BI.map(styles.slice((this.currentPage - 1) * this._const.pageCount, this.currentPage * this._const.pageCount), function (i, style) {
            return self._createUserCustomStyleButton(style);
        }));
        this.leftButton.setEnable(this.currentPage > 1);
        this.rightButton.setEnable(styles.length > this.currentPage * this._const.pageCount);
        this.pagination.populate(this.customStyles);
        this.pagination.setValue(this.currentPage);
    }
});
BI.GlobalStyleIndexPredictionStyle.EVENT_DELETE = "BI.GlobalStyleIndexPredictionStyle.EVENT_DELETE";
BI.GlobalStyleIndexPredictionStyle.EVENT_CHANGE = "BI.GlobalStyleIndexPredictionStyle.EVENT_CHANGE";
$.shortcut("bi.global_style_index_prediction_style", BI.GlobalStyleIndexPredictionStyle);