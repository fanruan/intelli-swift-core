/**
 * 交叉表
 * Created by Young's on 2017/1/17.
 */
BI.CrossTable = BI.inherit(BI.GroupTable, {
    _defaultConfig: function () {
        return BI.extend(BI.CrossTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cross-table"
        });
    },

    _init: function () {
        BI.CrossTable.superclass._init.apply(this, arguments);
    },

    _initModel: function () {
        var o = this.options;
        this.model = new BI.CrossTableModel({
            wId: o.wId,
            status: o.status
        });
    },

    _onPageChange: function (callback) {
        var self = this, wId = this.options.wId;
        this.loading();
        BI.Utils.getWidgetDataByID(wId, {
            success: function (jsonData) {
                if (!self._isJSONDataValid(jsonData)) {
                    return;
                }
                self.model.setDataAndPage(jsonData);
                try {
                    self.model.createTableAttrs(BI.bind(self._onClickHeaderOperator, self), BI.bind(self._populateNoDimsChange, self));
                    callback(self.model.getItems(), self.model.getHeader(), self.model.getCrossItems(), self.model.getCrossHeader());
                } catch (e) {
                    self.errorPane.setErrorInfo("error happens during populate for table: " + e);
                    self.errorPane.setVisible(true);
                }
                self.fireEvent(BI.CrossTable.EVENT_CHANGE, {
                    _page_: {
                        h: self.table.getHPage(),
                        v: self.table.getVPage()
                    }
                });
            },
            done: function () {
                self.loaded();
            }
        }, this.model.getExtraInfo());
    },

    _populateTable: function () {
        this.errorPane.setVisible(false);
        this._refreshAttrs();
        this.table.populate(this.model.getItems(), this.model.getHeader(), this.model.getCrossItems(), this.model.getCrossHeader());
    }

});
BI.CrossTable.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.cross_table", BI.CrossTable);