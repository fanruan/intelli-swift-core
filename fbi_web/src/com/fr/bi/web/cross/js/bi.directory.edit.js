;(function ($) {
    FS.Plugin.DirectoryContentEditor.push({

        acceptTypeByString: function (type) {
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

    FS.Plugin.EntrySupporter.push({

        cacheData : [],

        acceptTypeByNumber : function (type) {
            return '7' == type;
        },

        ui : function (hyperLinkIndex) {
            var catalogs = [FR.i18nText("FS-Generic-WF_Name"), FR.i18nText("FS-Report-BI_Analysis")];
            var catalogWidths = [156, 300];
            var tName = 'bi';
            this.biTable = this.createTable(catalogs, catalogWidths, tName, hyperLinkIndex).hide();
            return this.biTable;
        },

        update : function (data) {
            this.cacheData.push(data);
        },

        populate : function () {
            var data = this._getData();
            FS.REPORTMGR.biTable.popTableData(data, this.cacheData);
            //  需要清空缓存的值
            this.cacheData = [];
        },

        _getData : function () {
            var result = [];
            for (var i = 0, len = this.cacheData.length; i < len; i++) {
                var file = this.cacheData[i];
                var fileData = [file.text, {reportName:file.reportName, reportId:file.reportId}, file.id];
                result.push(fileData);
            }
            return result;
        },
        fillCoverData : function () {
            return 'bireport';
        }
    });
})(jQuery);