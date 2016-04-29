BI.LeftPointer = FR.extend(BI.Single, {
    _defaultConfig: function() {
        var conf = BI.LeftPointer.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            tagName: "div",
            baseCls: (conf.baseCls || "") + " x-pointer-left b-font horizon-center",
            height : 20,
            width : 10
        })
    },

    _switchOptions : function() {
        var opts = this.options;
        opts.iconHeight = opts.height;
        opts.iconWidth = opts.width;
        opts.height = 0;
        opts.width = 0;
    },

    _init : function() {
        this._switchOptions();
        BI.LeftPointer.superclass._init.apply(this, arguments);
        var opts = this.options;
        this.element.css({
            'border-top-width' : opts.iconHeight/2,
            'border-bottom-width' : opts.iconHeight/2,
            'border-right-width' : opts.iconWidth
        })

    }
});

$.shortcut("bi.left_pointer", BI.LeftPointer);