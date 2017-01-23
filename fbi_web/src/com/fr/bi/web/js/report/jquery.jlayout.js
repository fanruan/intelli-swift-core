/*!
 * jLayout JQuery Plugin v0.11
 *
 * Licensed under the revised BSD License.
 * Copyright 2008, Bram Stein
 * All rights reserved.
 */
/*global jQuery jLayout*/
if (jQuery && jLayout) {
    (function($){   
        $.fn.doLayout = function () {
        	//wei : 换成jquery1.9后，jquery的css方法(主要是style方法)做了比较大的改动，做了验证boxmodel
        	//的事情，比较耗时，可能因为同步的原因，当我们的表单过于复杂时，position:absolute不会生效，layout中做的
        	//事情太多。
        	this.css({
                position: 'absolute'
            });
        	if (this.data('jlayout')) {
                this.data('jlayout').layout(this, arguments);
            }
        };
        
        // richer:容器在其各个边缘留出的空间
        $.fn.insets = function () {
            var p = this.padding(),
            b = this.border();
            return {
                'top': p.top,
                'bottom': p.bottom + b.bottom + b.top,
                'left': p.left,
                'right': p.right + b.right + b.left
            };
        };

        // richer:获取 && 设置jQuery元素的边界
        $.fn.bounds = function (value) {
            var tmp = {hasIgnoredBounds : true};

            if (value) {
                if (!isNaN(value.x)) {
                    tmp.left = value.x;
                }
                if (!isNaN(value.y)) {
                    tmp.top = value.y;
                }
                if (value.width != null) {
                    tmp.width = (value.width - (this.outerWidth(true) - this.width()));
                    tmp.width = (tmp.width >= 0) ? tmp.width : value.width;
                    // fix chrome
                    //tmp.width = (tmp.width >= 0) ? tmp.width : 0;
                }
                if (value.height != null) {
                    tmp.height = value.height - (this.outerHeight(true) - this.height());
                    tmp.height = (tmp.height >= 0) ? tmp.height : value.height;
                    // fix chrome
                    //tmp.height = (tmp.height >= 0) ? tmp.height : value.0;
                }
                if($(this).data('sizeFixed')) {
                    tmp.width = value.width;
                }
                this.css(tmp);
                // richer:触发_resize事件
                var oldTmp = this.data('tmp');
                if (!oldTmp || !FR.equals(oldTmp, tmp)) {
                	this.triggerHandler(FR.Events.RESIZE, [tmp]);
                	this.data('tmp', tmp);
                }

                return this;
            }
            else {
                // richer:注意此方法只对可见元素有效
                tmp = this.position();
                return {
                    'x': tmp.left,
                    'y': tmp.top,
                    // richer:这里计算外部宽度和高度的时候，都不包括边框
                    'width': this.outerWidth(),
                    'height': this.outerHeight()
                };
            }
        };

        $.each(['min', 'max'], function (i, name) {
            $.fn[name + 'imumSize'] = function (value) {
                if (this.data('jlayout')) {
                    return this.data('jlayout')[name + 'imum'](this);
                }
                else {
                    return this[name + 'Size'](value);
                }
            };
        });

        $.fn.preferredSize = function () {
            var minSize,
            maxSize,
            margin = this.margin(),
            size = {
                width: 0,
                height: 0
            };

            if (this.data('jlayout')) {
                size = this.data('jlayout').preferred(this);

                minSize= this.minimumSize();
                maxSize = this.maximumSize();

                size.width += margin.left + margin.right;
                size.height += margin.top + margin.bottom;

                if (size.width < minSize.width || size.height < minSize.height) {
                    size.width = Math.max(size.width, minSize.width);
                    size.height = Math.max(size.height, minSize.height);
                }
                else if (size.width > maxSize.width || size.height > maxSize.height) {
                    size.width = Math.min(size.width, maxSize.width);
                    size.height = Math.min(size.height, maxSize.height);
                }
            }
            else {
                size.width = this.bounds().width + margin.left + margin.right;
                size.height = this.bounds().height + margin.top + margin.bottom;
            }
            return size;
        };
    })(jQuery);
};