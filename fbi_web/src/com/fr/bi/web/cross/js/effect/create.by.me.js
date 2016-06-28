(function ($) {

    $.extend(FS, {

        createByMe: function ($tab, $content, entry) {
            entry.contentEl.empty();
            var templateManage = BI.createWidget({
                type: "bi.template_manager",
                element: entry.contentEl
            });
            templateManage.on(BI.TemplateManager.EVENT_FOLDER_RENAME, function (id, name, pId, type) {
                //重命名或者新建文件夹
                BI.requestAsync("fr_bi", "template_folder_rename", {
                    id: id,
                    pId: pId,
                    name: name,
                    type: type
                }, function () {
                })
            });
            templateManage.on(BI.TemplateManager.EVENT_DELETE, function (id, type) {
                //删除
                BI.requestAsync("fr_bi", "template_folder_delete", {
                    id: id,
                    type: type
                }, function () {
                })
            });
            templateManage.on(BI.TemplateManager.EVENT_MOVE, function (selectedFolders, toFolder) {
                //移动
                BI.requestAsync("fr_bi", "template_folder_move", {
                    selected_folders: selectedFolders,
                    to_folder: toFolder
                }, function () {
                })
            });
            templateManage.on(BI.TemplateManager.EVENT_SHARE, function (reports, users, isEdit) {
                //分享
                BI.requestAsync("fr_bi", "template_folder_share", {
                    reports: FR.encrypt(FR.jsonEncode(reports), "neilsx"),
                    users: FR.encrypt(FR.jsonEncode(users), "neilsx"),
                    edit_shared: isEdit === true
                }, function () {
                    if(isEdit !== true) {
                        refreshFolderAndReportData();
                    }
                });
            });
            templateManage.on(BI.TemplateManager.EVENT_HANGOUT, function (id, status) {
                BI.requestAsync("fr_bi", "report_hangout", {
                    id: id,
                    status: status
                }, function () {
                });
            });

            refreshFolderAndReportData();

            function refreshFolderAndReportData(){
                BI.requestAsync('fr_bi', 'get_folder_report_list', {}, function (list) {
                    templateManage.resetModel(list);
                });
            }
        }
    });

})(jQuery);