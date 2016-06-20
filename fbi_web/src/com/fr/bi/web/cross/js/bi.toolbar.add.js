/**
 * 在决策平台中，报表管理页面可以添加BI相关的东西
 */
$.extend(FS.Plugin.ReportManagerAddon, {
    biButton: function (tools) {
        var self = this;
        if (FS.config.supportModules.indexOf("bi") != -1) {
            var addbibtn = {
                //添加BIICON及其事件
                iconCls: 'fs_reportmgr_bi_icon',
                width: 21,
                height: 21,
                hover: [function () {
                    $(this).addClass('fs_reportmgr_bi_icon_hover');
                }, function () {
                    $(this).removeClass('fs_reportmgr_bi_icon_hover');
                }],
                handler: {
                    mousedown: function () {
                        $(this).addClass('fs_reportmgr_bi_icon_click');
                    },
                    mouseleave: function () {
                        $(this).removeClass('fs_reportmgr_bi_icon_hover');
                        $(this).removeClass('fs_reportmgr_bi_icon_click');
                    },
                    mouseup: function () {
                        $(this).removeClass('fs_reportmgr_bi_icon_click');
                        if (!FS.REPORTMGR.DIR.dirTabletree.getSelectedNodes()[0] || FS.REPORTMGR.DIR.dirTabletree.getSelectedNodes()[0].pId === "0-2") {
                            FR.Msg.alert(BI.i18nText("FS-Generic-Simple_Alert"), BI.i18nText("FS-Report-No_Dir_Selected"));
                            return;
                        }
                        new BI.BIReportDialog({
                            onSave: function(data){
                                data.parentId = self.DIR.dirTabletree.getSelectedNodes()[0].id.substr(1);
                                FS.ExtendBI.addOrEditBI(data);
                            }
                        });
                    }
                }
            };
            tools.splice(0, 0, addbibtn);
        }
    }
});

$.extend(FS.Design.op, {
    19: function (designContainer) {
        FS.BIDezi.init(designContainer);
    }
});
FS.BIDezi = {
    init: function (renderer) {
        var src = FR.servletURL + '?op=fr_bi&cmd=init_dezi_pane';
        $('<iframe/>').css({height: '100%', width: '100%'})
            .attr({frameborder: 0, src: src}).appendTo(renderer);
    }
}