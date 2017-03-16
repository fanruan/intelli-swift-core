BI.MergeHistoryController = BI.inherit(BI.MVCController,  {

    _construct : function () {
       this.trigger = BI.Utils.triggerPreview();
    },

    populate : function (widget, model) {
        this.buttonGroup = [];
        var self = this;
        var items = model.get(ETLCst.ITEMS);;


        var branchArray = [];
        BI.each(items, function (idx, item) {
            var button = widget.createButton(item);
            self.buttonGroup.push(button);
            branchArray.push({
                el:button,
                id:item.id,
                pId:item.pId
            })
        });
        widget.path.populate(branchArray)
        //获取根节点
        var root =  widget.path.tree.getRoot();
        if(BI.isNotNull(root)){
            var child = root.getFirstChild();
            if(BI.isNotNull(child)) {
                this.showContentAndPreview(child.id, widget, model)
            }
        }
    },

    showContentAndPreview : function (id, widget, model) {
        if( this.branchValue === id) {
            return;
        }
        var items = model.get(ETLCst.ITEMS);
        this._setBranchValue(id)
        var item = BI.find(items, function (idx, item) {
            return item.id === id;
        });
        var name = item.text;
        var paneType = item.operatorType + ETLCst.ANALYSIS_TABLE_PANE
        var pane = BI.createWidget({
            type:paneType,
            width:"100%",
            height:"100%"
        })

        widget.contentTitle.setText(name);
        widget.contentView.empty();
        widget.contentView.populate([pane])
        pane.populate(item);
        BI.Layers.remove(widget.getName());
        BI.Layers.make(widget.getName(), widget.contentView,  {
            render: {
                //cls:"disable"
            }
        });
        BI.Layers.show(widget.getName());
        this._refreshPopData(widget, item);
    },

    _setBranchValue: function (id) {
        this.branchValue = id;
        BI.each(this.buttonGroup, function (idx, item) {
            item.setSelected(item.getValue() === id);
        })
    },

    _refreshPopData : function (widget, model){
        this.trigger(widget.previewTable, model, ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL, ETLCst.PREVIEW.MERGE)
    }

})