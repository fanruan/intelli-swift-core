/**
 * guy
 * 最基础的dom操作
 */
BI.extend(jQuery.fn, {

    destroy: function () {
        this.remove();
        if ($.browser.msie === true) {
            this[0].outerHTML = '';
        }
    },
    /**
     * 高亮显示
     * @param text 必需
     * @param keyword
     * @param py 必需
     * @returns {*}
     * @private
     */
    __textKeywordMarked__: function (text, keyword, py) {
        if (!BI.isKey(keyword)) {
            return this.text(text);
        }
        keyword = keyword + "";
        keyword = BI.toUpperCase(keyword);
        var textLeft = (text || "") + "";
        py = (py || BI.makeFirstPY(text)) + "";
        if (py != null) {
            py = BI.toUpperCase(py);
        }
        this.empty();
        while (true) {
            var tidx = BI.toUpperCase(textLeft).indexOf(keyword);
            var pidx = null;
            if (py != null) {
                pidx = py.indexOf(keyword);
                if (pidx >= 0) {
                    pidx = pidx % text.length;
                }
            }

            if (tidx >= 0) {
                this.append(textLeft.substr(0, tidx));
                this.append($("<span>").addClass("bi-keyword-red-mark")
                    .text(textLeft.substr(tidx, keyword.length)));

                textLeft = textLeft.substr(tidx + keyword.length);
                if (py != null) {
                    py = py.substr(tidx + keyword.length);
                }
            } else if (pidx != null && pidx >= 0 && Math.floor(pidx / text.length) === Math.floor((pidx + keyword.length - 1) / text.length)) {
                this.append(textLeft.substr(0, pidx));
                this.append($("<span>").addClass("bi-keyword-red-mark")
                    .text(textLeft.substr(pidx, keyword.length)));
                if (py != null) {
                    py = py.substr(pidx + keyword.length);
                }
                textLeft = textLeft.substr(pidx + keyword.length);
            } else {
                this.append(textLeft);
                break;
            }
        }

        return this;
    },

    getDomHeight: function (parent) {
        var clone = $(this).clone();
        clone.appendTo($(parent || "body"));
        var height = clone.height();
        clone.remove();
        return height;
    },

    //是否有竖直滚动条
    hasVerticalScroll: function () {
        return this[0].clientWidth < this[0].offsetWidth;
    },

    //是否有水平滚动条
    hasHorizonScroll: function () {
        return this[0].clientHeight < this[0].offsetHeight;
    },

    getStyle: function (name) {
        var obj = this[0];
        if (obj.currentStyle) {
            return obj.currentStyle[name];
        } else {
            return getComputedStyle(obj, false)[name];
        }
    },

    __isMouseInBounds__: function (e) {
        var offset2Body = this.offset();
        return !(e.pageX < offset2Body.left || e.pageX > offset2Body.left + this.outerWidth()
        || e.pageY < offset2Body.top || e.pageY > offset2Body.top + this.outerHeight())
    },

    __hasZIndexMask__: function (zindex) {
        return zindex && this.zIndexMask[zindex] != null;
    },

    __buildZIndexMask__: function (zindex, domArray) {
        this.zIndexMask = this.zIndexMask || {};//存储z-index的mask
        this.indexMask = this.indexMask || [];//存储mask
        var mask = BI.createWidget({
            type: "bi.center_adapt",
            cls: "bi-z-index-mask",
            items: domArray
        });

        mask.element.css({"z-index": zindex});
        BI.createWidget({
            type: "bi.absolute",
            element: this,
            items: [{
                el: mask,
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }]
        });
        this.indexMask.push(mask);
        zindex && (this.zIndexMask[zindex] = mask);
        return mask.element;
    },

    __releaseZIndexMask__: function (zindex) {
        if (zindex && this.zIndexMask[zindex]) {
            this.indexMask.remove(this.zIndexMask[zindex]);
            this.zIndexMask[zindex].destroy();
            return;
        }
        this.indexMask = this.indexMask || [];
        var indexMask = this.indexMask.pop();
        indexMask && indexMask.destroy();
    }
});

BI.extend(jQuery, {

    getLeftPosition: function (combo, popup, extraWidth, adjustWidth) {
        return {
            top: combo.element.offset().top,
            left: combo.element.offset().left - popup.element.outerWidth() - (extraWidth || 0) - (adjustWidth || 0)
        };
    },

    getRightPosition: function (combo, popup, extraWidth, adjustWidth) {
        var el = combo.element;
        return {
            top: el.offset().top,
            left: el.offset().left + el.outerWidth() + (extraWidth || 0) + (adjustWidth || 0)
        }
    },

    getTopPosition: function (combo, popup, extraHeight, adjustHeight) {
        return {
            top: combo.element.offset().top - popup.element.outerHeight() - (extraHeight || 0) - (adjustHeight || 0),
            left: combo.element.offset().left
        };
    },

    getBottomPosition: function (combo, popup, extraHeight, adjustHeight) {
        var el = combo.element;
        return {
            top: el.offset().top + el.outerHeight() + (extraHeight || 0) + (adjustHeight || 0),
            left: el.offset().left
        };
    },

    isLeftSpaceEnough: function (combo, popup, extraWidth, adjustWidth) {
        return $.getLeftPosition(combo, popup, extraWidth, adjustWidth).left >= 0;
    },

    isRightSpaceEnough: function (combo, popup, extraWidth, adjustWidth) {
        var viewBounds = popup.element.bounds(), windowBounds = $("body").bounds();
        return $.getRightPosition(combo, popup, extraWidth, adjustWidth).left + viewBounds.width <= windowBounds.width;
    },

    isTopSpaceEnough: function (combo, popup, extraHeight, adjustHeight) {
        return $.getTopPosition(combo, popup, extraHeight, adjustHeight).top >= 0;
    },

    isBottomSpaceEnough: function (combo, popup, extraHeight, adjustHeight) {
        var viewBounds = popup.element.bounds(), windowBounds = $("body").bounds();
        return $.getBottomPosition(combo, popup, extraHeight, adjustHeight).top + viewBounds.height <= windowBounds.height;
    },

    /**
     **获取相对目标的左边平行位置
     **
     */
    getComboLeftPosition: function (combo, popup, extraWidth, extraHeight) {
        extraWidth || (extraWidth = 0);
        extraHeight || (extraHeight = 0);
        var adjustWidth = 0, position;
        if ($.isLeftSpaceEnough(combo, popup, extraWidth, adjustWidth)) {
            position = $.getLeftPosition(combo, popup, extraWidth, adjustWidth);
        } else {
            position = $.getRightPosition(combo, popup, extraWidth, adjustWidth);
        }
        if (!$.isBottomSpaceEnough(combo, popup, extraHeight, 0)) {
            position.top = Math.min($("body").bounds().height - popup.element.outerHeight(), position.top);
        }
        return position;
    },
    getComboTopLeftPosition: function (combo, popup, extraWidth, extraHeight) {
        extraWidth || (extraWidth = 0);
        extraHeight || (extraHeight = 0);
        var adjustWidth = 0, position;
        if ($.isLeftSpaceEnough(combo, popup, extraWidth, adjustWidth)) {
            position = $.getLeftPosition(combo, popup, extraWidth, adjustWidth);
        } else {
            position = $.getRightPosition(combo, popup, extraWidth, adjustWidth);
        }
        if (!$.isTopSpaceEnough(combo, popup, -1 * combo.element.outerHeight(), extraHeight)) {
            position.top = 0;
        } else {
            position.top = $.getTopPosition(combo, popup, extraWidth).top + combo.element.outerHeight() - extraHeight;
        }
        return position;
    },
    /**
     **获取相对目标的右边平行位置
     **
     */
    getComboRightPosition: function (combo, popup, extraWidth, extraHeight) {
        extraWidth || (extraWidth = 0);
        extraHeight || (extraHeight = 0);
        var adjustWidth = 0, position;
        if ($.isRightSpaceEnough(combo, popup, extraWidth, adjustWidth)) {
            position = $.getRightPosition(combo, popup, extraWidth, adjustWidth);
        } else {
            position = $.getLeftPosition(combo, popup, extraWidth, adjustWidth);
        }
        if (!$.isBottomSpaceEnough(combo, popup, extraHeight, 0)) {
            position.top = Math.min($("body").bounds().height - popup.element.outerHeight(), position.top);
        }
        return position;
    },
    getComboTopRightPosition: function (combo, popup, extraWidth, extraHeight) {
        extraWidth || (extraWidth = 0);
        extraHeight || (extraHeight = 0);
        var adjustWidth = 0, position;
        if ($.isRightSpaceEnough(combo, popup, extraWidth, adjustWidth)) {
            position = $.getRightPosition(combo, popup, extraWidth, adjustWidth);
        } else {
            position = $.getLeftPosition(combo, popup, extraWidth, adjustWidth);
        }
        if (!$.isTopSpaceEnough(combo, popup, -1 * combo.element.outerHeight(), extraHeight)) {
            position.top = 0;
        } else {
            position.top = $.getTopPosition(combo, popup, extraWidth).top + combo.element.outerHeight() - extraHeight;
        }
        return position;
    },
    /**
     *获取下拉框的位置 combo:trigger popup: 下拉图, extraHeight:调整的高度
     * needAdaptHeight : 是否需要调整下拉框的高度  false,
     *
     * @return {
     * topPosition :   0,
     * leftPosition: 0,
     * //adaptHeight: 220,
     * }
     */
    getComboPosition: function (combo, popup, needAdaptHeight, extraHeight, offsetStyle) {
        extraHeight = extraHeight || 0;
        var el = combo.element, popEl = popup.element, adjustHeight = 0,
            el_offset = el.offset(), view_offset = popEl.offset(),
            comboBound = el.bounds(), viewBound = popEl.bounds(), windowBounds = $("body").bounds(),
            leftPosition = el_offset.left, topPosition, windowHeight = windowBounds.height;

        if ((leftPosition + viewBound.width) > windowBounds.width) {
            leftPosition = windowBounds.width - viewBound.width - adjustHeight;
        }
        if ("center" === offsetStyle) {
            leftPosition = el_offset.left + (comboBound.width - viewBound.width) / 2;
            if (leftPosition < 0) {
                leftPosition = 0;
            }
        }
        if ("right" === offsetStyle) {
            leftPosition = el_offset.left + comboBound.width - viewBound.width;
            if (leftPosition < 0) {
                leftPosition = 0;
            }
        }
        if (needAdaptHeight === true) {
            popup.resetHeight(BI.MAX);
            var currentHeight = Math.min(popup.attr("maxHeight"), popEl.outerHeight());
            if (currentHeight === 0) {
                currentHeight = 20;
            }
            var viewHeight = currentHeight + extraHeight;
            currentHeight = popup.options.maxHeight;
        } else {
            var viewHeight = viewBound.height + extraHeight;
        }

        if (el_offset.top + el.outerHeight() + viewHeight < windowHeight) {
            topPosition = el_offset.top + el.outerHeight() + extraHeight;
        } else if (el_offset.top - viewHeight > 0 || !needAdaptHeight) {
            topPosition = el_offset.top - viewHeight - extraHeight - adjustHeight;
        } else {
            //如果上面下面都放不下的话, 则比较下面和下面哪个地方剩下的位置大
            if (el_offset.top > windowHeight - ( el_offset.top + el.outerHeight() )) {
                topPosition = 0;
                currentHeight = el_offset.top - extraHeight - adjustHeight;
            } else {
                topPosition = el_offset.top + el.outerHeight() + extraHeight + adjustHeight;
                currentHeight = windowHeight - el_offset.top - el.outerHeight() - extraHeight - adjustHeight;
            }
        }

        var ob = {
            left: leftPosition,
            top: topPosition
        };

        if (currentHeight != null) {
            ob.adaptHeight = currentHeight;
        }

        return ob;
    },

    getComboTopPosition: function (combo, popup, needAdaptHeight, extraHeight, offsetStyle) {
        extraHeight = extraHeight || 0;
        var el = combo.element, popEl = popup.element, adjustHeight = 0,
            el_offset = el.offset(), view_offset = popEl.offset(),
            comboBound = el.bounds(), viewBound = popEl.bounds(), windowBounds = $("body").bounds(),
            leftPosition = el_offset.left, topPosition, windowHeight = windowBounds.height;

        if ((leftPosition + viewBound.width) > windowBounds.width) {
            leftPosition = windowBounds.width - viewBound.width - adjustHeight;
        }
        if ("center" === offsetStyle) {
            leftPosition = el_offset.left + (comboBound.width - viewBound.width) / 2;
            if (leftPosition < 0) {
                leftPosition = 0;
            }
        }
        if ("right" === offsetStyle) {
            leftPosition = el_offset.left + comboBound.width - viewBound.width;
            if (leftPosition < 0) {
                leftPosition = 0;
            }
        }
        if (needAdaptHeight === true) {
            popup.resetHeight(BI.MAX);
            var currentHeight = Math.min(popup.attr("maxHeight"), popEl.outerHeight());
            if (currentHeight === 0) {
                currentHeight = 20;
            }
            var viewHeight = currentHeight + extraHeight;
            currentHeight = popup.options.maxHeight;
        } else {
            var viewHeight = viewBound.height + extraHeight;
        }

        if (el_offset.top - viewHeight > 0) {
            topPosition = el_offset.top - viewHeight - extraHeight - adjustHeight;
        } else if (el_offset.top + el.outerHeight() + viewHeight < windowHeight || !needAdaptHeight) {
            topPosition = el_offset.top + el.outerHeight() + extraHeight;
        } else {
            //如果上面下面都放不下的话, 则比较下面和下面哪个地方剩下的位置大
            if (el_offset.top > windowHeight - ( el_offset.top + el.outerHeight() )) {
                topPosition = 0;
                currentHeight = el_offset.top - extraHeight - adjustHeight;
            } else {
                topPosition = el_offset.top + el.outerHeight() + extraHeight + adjustHeight;
                currentHeight = windowHeight - el_offset.top - el.outerHeight() - extraHeight - adjustHeight;
            }
        }

        var ob = {
            left: leftPosition,
            top: topPosition
        };

        if (currentHeight != null) {
            ob.adaptHeight = currentHeight;
        }

        return ob;
    }
});