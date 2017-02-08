BI.MonitorTable = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.MonitorTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls:"bi-monitor-table",
            status:BI.Monitor.constants.GOOD,
            column:0,
            row:2.1,
            name:"table",
            createChild:true,
            count:1,
            t:0,
            height:BI.Monitor.constants.TABLE_HEIGHT,
            width:BI.Monitor.constants.TABLE_WIDTH
        })
    },

    pushRelation : function (relation) {
        this.relations.push(relation);
    },

    getStatus : function () {
        var o = this.options;
        if(o.status === BI.Monitor.constants.GOOD){
            if(o.percent < 1) {
                return BI.Monitor.constants.GENERATING;
            }
            if(o.count > 1){
                return BI.Monitor.constants.WARNING;
            }
        }
        return o.status;
    },

    isGenerating : function () {
        return this.options.percent < 1;
    },

    addHover : function () {
        var o = this.options;
        this.element.addClass(o.baseCls + "-hover")
    },

    removeHover : function () {
        var o = this.options;
        this.element.removeClass(o.baseCls + "-hover");
    },

    _init : function () {
        BI.MonitorTable.superclass._init.apply(this, arguments);
        this.relations= [];
        var o = this.options;
        var text = o.name
        if(o.t === 1) {
            text = "Cube Table : "+ text;
        }
        if(o.status ===  BI.Monitor.constants.ERROR){
            text += " has been deleted!!"
        } else {
            text += "("+ o.count +" inuse cubes,"+o.percent * 100+"%)";
        }

        var cls = o.baseCls + "-" + this.getStatus();
        this.element.addClass(cls);
        if(this.isGenerating()){
            this.element.addClass(o.baseCls + "-generate");
        }
        var label = BI.createWidget({
            type:"bi.text_button",
            value: text,
            title: text + " id:" + o.value,
            height:this.getHeight(),
            width:this.getWidth()
        })
        if(o.createChild && o.t !== 1){
            label.on(BI.Button.EVENT_CHANGE, function(){
                if(self.getStatus() !== BI.Monitor.constants.ERROR){
                    BI.Monitor.createSingleTableView(o.value)
                }
            });
        }
        var self = this;
        this.element.hover(function () {
            BI.each(self.relations, function (idx, item) {
                item.addHover();
            })
        }, function () {
            BI.each(self.relations, function (idx, item) {
                item.removeHover();
            })
        })
        BI.createWidget({
            type: "bi.center_adapt",
            element: this.element,
            items: [label]
        })
    },


    getWidth: function () {
        return BI.Monitor.constants.TABLE_WIDTH;
    },

    getHeight : function () {
        return BI.Monitor.constants.TABLE_HEIGHT;
    },

    getX : function () {
        return this.options.column * (BI.Monitor.constants.TABLE_WIDTH +  BI.Monitor.constants.COLUMN_GAP)  + BI.Monitor.constants.ROW_GAP
    },

    getY : function () {
        return this.options.row * (BI.Monitor.constants.TABLE_HEIGHT +  BI.Monitor.constants.ROW_GAP)   + BI.Monitor.constants.ROW_GAP
    },

    getLeftPointer: function () {
        return {
            x: this.getX() + this.getWidth(),
            y: this.getY() + (this.getHeight()/2)
        }
    },

    getRightPointer: function () {
        return {
            x: this.getX(),
            y: this.getY() + (this.getHeight()/2)
        }
    },

    getTopPointer: function () {
        return {
            x: this.getX() + this.getWidth()/2,
            y: this.getY()
        }
    },

    getBottomPointer: function () {
        return {
            x: this.getX()+ this.getWidth()/2,
            y: this.getY() + this.getHeight()
        }
    }


})
$.shortcut("bi.monitor_table", BI.MonitorTable);