Data.Req = BIReq = {

    reqTableById: function (id, callback) {
        BI.requestAsync("fr_bi_base", "get_table", {id: id}, function (res) {
            callback(res);
        });
    },

    reqPreviewTableData4DeziByTableId: function (tableId, callback) {
        BI.requestAsync("fr_bi_dezi", "get_preview_table_data", {table_id: tableId}, function (res) {
            callback(res);
        })
    },

    reqTablesByPackId: function (packId, callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_brief_tables_of_one_package", {id: packId}, function (res) {
            callback(res);
        }, complete)
    },

    reqTablesDetailInfoByPackId: function (packName, callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_detail_tables_of_one_package", {name: packName}, function (res) {
            callback(res);
        }, complete)
    },

    reqTableByConnSchemaTName: function (connName, schemaName, tableName, callback) {
        var args = {
            connection_name: connName,
            schema_name: schemaName,
            table_name: tableName
        };
        BI.requestAsync("fr_bi_configure", "get_table_field_by_table_info", args, function (res) {
            callback(res);
        })
    },

    reqConnectionName: function (callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_connection_names", "", function (res) {
            callback(res);
        }, complete);
    },

    reqTablesByConnectionName: function (connectionName, callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_all_translated_tables_by_connection", {connectionName: connectionName}, function (res) {
            callback(res);
        }, complete);
    },

    reqTablesDetailInfoByTables: function (tables, callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_field_info_4_new_tables", {tables: tables}, function (res) {
            callback(res);
        }, complete);
    },

    reqPackage: function (callback) {
        BI.requestAsync("fr_bi_configure", "get_accessable_packages", "", function (res) {
            callback(res);
        });
    },

    reqPakageAndGroup: function (callback) {
        return BI.requestAsync("fr_bi_configure", "get_business_package_group", {}, callback);
    },
    reqPakageAndGroupSync: function () {
        return BI.requestSync("fr_bi_configure", "get_business_package_group", {});
    },

    reqAllTablesByConnection: function (data, callback) {
        BI.requestAsync("fr_bi_configure", "get_all_translated_tables_by_connection", data, callback);
    },

    reqWidgetSettingByData: function (data, callback, complete) {
        BI.requestAsync("fr_bi_dezi", "widget_setting", data, function (res) {
            callback(res);
        }, complete);
    },

    reqDeziNumberFieldMinMaxValueByfieldId: function (data, callback, complete) {
        BI.requestAsync("fr_bi_dezi", "dezi_get_field_min_max_value", data, function (res) {
            callback(res);
        }, complete);
    },

    reqRelationsByTableIds: function (data, callback) {
        BI.requestAsync("fr_bi_configure", "import_db_table_connection", data, function (res) {
            callback(res);
        })
    },

    reqTransByTableIds: function (tableIds, callback) {
        BI.requestAsync("fr_bi_configure", "get_trans_from_db", {tables: tableIds}, function (res) {
            callback(res);
        })
    },

    reqRelationAndTransByTables: function (data, callback, complete) {
        BI.requestAsync("fr_bi_configure", "import_db_table_connection", data, function (res) {
            callback(res);
        }, complete)
    },

    reqFieldsDataByData: function (data, callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_field_value", data, function (res) {
            callback(res);
        }, complete);
    },

    reqFieldsDataByFieldId: function (data, callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_field_value_by_field_id", data, function (res) {
            callback(res);
        }, complete);
    },

    reqCubeStatusByTable: function (table, callback, complete) {
        BI.requestAsync("fr_bi_configure", "check_generate_cube", {table: table}, function (res) {
            callback(res);
        }, complete)
    },

    reqPreviewDataByTableAndFields: function (table, fields, callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_preview_table_conf", {table: table, fields: fields}, function (res) {
            callback(res);
        }, complete)
    },

    reqCircleLayerLevelInfoByTableAndCondition: function (table, layerInfo, callback, complete) {
        BI.requestAsync("fr_bi_configure", "create_fields_union", {
            table: table,
            id_field_name: layerInfo.id_field_name,
            parentid_field_name: layerInfo.parentid_field_name,
            divide_length: layerInfo.divide_length,
            fetch_union_length: layerInfo.fetch_union_length
        }, function (res) {
            callback(res);
        }, complete);
    },

    reqTestConnectionByLink: function (link, callback, complete) {
        BI.requestAsync("fr_bi_configure", "test_data_link", {linkData: link}, function (res) {
            callback(res);
        }, complete)
    },

    reqTestConnectionByLinkName: function (name, callback, complete) {
        BI.requestAsync("fr_bi_configure", "test_data_link_name", {name: name}, function (res) {
            callback(res);
        }, complete)
    },

    reqSchemasByLink: function (link, callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_schemas_by_link", {linkData: link}, function (res) {
            callback(res);
        }, complete);
    },

    reqNumberFieldMaxMinValue: function (table, fieldName, callback, complete) {
        BI.requestAsync("fr_bi_configure", "number_max_min", {
            table: table,
            fieldName: fieldName
        }, function (res) {
            callback(res);
        }, complete)
    },

    reqFieldsInNewTable: function (table, id, callback) {
        BI.requestAsync("fr_bi_configure", "get_fields_new_table", {
            table: table,
            id: id
        }, function (res) {
            callback(res);
        })
    },

    reqTablesOfOnePackage: function (pId, callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_tables_of_one_package", {
            id: pId
        }, function (res) {
            callback(res);
        }, complete)
    },

    reqUpdateTablesOfOnePackage: function (data, callback, complete) {
        BI.requestAsync("fr_bi_configure", "update_tables_in_package", data, function (res) {
            callback(res);
        }, complete)
    },

    reqSaveFileGetExcelData: function (data, callback, complete) {
        BI.requestAsync("fr_bi_configure", "save_file_get_excel_data", data, function (res) {
            callback(res);
        }, complete)
    },

    reqExcelDataByFileName: function (data, callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_excel_data_by_file_name", data, function (res) {
            callback(res);
        }, complete)
    },

    reqSaveDataLink: function (data, callback, complete) {
        BI.requestAsync("fr_bi_configure", "modify_data_link", data, function () {
            callback();
        }, complete)
    },

    reqCubePath: function (callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_cube_path", {}, function (res) {
            callback(res.cubePath);
        }, complete)
    },

    reqCheckCubePath: function (path, callback, complete) {
        BI.requestAsync("fr_bi_configure", "check_cube_path", {fileName: path}, function (res) {
            callback(res.cubePath);
        }, complete)
    },

    reqSaveCubePath: function (path, callback, complete) {
        BI.requestAsync("fr_bi_configure", "set_cube_path", {fileName: path}, function (res) {
            callback(res);
        }, complete)
    },

    reqSaveLoginField: function (data, callback, complete) {
        BI.requestAsync("fr_bi_configure", "save_login_field", data, function (res) {
            callback();
        }, complete)
    },

    reqServerSetPreviewBySql: function (data, callback, complete) {
        BI.requestAsync("fr_bi_configure", "preview_server_link", data, function (res) {
            callback(res);
        }, complete)
    },

    reqAllTemplates: function (callback, complete) {
        BI.requestAsync('fr_bi', 'get_folder_report_list_4_reuse', {}, function (items) {
            callback(items);
        }, complete)
    },

    reqAllReportsData: function (callback, complete) {
        BI.requestAsync("fr_bi", "get_all_reports_data", {}, function (data) {
            callback(data);
        }, complete);
    },

    reqWidgetsByTemplateId: function (tId, callback, complete) {
        BI.requestAsync("fr_bi", "get_widget_from_template", {id: tId}, function (data) {
            callback(data);
        }, complete);
    },

    reqTranslationsRelationsFields: function (callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_translations_relations_fields_4_conf", {}, function (data) {
            callback(data);
        }, complete)
    },

    reqUpdatePreviewSqlResult: function (data, callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_preview_table_update", data, function (res) {
            callback(res);
        }, complete)
    },

    reqModifyGlobalUpdateSetting: function (data, callback, complete) {
        BI.requestAsync("fr_bi_configure", "modify_global_update_setting_action", data, function (res) {
            callback(res);
        }, complete)
    },

    reqCubeLog: function (callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_cube_log", {}, function (res) {
            callback(res);
        }, complete)
    },

    reqSavePackageAuthority: function (data, callback, complete) {
        BI.requestAsync("fr_bi_configure", "save_package_authority", data, function (res) {
            callback(res);
        }, complete);
    },

    reqAllBusinessPackages: function (callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_all_business_packages", {}, function (res) {
            callback(res);
        }, complete);
    },

    getTableNamesOfAllPackages: function (callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_table_names_of_all_packages", {}, function (res) {
            callback(res);
        }, complete);
    },

    reqGenerateCubeByTable: function (tableInfo, callback, complete) {
        BI.requestAsync("fr_bi_configure", "set_cube_generate", {
                // connectionName: table.connection_name,
                // tableName: table.table_name,
                // tableId: table.id
                baseTableId: tableInfo.baseTable.id,
                isETL: tableInfo.isETL,
                ETLTableId: tableInfo.ETLTable==undefined?"":tableInfo.ETLTable.id
            },
            function (res) {
                callback(res);
            }, complete)
        ;
    },
    reqGenerateCube: function (callback, complete) {
        BI.requestAsync("fr_bi_configure", "set_cube_generate", {}, function (res) {
            callback(res);
        }, complete);
    },

    reqPrimaryTablesByTable: function (table, callback, complete) {
        BI.requestAsync("fr_bi_configure", "get_primary_tables_by_table", table, function (res) {
            callback(res);
        }, complete);
    },

    reqGetChartPreStyle: function () {
        return BI.requestSync('fr_bi_base', 'get_config_setting', null);
    }
};
