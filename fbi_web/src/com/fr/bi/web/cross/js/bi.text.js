(function($){
    /**
     * @class FS.TextEditor
     * @extends BI.Widget
     */
    FS.TextEditor = BI.inherit(BI.Widget, {
        _defaultConfig: function () {
            return $.extend(FS.TextEditor.superclass._defaultConfig.apply(this, arguments), {
                baseCls: 'fs-text',
                height : 30,
                width : 'auto',
                value : ''
            });
        },

        _init: function () {
            FS.TextEditor.superclass._init.apply(this, arguments);
            var opts = this.options;
            var $wrapper = $('<div class="fs-text-input-wrap">').appendTo(this.element);
            this.$editComp = $('<input class="fs-text-input"/>').appendTo($wrapper)
                .css({
                    height : opts.height - 4,
                    lineHeight : (opts.height - 4) + 'px'
                });
            if (opts.value) {
                this.setValue(opts.value);
            }
        },

        setValue : function(value) {
            this.$editComp.val(value);
        },

        getValue : function() {
            return this.$editComp.val();
        }
    });
    $.shortcut('fs.text', FS.TextEditor);
})(jQuery);