(function ($) {
    /**
     * 标题，不带图标
     *
     *      @example
     *      var $div = $('<div>');
     *      var title = FR.createWidget({
     *      type:'fs.label',
     *      width : 'auto',
     *      height : 50,
     *      background:'#F4F4F4',
     *      text:'编辑这个模板',
     *      fontsize:16,
     *      align:20
     *      });
     *
     * @class FS.Title
     * @extends BI.Widget
     *
     * @cfg {JSON} options 配置属性
     * @cfg {Number} [options.width=auto] 宽度
     * @cfg {Number} [options.height=30] 高度
     * @cfg {Number} [options.background='#F4F4F4'] 背景颜色
     * @cfg {Number} [options.text='编辑这个模板'] 文本内容
     * @cfg {Number} [options.fontsize=16] 文本字体大小
     * @cfg {Number} [options.margin_left=20] 文本字体与左边框的边距
     * @cfg {Number} [options.color='#808080'] 文本字体颜色
     */
    FS.Label = BI.inherit(BI.Widget, {
        _defaultConfig: function () {
            return $.extend(FS.Label.superclass._defaultConfig.apply(this, arguments), {
                baseCls: 'fs-label',
                hgap : 0
            });
        },
        /**
         * 初始化，主要定制CSS样式
         */
        _init: function () {
            FS.Label.superclass._init.apply(this, arguments);
            var opts = this.options;
            this.element.css({
                width: opts.width || 'auto',
                height: opts.height || 30,
                'line-height': (opts.height || 30) + 'px',
                paddingLeft : opts.hgap
            });
            this.element.text(opts.text || "");

        },
        getText: function () {
            return this.options.text;
        },
        setText: function (text) {
            this.element.text(text);
        }
    });
    $.shortcut("fs.label", FS.Label);
})(jQuery);