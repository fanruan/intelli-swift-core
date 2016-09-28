/**
 * Created by zcf on 2016/9/26.
 */
BI.IntervalSlider = BI.inherit(BI.Widget, {
    _constant: {
        EDITOR_WIDTH: 90,
        HEIGHT: 28
    },
    _defaultConfig: function () {
        return BI.extend(BI.IntervalSlider.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-single-slider"
        })
    },
    _init: function () {
        BI.IntervalSlider.superclass._init.apply(this, arguments);

        var self = this;
        var c = this._constant;
        this.enable = false;

        this.track = BI.createWidget({
            type: "bi.single_slider_track"
        });

        this.labelOne = BI.createWidget({
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
        this.labelOne.on(BI.Editor.EVENT_CONFIRM, function () {
            var percent = self._getPercentByValue(this.getValue());
            self._setLabelOnePosition(percent);
            self._setSliderOnePosition(percent);
            self._setBlueTrack();
        });

        this.labelTwo = BI.createWidget({
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
        this.labelTwo.on(BI.Editor.EVENT_CONFIRM, function () {
            var percent = self._getPercentByValue(this._getValue());
            self._setLabelTwoPosition(percent);
            self._setSliderTwoPosition(percent);
            self._setBlueTrack();
        });

        this.sliderOne = BI.createWidget({
            type: "bi.single_slider_slider"
        });
        this.sliderOne.element.draggable({
            axis: "x",
            containment: this.track.element,
            drag: function (e, ui) {
                var percent = (ui.position.left) * 100 / (self.track.getLength() - 30);
                self._setLabelOnePosition(percent);
                self.labelOne.setValue(self._getValueByPercent(percent));
                self._setBlueTrack();
            },
            stop: function (e, ui) {
                var percent = (ui.position.left) * 100 / (self.track.getLength() - 30);
                self._setSliderOnePosition(percent);
            }
        });

        this.sliderTwo = BI.createWidget({
            type: "bi.single_slider_slider"
        });
        this.sliderTwo.element.draggable({
            axis: "x",
            containment: this.track.element,
            drag: function (e, ui) {
                var percent = (ui.position.left) * 100 / (self.track.getLength() - 30);
                self._setLabelTwoPosition(percent);
                self.labelTwo.setValue(self._getValueByPercent(percent));
                self._setBlueTrack();
            },
            stop: function (e, ui) {
                var percent = (ui.position.left) * 100 / (self.track.getLength() - 30);
                self._setSliderTwoPosition(percent);
            }
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
            },
                this._createLayout(this.labelOne, this.labelTwo, 0, 90),
                this._createLayout(this.sliderOne, this.sliderTwo, 30, 30)
            ]
        })
    },
    _createLayout: function (widgetOne, widgetTwo, top, height) {
        return {
            el: {
                type: "bi.vertical",
                items: [{
                    type: "bi.absolute",
                    items: [{
                        el: widgetOne,
                        top: 0,
                        left: "0%"
                    }]
                }, {
                    type: "bi.absolute",
                    items: [{
                        el: widgetTwo,
                        top: 0,
                        left: "100%"
                    }]
                }],
                rgap: 30,
                height: height
            },
            top: top,
            left: 0,
            width: "100%"
        }
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
    _setBlueTrack: function () {
        var percentOne = this._getPercentByValue(this.labelOne.getValue());
        var percentTwo = this._getPercentByValue(this.labelTwo.getValue());
        if (percentOne <= percentTwo) {
            this.track.setBlueTrackLeft(percentOne);
            this.track.setBlueTrackWidth(percentTwo - percentOne);
        } else {
            this.track.setBlueTrackLeft(percentTwo);
            this.track.setBlueTrackWidth(percentOne - percentTwo);
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
    _getValueByPercent: function (percent) {
        return (((this.max - this.min) * percent) / 100 + this.min);
    },
    _getPercentByValue: function (v) {
        return (v - this.min) * 100 / (this.max - this.min);
    },

    getValue: function () {
        var valueOne = this.labelOne.getValue();
        var valueTwo = this.labelTwo.getValue();
        if (valueOne <= valueTwo) {
            return [valueOne, valueTwo]
        } else {
            return [valueTwo, valueOne]
        }
    },

    setValue: function (v) {
        var valueOne = BI.parseFloat(v[0]);
        var valueTwo = BI.parseFloat(v[1]);
        if ((!isNaN(valueOne)) && (!isNaN(valueTwo)) && this._checkValidation(valueOne) && this._checkValidation(valueTwo) && this.enable) {
            this.labelOne.setValue(valueOne);
            this.labelTwo.setValue(valueTwo);
            this._setAllPosition(this._getPercentByValue(valueOne), this._getPercentByValue(valueTwo));
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
        if ((!isNaN(minNumber)) && (!isNaN(maxNumber)) && (maxNumber > minNumber )) {
            this.min = minNumber;
            this.max = maxNumber;
            this.enable = true;
            this._setVisible(true);
            this._setErrorText();
            if (BI.isNotEmptyArray(value)) {
                this.setValue(value)
            } else {
                this.labelOne.setValue(minNumber);
                this.labelTwo.setValue(maxNumber);
                this._setAllPosition(0, 100)
            }
        }
    }
});
$.shortcut("bi.interval_slider", BI.IntervalSlider);
// BI.IntervalSlider = BI.inherit(BI.Widget, {
//     _constant: {
//         // TRACK_BORDER_WIDTH: 7,
//         // SLIDER_WIDTH_HALF: 15,
//         // SLIDER_WIDTH: 30,
//         EDITOR_WIDTH: 90,
//         HEIGHT: 28
//     },
//
//     _defaultConfig: function () {
//         return BI.extend(BI.IntervalSlider.superclass._defaultConfig.apply(this, arguments), {
//             baseCls: "bi-single-slider"
//         })
//     },
//     _init: function () {
//         BI.IntervalSlider.superclass._init.apply(this, arguments);
//
//         this.min = 0;
//         this.max = 100;
//
//         var self = this;
//         var c = this._constant;
//         this.track = BI.createWidget({
//             type: "bi.single_slider_track"
//         });
//         this.lowerLabel = BI.createWidget({
//             type: "bi.sign_editor",
//             cls: "slider-editor-button",
//             errorText: "",
//             height: c.HEIGHT,
//             width: c.EDITOR_WIDTH,
//             validationChecker: function (v) {
//                 // return self._checkValidation(v);
//             },
//             quitChecker: function (v) {
//                 //return self._checkValidation(v);
//             }
//         });
//         this.lowerLabel.on(BI.Editor.EVENT_CONFIRM, function () {
//             var percent = self._getPercentByValue(this.getValue());
//             self._setLeftPosition(percent);
//             self._setLowerSliderPosition(percent);
//         });
//         this.upperLabel = BI.createWidget({
//             type: "bi.sign_editor",
//             cls: "slider-editor-button",
//             errorText: "",
//             height: c.HEIGHT,
//             width: c.EDITOR_WIDTH,
//             validationChecker: function (v) {
//                 // return self._checkValidation(v);
//             },
//             quitChecker: function (v) {
//                 //return self._checkValidation(v);
//             }
//         });
//         this.upperLabel.on(BI.Editor.EVENT_CONFIRM, function () {
//             var percent = self._getPercentByValue(this.getValue());
//             self._setRightPosition(percent);
//             self._setUpperSliderPosition(percent);
//         });
//         this.lowerSlider = BI.createWidget({
//             type: "bi.single_slider_slider"
//         });
//         this.lowerSliderRegion = BI.createWidget({
//             type: "bi.layout"
//         });
//         this.upperSlider = BI.createWidget({
//             type: "bi.single_slider_slider"
//         });
//         this.upperSliderRegion = BI.createWidget({
//             type: "bi.layout"
//         });
//
//         this.lowerSlider.element.draggable({
//             axis: "x",
//             containment: this.lowerSliderRegion.element,
//             drag: function (e, ui) {
//                 var percent = (ui.position.left) * 100 / (self.track.getLength() - 30);
//                 self._setLeftPosition(percent);
//                 self.lowerLabel.setValue(self._getValueByPercent(percent));
//             },
//             stop: function (e, ui) {
//                 var percent = (ui.position.left) * 100 / (self.track.getLength() - 30);
//                 self._setLowerSliderPosition(percent);
//             }
//         });
//         this.upperSlider.element.draggable({
//             axis: "x",
//             containment: this.upperSliderRegion.element,
//             drag: function (e, ui) {
//                 var percent = (ui.position.left) * 100 / (self.track.getLength() - 30);
//                 self._setRightPosition(percent);
//                 self.upperLabel.setValue(self._getValueByPercent(percent));
//             },
//             stop: function (e, ui) {
//                 var percent = (ui.position.left) * 100 / (self.track.getLength() - 30);
//                 self._setUpperSliderPosition(percent);
//             }
//         });
//         this._setVisible(false);
//         BI.createWidget({
//             type: "bi.absolute",
//             element: this.element,
//             items: [
//                 this._createItem(this.track),
//                 this._createItem(this.lowerSliderRegion),
//                 this._createItem(this.upperSliderRegion),
//                 this._createLayout(this.lowerSlider, this.upperSlider, 30),
//                 this._createLayout(this.lowerLabel, this.upperLabel, 0)
//             ]
//         })
//     },
//     _createItem: function (widget) {
//         return {
//             el: widget,
//             top: 33,
//             left: 0,
//             width: "100%",
//             height: 23
//         }
//     },
//     _createLayout: function (widgetOne, widgetTwo, top) {
//         return {
//             el: {
//                 type: "bi.vertical",
//                 items: [{
//                     type: "bi.absolute",
//                     items: [{
//                         el: widgetOne,
//                         top: 0,
//                         left: "0%"
//                     }]
//                 }, {
//                     type: "bi.absolute",
//                     items: [{
//                         el: widgetTwo,
//                         top: 0,
//                         left: "100%"
//                     }]
//                 }],
//                 rgap: 30,
//                 height: 30
//             },
//             top: top,
//             left: 0,
//             width: "100%"
//         }
//     },
//     _setBlueTrack: function (width, left) {
//         this.track.setBlueTrackWidth(width);
//         this.track.setBlueTrackLeft(left)
//     },
//     _setLowerLabelPosition: function (percent) {
//         this.lowerLabel.element.css({"left": percent + "%"});
//     },
//     _setUpperLabelPosition: function (percent) {
//         this.upperLabel.element.css({"left": percent + "%"});
//     },
//     _setLowerSliderPosition: function (percent) {
//         this.lowerSlider.element.css({"left": percent + "%"});
//     },
//     _setUpperSliderPosition: function (percent) {
//         this.upperSlider.element.css({"left": percent + "%"});
//     },
//     _setUpperSliderRegion: function (width, left) {
//         this.upperSliderRegion.element.css({"width": width + "%", "left": left + "%"});
//     },
//     _setLowerSliderRegion: function (width) {
//         this.lowerSliderRegion.element.css({"width": width + "%"})
//     },
//     _setLeftPosition: function (percent) {
//         this.lowerSliderPercent = percent;
//         this._setLowerLabelPosition(percent);
//         this._setUpperSliderRegion((100 - percent), percent);
//         this._setBlueTrack((this.upperSliderPercent - percent), percent);
//     },
//     _setRightPosition: function (percent) {
//         this.upperSliderPercent = percent;
//         this._setUpperLabelPosition(percent);
//         this._setLowerSliderRegion(percent);
//         this._setBlueTrack((percent - this.lowerSliderPercent), this.lowerSliderPercent);
//     },
//     _setVisible: function (bool) {
//         this.lowerLabel.setVisible(bool);
//         this.lowerSlider.setVisible(bool);
//         this.upperLabel.setVisible(bool);
//         this.upperSlider.setVisible(bool);
//     },
//     _setAllPosition: function (lowerPercent, upperPercent) {
//         this._setLeftPosition(lowerPercent);
//         this._setRightPosition(upperPercent);
//     },
//     _getValueByPercent: function (percent) {
//         return ((this.max - this.min) * percent + this.min) / 100;
//     },
//     _getPercentByValue: function (v) {
//         return (v - this.min) * 100 / (this.max - this.min);
//     },
//     getValue: function () {
//         return [this.lowerLabel.getValue(), this.upperLabel.getValue()];
//     },
//     setValue: function (array) {
//         this.lowerLabel.setValue(array[0]);
//         this.upperLabel.setValue(array[1]);
//         var lowerPercent = this._getPercentByValue(array[0]);
//         var upperPercent = this._getPercentByValue(array[1]);
//         this._setAllPosition(lowerPercent, upperPercent);
//     },
//     reset: function () {
//         this._setVisible(false);
//         this.track.reset();
//     },
//     populate: function (min, max) {
//         this.min = min;
//         this.max = max;
//         if (BI.isNotNull(min) && BI.isNotNull(max)) {
//             this._setVisible(true);
//             this.lowerLabel.setValue(min);
//             this.upperLabel.setValue(max);
//             this._setAllPosition(0, 100);
//         }
//     }
// });
// $.shortcut("bi.interval_slider", BI.IntervalSlider);
// BI.IntervalSlider = BI.inherit(BI.Widget, {
//     _constant: {
//         TRACK_BORDER_WIDTH: 7,
//         SLIDER_WIDTH_HALF: 15,
//         SLIDER_WIDTH: 30,
//         EDITOR_WIDTH: 90,
//         HEIGHT: 30
//     },
//
//     _defaultConfig: function () {
//         return BI.extend(BI.IntervalSlider.superclass._defaultConfig.apply(this, arguments), {
//             baseCls: "bi-single-slider"
//         });
//     },
//     _init: function () {
//         BI.IntervalSlider.superclass._init.apply(this, arguments);
//
//         var self = this;
//         var c = this._constant;
//         //≤‚ ‘”√
//         this.min = 0;
//         this.max = 100;
//         this.value = 0;
//         //
//         this.track = BI.createWidget({
//             type: "bi.track"
//         });
//         this.lowerEditor = BI.createWidget({
//             type: "bi.editor",
//             cls: "slider-editor-button",
//             errorText: "aaa",
//             height: c.HEIGHT,
//             width: c.EDITOR_WIDTH,
//             // validationChecker: function (v) {
//             //     return self._checkValidation(v);
//             // },
//             quitChecker: function (v) {
//                 return false;
//             }
//         });
//         this.lowerEditor.on(BI.Editor.EVENT_CONFIRM, function () {
//             var px = self._getPxByValue(this.getValue());
//             self.lowerSliderLeft = px;
//             self._setLowerEditorPosition(px);
//             self._setLowerSliderPosition(px);
//             self._setUpperSliderRegion((self._getTrackLength() - px), px);
//             self._setBlueTrack((self.upperSliderLeft - px), px);
//         });
//
//         this.upperEditor = BI.createWidget({
//             type: "bi.editor",
//             cls: "slider-editor-button",
//             errorText: "aaa",
//             height: c.HEIGHT,
//             width: c.EDITOR_WIDTH,
//             // validationChecker: function (v) {
//             //     return self._checkValidation(v);
//             // },
//             quitChecker: function (v) {
//                 return false;
//             }
//         });
//         this.upperEditor.on(BI.Editor.EVENT_CONFIRM, function () {
//             var px = self._getPxByValue(this._getValue());
//             self.upperSliderLeft = px;
//             self._setUpperEditorPosition(px);
//             self._setUpperSliderPosition(px);
//             self._setLowerSliderRegion(px + c.SLIDER_WIDTH);
//             self._setBlueTrack((px - self.lowerSliderLeft), (self.lowerSliderLeft + c.SLIDER_WIDTH_HALF));
//         });
//
//         this.lowerSlider = BI.createWidget({
//             type: "bi.slider"
//         });
//         this.lowerSliderRegion = BI.createWidget({
//             type: "bi.layout"
//         });
//         this.upperSlider = BI.createWidget({
//             type: "bi.slider"
//         });
//         this.upperSliderRegion = BI.createWidget({
//             type: "bi.layout"
//         });
//         //bug
//         this.lowerSliderLeft = 0;
//         this.upperSliderLeft = this._getValueLength();
//         this.track.setBlueWidthAndLeft(this._getValueLength(), c.SLIDER_WIDTH_HALF);
//         //
//         this.lowerSlider.element.draggable({
//             axis: "x",
//             containment: self.lowerSliderRegion.element,
//             drag: function (e, ui) {
//                 var left = ui.position.left;
//                 self.lowerSliderLeft = left;
//                 self._setUpperSliderRegion((self._getTrackLength() - left), left);
//                 self._setLowerEditorPosition(left);
//                 self._setBlueTrack((self.upperSliderLeft - left), (left + c.SLIDER_WIDTH_HALF));
//                 self.lowerEditor.setValue(self._getValueByPx(left));
//             }
//         });
//         this.upperSlider.element.draggable({
//             axis: "x",
//             containment: self.upperSliderRegion.element,
//             drag: function (e, ui) {
//                 var left = ui.position.left;
//                 self.upperSliderLeft = left;
//                 self._setLowerSliderRegion(left + c.SLIDER_WIDTH);
//                 self._setUpperEditorPosition(left - 60);
//                 self._setBlueTrack((left - self.lowerSliderLeft), (self.lowerSliderRegion + c.SLIDER_WIDTH_HALF));
//                 self.upperEditor.setValue(self._getValueByPx(left));
//             }
//         });
//
//         BI.createWidget({
//             type: "bi.absolute",
//             element: this.element,
//             items: [{
//                 el: {
//                     type: "bi.vertical",
//                     items: [this.track],
//                     rgap: 14,
//                     height: 24
//                 },
//                 top: 33,
//                 left: 0,
//                 width: "100%"
//             }, {
//                 el: this.upperSliderRegion,
//                 top: 33,
//                 left: 0,
//                 width: "100%",
//                 height: 24
//             }, {
//                 el: this.lowerSliderRegion,
//                 top: 33,
//                 left: 0,
//                 width: "100%",
//                 height: 24
//             }, {
//                 el: this.lowerSlider,
//                 top: 30,
//                 left: 0
//             }, {
//                 el: this.upperSlider,
//                 top: 30,
//                 right: 0
//             }, {
//                 el: this.lowerEditor,
//                 top: 0,
//                 left: 0
//             }, {
//                 el: this.upperEditor,
//                 top: 0,
//                 right: 0
//             }]
//         });
//     },
//     _setBlueTrack: function (width, left) {
//         this.track.setBlueWidthAndLeft(width, left);
//     },
//     _setUpperEditorPosition: function (left) {
//         this.upperEditor.element[0].style.left = left + "px";
//     },
//     _setLowerEditorPosition: function (left) {
//         this.lowerEditor.element[0].style.left = left + "px";
//     },
//     _setUpperSliderPosition: function (left) {
//         this.upperSlider.element[0].style.left = left + "px"
//     },
//     _setLowerSliderPosition: function (left) {
//         this.lowerSlider.element[0].style.left = left + "px"
//     },
//     _setUpperSliderRegion: function (width, left) {
//         this.upperSliderRegion.element[0].style.width = width + "px";
//         this.upperSliderRegion.element[0].style.left = left + "px";
//     },
//     _setLowerSliderRegion: function (width) {
//         this.lowerSliderRegion.element[0].style.width = width + "px";
//     },
//     _getValueByPx: function (px) {
//         var length = this._getValueLength();
//         return px * (this.max - this.min) / length;
//     },
//     _getPxByValue: function (v) {
//         var d = this.max - this.min;
//         if (d != 0) {
//             var length = this._getValueLength();
//             return v * length / d;
//         } else {
//             return 0
//         }
//     },
//
//     _getTrackLength: function () {
//         return this.track.getLength();
//     },
//     _getValueLength: function () {
//         return (this.track.getLength() - 30)//ª¨øÈøÌ∂»
//     },
//     _setVisible:function (bool) {
//         this.lowerEditor.setVisible(bool);
//         this.upperEditor.setVisible(bool);
//         this.lowerSlider.setVisible(bool);
//         this.upperSlider.setVisible(bool);
//     },
//     getValue: function () {
//         var lower = this.lowerEditor.getValue();
//         var upper = this.upperEditor.getValue();
//         return [lower, upper];
//     },
//     setValue: function (v) {
//         this.lowerEditor.setValue(v[0]);
//         this.upperEditor.setValue(v[1]);
//
//         var lowerPx = this._getPxByValue(v[0]);
//         var upperPx = this._getPxByValue(v[1]);
//
//         this._setPosition(lowerPx, upperPx);
//     },
//     _setPosition: function (lowerPx, upperPx) {
//         this.lowerSliderLeft = lowerPx;
//         this._setLowerSliderPosition(lowerPx);
//         this._setLowerEditorPosition(lowerPx);
//         this._setLowerSliderRegion(upperPx + 30);
//         this.upperSliderLeft = upperPx;
//         this._setUpperEditorPosition(upperPx);
//         this._setUpperSliderPosition(upperPx);
//         this._setUpperSliderRegion((this._getTrackLength() - lowerPx), lowerPx);
//         this._setBlueTrack((upperPx - lowerPx), (lowerPx + 15));
//     },
//     resize: function () {
//         var lowerPx = this._getPxByValue(this.lowerEditor.getValue());
//         var upperPx = this._getPxByValue(this.upperEditor.getValue());
//         this._setPosition(lowerPx, upperPx);
//     },
//     reset:function () {
//
//     },
//     populate: function (min, max) {
//         this.min=min;
//         this.max=max;
//
//         this.lowerSliderLeft = 0;
//         this.upperSliderLeft = this._getValueLength();
//         this.track.setBlueWidthAndLeft(this._getValueLength(), c.SLIDER_WIDTH_HALF);
//     }
//
// });
// $.shortcut("bi.interval_slider", BI.IntervalSlider);