(function($){
    /**
     * 按钮，不带图标
     *
     *      @example
     *      var $div = $('<div>').css({
     *           position : 'absolute',
     *           top : 5,
     *         left : 5
     *      });
     *      var btn = FR.createWidget({
     *          type : 'fs.button',
     *          renderEl : $div,
     *          level : 'warning',
     *          text : '确认删除'
     *      });
     *
     * @class FS.Button
     * @extends BI.Widget
     *
     * @cfg {JSON} options 配置属性
     * @cfg {Number} [options.width=100] 宽度
     * @cfg {Number} [options.height=36] 高度
     * @cfg {'common'/'success'/'warning'/'ignore'} [options.level='common'] 按钮类型，用不同颜色强调不同的场景
     * @cfg {Function} [options.handler=null] 按钮绑定的事件
     */
    FS.Button = BI.inherit(BI.Widget, {
        _defaultConfig: function () {
            return $.extend(FS.Button.superclass._defaultConfig.apply(this, arguments), {
                baseCls: 'fs-button',
                width : 100,
                height : 30,
                level : 'common',
                handler : null
            });
        },
        _init: function () {
            FS.Button.superclass._init.apply(this, arguments);
            var opts = this.options, self = this;
            this._recalculateBounds();
            if (opts.handler) {
                this.element.bind('click', function() {
                    FR.applyFunc(self, opts.handler);
                });
            }
            if (opts.text) {
                this.element.text(opts.text);
            }
            this.element.addClass('button-' + opts.level);
        },

        _recalculateBounds : function() {
            var opts = this.options;
            this.element.css({
                width : this.fixNumber(opts.width),
                height : this.fixNumber(opts.height),
                'line-height' : this.fixNumber(opts.height) + 'px'
            });
        },

        doResize : function(give) {
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
            if (give.width) {
                opts.width = give.width;
            }
            if (give.height) {
                opts.height = give.height;
            }
            this._recalculateBounds();
        },

        fixNumber : function(n) {
            return n - 2;
        }
    });
    $.shortcut('fs.button', FS.Button);
})(jQuery);