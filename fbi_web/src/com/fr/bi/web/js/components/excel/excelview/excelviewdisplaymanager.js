/**
 * Created by zcf on 2016/11/24.
 */
BI.ExcelViewDisplayManager = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ExcelViewDisplayManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-excel-view-display-manager",
            excelId: ""
        })
    },

    _init: function () {
        BI.ExcelViewDisplayManager.superclass._init.apply(this, arguments);

        var self = this, o = this.options;

        this.excelId = o.excelId;

        this.currentId = "";
        this.idSuffix = "";
        this.resultMap = {};

        this.styleExcel = BI.createWidget({
            type: "bi.layout",
            scrollable: true,
            element: this.element
        });
        this.styleExcel.element.click(function (event) {
            var target = event.target;
            if (target.tagName === "TD") {
                var id = target.id;
                self.currentId = id.split("-")[0];
                self.fireEvent(BI.ExcelViewDisplayManager.CLICK);
            }
        });
    },

    _getExcelTable: function () {
        return this.styleExcel.element.find(".x-table");
    },

    _populate: function () {
        var self = this, flag = true;
        var table = this._getExcelTable();
        var tds = table.find("td").toArray();
        BI.each(tds, function (i, td) {
            var id = td.id;
            var cellId = id.split("-")[0];
            if (flag) {
                self.idSuffix = id.slice(cellId.length);
                flag = false;
            }
            var value = td.innerText;
            if (BI.isNotEmptyString(value)) {
                self.resultMap[cellId] = value;
            }
        });
    },

    _getTdById: function (id) {
        var table = this._getExcelTable();
        return table.find("#" + id + this.idSuffix);
    },

    setTdDraggable: function (id, draggable) {
        var td = this._getTdById(id);
        td.draggable(draggable)
    },

    setTdSelectById: function (enable, id) {
        var td = this._getTdById(id);
        if (enable) {
            td.removeClass("excel-td-select").addClass("excel-td-select");
            td.removeClass("select-data-level0-item-button").addClass("select-data-level0-item-button");
        } else {
            td.removeClass("excel-td-select");
            td.removeClass("select-data-level0-item-button");
        }
    },

    getCurrentCellId: function () {
        return this.currentId;
    },

    getValueByCellId: function (id) {
        return this.resultMap[id] || "";
    },

    getAllValue: function () {
        return this.resultMap;
    },

    setValue: function () {

    },

    setExcel: function (excelId, callback) {
        var self = this;
        if (this.excelId !== excelId) {
            this.excelId = excelId;
            var mask = BI.createWidget({
                type: "bi.loading_mask",
                masker: BICst.BODY_ELEMENT,
                text: BI.i18nText("BI-Loading")
            });
            BI.Utils.getExcelHTMLView(this.excelId, function (data) {
                self.styleExcel.empty();
                self.styleExcel.element.append(data.excelHTML);
                self._populate();
                callback();
            }, function () {
                mask.destroy();
            })
        }
    },

    populate: function () {

    }
});
BI.ExcelViewDisplayManager.CLICK = "BI.ExcelViewDisplayManager.CLICK";
$.shortcut("bi.excel_view_display_manager", BI.ExcelViewDisplayManager);