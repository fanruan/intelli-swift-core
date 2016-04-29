(function ($) {

    $.extend(FS, {

        shareToMe : function($content, entry) {

            var div1 = $('<div/>').appendTo(entry.contentEl);
            var tab = this;

             var list = [{"text":"a","lastModify":"2015.12.12"},{"text":"muban1","lastModify":"2015.12.12"},{"text":"muban2","lastModify":"2015.12.12"}];
            /*
            var list = BI.request('get_share_2_me_report_list',{},{
                op:'fr_bi',
                type: 'GET',
                dataType:'json',
                async:false
            });*/

            var table = new FS.SharedTable({
                renderEl : div1,
                items:list,
                tab : tab,
                needIcon:false,
                onEditIconClick:function(e,item){
                    new FS.EditTemplateDialog({
                        content : item
                    });
                },
                onDeleteIconClick:function(e,item){
                },
                onReNameIconClick:function(e,item){

                },
                onShareIconClick:function(e,item){
                    new FS.ShareTemplateDialog({

                    });
                }
            });
            //搜索控件
            var title = $('.fs-bi-mine-table-title');
            var target = $('<div/>').appendTo($('.fs-bi-mine-table-col-second-time',title[title.length - 1]));
            target.css({position: 'absolute',top:0,right:'20px'});
            var search = new FS.QueryBox({
                renderEl : target,
                onFocus : function(){
                } ,
                onBlur : function(){
                },
                onKeyUp : function(){
                    var v = this.getValue();
                    if(v===null|| v.length===0){
                        table.resetMyBITable(table.options.items);
                        return;
                    }
                    var items = [];
                    $.each(table.options.items,function(i,node){
                        var t = node.text;              //遍历items，筛选出包含搜索框中输入文字的列表项
                        if(t.indexOf(v) > -1){
                            items.push(node);
                        }
                    });
                    table.resetMyBITable(items);
                },
                onSearch : function(){

                }
            });
        }


    });
})(jQuery);