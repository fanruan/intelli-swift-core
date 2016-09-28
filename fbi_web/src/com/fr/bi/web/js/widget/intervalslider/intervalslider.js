/**
 * Created by zcf on 2016/9/26.
 */
BI.IntervalSlider = BI.inherit(BI.Widget, {
    _constant: {
        // TRACK_BORDER_WIDTH: 7,
        // SLIDER_WIDTH_HALF: 15,
        // SLIDER_WIDTH: 30,
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

        this.min = 0;
        this.max = 100;

        var self = this;
        var c = this._constant;
        this.track = BI.createWidget({
            type: "bi.track"
        });
        this.lowerLabel = BI.createWidget({
            type: "bi.sign_editor",
            cls: "slider-editor-button",
            errorText: "",
            height: c.HEIGHT,
            width: c.EDITOR_WIDTH,
            validationChecker: function (v) {
                // return self._checkValidation(v);
            },
            quitChecker: function (v) {
                //return self._checkValidation(v);
            }
        });
        this.lowerLabel.on(BI.Editor.EVENT_CONFIRM, function () {
            var percent = self._getPercentByValue(this.getValue());
            self._setLeftPosition(percent);
            self._setLowerSliderPosition(percent);
        });
        this.upperLabel = BI.createWidget({
            type: "bi.sign_editor",
            cls: "slider-editor-button",
            errorText: "",
            height: c.HEIGHT,
            width: c.EDITOR_WIDTH,
            validationChecker: function (v) {
                // return self._checkValidation(v);
            },
            quitChecker: function (v) {
                //return self._checkValidation(v);
            }
        });
        this.upperLabel.on(BI.Editor.EVENT_CONFIRM, function () {
            var percent = self._getPercentByValue(this.getValue());
            self._setRightPosition(percent);
            self._setUpperSliderPosition(percent);
        });
        this.lowerSlider = BI.createWidget({
            type: "bi.slider"
        });
        this.lowerSliderRegion = BI.createWidget({
            type: "bi.layout"
        });
        this.upperSlider = BI.createWidget({
            type: "bi.slider"
        });
        this.upperSliderRegion = BI.createWidget({
            type: "bi.layout"
        });

        this.lowerSlider.element.draggable({
            axis: "x",
            containment: this.lowerSliderRegion.element,
            drag: function (e, ui) {
                var percent = (ui.position.left) * 100 / (self.track.getLength() - 30);
                self._setLeftPosition(percent);
                self.lowerLabel.setValue(self._getValueByPercent(percent));
            },
            stop: function (e, ui) {
                var percent = (ui.position.left) * 100 / (self.track.getLength() - 30);
                self._setLowerSliderPosition(percent);
            }
        });
        this.upperSlider.element.draggable({
            axis: "x",
            containment: this.upperSliderRegion.element,
            drag: function (e, ui) {
                var percent = (ui.position.left) * 100 / (self.track.getLength() - 30);
                self._setRightPosition(percent);
                self.upperLabel.setValue(self._getValueByPercent(percent));
            },
            stop: function (e, ui) {
                var percent = (ui.position.left) * 100 / (self.track.getLength() - 30);
                self._setUpperSliderPosition(percent);
            }
        });
        this._setVisible(false);
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [
                this._createItem(this.track),
                this._createItem(this.lowerSliderRegion),
                this._createItem(this.upperSliderRegion),
                this._createLayout(this.lowerSlider, this.upperSlider, 30),
                this._createLayout(this.lowerLabel, this.upperLabel, 0)
            ]
        })
    },
    _createItem: function (widget) {
        return {
            el: widget,
            top: 33,
            left: 0,
            width: "100%",
            height: 23
        }
    },
    _createLayout: function (widgetOne, widgetTwo, top) {
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
                height: 30
            },
            top: top,
            left: 0,
            width: "100%"
        }
    },
    _setBlueTrack: function (width, left) {
        this.track.setBlueTrackWidth(width);
        this.track.setBlueTrackLeft(left)
    },
    _setLowerLabelPosition: function (percent) {
        this.lowerLabel.element.css({"left": percent + "%"});
    },
    _setUpperLabelPosition: function (percent) {
        this.upperLabel.element.css({"left": percent + "%"});
    },
    _setLowerSliderPosition: function (percent) {
        this.lowerSlider.element.css({"left": percent + "%"});
    },
    _setUpperSliderPosition: function (percent) {
        this.upperSlider.element.css({"left": percent + "%"});
    },
    _setUpperSliderRegion: function (width, left) {
        this.upperSliderRegion.element.css({"width": width + "%", "left": left + "%"});
    },
    _setLowerSliderRegion: function (width) {
        this.lowerSliderRegion.element.css({"width": width + "%"})
    },
    _setLeftPosition: function (percent) {
        this.lowerSliderPercent = percent;
        this._setLowerLabelPosition(percent);
        this._setUpperSliderRegion((100 - percent), percent);
        this._setBlueTrack((this.upperSliderPercent - percent), percent);
    },
    _setRightPosition: function (percent) {
        this.upperSliderPercent = percent;
        this._setUpperLabelPosition(percent);
        this._setLowerSliderRegion(percent);
        this._setBlueTrack((percent - this.lowerSliderPercent), this.lowerSliderPercent);
    },
    _setVisible: function (bool) {
        this.lowerLabel.setVisible(bool);
        this.lowerSlider.setVisible(bool);
        this.upperLabel.setVisible(bool);
        this.upperSlider.setVisible(bool);
    },
    _setAllPosition: function (lowerPercent, upperPercent) {
        this._setLeftPosition(lowerPercent);
        this._setRightPosition(upperPercent);
    },
    _getValueByPercent: function (percent) {
        return ((this.max - this.min) * percent + this.min) / 100;
    },
    _getPercentByValue: function (v) {
        return (v - this.min) * 100 / (this.max - this.min);
    },
    getValue: function () {
        return [this.lowerLabel.getValue(), this.upperLabel.getValue()];
    },
    setValue: function (array) {
        this.lowerLabel.setValue(array[0]);
        this.upperLabel.setValue(array[1]);
        var lowerPercent = this._getPercentByValue(array[0]);
        var upperPercent = this._getPercentByValue(array[1]);
        this._setAllPosition(lowerPercent, upperPercent);
    },
    reset: function () {
        this._setVisible(false);
        this.track.reset();
    },
    populate: function (min, max) {
        this.min = min;
        this.max = max;
        if (BI.isNotNull(min) && BI.isNotNull(max)) {
            this._setVisible(true);
            this.lowerLabel.setValue(min);
            this.upperLabel.setValue(max);
            this._setAllPosition(0, 100);
        }
    }
});
$.shortcut("bi.interval_slider", BI.IntervalSlider);
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