(function($){
    /**
     * 标题，不带图标
     *
     *      @example
     *      var $div = $('<div>');
     *      var title = new FS.Title({
     *      type:'fs.title',
     *      width : 'auto',
     *      height : 50,
     *      background:'#F4F4F4',
     *      text:'编辑这个模板',
     *      fontsize:16,
     *      margin_left:20
     *      });
     *
     * @class FS.Title
     * @extends FR.Widget
     *
     * @cfg {JSON} options 配置属性
     * @cfg {Number} [options.width=auto] 宽度
     * @cfg {Number} [options.height=36] 高度
     * @cfg {Number} [options.background='#F4F4F4'] 背景颜色
     * @cfg {Number} [options.text='编辑这个模板'] 文本内容
     * @cfg {Number} [options.font_size=16] 文本字体大小
     * @cfg {Number} [options.padding_left=20] 文本字体与左边框的边距
     * @cfg {Number} [options.color='#808080'] 文本字体颜色
     */
    FS.Title = BI.inherit(FR.Widget, {
        _defaultConfig: function () {
            return $.extend(FS.Title.superclass._defaultConfig.apply(this, arguments), {
                baseCls: 'fs-title'
            });
        },
        //初始化，主要定制CSS样式
        _init: function () {
            FS.Title.superclass._init.apply(this, arguments);
            var opts = this.options;
            this.element.css({
                width : opts.width,
                height : opts.height,
                'line-height' : opts.height + 'px'
            });
            this.element.text(opts.text);
        }
    });
    $.shortcut('fs.title', FS.Title);
})(jQuery);