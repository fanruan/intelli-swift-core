/**
 * Created by young.
 */

/*
 *  单表的setting
 */
FR.FEConfigure = {};

FR.FEConfigure.BIOneTableSettingCard = FR.extend( FR.__BIWidget__, {
    _baseClassOfElement: function() {
        return "fr-bi-one-table-setting-card";
    },

    _className: function() {
        return "FR.FEConfigure.BIOneTableSettingCard";
    },

    _init: function() {
        var self = this;
        FR.FEConfigure.BIOneTableSettingCard.superclass._init.apply( this, arguments );
        this.hasCreate = false;
        this.element.addClass("absolute-full-size").css({"z-index": "10000000"});
        this.createBox();
    },

    createBox: function() {
        var self = this;

        function onReturn() {
            FR.FEConfigure.switchToOnePackageSetting( self.packageName, false );
        };

        FR.FEConfigure.EXCEL_DATA_SET_PANE = -2;
        FR.FEConfigure.SERVER_DATA_SET_PANE = -1;
        FR.FEConfigure.FULL_TABLE_INFRO = 0;
        FR.FEConfigure.ETT_MANAGE_PANE = 1;
        FR.FEConfigure.ETL_SELECT_DATA = 2;

        this.fullTableInfor = new FR.FEConfigure.BIFullTableInforPane( {
            domParent: this.element,
            onSave: function( saveTable ) {
                onReturn();
            },
            onCancel: function() {
                onReturn();
            },
            onAddTableConnection: function( ) {
                if( self.seplateTables.length == 0 ) {
                    FR.FEConfigure.populateETLConvertTablePane( null, self.seplateTables.length );
                } else {
                    self.addTableFromEtlTable(  )
                }
            },
            onRemove: function() {

                var t = self.clonedStoredTable;
                var data = FR.BIFunc.getBaseTableInfor( t );
                FR.ajax({
                    url:FR.servletURL + "?op=fr_bi_configure&cmd=remove_table_from_business_package" + "&num=" + Math.random(),
                    data: data,
                    async: false,
                    complete:function(res, status) {
                        onReturn();
                    }
                });
                var fs = window.top.FS;
                fs.showMainPage();
                fs._removeTab(data.full_file_name.substring(0, 18));
                $div = $("body",parent.document).find('#fe_data');
                fs.fill_data($div);
            }
        });


        this.cardLayout = new FR.BCardLayout({
            renderEl: this.element,
            items:[{
                cardName: FR.FEConfigure.FULL_TABLE_INFRO,
                el: this.fullTableInfor.element
            }]
        });

        this.cardLayout.doLayout();
        this.hasCreate = true;
    },

    layoutComponent: function() {

        if( this.hasCreate ) {

            this.cardLayout.doLayout();

            this.fullTableInfor.layoutComponent();
        }
    },

    initTableName: function( type, data  ) {
        var allTableName = this.getAllUsedTableName();

        if( type == FR.FEConfigure.EXCEL_DATA_SET_PANE) {
//            var baseName =  "excel数据集";
            if((data.file_name.substring((data.file_name).length-1,(data.file_name).length))=='x'){
                var baseName = data.file_name.substring(0,(data.file_name).length-5);
            }else{
                var baseName = data.file_name.substring(0,(data.file_name).length-4);
            }
        } else {
            var baseName = "服务器数据集";
        }

        return FR.BIFunc.createANewName( baseName, function( nameChecked ) {

            for( var i= 0, len = allTableName.length; i<len; i++ ) {
                if( nameChecked == allTableName[i] ) {
                    return false;
                }
            }

            return true;
        });
    },

    switch2FullTableInfor: function( table ) {

        this.cardLayout.showCardByName( FR.FEConfigure.FULL_TABLE_INFRO );
        this.fullTableInfor.populate( table );
    },

    _initBoxPackage: function( table, packageName ) {
        if( !this.hasCreate ) {
            this.createBox(  );
        }

        //用于在修改时的删除用，避免原对象已被etl修改掉
        this.clonedStoredTable = FR.clone( table );

        if( packageName != null ) {
            this.packageName = packageName;
        }
    },

    /**
     *
     * @param table          if( table == null 则表示添加etl表)
     * @param packageName
     * @param isAddTable  是否添加   todo 有必要保存一个全局变量来放isAddTable
     */
    populate: function( table, packageName, isAddTable ) {
        this.isAddTable = isAddTable;

        this._initBoxPackage( table, packageName );

        this.seplateTables = [];
        if( table != null ) {
            this.seplateTables.push( table );
        } else {
            this.seplateTables = [];
        }

        this.switch2FullTableInfor( table );
    }
});


//用于表格的全部信息展示
FR.FEConfigure.BIFullTableInforPane = FR.extend( FR.__BIWidget__, {

    _baseClassOfElement: function() {
        return "fr-bi-full-table-infor-pane";
    },

    _className: function() {
        return "FR.FEConfigure.BIFullTableInforPane";
    },

    _init: function() {
        var self = this;
        FR.FEConfigure.BIFullTableInforPane.superclass._init.apply( this, arguments );
        this.element.addClass("full-size");

        var $north = $("<div></div>").addClass("fr-bi-one-table-base-structure-north");
        var $south = $("<div></div>").addClass("fr-bi-one-table-base-structure-south");

        var $center = $("<div></div>").addClass("fr-bi-one-table-base-structure-center");

        $("<div></div>").addClass("fr-bi-one-table-center-vertical-line").appendTo( $center );
        $("<div></div>").addClass("fr-bi-one-table-center-vertical-line").css({"margin-left": "0px"}).appendTo( $north );

        this.borderLayout = new FR.BorderLayout({
            renderEl: this.element,
            items:[{
                region:'north', el:$north,  height:40
            }, {
                region:'center', el:$center
            }, {
                region:'south', el:$south,  height:60
            }]
        });

        this.borderLayout.doLayout();

        this.createCenter( $center );
        this.createNorth( $north );
        this.createSouth( $south );
    },

    layoutComponent: function() {
        this.borderLayout.doLayout();
    },

    //刷新当前界面的状态
    refreshPaneState: function( ) {
        var isAdd = FR.FEConfigure.oneTableSettingPane.isAddTable;
        this.removeOutPackage.setEnable( !isAdd );

        var tables = FR.FEConfigure.oneTableSettingPane.seplateTables;

        this.saveButton.setEnable( false );
        this.detailFieldInfor.setEtlTableNull();
        this.saveButton.setTitle( "" );

        if( tables.length == 1 ) {
            this.saveButton.setText("保存");

            this.saveTable = tables[0];
            this.detailFieldInfor.populate( this.saveTable );

        } else if( tables.length == 0 ) {

            this.$eastHint.show();
            this.detailFieldInfor.populateNull( "右边Etl表不能为空" );
            this.saveButton.setTitle( "右边Etl表不能为空" );
        } else {

            this.detailFieldInfor.populateNull("右边Etl最终表只能有一张");
            this.saveButton.setTitle( "右边Etl最终表只能有一张" );
//            this.updateButton.setTitle( "右边Etl最终表只能有一张" );
//            this.displayButton.setTitle( "右边Etl最终表只能有一张" );
        }
    },

    createNorth: function( $north ) {
        var self = this;

        var westText = $("<div></div>").text(FR.i18nText("fbi_Table_Data_Setting")).appendTo( $north );
    },

    createSouth: function( $south ) {
        var self = this;

        this.cancelButton = new FR.BITextButton({
            domParent: $south,
            text4Button: FR.i18nText("fbi_cancel"),
            style: "white",
            width: 100,    height: 26,
            class4Widget: "fr-bi-one-table-base-structure-cancel-button",
            handler: function() {
//                self.options.onCancel.apply( this, arguments);
                var fs = window.top.FS;
                fs.showMainPage();
                var name = self.saveTable.full_file_name;
                fs._removeTab(name.substring(0,18));
//                self.options.onRemove.apply( this, arguments );
            }
        });

        this.saveButton = new FR.BITextButton({
            domParent: $south,
            text4Button: FR.i18nText("fbi_save"),
            style: "blue",
            width: 100,
            height: 26,
            class4Widget: "fr-bi-one-table-base-structure-save-button",
            handler: function() {
                self.saveTableInfor();
            }
        });

        this.previewButton = new FR.BITextButton({
            /*   domParent: $south,*/
            text4Button: "预览",
            style: "white",
            width: 60,
            height: 26,
            class4Widget: "fr-bi-service-data-preview-table",
            handler: function() {
                var a = self.getDisplayFieldData();
                //预览表格数据
                FR.FEConfigure.__float_setting.switch2PreviewOneTable( self.saveTable, a[0], a[1] );
            }
        });

        this.removeOutPackage = new FR.BITextLink({
//            domParent: $south,
            text:"删除",
            class4Widget: "fr-bi-service-data-remove-out-package",
            handler:function() {
                self.options.onRemove.apply( this, arguments );
            }
        });
    },

    rpc4FEConfigure : function(cmd, return_string, extraData) {
        var ob = {
            url : FR.servletURL + "?op=fbi_no_need_login&cmd=" + cmd +"&_="+Math.random()
        }
        if ( return_string ) {
            ob["returnString"] = true;
        }
        if (extraData != null) {
            ob["data"] = FR.clone(extraData);
        }

        return FR.SyncAjax(ob);
    },

    removeTableAction: function( t ) {

        var data = FR.BIFunc.getBaseTableInfor( t );
        FR.ajax({
            url:FR.servletURL + "?op=fr_bi_configure&cmd=remove_table_from_business_package" + "&num=" + Math.random(),
            data: data,
            async: false,
            timeout:10000
        });

    },

    saveTableInfor: function() {
        var self = this;

        //移除业务包操作
        var pane =  FR.FEConfigure.oneTableSettingPane;
        var packageName = pane.packageName;
        if (!pane.isAddTable){
            self.removeTableAction(pane.clonedStoredTable);
            self.saveTable.md5_table_name = pane.clonedStoredTable.md5_table_name;
        }

        //加入etl类型的符号
        $.extend( self.saveTable, {
            modified_relation_array:  self.detailFieldInfor.fieldRelations,
            package_name: packageName
        });
        $(".fr-bi-one-table-base-structure-center-west-box").__buildZIndexLoadingMask__($(".fr-bi-one-table-base-structure-center-west-box").css("z-index") - 1, "正在加载", 0);
        FR.ajax({
            url:FR.servletURL + "?op=fbi_no_need_login&cmd=fe_synchronous_table_infor_in_package" + "&num=" + Math.random(),
            type:"POST",
            data: {
                new_table: encodeURIComponent(FR.jsonEncode(self.saveTable)),
                is_add : pane.isAddTable
            },
            timeout:10000,
            error:function(res, status) {
                FR.Msg.toast("本次未连接成功");
                $(".fr-bi-one-table-base-structure-center-west-box").__releaseZIndexLoadingMask__();
                $(".fr-bi-one-table-base-structure-center-west-box").__buildZIndexFreshMask__( self.element.css("z-index")+1,"本次加载失败，点击刷新重试",0, function(){
                    self.saveTableInfor();
                    $(".fr-bi-one-table-base-structure-center-west-box").__releaseZIndexLoadingMask__();
                });
            },
            success:function(data, status, res) {
//                self.options.onSave.apply(this, [self.saveTable]);
                $(".fr-bi-one-table-base-structure-center-west-box").__releaseZIndexLoadingMask__();
                var fs = window.top.FS;
                fs.showMainPage();
                $div = $("body",parent.parent.document).find('#fe_data');
                if(pane.isAddTable){
                    fs.fill_data_add($div, self.saveTable.full_file_name);
                }
//                fs.fill_data($div);
            }
        });
    },

    getDisplayFieldData: function() {
        var detail = this.detailFieldInfor;
        return [ detail.getFieldNames(), detail.getAllDisabledFieldNames() ];
    },

    //当设置完wildcard之后
    afterSettingWildcard: function() {
        //刷新关联图
        this.etlTableDiagram.populateAllTables();
        //刷新表格
        this.refreshPaneState();
    },

    createCenter: function( $center ) {

        var $west = $("<div></div>").addClass("fr-bi-one-table-base-structure-center-west").appendTo( $center );
        this.createCenterWest( $west );

        var $east = $("<div></div>").addClass("fr-bi-one-table-base-structure-center-east horizon-center").appendTo( $center );
        this.createCenterEast( $east )
    },

    createCenterWest: function( $west ) {
        var self = this;

        this.detailFieldInfor = new FR.FEConfigure.TableDetailFieldInfro({
            domParent: $west ,
            getTableDetailData: function( isRefresh ) {
                $(".fr-bi-one-table-base-structure-center-west-box").__buildZIndexLoadingMask__($(".fr-bi-one-table-base-structure-center-west-box").css("z-index") - 1, "正在加载", 0);
                var detailInfro = this;
                FR.ajax({
                    url:FR.servletURL + "?op=fr_bi_configure&cmd=get_fields_new_table&random=" + Math.random(),
                    data: {
                        table: self.saveTable
                    },
                    type: "POST",
                    error:function(res, status) {
                        detailInfro.onError();
                        $(".fr-bi-one-table-base-structure-center-west-box").__releaseZIndexLoadingMask__();
                        $(".fr-bi-one-table-base-structure-center-west-box").__buildZIndexFreshMask__( self.element.css("z-index")+1,"本次加载失败，点击刷新重试",0, function(){
                            self.createCenterWest($west);
                            $(".fr-bi-one-table-base-structure-center-west-box").__releaseZIndexLoadingMask__();
                        });
                    },
                    success:function(data, status, res) {
                        var _data = FR.jsonDecode(res.responseText);
                        if( res.responseText == "{}" || "__ERROR_MESSAGE__" in _data) {
                            this.error();
                            return;
                        }
                        self.saveButton.setEnable( true );
                        self.saveTable.fields = _data["fields"] || [];
                        detailInfro.onSuccess( _data )
                        $(".fr-bi-one-table-base-structure-center-west-box").__releaseZIndexLoadingMask__();
                    }
                });
            }
        });
    },

    createCenterEast: function( $east ) {
        var $box = $("<div></div>").addClass("fr-bi-one-table-base-structure-center-east-box").appendTo( $east );
        $("<p></p>").text("表数据").css({"margin-top": "-2px"}).appendTo( $box );

        this.emptyHinp = $("<div></div>").text("请上传excel数据:").addClass("font-type-24 color-c horizon-center").appendTo( $box ).hide();
        this.preiviewExcel = $("<div></div>").addClass("preview-excel").appendTo( $box );
    },

    showExcelView: function() {

//        if( this.full_file_name != null ) {

        this.saveButton.setEnable( true );
        this.emptyHinp.hide();
        this.preiviewExcel.show().empty();

        this.setPreviewTable();
    },

    //通过excel展示excel表格信息
    setPreviewTable: function() {

        var self = this;
        //如果已经有设置的字段
        if( this.full_file_name != null ) {
            this.element.__buildZIndexLoadingMask__(1, "正在获取excel文件信息", 0);
            FR.ajax({
                url: FR.servletURL + "?op=fr_bi_configure&cmd=get_excel_data_value&random=" + Math.random(),
                data:{
                    full_file_name: this.full_file_name
                },
                error:function(res, status) {
                    $.buildWarningTransition(self.element, "本次未连接成功");
                    self.element.__releaseZIndexLoadingMask__();
                    self.element.__buildZIndexFreshMask__( self.element.css("z-index")+1,"本次加载失败，点击刷新重试",0, function(){
                        self.setPreviewTable();
                        self.element.__releaseZIndexLoadingMask__();
                    });
                },
                success:function(res, status) {
                    var flag = self.dealWidthFieldNames( FR.jsonDecode( res )["fields"] );
                    if(!flag){
                        self.element.__releaseZIndexLoadingMask__();
                        self.cancelButton.element.click();
                    }else{
                        self.element.__releaseZIndexLoadingMask__();
                        self.populateTable( FR.jsonDecode( res )["data"]);
                    }
                }
            })
        }
    },

    populateTable: function( data) {
        var $table = $("<table>").addClass("preview-table").appendTo( this.preiviewExcel );

        var columnCount = data[0].length;

        for( var row = 0, rowCount = data.length; row<rowCount; row++ ) {
            var tr = $("<tr></tr>").appendTo( $table );
            var oneRow = data[row];

            for( var column= 0; column<columnCount; column++) {
                tr.append( $("<td></td>").addClass("preview-table-column-" + column).text( oneRow[column] ) );
            }
        }
    },

    dealWidthFieldNames: function( fields ) {
        /*   星号 (*)
         竖线 (|)
         反斜杠 (\)
         冒号 (:)
         双引号 (“)
         小于号 (<)
         大于号 (>)
         问号 (?)
         正斜杠 (/)*/
        var f = [];
        var req = /[\*|\\:"<>?\\/]+/g;
        var trueField = true;

        for( var i=0; i<fields.length; i++ ) {
            var baseName = fields[i] || "字段";

            f.push( {
                field_type: fields[i].field_type,
                field_name: FR.BIFunc.createANewName( baseName, function( nameChecked ) {

                    for( var i= 0, len = f.length; i<len; i++ ) {
                        if( nameChecked == f[i].field_name ) {
                            return false;
                        }
                    }
                    if( req.test(nameChecked) ){
                        trueField = false;
                    }
                    return true;
                })
            })
        }
        this.saveButton.element.show();
        if( !trueField ) {
            alert("您的Excel表格首行字段中包含*、|、\、:、\"、<、>、?、/非法字符，请检查后重新导入！");
        }
        this.saveButton.setEnable( trueField );
        return trueField;
    },

    /**
     * @param table      基本表  saveTable 保存的表信息
     */
    populate: function( table ) {
        this.full_file_name = table.full_file_name;
        this.saveTable = table;
        this.showExcelView();
//        this.etlTableDiagram.populate();
        this.refreshPaneState();
    }
});

/**
 * 表格详细字段的信息暂时
 */
FR.FEConfigure.TableDetailFieldInfro = FR.extend(FR.__BIWidget__, {
    _baseClassOfElement: function() {
        return "fr-bi-table-detail-field-infro";
    },
    _className:function() {
        return "FR.FEConfigure.TableDetailFieldInfro";
    },

    _init : function() {
        var self = this;
        FR.FEConfigure.TableDetailFieldInfro.superclass._init.apply(this, arguments);
        this.element.addClass("fr-bi-one-table-base-structure-center-west-box");

        this.$north = $("<div></div>").appendTo( this.element );
        this.$center = $("<div></div>").addClass("fr-bi-table-detail-field-center").appendTo( this.element );

        this.createCenter( this.$center );

        this.$hint = $("<div></div>").addClass("font-hint").appendTo( this.element ).hide();
    },


    createCenter:function( $center ) {

        var $title = $("<div>").appendTo( $center );
        this.appendTableTitle( $title );

        this.$fieldCenterBlock = $("<div></div>").addClass("fr-bi-table-detail-fields-block").appendTo( $center );
    },

    _createTableNameEditor: function(editorWidth){
        return new FR.BIDictionaryEditor({
            css4Widget: {"margin-top":"1px", "border": "1px rgb(38,169,235) solid"},
            height:"20px",
            maxWidth: editorWidth
        });
    },

    _createWrapperLoad: function(wrapper){
        wrapper.appendAnEmptyCell().append( $("<div></div>").addClass("float-right fr-bi-refresh-small_icon fr-bi-full-table-infro-refresh-icon").click(function(){
            self.$errorLog.text("正在刷新...");
            self.populateByTableInfor( true );
        }) );
    },

    createTableNameNorth: function( connection_name ) {
        var self = this;
        this.$north.empty();

        var width = this.$north.width();
        var editorWidth = width - 40;
        var text = "";

        if( connection_name == FR.BIConstants.TABLE_TYPE_EXCEL) {
            editorWidth -= 100;
            text = "重新修改excel";
        } else if( connection_name == FR.BIConstants.TABLE_TYPE_SQL ) {
            editorWidth -= 100;
            text = "重新修改sql";
        }

        this.tableNameEditor = this._createTableNameEditor(editorWidth);

        this.tableNameEditor.on(FR.BITextEditor.EVENT_STOP_EDITING, function(){
            var t = self.storedTableInfor;
            var v = self.tableNameEditor.getValue();

            //excel,sql修改原始名 ,etl表修改转义名
            if( t.connection_name == FR.BIConstants.TABLE_TYPE_EXCEL || t.connection_name == FR.BIConstants.TABLE_TYPE_SQL ){
                t.table_name = v[1] ;
                self.tableNameEditor.setValue(v[1] || v[0] , null) ;
            } else {

                t.table_name_text = v[1];
            }
        });
        this.tableNameEditor.$real.css({"color": "#999999"});

        var nameDiv = $("<div></div>").addClass("fr-bi-full-table-infro-west-name-div").appendTo(  this.$north );

        var wrapper = new FR.VerticalMiddleWrapper({
            domParent: nameDiv,
            css4Widget: {"width": "100%"}
        });
        wrapper.appendAnEmptyCell().append( $("<span></span>").text("表名： ") );
        wrapper.appendAnEmptyCell().append( this.tableNameEditor.element );

        this.$errorLog = wrapper.appendAnEmptyCell().addClass("fr-bi-fields-hint-error");

        this._createWrapperLoad(wrapper);
    },

    //用于添加etl
    setEtlTableNull: function() {
        this.$fieldCenterBlock.empty();
        this.$north.empty();
    },

    getAllDisabledFieldNames: function() {
        var fieldNames = [];

        $("input[type=checkbox]", this.$fieldCenterBlock).each(function( index, dom) {
            if( !dom.checked ) {
                if( $(dom).attr("name") != null ) {
                    fieldNames.push( $(dom).attr("name") );
                }
            }
        })

        return fieldNames;
    },

    //获取所有的字段名字
    getFieldNames: function() {
        var fieldNames = [];

        $("input[type=checkbox]", this.$fieldCenterBlock).each(function( index, dom) {
            if( $(dom).attr("name") != null ) {
                fieldNames.push( $(dom).attr("name") );
            }
        });

        return fieldNames;
    },

    //获取表格数据失败之后
    onError: function() {
        if( $(".fr-bi-loading-bar", this.element ).length > 0) {
            this.$fieldCenterBlock.empty();
        }
        this.$errorLog.text("数据获取失败!");
    },

    //获取表格数据成功之后
    onSuccess: function( _data ) {

        this.$errorLog.text("");

        if( _data["table_name_text"] ) {
            this.storedTableInfor["table_name_text"] = _data["table_name_text"];
        }

        this.setTableNameText( this.storedTableInfor );

        this.populateByData( _data );
    },

    //保存表格的更改
    onSaveFn: function() {
        var self = this;
        $(".fr-bi-field-translator-input", self.element).focusout();

    },

    getKeyFieldTableName:function(keyField) {
        if(keyField == null) {
            return "";
        }
        if(keyField["table_name_text"]) {
            return keyField["table_name_text"] + "(" + keyField["table_name"] + ")";
        }
        return keyField["table_name"];
    },

    //设置好关系之后的返回操作
    onSaveRelations: function( keyFields ) {
        this.storedField["primary_key"] = keyFields;
        var len = keyFields.length;
        if( len != 0 ) {
            this.storedField["join_analyse"] = false;
        }

        this.refreshUsefulEditor(this.storedUsefulEditor, this.storedField);
        this.setTdRelationText( this.storedKeyTableTd, keyFields );

        var tmpArray = [];
        for(var i = 0; i < this.fieldRelations.length; i++) {
            if(this.fieldRelations[i]["foregin_field"] != this.storedField) {
                tmpArray.push(this.fieldRelations[i]);
            }
        }
        this.fieldRelations = tmpArray;
        this.fieldRelations.push(this.storedField);
    },

    setTdRelationText: function( $td, keyFields ) {
        var text = "";
        if( keyFields == null ) {
            $td.text(text);
            return;

        } else if( !(keyFields instanceof Array )  ){
            var len = 1;
            keyFields = [keyFields];
        } else {
            var len = keyFields.length;
        }

        for( var i=0; i<len; i++ ) {
            if( i==0 ) {
                text =  this.getKeyFieldTableName( keyFields[i] );
            } else {
                text += "<br>" + this.getKeyFieldTableName(keyFields[i]);
            }
        }
        $td.html( text );
    },

    setTableNameText: function( tableInfor ) {
        var self = this;

        var tne = self.tableNameEditor;
        var schema_name = tableInfor["schema_name"];
        var table_name = tableInfor["table_name"];

        if( tne.isEditing ) {
            tne.stopEditing();
            tne.setValue((schema_name ? schema_name + "." + table_name : table_name), tne.getShowValue());
        }else {
            tne.setValue((schema_name ? schema_name + "." + table_name : table_name), tableInfor["table_name_text"]);
        }
    },

    populate:function( tableInfor ) {
        var self = this;
        self.$north.show();
        self.$center.show();
        self.$hint.hide();
        this.$fieldCenterBlock.empty();

        var connectionName = tableInfor.connection_name;

        this.createTableNameNorth( connectionName );

        this.storedTableInfor = tableInfor;
        this.setTableNameText( tableInfor );
        this.populateByTableInfor();
    },

    //在没有table时用的
    populateNull: function( text ) {
        this.$north.hide();
        this.$center.hide();
        this.$hint.show().text( text );
    },

    //刷新界面
    populateByTableInfor: function() {

        this.fieldRelations = [];
        this.options.getTableDetailData.apply( this, arguments);
    },

    refreshUsefulEditor:function($dom, field) {

    },

    /**
     * @param fieldsInfor       字段信息
     * @param disabledFields    不可用的字段信息
     */
    populateByFields:function(fieldsInfor , disabledFields ) {
        var self = this;
        $.each(fieldsInfor, function(idx, field) {
            var typeBackClass = "";
            switch(parseInt(field["field_type"])) {
                case FR.BIConstants.DIMENSION_TYPE_NUMBER:
                    typeBackClass = "fr-bi-config-type-num";
                    break;
                case FR.BIConstants.DIMENSION_TYPE_STRING:
                    typeBackClass = "fr-bi-config-type-string";
                    break;
                case FR.BIConstants.DIMENSION_TYPE_DATE:
                    typeBackClass = "fr-bi-config-type-date";
            }
            var iconCombo = new FR.BIIconCombo({
                width: 40, height: 20,
                list_width: 72,
                setIcon: true,
                iconfull: true,
                items: FR.BIConstants.FIELD_TYPE_ITEMS,
                defaultValue:parseInt( field["field_type"])
            });

            iconCombo.on(FR.BICombo.EVENT_SELECTED_VALUE_MODIFIED, function(v) {
                field.field_type = v;
            });
            var $typeBody = $("<td>").append(iconCombo.element);


            var $translationBody = $("<td>").addClass("fr-bi-field-item-table-enable").
                text(field["field_name_text"] ? field["field_name_text"] : "").css({width:"29%",cursor:"text"});

            $translationBody.click(function() {
                if($translationBody.children().length > 0) {
                    return;
                }
                var $translationEditor = $("<textarea>").addClass("fr-bi-field-translator-input");
                $translationEditor.textareaAutoHeight();
                $translationEditor
                    .keyup(function() {
                        field["field_name_text"] = $translationEditor.val();
                    }).blur(function() {
                        $translationBody.empty();
                        $translationBody.text(field["field_name_text"] ? field["field_name_text"] : "");
                    });
                $translationEditor.css({"width":"80%"});
                $translationEditor.val(field["field_name_text"] ? field["field_name_text"] : "");
                $translationBody.empty();
                $translationBody.append($translationEditor);
                $translationEditor.focus();
                $translationEditor.select();
            });

            var $isUsefulEditor = $("<input type='checkbox'>").data({"field": field}).click( function() {
                self.refreshAllSelectedBox();
                field["join_analyse"] = $isUsefulEditor.is(":checked");
            });

            if( $.inArray(field.field_name, disabledFields) > -1 ) {
                field['join_analyse'] = false;
            }
            self.refreshUsefulEditor($isUsefulEditor, field);

            var $keyTableTd = $("<td>").addClass("fr-bi-field-item-table-enable").css({width:"29%",cursor:"pointer"});
            self.setTdRelationText($keyTableTd, field["primary_key"]);

            $keyTableTd.click(function(){
                self.onClickRelationTd( $keyTableTd, $isUsefulEditor, field );
            });

            var $tr = $("<tr>").append($("<td>").css({"padding-left":"2px", "padding-right":"2px"}).text(field["field_name"]))
                .append($typeBody);

            if( $isUsefulEditor.attr("checked") ) {
                self.usableFieldTr.push( $tr );
            } else {
                self.disabledFieldTr.push( $tr );
            }
        });
    },

    onClickRelationTd: function( $td, $usefulEditor, field ) {
        var self = this;

        self.storedKeyTableTd = $td;
        self.storedUsefulEditor = $usefulEditor;
        self.storedField = field;
        if( field["primary_key"] ) {

            FR.FEConfigure.afterSetDBRelation(field, field["primary_key"]);
        } else {

            FR.FEConfigure.populateSelectedRelationPane(field, [], self.storedTableInfor["package_name"]);
        }
    },

    refreshAllUsableState: function() {
        $("input[type=checkbox]", this.$fieldCenterBlock).each(function( index, dom) {
            var state = dom.checked;
            var field = $(dom).data("field");
            field.join_analyse = state;
        })
    },


    //有必要把不需要每次都加载的拿出来
    populateByData: function( _data ) {
        var self = this;

        var fields = _data["fields"];

        this.$fieldCenterBlock.empty();
        this.usableFieldTr = [];
        this.disabledFieldTr = [];

        var $fieldsTable = this.createFieldItemTable().appendTo( this.$fieldCenterBlock );

        this.populateByFields( fields, _data["disabledField"] || [] );

        for( var i=0; i<this.usableFieldTr.length; i++ ) {
            this.usableFieldTr[i].appendTo( $fieldsTable );
        }
        for( var j=0; j<this.disabledFieldTr.length; j++ ) {
            this.disabledFieldTr[j].appendTo( $fieldsTable );
        }

        this.refreshAllSelectedBox();
    },

    appendTableTitle: function( $title ) {
        var self = this;
        $("<div>").css({"width":"100%", "height":"10px"}).appendTo($title);

        var allSelectedFn = function( isSelected ) {
            $("input[type=checkbox]", self.element).each( function(index, dom){
                if( !dom.disabled ) {
                    dom.checked = isSelected;
                }
            })
            self.refreshAllUsableState();
        };
        this.allCheckBox =  $("<input type='checkbox'>").click( function() {
            var isSelected = this.checked;
            allSelectedFn( isSelected );
        });
        this.halfSelected = $("<div>").css({"position": "relative", "display": "inline-block"}).addClass("fr-bi-field-item-half-selected").hide().click( function() {
            allSelectedFn( false );
            self.refreshAllSelectedBox();
        });

        var analyseBlock = $("<div></div>").css("position", "relative").append( this.allCheckBox ).append( this.halfSelected).
            append( $("<span>").css({"vertical-align": "top"}).text("参与分析") );

        var $table = this.createFieldItemTable();
        $table.append(
            $("<tr>").append($("<td>").text("原字段名"))
                .append($("<td>").text("类型"))
        ).appendTo($title);
    },

    createFieldItemTable: function() {
        var $table = $("<table></table>").addClass("fr-bi-field-item-table");
        var $colGroup = $("<colgroup></colgroup>").appendTo( $table );

        $("<col>").css({"width": "28%", "min-width": "74px"}).appendTo( $colGroup );
        $("<col>").css({"width": "16%", "min-width": "40px"}).appendTo( $colGroup );

        return $table;
    },

    refreshAllSelectedBox: function() {
        var isAllSelected = true;
        var isAllNotSelected = true;

        $("input[type=checkbox]", this.$fieldCenterBlock).each(function( index, dom) {

            if( !dom.disabled && $(dom).data("field")!=null ) {
                if( dom.checked ) {
                    isAllNotSelected = false;
                } else {
                    isAllSelected = false;
                }
            }
        })

        if( !isAllNotSelected && !isAllSelected ) {
            this.halfSelected.show();
            this.allCheckBox.hide();
        } else {
            this.halfSelected.hide();
            this.allCheckBox.show();
            this.allCheckBox[0].checked = isAllSelected;
        }
    }
});

/**
 * 表格详细字段的信息暂时
 */
FR.BIConfigure.TableDetailFieldInfro_Rename = FR.extend(FR.FEConfigure.TableDetailFieldInfro, {
    _baseClassOfElement: function() {
        return "fr-bi-table-detail-field-infro";
    },
    _className:function() {
        return "FR.BIConfigure.TableDetailFieldInfro";
    },

    _init : function() {
        FR.BIConfigure.TableDetailFieldInfro.superclass._init.apply(this, arguments);
    },

    _createTableNameEditor: function(editorWidth){
        return new FR.BIDictionaryEditor({
            css4Widget: {"margin-top":"1px", "border": "1px rgb(38,169,235) solid", "width": "110px"},
            height:"20px",
            allowBlank: false,
            maxWidth: editorWidth
        });
    },

    _createWrapperLoad: function(wrapper){

    },

    refreshUsefulEditor:function($dom, field) {
        $dom.attr("checked", field['join_analyse']);
    },

    onClickRelationTd: function( $td, $usefulEditor, field ) {
        var self = this;

        self.storedKeyTableTd = $td;
        self.storedUsefulEditor = $usefulEditor;
        self.storedField = field;
        if( field["primary_key"] ) {

            FR.BIConfigure.afterSetDBRelation(field, field["primary_key"]);
        } else {

            FR.BIConfigure.populateSelectedRelationPane(field, [], self.storedTableInfor["package_name"]);
        }
    },

    refreshAllSelectedBox: function() {
        var isAllSelected = true;
        var isAllNotSelected = true;

        $("input[type=checkbox]", this.$fieldCenterBlock).each(function( index, dom) {

            if( !dom.disabled && $(dom).data("field")!=null ) {
                if( dom.checked ) {
                    isAllNotSelected = false;
                } else {
                    isAllSelected = false;
                }
            }
        })

        if( !isAllNotSelected && !isAllSelected ) {
            this.halfSelected.show();
            this.allCheckBox.hide();
        } else {
            this.halfSelected.hide();
            this.allCheckBox.show();
            this.allCheckBox[0].checked = isAllSelected;
        }
    }
});

$.extend(FR.FEConfigure, {
    _init: function( options ) {
        var self = this;
        this.tableName = options.tableName;
        this.md5TableName = options.md5TableName;

        this.oneTableSettingPane = new FR.FEConfigure.BIOneTableSettingCard({
            domParent: $("body")
        });

        function resizeFn( event ) {
            if( event.target != window) {
                return;
            }

            self.oneTableSettingPane.layoutComponent();
        }
        window.onresize = resizeFn;

        FR.FEConfigure.switch2SelectedCellExcelSetting( this.tableName,this.md5TableName ,"Excel数据集");
    },

    _init_imported_excel : function(full_file_name){
        var self = this;
        //加载数据放到这里，避免在打开tab的时候卡
        $("body").__buildZIndexLoadingMask__($("body").css("z-index") + 1, "正在加载", 0);
        self.flag = false;
        setTimeout(function() {
            if(!self.flag) {
                $("body").__releaseZIndexLoadingMask__();
                $("body").__buildZIndexLoadingMask__($("body").css("z-index") + 1, "亲，您的数据量比较大，请耐心等待哦", 0);
            }
        }, 20000);
        FR.ajax({
            url:FR.servletURL + "?op=fbi_no_need_login&cmd=fe_get_imported_excel" + "&num=" + Math.random(),
            type:"POST",
            data: {
                full_file_name: full_file_name
            },
            timeout:600000,
            error:function(res, status) {
                FR.Msg.toast("本次未连接成功");
                $("body").__releaseZIndexLoadingMask__();
            },
            success:function(data, status, res) {
                self.flag = true;
                var _tag = FR.jsonDecode(res.responseText);
                try{
                    self.fe_init_import_excel_conf(_tag.tag, full_file_name);
                }catch(e){
                    console.log(e);
                }
                $("body").__releaseZIndexLoadingMask__();
            }
        });
    },

    fe_init_import_excel_conf : function(tag, full_file_name){
        var self = this;
        var $rightDiv = $("#fieldnames").css({"overflow":"auto"});
        $rightDiv.css({"width": "248px","border":"1px solid #c8c8c8","top": "10px","right":"10px","bottom":"70px"});

        $("#importexcel").css({"border":"1px solid #c8c8c8","top": "10px","bottom":"70px"});
        var $div = $("<div id='tag_left'></div>").html(tag).appendTo($("#importexcel"));
        $div.css({"overflow": "auto","height" : "100%", "position": "relative"});

        var $fieldsDiv = $("<div id='fields_type'></div>").appendTo($rightDiv);
        var full_name = full_file_name;
        var table = $("table.x-table", $div);
        if (table && table.length > 0) {
            self.changeTable($(table));
        }

        $(".x-table").addClass("preview-table");
        this.$fieldTable = $("<table>").css({"font-family": "Microsoft YaHei"});
        var tds;
        var trs;
        try{
            tds = $("td", $div);
            trs = $("tr", $div);
        }catch (e){
            $("body").__releaseZIndexLoadingMask__();
            alert("数据量太大啦，不是我们的性能不好，前台浏览器已经内存溢出了...");
            return;
        }
        var column_num = (tds.length)/(trs.length);

        var array = [];
        array[0] = $(tds[0]).attr("id");
        array[1] = $(tds[tds.length - 1]).attr("id");

        var s_X = $(tds[0]).offset().top;
        var s_Y = $(tds[0]).offset().left;

        var e_X = $(tds[tds.length - 1]).offset().top;
        var e_Y = $(tds[tds.length - 1]).offset().top;

        var column_row_s_ =  FR.cellStr2ColumnRow( array[0].split("-")[0] );
        var column_row_e_ =  FR.cellStr2ColumnRow( array[1].split("-")[0] );

        var ids = [];
        ids.push(column_row_s_);
        ids.push(column_row_e_);
        var ids_all = ids;

        var is_all = true;
        var saveTable = FR.jsonDecode(FR.rpc4FEConfigure("get_selected_excel_JSON", true,{"ids":ids ,"full_file_name":full_file_name,"is_all" : is_all}));
        var saveTable_all = saveTable;
        self.fillRightDiv(ids, tds, $fieldsDiv, saveTable);
        var $bottomDiv = $("<div id = 'bottomDiv'></div>").css({"position":"fixed","bottom": "30px","height": "30px","width": "278"}).appendTo($rightDiv);
        this.reChooseButton = new FR.BITextButton({
            domParent: $div,
            text4Button: "选择模式",
            height: 26,
            width: 48,
            style: 'white',
            class4Widget: "save",
            handler:function() {
                $commentDiv.hide();
                self.defaultButton.element.css({"display":"block"});
                self.reChooseButton.element.css({"display":"none"});
                $("#importexcel").__buildZIndexLoadingMask__($("#importexcel").css("z-index") + 1, "正在加载", 0);
                FR.ajax({
                    url:FR.servletURL + "?op=fbi_no_need_login&cmd=fe_get_full_imported_excel" + "&num=" + Math.random(),
                    type:"POST",
                    data: {
                        full_file_name : full_file_name
                    },
                    timeout:60000,
                    error:function(res, status) {
                        FR.Msg.toast("本次未连接成功");
                        $("#importexcel").__releaseZIndexLoadingMask__();
                    },
                    success:function(data, status, res) {
                        tag_ = FR.jsonDecode(res.responseText);
                        $div.empty();
                        $div.html(tag_.tag);
                        var table_ = $("table.x-table", $div);
                        if (table_ && table_.length > 0) {
                            self.changeTable($(table_));
                        }
                        $(".x-table").addClass("preview-table");
                        $fieldsDiv.empty();
                        self.fillRightDiv(ids_all, tds, $fieldsDiv, saveTable_all);
                        ids = [];

                        $(".x-table").addClass("preview-table");
                        this.$fieldTable = $("<table>").css({"font-family": "Microsoft YaHei"}).attr({"title": "点击设置选择数据起点"});
                        var tds;
                        var trs;
                        try{
                            tds = $("td", $div);
                            trs = $("tr", $div);
                        }catch (e){
                            $("body").__releaseZIndexLoadingMask__();
                            alert("数据量太大，格子太多，前台浏览器已经内存溢出了，建议不要切换到选择模式，使用全部数据.");
                            return;
                        }
                        column_num = (tds.length)/(trs.length);

                        array[0] = $(tds[0]).attr("id");
                        array[1] = $(tds[tds.length - 1]).attr("id");

                        for (var i = 0; i < tds.length; i++) {
                            var td = tds[i];
                            if ($(td).attr("id")) {
                                $(td).css({'font-family': 'Microsoft YaHei', 'cursor': 'cell'}).addClass("fr-bi-excel-setting-td fr-bi-excel-setting-td-selected");
                            }
                        }

                        //绑定鼠标点击事件
                        $div.click( function(e){
                            if (e.target.tagName !== "TD") {
                                return;
                            };
                            var id = $(e.target).attr("id");
                            if(array.length >= 2){
                                $div.find("table").attr("title", "点击设置选择数据起点");
                                array = [];
                                for (var i = 0; i < tds.length; i++) {
                                    var td = tds[i];
                                    if ($(td).hasClass("fr-bi-excel-setting-td-selected")) {
                                        $(td).removeClass("fr-bi-excel-setting-td-selected");
                                    }
                                }
                                if(self.confirmButton){
                                    self.confirmButton.setEnable(false);
                                }
                            }
                            if(array.length == 0){
                                $div.find("table").attr({"title": "点击设置选择数据终点"});
                                array[0] = id;
                                $(e.target).addClass("fr-bi-excel-setting-td-selected");
                                self.reChooseButton.setEnable(true);
                            }else if(array.length == 1){
                                $div.find("table").attr({"title": "点击设置选择数据起点"});
                                array[1] = id;
                                $(e.target).addClass("fr-bi-excel-setting-td-selected");
                            }
                            if(array.length == 2){
                                var column_row_s =  FR.cellStr2ColumnRow( array[0].split("-")[0] );
                                var column_row_e =  FR.cellStr2ColumnRow( array[1].split("-")[0] );
                                var s_col = column_row_s.col + 1;
                                var s_row = column_row_s.row + 1;
                                var e_col = column_row_e.col + 1;
                                var e_row = column_row_e.row + 1;

                                var start_p = s_row > 0?s_col + column_num * ( s_row - 1):s_col;
                                var end_p = e_col * e_row;

                                for(var i = 0; i <= e_row - s_row ; i ++){
                                    for(var j = 0; j <= e_col - s_col ; j ++){
                                        var td = tds[start_p + i*column_num + j - 1];
                                        $(td).addClass("fr-bi-excel-setting-td-selected");
                                    }
                                }

                                ids = [];
                                ids.push(column_row_s);
                                ids.push(column_row_e);

                                saveTable = FR.jsonDecode(FR.rpc4FEConfigure("get_selected_excel_JSON", true,{"ids":ids ,"full_file_name":full_file_name}));
                                is_all = false;
                                $fieldsDiv.empty();
                                self.fillRightDiv(ids, tds, $fieldsDiv, saveTable);

                                if(self.confirmButton){
                                    self.confirmButton.setEnable(true);
                                    self.reChooseButton.setEnable(true);
                                }

                            }

                            self.refreshFieldById(id);
                            self.refreshExcelTd();

                        });

                        //绑定mousemove事件来显示
                        $div.mousemove( function( e) {
                            if(e.target.tagName !== "TD") {
                                if( self.$fieldTip ) {
                                    self.$fieldTip.hide();
                                }
                                return;
                            }
                            self.refreshTip(e.clientX-243,  e.clientY + 20 );
                        });

                        $div.mouseout( function( e) {
                            if( self.$fieldTip ) {
                                self.$fieldTip.hide();
                            }
                        });
                        $("#importexcel").__releaseZIndexLoadingMask__();
                    }
                });
            }
        });

        this.reChooseButton.element.attr("title", "点击从左边展示的数据中选择你需要的部分数据，数据量超过10M建议不要使用.");
        var $btReDiv = $("<div id='btReDiv'></div>").css({"margin-left": "20px", "height" : "26px","float":"left", "width" : "70px"}).appendTo($bottomDiv);
        this.reChooseButton.element.addClass("float-left").appendTo( $btReDiv );

        this.defaultButton = new FR.BITextButton({
            domParent: $div,
            text4Button: "默认模式",
            height: 26,
            width: 48,
            style : 'white',
            class4Widget: "save",
            handler:function() {
                $commentDiv.show();
                $div.unbind("click");
                self.confirmButton.setEnable(true);
                self.defaultButton.element.css({"display":"none"});
                self.reChooseButton.element.css({"display":"block"});
                is_all = true;
                $("#importexcel").__buildZIndexLoadingMask__($("#importexcel").css("z-index") + 1, "正在加载", 0);
                FR.ajax({
                    url:FR.servletURL + "?op=fbi_no_need_login&cmd=fe_get_part_imported_excel" + "&num=" + Math.random(),
                    type:"POST",
                    data: {
                        ids : ids_all,
                        full_file_name : full_file_name,
                        is_all : is_all
                    },
                    timeout:60000,
                    error:function(res, status) {
                        FR.Msg.toast("本次未连接成功");
                        $("#importexcel").__releaseZIndexLoadingMask__();
                    },
                    success:function(data, status, res) {
                        var tag__ = FR.jsonDecode(res.responseText);

                        saveTable = FR.jsonDecode(FR.rpc4FEConfigure("get_selected_excel_JSON", true,{"ids":ids_all ,"full_file_name":full_file_name,"is_all" : is_all}));
                        $div.empty();
                        $div.html(tag__.tag);

                        tds = $("td", $div);
                        trs = $("tr", $div);
                        if ($.browser.msie && parseFloat($.browser.version) < 11.0){
                            var tr_last = trs[trs.length - 1];
                            $(tr_last).css({"display": "none"});
                            trs.splice(trs.length - 1,1);
                            tds.splice(tds.length - column_num, column_num);
                        }
                        var table_ = $("table.x-table", $div);
                        if (table_ && table_.length > 0) {
                            self.changeTable($(table_));
                        }
                        $(".x-table").addClass("preview-table");
                        $fieldsDiv.empty();
                        self.fillRightDiv(ids, tds, $fieldsDiv, saveTable_all);
                        ids = [];
                        $(".x-table").addClass("preview-table");
                        this.$fieldTable = $("<table>").css({"font-family": "Microsoft YaHei"});
                        $("#importexcel").__releaseZIndexLoadingMask__();
                    }
                });
            }
        });
        this.defaultButton.element.attr("title", "默认将所有数据都参与分析,但在左边只会展示前50行，注意，第一行将作为列名.");
        this.defaultButton.element.css({"display":"none"}).addClass("float-left").appendTo($btReDiv);

        this.confirmButton = new FR.BITextButton({
            domParent: $div,
            text4Button: "生成分析",
            height: 26,
            width: 48,
            style : 'white',
            class4Widget: "save",
            handler:function() {
                $("body").__buildZIndexLoadingMask__($("body").css("z-index") - 1, "正在生成分析", 0);
                FR.ajax({
                    url:FR.servletURL + "?op=fbi_no_need_login&cmd=fe_synchronous_table_infor_in_package" + "&num=" + Math.random(),
                    type:"POST",
                    data: {
                        new_table: saveTable,
                        is_add : true,
                        is_all : is_all
                    },
                    timeout:120000,
                    error:function(res, status) {
                        FR.Msg.toast("本次未连接成功");
                        $("body").__releaseZIndexLoadingMask__();
                        $("body").__buildZIndexFreshMask__( $("body").css("z-index")+1,"本次加载失败，点击刷新重试",0, function(){
                            self.saveTableInfor();
                            $("body").__releaseZIndexLoadingMask__();
                        });
                    },
                    success:function(data, status, res) {
                        var isGenerateCube = "";
                        var checker = function(){
                            isGenerateCube = FR.rpc4biConfigure("get_cube_status", false, "");
                            if(!isGenerateCube.cubeStatus) {
                                $("body").__releaseZIndexLoadingMask__();
                                var fs = window.top.FS;
                                $div = $("body",parent.parent.document).find('#fe_data');
                                var _data = FR.jsonDecode(res.responseText);
                                fs.fill_data_fs($div);
                                if(saveTable.tab_name != ''){
                                    fs._removeTab(saveTable.tab_name);
                                }else{
                                    fs._removeTab( saveTable.full_file_name.substring(0, 18) );
                                }
                                fs.newReport_( saveTable.full_file_name , saveTable.table_name, _data["md5_table_name"] );
                                clearInterval(timer);
                            }
                        };
                        timer = setInterval(checker, 1500);
                    }
                });
            }
        });

        var $btDiv = $("<div id='btDiv'></div>").css({"margin-left": "40px", "height" : "26px","float":"left", "width" : "70px"}).appendTo($bottomDiv);
        this.confirmButton.element.css({"margin-left": "20px"}).addClass("float-left").appendTo( $btDiv );

        //绑定mousemove事件来显示
        $div.mousemove( function( e) {
            if(e.target.tagName !== "TD") {
                if( self.$fieldTip ) {
                    self.$fieldTip.hide();
                }
                return;
            }
            self.refreshTip(e.clientX-243,  e.clientY + 20 );
        });

        $div.mouseout( function( e) {
            if( self.$fieldTip ) {
                self.$fieldTip.hide();
            }
        });

        var $commentDiv = $("<div id='commentDiv'></div>").css({"margin-left": "20px", "height" : "26px","float":"left", "width" : "600px","font-size":"14px","margin-top":"15px"}).appendTo($("#importexcel"));
        $("<span>").text("提示：1、默认把首行作为列名；2、只列出表格的前50行；3、支持的文件类型：xls,csv,xlsx").appendTo($commentDiv);
    },



    fillRightDiv : function(ids, tds, $fieldsDiv, saveTable){
        var self = this;
        self.detailFieldInfor = new FR.FEConfigure.TableDetailFieldInfro({
            domParent: $fieldsDiv ,
            getTableDetailData: function( isRefresh ) {
                $(".fr-bi-one-table-base-structure-center-west-box").__buildZIndexLoadingMask__($(".fr-bi-one-table-base-structure-center-west-box").css("z-index") - 1, "正在加载", 0);
                var detailInfro = this;
                FR.ajax({
                    url:FR.servletURL + "?op=fr_bi_configure&cmd=get_fields_new_table&random=" + Math.random(),
                    data: {
                        table: saveTable
                    },
                    type: "POST",
                    error:function(res, status) {
                        detailInfro.onError();
                        $(".fr-bi-one-table-base-structure-center-west-box").__releaseZIndexLoadingMask__();
                        $(".fr-bi-one-table-base-structure-center-west-box").__buildZIndexFreshMask__( self.element.css("z-index")+1,"本次加载失败，点击刷新重试",0, function(){
                            self.createCenterWest($west);
                            $(".fr-bi-one-table-base-structure-center-west-box").__releaseZIndexLoadingMask__();
                        });
                    },
                    success:function(data, status, res) {
                        var _data = FR.jsonDecode(res.responseText);
                        if( res.responseText == "{}" || "__ERROR_MESSAGE__" in _data) {
                            this.error();
                            return;
                        }
                        self.detailFieldInfor.populateByData( saveTable );
                        detailInfro.onSuccess( saveTable );

                        $(".fr-bi-one-table-base-structure-center-west-box").__releaseZIndexLoadingMask__();
                    }
                });
            }
        });
        self.detailFieldInfor.populate( saveTable );

        self.detailFieldInfor.populateByTableInfor();
    },

    changeTable: function ($table) {
        $table.addClass("fr-bi-excel-table");

        this.createColumnDiv($table);
//        this.createRowDiv($table);
    },

    //增加123....列
    createColumnDiv: function ($table) {
        $table.parent().css({"position": "absolute", "left": "27px", "top": "18px"});
    },

    //增加ABCD....行
    createRowDiv: function ($table) {

        var colGroup = $("<div>").addClass("fr-bi-excel-table-row");

        $("col", $table).each(function (index, item) {

            var $td = $("<div>").css("width", $(item).outerWidth() - 1).text(FR.digit2Letter(index + 1));
            if (index == 0) {
                $td.addClass("fr-bi-excel-table-row-td-first")
            } else {
                $td.addClass("fr-bi-excel-table-row-td")
            }

            $td.appendTo(colGroup);
        });

        colGroup.prependTo(this.$excelCenter);
    },

    //刷新excelTd格子的被选中状态
    refreshExcelTd: function () {
        var fieldPositions = this.updateAllField();

        var ids = [];
        var fieldName = [];
        for (var i = 0; i < fieldPositions.length; i++) {
            ids.push(FR.columnRow2CellStr(fieldPositions[i].column_row));
            fieldName.push(fieldPositions[i].field_name);
        }

        $("td", $("body")).each(function (index, dom) {
            if ($(dom).attr("id") == null) {
                return;
            }

            var id = $(dom).attr("id").split("-")[0];
            var index = $.inArray(id, ids);
            if (index > -1) {
                $(dom).addClass("fr-bi-excel-setting-td-setted");
                $(dom).attr("title", fieldName[index]);
            } else {
                $(dom).removeClass("fr-bi-excel-setting-td-setted");
                $(dom).removeAttr("title");
            }
        })
    },

    //获取字段的columnRow信息
    updateAllField: function () {
        var fieldPosition = [];

        $("tr", this.$fieldTable).each(function (domIndex, dom) {
            if ($(dom).attr("crId")) {
                var ob = {
                    field_name: $(dom).attr("name"),
                    column_row: FR.cellStr2ColumnRow($(dom).attr("crId"))
                }

                fieldPosition.push(ob);
            }
        });

        return fieldPosition;
    },

    //在点击excel的td的时候刷新field选项
    refreshFieldById: function (id) {
        var fieldTr = $("tr", this.$fieldTable);

        if (fieldTr.length == 0) {
            return;
        }

        var len = fieldTr.length;
    },

    //当tr被设置之后
    setTrSetted: function (tr, id) {
        //修改其他field的位置
        var s = id.split("-")[0];
        $("tr", this.$fieldTable).each(function (index, item) {
            if ($(item).attr("crId") == s) {
                $($(item).children()[1]).text("");
                $(item).removeAttr("crId");
            }
        });
        var dom = $(tr);
        dom.addClass("fr-bi-excel-setting-field-setted");
        dom.attr("crId", s);
    },

    refreshTip: function (x, y) {

        if (!this.$fieldTip) {
            this.$fieldTip = $("<div>").css({"position": "absolute", "background-color": "rgb(5,148,217)"}).appendTo($("body"));
        }

        this.$fieldTip.show();

        var name = $(".fr-bi-excel-setting-field-selected", this.$fieldTable).attr("name");
    },

    rpc4biConfigure : function(cmd, return_string, extraData) {
        var ob = {
            url : FR.servletURL + "?op=fr_bi_configure&cmd=" + cmd +"&_="+Math.random()
        }
        if ( return_string ) {
            ob["returnString"] = true;
        }
        if (extraData != null) {
            ob["data"] = FR.clone(extraData);
        }

        return FR.SyncAjax(ob);
    },

    rpc4FEConfigure : function(cmd, return_string, extraData) {
        var ob = {
            url : FR.servletURL + "?op=fbi_no_need_login&cmd=" + cmd +"&_="+Math.random()
        }
        if ( return_string ) {
            ob["returnString"] = true;
        }
        if (extraData != null) {
            ob["data"] = FR.clone(extraData);
        }

        return FR.SyncAjax(ob);
    },

    prepare:function() {
        var self = this;

        if (this.saved_all_roles == null) {
            this.saved_all_roles = {};
            $.extend(this.saved_all_roles, {
                COMPANY:{
                    roles:[], nameFunction:function(role) {
                        return role["departmentname"] + "的" + role["postname"]
                    }
                },
                CUSTOM:{
                    roles:[], nameFunction:function(role) {
                        return role["text"]
                    }
                }
            })
            FR.ajax({
                url:FR.servletURL + "?op=fs_set&cmd=auth_getallrole" + "&_=" + Math.random(),
                type:"POST",
                async: false,
                complete:function(res, status) {
                    var rolesFromServer = FR.jsonDecode(res.responseText);

                    $.each(rolesFromServer, function(idx, role) {
                        var rt = role["text"] != null ? FR.BIConstants.ROLE_TYPE_CUSTOM : FR.BIConstants.ROLE_TYPE_COMPANY;

                        if (role["text"] != null) {
                            self.saved_all_roles["CUSTOM"]["roles"].push($.extend(role, {
                                roleType: FR.BIConstants.ROLE_TYPE_CUSTOM
                            }));
                        } else {
                            self.saved_all_roles["COMPANY"]["roles"].push($.extend(role, {
                                roleType: FR.BIConstants.ROLE_TYPE_COMPANY
                            }));
                        }
                    })
                }
            })
        }

        return this.saved_all_roles;
    },

    getAllRoles:function() {
        var structure = this.prepare();
        var allRoles = [];

        $.each(["COMPANY", "CUSTOM"], function(idx, type) {
            allRoles = allRoles.concat(structure[type]["roles"]);
        })

        return allRoles;
    },

    getRoleNameByRoleTypeAndId: function(roleType, roleId) {
        var structure = this.prepare();
        var typedRoleOb = structure[roleType == FR.BIConstants.ROLE_TYPE_COMPANY ? "COMPANY" : "CUSTOM"];
        for (var ri = 0; ri < typedRoleOb["roles"].length; ri++) {
            var role = typedRoleOb["roles"][ri];
            if (role["id"] == roleId) {
                return typedRoleOb["nameFunction"].call(this, role);
            }
        }

        return null;
    },

    switch2SelectedCellExcelSetting: function(full_file_name, md5_table_name, packageName){
        var fe_name = "";
        var lastChar = full_file_name.substring(full_file_name.length - 1, full_file_name.length );
        if(lastChar == 's' || lastChar == 'v'){
            if(full_file_name.substring(full_file_name.length - 7,full_file_name.length - 4) == "_fe"){
                fe_name = full_file_name;
            }else{
                fe_name = full_file_name.substring(0,full_file_name.length - 4) + "_fe.xls";
            }
        }else{
            if(full_file_name.substring(full_file_name.length - 8,full_file_name.length - 5) == "_fe"){
                fe_name = full_file_name;
            }else{
                fe_name = full_file_name.substring(0,full_file_name.length - 5) + "_fe.xlsx";
            }
        }
//        this.mainCardLayout.showCardByName(FR.BIConstants.CONF_TABLE_SET);

        var tableInfor = this.rpc4FEConfigure("bi_get_table_detail_infor_from_excel", false, {fe_name: fe_name, md5_table_name: md5_table_name});
        this.oneTableSettingPane.populate( tableInfor, packageName, false );
    },

    getFieldTypeFromClassType: function( classType ) {
        if( classType == 5 ) {
            return FR.BIConstants.DIMENSION_TYPE_STRING;
        } else if( classType == 2) {
            return FR.BIConstants.DIMENSION_TYPE_NUMBER;
        } else {
            return FR.BIConstants.DIMENSION_TYPE_DATE;
        }

    }
});

FR.BIConstants.FIELD_TYPE_ITEMS = [{
    value: FR.BIConstants.DIMENSION_TYPE_STRING, text_class: "fr-bi-config-type-string", text:"文本", combo_class: "fr-bi-config-type-string-combo"
}, {
    value: FR.BIConstants.DIMENSION_TYPE_NUMBER, text_class: "fr-bi-config-type-num", text:"数值", combo_class: "fr-bi-config-type-number-combo"
}, {
    value: FR.BIConstants.DIMENSION_TYPE_DATE, text_class: "fr-bi-config-type-date", text:"日期", combo_class: "fr-bi-config-type-date-combo"
}];