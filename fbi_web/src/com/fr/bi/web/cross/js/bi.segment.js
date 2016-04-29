(function ($) {
    /**
     * 分段控件
     *
     *      @example
     *      var $container = $('<div style="position:absolute; top:5px; left:5px; width:250px; height : 40px">').appendTo('body');
     *      var sg = FR.createWidget({
     *          renderEl : $container,
     *          type : 'fs.segment',
     *          valueField : 'text',
     *          items : [{text : '第一个'}, {text : '第二个'}, {text : '第三个'}]
     *      });
     *
     * @class FS.Segment
     * @extends FR.Widget
     *
     * @cfg {JSON} options 配置属性
     * @cfg {String} [options.textField='text'] 显示值字段名
     * @cfg {String} [options.valueField='value'] 实际值字段名
     * @cfg {Number} [options.width=200] 宽度
     * @cfg {Number} [options.height=30] 高度
     * @cfg {Array}  options.items 数据
     * @cfg {Number} [options.defaultActiveIndex] 默认选中的索引
     * @cfg {Function} [options.handler=null] 选中时触发的函数
     * @cfg {Number} options.handler.index 选中的索引
     */
    FS.Segment = BI.inherit(FR.Widget, {

        constants: {
            border: 1
        },

        _defaultConfig: function () {
            return $.extend(FS.Segment.superclass._defaultConfig.apply(this, arguments), {
                baseCls: 'fs-segment',
                textField: 'text',
                valueFiled: 'value',
                width: 200,
                height: 30,
                items: [],
                defaultActiveIndex: 0,
                handler: null
            });
        },
        _init: function () {
            FS.Segment.superclass._init.apply(this, arguments);
            var opts = this.options;
            for (var i = 0, count = opts.items.length; i < count; i++) {
                var item = opts.items[i];
                var $segment = $('<div>').addClass('fs-segment-item').data('index', i).text(item[opts.textField]).appendTo(this.element);
                if (!this.store) {
                    this.store = [];
                }
                this.store.push($segment);
                this._createSegment($segment, item, count, i);
            }
            this._recalculateBounds();
            this.element.css({
                'line-height': opts.height + 'px'
            });
        },

        _createSegment: function ($segment, item, count, index) {
            var opts = this.options, self = this;
            if (index === count - 1) {
                $segment.addClass('fs-segment-last');
            } else {
                $segment.addClass('fs-segment-split');
            }
            if (index === 0) {
                $segment.addClass('fs-segment-first');
            }
            if (opts.defaultActiveIndex === index) {
                this.$active = $segment.addClass('active');
                self.activeIndex = opts.defaultActiveIndex;
            }
            $segment.bind('click', function () {
                var current = $(this), index = current.data('index');
                if (self.$active == current) {
                    return;
                }
                self.activeIndex = index;
                self._select();
                if ($.isFunction(opts.handler)) {
                    opts.handler.apply(self, [index]);
                }
            });
        },

        _select: function () {
            if (this.$active) {
                this.$active.removeClass('active');
            }
            this.$active = this.store[this.activeIndex].addClass('active');
        },
        _recalculateBounds: function () {
            var opts = this.options;
            for (var index = 0, count = this.store.length; index < count; index++) {
                var $segment = this.store[index];
                // 平均宽度
                var average = Math.floor(opts.width / count);
                // 头部要剪掉左右的边框
                if (index === 0) {
                    $segment.css({
                        left: 0,
                        width: average - this.constants.border * 2
                    });
                } else {
                    $segment.css({
                        left: average * index,
                        width: average - this.constants.border
                    });
                }
                $segment.css({
                    height: opts.height - this.constants.border * 2,
                    'line-height': (opts.height - this.constants.border * 2) + 'px'
                });
            }
        },

        getValue: function () {
            var opts = this.options;
            var item = opts.items[this.activeIndex];
            if (!item) {
                return null;
            } else {
                return item[opts.valueFiled];
            }
        },
        setValue: function (value) {
            var opts = this.options;
            for (var i = 0, len = opts.items.length; i < len; i++) {
                var item = opts.items[i];
                if (item[opts.valueFiled] == value) {
                    this.activeIndex = i;
                    break;
                }
            }
            this._select();
        },

        doResize: function (give) {
            var opts = this.options;
            if (_.isEqual({
                    width: give.width,
                    height: give.height
                }, {
                    width: opts.width,
                    height: opts.height
                })) {
                return;
            }
            if (!isNaN(give.width)) {
                this.options.width = give.width;
            }
            if (!isNaN(give.height)) {
                this.options.height = give.height;
            }
            this._recalculateBounds();
        }
    });
    $.shortcut('fs.segment', FS.Segment);
})(jQuery);