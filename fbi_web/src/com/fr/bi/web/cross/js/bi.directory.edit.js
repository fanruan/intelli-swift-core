;(function ($) {
    FS.Plugin.DirectoryContentEditor.push({

        acceptType: function (type) {
            return type == 'bi';
        },

        hyperlink: function (renderEl, data) {
            var tdname = data.reportName;
            var url = FR.buildServletUrl({
                op: 'fr_bi',
                cmd: 'bi_init',
                id: data.reportId,
                openFromShare: true,
                systemManager: true,
                createBy: "-999"
            });
            $('<a target="_blank" href=' + url + '/>')
                .text(FS.getShowText(tdname, 280, 12, 'SimSun'))
                .attr('title', tdname)
                .appendTo(renderEl);
        },

        action: function (originaldata) {
            //使用BI控件
            var self = this;
            var data = FR.clone(originaldata);
            new BI.BIReportDialog({
                report: data,
                onSave: function (bi) {
                    bi.id = data.id.substr(1);
                    bi.parentId = self.DIR.dirTabletree.getSelectedNodes()[0].id.substr(1);
                    FS.ExtendBI.addOrEditBI(bi, data.id.substr(1), data.sortindex);
                }
            });
        }
    });
    FS.ExtendBI = {};
    $.extend(FS.ExtendBI, {

        addOrEditBI: function (bi, id, sortindex) {
            if (!bi) {
                return false;
            }
            if (sortindex) {
                bi.sortindex = sortindex;
            }
            BI.requestAsync("fr_bi", "hangout_report_to_plate", {
                report: bi
            }, function (report) {
                //DOM操作
                var tName = 'bi';
                var catalogWidths = [156, 300];
                var hyperLinkIndex = 1;
                var data = [report.text,
                    {
                        reportName: report.reportName,
                        reportId: report.reportId,
                        createBy: report.createBy
                    }, report.id];
                var originaldata = data;
                if ($('.fs_reportmgr_table_content_' + tName).children().length <= 0) {
                    $('.fs_reportmgr_table_' + tName).show();
                }
                FS.REPORTMGR.addTableData(tName, catalogWidths, hyperLinkIndex, data, originaldata);
            });
        }

    });
})(jQuery);