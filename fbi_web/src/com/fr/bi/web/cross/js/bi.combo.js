;
(function ($) {
    /**
     * 下拉框
     *     @example
     *     var $div = $('<div/>').appendTo($('body'));
     *     var combo = new FS.ComboBox({
     *           renderEl : $div,
     *           width : 260,
     *           height : 30,
     *           valueField:'text',
     *           items:[{"text":"ListItem1"},{"text":"ListItem2"},{"text":"ListItem3"}]
     *     });
     *
     * @class FS.ComboBox
     * @extends FR.Widget
     *
     * @cfg {JSON} options 配置属性
     * @cfg {String} [options.textField='text'] 显示值
     * @cfg {String} [options.valueField='value'] 实际值
     * @cfg {Number} [options.width=200] 控件宽度
     * @cfg {Number} [options.height=30] 控件高度
     * @cfg {Number} [options.triggerBtnWith=30] 下拉按钮的宽度
     * @cfg {JSON} options.items 下拉列表项
     * @cfg {Number} [options.maxItemLength=5] 项数超过设置值时出现滚动条
     * @cfg {Number} [options.defaultActiveIndex=0] 默认被选中的列表项
     * @cfg {Number} [options.listItemHeight = 25] 列表项的高
     * @cfg {Function} [options.handler=null] 点击列表项触发的事件函数
     * @cfg {Boolean} [options.isAutoWidth=null] 宽度自适应
     */
    var hasExpanded = false;

    FS.ComboBox = BI.inherit(BI.Widget, {

        constants : {
            paddingLeft : 3,
            paddingRight : 3,
            paddingTop : 1,
            paddingBottom : 1,
            border : 1
        },

        _defaultConfig: function () {
            return $.extend(FS.ComboBox.superclass._defaultConfig.apply(this, arguments), {
                baseCls: 'fs-combo',
                textField : 'text',
                valueField : 'value',
                width: 200,
                height: 30,
                triggerBtnWith:30,
                items: [],
                maxItemLength:5,
                defaultActiveIndex: 0,
                listItemHeight:25,
                handler: null,
                isAutoWidth:false
            });
        },

        _init: function () {
            FS.ComboBox.superclass._init.apply(this, arguments);
            var opts = this.options, self = this;
            this.$editCompDiv = $('<div class="bi-input-wrap">').appendTo(this.element).css({
                left:0,
                right:opts.triggerBtnWith
            });
            this.$editComp = $('<input type="text" class="fs-combo-input"/>').appendTo(this.$editCompDiv).width('100%');
            this.$trigger = $('<div>').css({
                width: opts.triggerBtnWith - this.constants.border * 2,
                height: opts.height - this.constants.border * 2,
                'line-height': (opts.height - this.constants.border * 2) + 'px'
            }).appendTo(this.element);

            if(!opts.isAutoWidth){
                this.$editComp.css({
                    width: opts.width - opts.triggerBtnWith - this.constants.paddingLeft - this.constants.paddingRight - this.constants.border * 2,
                    height: opts.height - this.constants.paddingTop - this.constants.paddingBottom - this.constants.border * 2,
                    'line-height': (opts.height - this.constants.paddingTop - this.constants.paddingBottom - this.constants.border * 2) + 'px'
                });
                this.$trigger.addClass("fs-combo-trigger");
            }else{
                this.$editComp.css({
                    width:'100%',
                    height: opts.height - this.constants.paddingTop - this.constants.paddingBottom - this.constants.border * 2,
                    'line-height': (opts.height - this.constants.paddingTop - this.constants.paddingBottom - this.constants.border * 2) + 'px'
                });
                this.$trigger.addClass("fs-combo-trigger-absolute");
            }

            this.$trigger.append('<i class="fs-trigger bbr-font"></i>').bind('mousedown', function () {
                self.triggerClick();
                return false;
            });

            //下拉列表框
            this.$viewContainer = $('<div class="fs-bi-combo-list">').appendTo('body').hide();
            this.$view = $('<div>').appendTo(this.$viewContainer);
            if(!opts.isAutoWidth){
                this.$view.css({
                    width:opts.width - this.constants.border*2,
                    'z-index':999
                });
            }else{
                this.$view.css({
                    width:'auto',
                    'z-index':999
                });
            }

            var totalHeight = opts.items.length * (opts.listItemHeight);
            if(opts.items.length > opts.maxItemLength){
                totalHeight = opts.maxItemLength * (opts.listItemHeight);
                this.$view.addClass("fs-bi-combo-list-scroll");
            }
            this.$view.height(totalHeight);
            $.each(opts.items, function(idx, item){
                var $Item = $('<div>').addClass("fs-bi-combo-list-item").data("index",idx).text(item[opts.textField]).appendTo(self.$view);
                if(!self.store){
                    self.store=[];
                }
                self.store.push($Item);
                self._createListItem($Item,item,idx);
            });
            this.outerBody = $('body');
        },

        /**
         *点击下拉按钮时调用
         *
         */
        triggerClick : function() {

            var self = this,opts = this.options;
            if(!this.isEnabled()){
                return;
            }

            if(this.isExpanded()){
                hasExpanded = false;
                this.collapse();
            }else{
                this.$viewContainer.css({
                    width:self.element.width()-this.constants.border*2,
                    'top':self.element.offset().top + opts.height,
                    'left':self.element.offset().left
                });
                if(hasExpanded===true){
                    this.outerBody.trigger("mousedown");
                }
                this.$viewContainer.show();
                this.outerBody.bind('mousedown',this,this._collapseWithParam);
                hasExpanded=true;
            }
        },

        /**
         * @private
         * 创建一个列表项
         * @param {Object} $item 需要添加样式和绑定事件的Jquery对象
         * @param {JSON} item  列表项
         * @param {JSON} item.text  列表项的显示值
         * @param {JSON} item.value  列表项的实际值
         * @param {Number} index  列表项的索引
         *
         */
        _createListItem:function($item,item,index){
            var opts = this.options,self = this;
            if(opts.defaultActiveIndex===index){
                this.$active = $item.addClass('fs-bi-combo-selected');//当前被选对象
                self.activeIndex = opts.defaultActiveIndex;             //被选对象索引
            }
            $item.bind('mousedown',function(){
                var current = $(this),index = current.data("index");
                if(self.$active===current){
                    return;
                }
                self.activeIndex = index;
                self._select();
                self.setValue(item[opts.valueField]);
                self.collapse();
                if ($.isFunction(opts.handler)) {
                    opts.handler.apply(self, [index]);
                }
            });
        },

        /**
         *
         * @private
         */
        _select:function(){
            if(this.$active!==null){
                this.$active.removeClass('fs-bi-combo-selected');
            }
            this.$active = this.store[this.activeIndex].addClass('fs-bi-combo-selected');
        },

        /**
         *
         * @param {Object} e 上下文环境
         * @private
         */
        _collapseWithParam:function(e){
            var self = e.data;
            self.collapse();
        },

        /**
         *收起下拉列表
         */
        collapse:function(){
            if (!this.isExpanded()) {
                return;
            }
            this.$viewContainer.hide();
           $('body').unbind('mousedown',this,this._collapseWithParam);
        },

        /**
         * 返回列表项当前是否展开
         * @returns {Boolean} 是否展开
         */
        isExpanded:function(){
            return this.$viewContainer && this.$viewContainer.is(":visible");
        },

        /**
         * 设置输入框的值
         * @param {String/Number} value 需要在设置的输入框中的值
         */
        setValue : function(value) {
            var self = this, opts = this.options;
            var targetItem = null;

            $.each(opts.items, function(idx, item){
                targetItem = item;
                if(item[opts.valueField]===value){
                    self.activeIndex = idx;
                    return false;
                }
            });

            if(targetItem!==null){
                this.$editComp.val(targetItem[opts.textField]);
            }

            this._select();
        },

        /**
         * 获取在输入框中的值
         * @return {String} 返回当前输入框的值
         */
        getValue : function() {
            var opts = this.options;
            var item = opts.items[this.activeIndex];
            if(!item){
                return null;
            }else{
                return item[opts.valueField];
            }

        },

        rebuild: function (items) {
            this.element.empty();
            this.$view.empty();
            this.options.items = items;
            if (items.widgetUrl!==null) {
                this.options.widgetUrl = items.widgetUrl;
            }
            this._init();
        }

    });
    $.shortcut('fs.combo', FS.ComboBox);
})(jQuery);