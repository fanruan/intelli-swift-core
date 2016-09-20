/**
 * Created by zcf on 2016/8/30.
 */
BI.GlobalStyleIndexPredictionStyle = BI.inherit(BI.Widget, {
    _const: {
        WHITE: "#ffffff",
        GRAY: "#cccccc",
        PAGE_ONE: 1,
        PAGE_TWO: 2
        // FIRST_PAGE_CUSTOM_STYLE_NUMBER: 3,
        // ALL_PREDICTION_STYLE_NUMBER: 5
    },
    _defaultConfig: function () {
        return BI.extend(BI.GlobalStyleIndexPredictionStyle.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-global-style-index-prediction-style",
            initTotalPage: 1,
            initCurrentPage: 1,
            allUserCustomStyle: []
        })
    },

    _init: function () {
        BI.GlobalStyleIndexPredictionStyle.superclass._init.apply(this, arguments);

        var o = this.options, self = this;
        this.totalPage = o.initTotalPage;
        this.currentPage = o.initCurrentPage;
        this.allUserCustomStyle = o.allUserCustomStyle;

        this.leftButton = BI.createWidget({
            type: "bi.global_style_canvas_button",
            direction: "left",
            initState: false
        });
        this.leftButton.on(BI.GlobalStyleCanvasButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleIndexPredictionStyle.PAGE_CHANGE, "left");
        });
        var leftLayout = BI.createWidget({
            type: "bi.float_center_adapt",
            items: [this.leftButton]
        });

        this.rightButton = BI.createWidget({
            type: "bi.global_style_canvas_button",
            direction: "right",
            initState: true
        });
        this.rightButton.on(BI.GlobalStyleCanvasButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleIndexPredictionStyle.PAGE_CHANGE, "right");
        });
        var rightLayout = BI.createWidget({
            type: "bi.float_center_adapt",
            items: [this.rightButton]
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
            self.fireEvent(BI.GlobalStyleIndexPredictionStyle.EVENT_CHANGE, this)
        });

        var centerItems = BI.createWidget({
            type: "bi.htape",
            items: [{
                el: leftLayout,
                width: 29
            }, {
                el: this.centerButtonGroup
            }, {
                el: rightLayout,
                width: 29
            }]
        });


        this.bottomItem = BI.createWidget({
            type: "bi.button_group",
            items: [],
            layouts: [{
                type: "bi.horizontal_adapt"
            }],
            height: 40
        });
        this._populate();
        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.label"
                },
                height: 9
            }, {
                el: centerItems,
                height: 160
            }, {
                el: this.bottomItem
            }]
        })
    },

    _createButton: function (value, title) {
        return BI.createWidget({
            type: "bi.global_style_style_button",
            cls: "button-shadow",
            title: title,
            value: value
        })
    },

    _getCanvasColour: function (isSelected) {
        if (isSelected) {
            return this._const.WHITE
        } else {
            return this._const.GRAY
        }
    },

    _currentPager: function (totalNumber, currentNumber) {
        var self = this;
        var canvas = BI.createWidget({
            type: "bi.canvas",
            height: 10,
            width: totalNumber * 15 - 5
        });
        BI.each(BI.makeArray(totalNumber), function (i) {
            canvas.circle(i * 15 + 5, 5, 5, self._getCanvasColour(i == (currentNumber - 1)))
        });
        canvas.stroke();
        return canvas
    },

    _createAdministratorStyle: function () {
        return this._createButton(BICst.GLOBALPREDICTIONSTYLE.DEFAULT, BI.i18nText("BI-Administrator_Set_Style"))
    },

    _createPredictionStyleOne: function () {
        return this._createButton(BICst.GLOBALPREDICTIONSTYLE.ONE, BI.i18nText("BI-Prediction_Style_One"))
    },

    _createPredictionStyleTwo: function () {
        return this._createButton(BICst.GLOBALPREDICTIONSTYLE.TWO, BI.i18nText("BI-Prediction_Style_Two"))
    },

    // _createPredictionStyleThree: function () {
    //     return this._createButton(3, BI.i18nText("BI-Prediction_Style_Three"), "")
    // },
    //
    // _createPredictionStyleFour: function () {
    //     return this._createButton(4, BI.i18nText("BI-Prediction_Style_Four"), "")
    // },

    _createUserCustomButton: function (name, value) {
        var self = this;
        var button = BI.createWidget({
            type: "bi.global_style_user_custom_button",
            cls: "button-shadow",
            text: name,
            value: value
        });
        button.on(BI.GlobalStyleUserCustomButton.EVENT_SELECT, function () {
            self.fireEvent(BI.GlobalStyleIndexPredictionStyle.CUSTOM_SELECT, this)
        });
        button.on(BI.GlobalStyleUserCustomButton.EVENT_DELETE, function () {
            self.fireEvent(BI.GlobalStyleIndexPredictionStyle.CUSTOM_DELETE, this)
        });
        return button
    },

    _setAllButton: function () {
        if (this.totalPage == this.currentPage == this._const.PAGE_ONE) {
            this.leftButton.setState(false);
            this.rightButton.setState(false);
        }
        if (this.totalPage == this._const.PAGE_TWO) {
            if (this.currentPage == this._const.PAGE_ONE) {
                this.leftButton.setState(false);
                this.rightButton.setState(true);
            }
            if (this.currentPage == this._const.PAGE_TWO) {
                this.leftButton.setState(true);
                this.rightButton.setState(false);
            }
        }
        this.bottomItem.populate([this._currentPager(this.totalPage, this.currentPage)]);
    },

    _populatePageOne: function () {
        var self = this;
        var style = [this._createAdministratorStyle(), this._createPredictionStyleOne(), this._createPredictionStyleTwo()];
        BI.each(self.allUserCustomStyle, function (i, value) {
            if (i < 3) {
                value.currentStyle = i + 5;
                style.push(self._createUserCustomButton(BI.i18nText("BI-Custom_Style_" + (i + 1)), value))
            }
        });
        this.centerButtonGroup.populate(style);
    },

    _populatePageTwo: function () {
        var self = this;
        var style = [];
        BI.each(self.allUserCustomStyle, function (i, value) {
            if (i >= 3) {
                value.currentStyle = i + 5;
                style.push(self._createUserCustomButton(BI.i18nText("BI-Custom_Style_" + (i + 1)), value))
            }
        });
        this.centerButtonGroup.populate(style);
    },

    deleteCustomButton: function (button) {
        var value = button.getValue();
        var index = (value.currentStyle - 5);
        this.allUserCustomStyle.splice(index, 1);
        if (this.getCustomNumber() <= 3) {
            this.currentPage = this._const.PAGE_ONE
        }
        this._populate();
    },

    addUserCustomButton: function (value) {
        value.currentStyle = (this.allUserCustomStyle.length + 5);
        this.allUserCustomStyle.push(value);
        if (this.getCustomNumber() > 3) {
            this.currentPage = this._const.PAGE_TWO;
        }
        this._populate();
        this.centerButtonGroup.setValue(value);
    },

    getCustomNumber: function () {
        return this.allUserCustomStyle.length;
    },

    getValue: function () {
        return {
            "currentStyle": this.centerButtonGroup.getValue()[0],
            "allUserCustomStyle": this.allUserCustomStyle
        }
    },

    setValue: function (v) {
        if (BI.isNotNull(v)) {
            if (BI.isNotNull(v.allUserCustomStyle)) {
                this.allUserCustomStyle = v.allUserCustomStyle;
            }
            if (BI.isNotNull(v.currentStyle)) {
                if (v.currentStyle.currentStyle >= 8) {// 8 = 3 + 5
                    this.currentPage = this._const.PAGE_TWO;
                }
            }
            this._populate();
            if (BI.isNotNull(v) && BI.isNotNull(v.currentStyle)) {
                this.centerButtonGroup.setValue(v.currentStyle);
            }
        } else {
            this._populate();
        }
    },

    pageChange: function (direction) {
        if (direction === "left") {
            this.currentPage--;
        }
        if (direction === "right") {
            this.currentPage++;
        }
        this._populate();
    },

    populate: function (currentPage) {
        this.currentPage = currentPage;
        this._populate();
    },

    _populate: function () {
        if (this.allUserCustomStyle.length <= 3) {
            this.totalPage = this._const.PAGE_ONE;
            this.currentPage = this._const.PAGE_ONE;
            this._setAllButton();
            this._populatePageOne();
        } else {
            this.totalPage = this._const.PAGE_TWO;
            this._setAllButton();
            if (this.currentPage == this._const.PAGE_ONE) {
                this._populatePageOne();
            }
            if (this.currentPage == this._const.PAGE_TWO) {
                this._populatePageTwo();
            }
        }
    }
});
BI.GlobalStyleIndexPredictionStyle.CUSTOM_SELECT = "BI.GlobalStyleIndexPredictionStyle.CUSTOM_SELECT";
BI.GlobalStyleIndexPredictionStyle.CUSTOM_DELETE = "BI.GlobalStyleIndexPredictionStyle.CUSTOM_DELETE";
BI.GlobalStyleIndexPredictionStyle.PAGE_CHANGE = "BI.GlobalStyleIndexPredictionStyle.PAGE_CHANGE";
BI.GlobalStyleIndexPredictionStyle.EVENT_CHANGE = "BI.GlobalStyleIndexPredictionStyle.EVENT_CHANGE";
$.shortcut("bi.global_style_index_prediction_style", BI.GlobalStyleIndexPredictionStyle);