/**
 * Created by zcf on 2016/9/22.
 */
BI.SingleSlider = BI.inherit(BI.Widget, {
    _constant: {
        EDITOR_WIDTH: 90,
        HEIGHT: 28
    },

    _defaultConfig: function () {
        return BI.extend(BI.SingleSlider.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-single-slider"
        });
    },

    _init: function () {
        BI.SingleSlider.superclass._init.apply(this, arguments);

        var self = this;
        var c = this._constant;
        this.enable = false;
        this.track = BI.createWidget({
            type: "bi.single_slider_track"
        });

        this.slider = BI.createWidget({
            type: "bi.single_slider_slider"
        });
        this.slider.element.draggable({
            axis: "x",
            containment: this.track.element,
            drag: function (e, ui) {
                var percent = (ui.position.left) * 100 / (self.track.getLength() - 30);
                self._setBlueTrack(percent);
                self._setLabelPosition(percent);
                self.label.setValue(self._getValueByPercent(percent));
            },
            stop: function (e, ui) {
                var percent = (ui.position.left) * 100 / (self.track.getLength() - 30);
                self._setSliderPosition(percent);
                self.fireEvent(BI.SingleSlider.EVENT_CHANGE);
            }
        });

        var sliderVertical = BI.createWidget({
            type: "bi.vertical",
            items: [{
                type: "bi.absolute",
                items: [this.slider]
            }],
            rgap: 30,
            height: 30
        });
        sliderVertical.element.click(function (e) {
            if (self.enable) {
                var offset = e.clientX - self.element.offset().left;
                var percent = offset * 100 / self.track.getLength();
                self._setAllPosition(percent);
                self.label.setValue(self._getValueByPercent(percent));
                self.fireEvent(BI.SingleSlider.EVENT_CHANGE);
            }
        });

        this.label = BI.createWidget({
            type: "bi.sign_editor",
            cls: "slider-editor-button",
            errorText: "",
            height: c.HEIGHT,
            width: c.EDITOR_WIDTH,
            validationChecker: function (v) {
                return self._checkValidation(v);
            },
            quitChecker: function (v) {
                return self._checkValidation(v);
            }
        });
        this.label.on(BI.SignEditor.EVENT_CONFIRM, function () {
            var percent = self._getPercentByValue(this.getValue());
            self._setAllPosition(percent);
            self.fireEvent(BI.SingleSlider.EVENT_CHANGE);
        });
        this._setVisible(false);
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.track,
                top: 33,
                left: 0,
                width: "100%",
                height: 23
            }, {
                el: sliderVertical,
                top: 30,
                left: 0,
                width: "100%"
            }, {
                el: {
                    type: "bi.vertical",
                    items: [{
                        type: "bi.absolute",
                        items: [this.label]
                    }],
                    rgap: 30,
                    height: 30
                },
                top: 0,
                left: 0,
                width: "100%"
            }]
        })
    },

    _setSliderPosition: function (percent) {
        this.slider.element.css({"left": percent + "%"});
    },
    _setLabelPosition: function (percent) {
        this.label.element.css({"left": percent + "%"});
    },
    _setBlueTrack: function (percent) {
        this.track.setBlueTrackWidth(percent);
    },
    _getValueByPercent: function (percent) {
        return (((this.max - this.min) * percent) / 100 + this.min);
    },
    _getPercentByValue: function (v) {
        return (v - this.min) * 100 / (this.max - this.min);
    },
    _checkValidation: function (v) {
        return !(BI.isNull(v) || v < this.min || v > this.max)
    },
    _setAllPosition: function (percent) {
        this._setSliderPosition(percent);
        this._setLabelPosition(percent);
        this._setBlueTrack(percent);
    },
    _setVisible: function (visible) {
        this.slider.setVisible(visible);
        this.label.setVisible(visible);
    },

    getValue: function () {
        this.label.getValue();
    },

    setValue: function (v) {
        var value = BI.parseFloat(v);
        if ((!isNaN(value)) && this._checkValidation(value) && this.enable) {
            this.label.setValue(value);
            var percent = this._getPercentByValue(value);
            this._setAllPosition(percent);
        }
    },

    reset: function () {
        this._setVisible(false);
        this.enable = false;
        this.track.reset();
    },

    populate: function (min, max, value) {
        var minNumber = BI.parseFloat(min);
        var maxNumber = BI.parseFloat(max);
        var valueNumber = BI.parseFloat(value);
        if ((!isNaN(minNumber)) && (!isNaN(maxNumber)) && (maxNumber > minNumber )) {
            this.min = minNumber;
            this.max = maxNumber;
            this._setVisible(true);
            this.enable = true;
            this.label.setErrorText(BI.i18nText("BI-Please_Enter") + minNumber + "-" + maxNumber + BI.i18nText("BI-De") + BI.i18nText("BI-Number"));
            if (!isNaN(valueNumber)) {
                this.label.setValue(valueNumber);
                this._setAllPosition(this._getPercentByValue(valueNumber));
            } else {
                this.label.setValue(minNumber);
                this._setAllPosition(0);
            }
        }
    }
});
BI.SingleSlider.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.single_slider", BI.SingleSlider);