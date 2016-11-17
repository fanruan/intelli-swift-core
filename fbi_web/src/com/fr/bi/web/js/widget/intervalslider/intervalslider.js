/**
 * Created by zcf on 2016/9/26.
 */
BI.IntervalSlider = BI.inherit(BI.Widget, {
    _constant: {
        EDITOR_WIDTH: 90,
        EDITOR_HEIGHT: 30,
        HEIGHT: 28,
        SLIDER_WIDTH_HALF: 15,
        SLIDER_WIDTH: 30,
        SLIDER_HEIGHT: 30,
        TRACK_HEIGHT: 24
    },
    _defaultConfig: function () {
        return BI.extend(BI.IntervalSlider.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-single-slider bi-slider-track"
        })
    },
    _init: function () {
        BI.IntervalSlider.superclass._init.apply(this, arguments);

        var self = this;
        var c = this._constant;
        this.enable = false;

        this.backgroundTrack = BI.createWidget({
            type: "bi.layout",
            cls: "background-track",
            height: c.TRACK_HEIGHT
        });
        this.grayTrack = BI.createWidget({
            type: "bi.layout",
            cls: "gray-track",
            height: 8
        });
        this.blueTrack = BI.createWidget({
            type: "bi.layout",
            cls: "blue-track",
            height: 8
        });
        this.track = this._createTrackWrapper();

        this.labelOne = BI.createWidget({
            type: "bi.sign_editor",
            cls: "slider-editor-button",
            errorText: "",
            allowBlank: false,
            height: c.HEIGHT,
            width: c.EDITOR_WIDTH,
            validationChecker: function (v) {
                return self._checkValidation(v);
            },
            quitChecker: function (v) {
                return self._checkValidation(v);
            }
        });
        this.labelOne.on(BI.Editor.EVENT_CONFIRM, function () {
            var percent = self._getPercentByValue(this.getValue());
            self._setLabelOnePosition(percent);
            self._setSliderOnePosition(percent);
            self._setBlueTrack();
            self.fireEvent(BI.IntervalSlider.EVENT_CHANGE);
        });

        this.labelTwo = BI.createWidget({
            type: "bi.sign_editor",
            cls: "slider-editor-button",
            errorText: "",
            allowBlank: false,
            height: c.HEIGHT,
            width: c.EDITOR_WIDTH,
            validationChecker: function (v) {
                return self._checkValidation(v);
            },
            quitChecker: function (v) {
                return self._checkValidation(v);
            }
        });
        this.labelTwo.on(BI.Editor.EVENT_CONFIRM, function () {
            var percent = self._getPercentByValue(this.getValue());
            self._setLabelTwoPosition(percent);
            self._setSliderTwoPosition(percent);
            self._setBlueTrack();
            self.fireEvent(BI.IntervalSlider.EVENT_CHANGE);
        });

        this.sliderOne = BI.createWidget({
            type: "bi.single_slider_slider"
        });
        this.sliderOne.element.draggable({
            axis: "x",
            containment: this.grayTrack.element,
            scroll: false,
            drag: function (e, ui) {
                var percent = (ui.position.left) * 100 / (self._getGrayTrackLength());
                self._setLabelOnePosition(percent);
                self.labelOne.setValue(self._getValueByPercent(percent));
                self._setBlueTrack();
            },
            stop: function (e, ui) {
                var percent = (ui.position.left) * 100 / (self._getGrayTrackLength());
                self._setSliderOnePosition(percent);
                self.fireEvent(BI.IntervalSlider.EVENT_CHANGE);
            }
        });

        this.sliderTwo = BI.createWidget({
            type: "bi.single_slider_slider"
        });
        this.sliderTwo.element.draggable({
            axis: "x",
            containment: this.grayTrack.element,
            scroll: false,
            drag: function (e, ui) {
                var percent = (ui.position.left) * 100 / (self._getGrayTrackLength());
                self._setLabelTwoPosition(percent);
                self.labelTwo.setValue(self._getValueByPercent(percent));
                self._setBlueTrack();
            },
            stop: function (e, ui) {
                var percent = (ui.position.left) * 100 / (self._getGrayTrackLength());
                self._setSliderTwoPosition(percent);
                self.fireEvent(BI.IntervalSlider.EVENT_CHANGE);
            }
        });
        this._setVisible(false);

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.vertical",
                    items: [{
                        type: "bi.absolute",
                        items: [{
                            el: this.track,
                            width: "100%",
                            height: c.TRACK_HEIGHT
                        }]
                    }],
                    hgap: 7,
                    height: c.TRACK_HEIGHT
                },
                top: 33,
                left: 0,
                width: "100%"
            },
                this._createLabelWrapper(),
                this._createSliderWrapper()
            ]
        })
    },
    _createLabelWrapper: function () {
        var c = this._constant;
        return {
            el: {
                type: "bi.vertical",
                items: [{
                    type: "bi.absolute",
                    items: [{
                        el: this.labelOne,
                        top: 0,
                        left: "0%"
                    }]
                }, {
                    type: "bi.absolute",
                    items: [{
                        el: this.labelTwo,
                        top: 0,
                        left: "100%"
                    }]
                }],
                rgap: c.EDITOR_WIDTH,
                height: 90
            },
            top: 0,
            left: 0,
            width: "100%"
        }
    },
    _createSliderWrapper: function () {
        var c = this._constant;
        return {
            el: {
                type: "bi.vertical",
                items: [{
                    type: "bi.absolute",
                    items: [{
                        el: this.sliderOne,
                        top: 0,
                        left: "0%"
                    }]
                }, {
                    type: "bi.absolute",
                    items: [{
                        el: this.sliderTwo,
                        top: 0,
                        left: "100%"
                    }]
                }],
                hgap: c.SLIDER_WIDTH_HALF,
                height: c.SLIDER_HEIGHT
            },
            top: 30,
            left: 0,
            width: "100%"
        }
    },
    _createTrackWrapper: function () {
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: this.backgroundTrack,
                width: "100%"
            }, {
                el: {
                    type: "bi.vertical",
                    items: [{
                        type: "bi.absolute",
                        items: [{
                            el: this.grayTrack,
                            top: 0,
                            left: 0,
                            width: "100%"
                        }, {
                            el: this.blueTrack,
                            top: 0,
                            left: 0,
                            width: "0%"
                        }]
                    }],
                    hgap: 8,
                    height: 8
                },
                top: 8,
                left: 0,
                width: "100%"
            }]
        })
    },
    _checkValidation: function (v) {
        return !(BI.isNull(v) || v < this.min || v > this.max)
    },
    _checkOverlap: function () {
        var labelOneLeft = this.labelOne.element[0].offsetLeft;
        var labelTwoLeft = this.labelTwo.element[0].offsetLeft;
        if (labelOneLeft <= labelTwoLeft) {
            if ((labelTwoLeft - labelOneLeft) < 90) {
                this.labelTwo.element.css({"top": 60});
            } else {
                this.labelTwo.element.css({"top": 0});
            }
        } else {
            if ((labelOneLeft - labelTwoLeft) < 90) {
                this.labelTwo.element.css({"top": 60});
            } else {
                this.labelTwo.element.css({"top": 0});
            }
        }
    },
    _setLabelOnePosition: function (percent) {
        this.labelOne.element.css({"left": percent + "%"});
        this._checkOverlap();
    },
    _setLabelTwoPosition: function (percent) {
        this.labelTwo.element.css({"left": percent + "%"});
        this._checkOverlap();
    },
    _setSliderOnePosition: function (percent) {
        this.sliderOne.element.css({"left": percent + "%"});
    },
    _setSliderTwoPosition: function (percent) {
        this.sliderTwo.element.css({"left": percent + "%"});
    },
    _setBlueTrackLeft: function (percent) {
        this.blueTrack.element.css({"left": percent + "%"});
    },
    _setBlueTrackWidth: function (percent) {
        this.blueTrack.element.css({"width": percent + "%"});
    },
    _setBlueTrack: function () {
        var percentOne = this._getPercentByValue(this.labelOne.getValue());
        var percentTwo = this._getPercentByValue(this.labelTwo.getValue());
        if (percentOne <= percentTwo) {
            this._setBlueTrackLeft(percentOne);
            this._setBlueTrackWidth(percentTwo - percentOne);
        } else {
            this._setBlueTrackLeft(percentTwo);
            this._setBlueTrackWidth(percentOne - percentTwo);
        }
    },
    _setAllPosition: function (one, two) {
        this._setSliderOnePosition(one);
        this._setLabelOnePosition(one);
        this._setSliderTwoPosition(two);
        this._setLabelTwoPosition(two);
        this._setBlueTrack();
    },
    _setVisible: function (visible) {
        this.sliderOne.setVisible(visible);
        this.sliderTwo.setVisible(visible);
        this.labelOne.setVisible(visible);
        this.labelTwo.setVisible(visible);
    },
    _setErrorText: function () {
        var errorText = BI.i18nText("BI-Please_Enter") + this.min + "-" + this.max + BI.i18nText("BI-De") + BI.i18nText("BI-Number");
        this.labelOne.setErrorText(errorText);
        this.labelTwo.setErrorText(errorText);
    },
    _getGrayTrackLength: function () {
        return this.grayTrack.element[0].scrollWidth
    },
    _getValueByPercent: function (percent) {
        return (((this.max - this.min) * percent) / 100 + this.min);
    },
    _getPercentByValue: function (v) {
        return (v - this.min) * 100 / (this.max - this.min);
    },

    getValue: function () {
        var valueOne = BI.parseFloat(this.labelOne.getValue());
        var valueTwo = BI.parseFloat(this.labelTwo.getValue());
        if (valueOne <= valueTwo) {
            return [valueOne, valueTwo]
        } else {
            return [valueTwo, valueOne]
        }
    },

    setValue: function (v) {
        var valueOne = BI.parseFloat(v[0]);
        var valueTwo = BI.parseFloat(v[1]);
        if (this.enable && (!isNaN(valueOne)) && (!isNaN(valueTwo)) && this._checkValidation(valueOne) && this._checkValidation(valueTwo)) {
            this.labelOne.setValue(valueOne);
            this.labelTwo.setValue(valueTwo);
            this._setAllPosition(this._getPercentByValue(valueOne), this._getPercentByValue(valueTwo));
        }
    },

    reset: function () {
        this._setVisible(false);
        this.enable = false;
        this._setBlueTrackWidth(0);
    },

    populate: function (min, max, value) {
        var minNumber = BI.parseFloat(min);
        var maxNumber = BI.parseFloat(max);
        if ((!isNaN(minNumber)) && (!isNaN(maxNumber)) && (maxNumber > minNumber )) {
            this.min = minNumber;
            this.max = maxNumber;
            this.enable = true;
            this._setVisible(true);
            this._setErrorText();
            if (BI.isNotEmptyArray(value) && BI.isNotNull(value[0])) {
                this.setValue(value)
            } else {
                this.labelOne.setValue(minNumber);
                this.labelTwo.setValue(maxNumber);
                this._setAllPosition(0, 100)
            }
        }
    }
});
BI.IntervalSlider.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.interval_slider", BI.IntervalSlider);