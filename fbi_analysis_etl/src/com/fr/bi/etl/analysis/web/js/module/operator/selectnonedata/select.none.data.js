/**
 * Created by windy on 2017/4/11.
 */
BI.AnalysisETLOperatorSelectNoneData = BI.inherit(BI.AnalysisETLOperatorAbstractPane, {

    props: {
        extraCls: "bi-analysis-etl-operator-select-data",
        value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.CHOOSE_NONE_FIELD,
        showContent :false
    },

    _init : function() {
        this._editing = false;
        this.model = new BI.AnalysisETLOperatorSelectNoneDataPaneModel({});
    },

    refreshCenterState : function() {
        this.center.setEnable(!this._editing )
    },

    _populate: function(){
        this.center.populate(this.model.update(), BI.extend(this.options, BI.extend(this.options, {
            showContent: this.options.showContent
        })));
        this.refreshCenterState();
        this.refreshPopData(ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL);
        this.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, this.model.getValue(ETLCst.FIELDS))
    },

    populate : function (m, options) {
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    }
})

BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.SELECT_NONE_DATA, BI.AnalysisETLOperatorSelectNoneData);