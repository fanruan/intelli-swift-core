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

    reqTablesByPackId: function (packId, callback) {
        BI.requestAsync("fr_bi_configure", "get_brief_tables_of_one_package", {id: packId}, function (res) {
            callback(res);
        })
    },

    reqFieldsByTableId: function (tableId, callback) {
        BI.requestAsync("fr_bi_configure", "get_fields_4_relation_in_table", {id: tableId}, function (res) {
            callback(res);
        })
    },

    reqTablesDetailInfoByPackId: function (packName, callback) {
        BI.requestAsync("fr_bi_configure", "get_detail_tables_of_one_package", {name: packName}, function (res) {
            callback(res);
        })
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

    reqConnectionName: function (callback) {
        BI.requestAsync("fr_bi_configure", "get_connection_names", "", function (res) {
            callback(res);
        });
    },

    reqTablesByConnectionName: function (connectionName, callback) {
        BI.requestAsync("fr_bi_configure", "get_all_translated_tables_by_connection", {connectionName: connectionName}, function (res) {
            callback(res);
        });
    },

    reqTablesDetailInfoByTables: function (tables, callback) {
        BI.requestAsync("fr_bi_configure", "get_field_info_4_new_tables", {tables: tables}, function (res) {
            callback(res);
        });
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

    reqWidgetSettingByData: function (data, callback) {
        BI.requestAsync("fr_bi_dezi", "widget_setting", data, function (res) {
            callback(res);
        });
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

    reqRelationAndTransByTables: function (data, callback) {
        BI.requestAsync("fr_bi_configure", "import_db_table_connection", data, function (res) {
            callback(res);
        })
    },

    reqFieldsDataByData: function (data, callback) {
        BI.requestAsync("fr_bi_configure", "get_field_value", data, function (res) {
            callback(res);
        });
    },

    reqFieldsDataByFieldId: function (data, callback) {
        BI.requestAsync("fr_bi_configure", "get_field_value_by_field_id", data, function (res) {
            callback(res);
        });
    },

    reqCubeStatusByTable: function (table, callback) {
        BI.requestAsync("fr_bi_configure", "check_generate_cube", {table: table}, function (res) {
            callback(res);
        })
    },

    reqPreviewDataByTableAndFields: function (table, fields, callback) {
        BI.requestAsync("fr_bi_configure", "get_preview_table_conf", {table: table, fields: fields}, function (res) {
            callback(res);
        })
    },

    reqCircleLayerLevelInfoByTableAndCondition: function (table, layerInfo, callback) {
        BI.requestAsync("fr_bi_configure", "create_fields_union", {
            table: table,
            id_field_name: layerInfo.id_field_name,
            parentid_field_name: layerInfo.parentid_field_name,
            divide_length: layerInfo.divide_length,
            fetch_union_length: layerInfo.fetch_union_length
        }, function (res) {
            callback(res);
        });
    },

    reqTestConnectionByLink: function (link, callback) {
        BI.requestAsync("fr_bi_configure", "test_data_link", {linkData: link}, function (res) {
            callback(res);
        })
    },

    reqTestConnectionByLinkName: function (name, callback) {
        BI.requestAsync("fr_bi_configure", "test_data_link_name", {name: name}, function (res) {
            callback(res);
        })
    },

    reqSchemasByLink: function (link, callback) {
        BI.requestAsync("fr_bi_configure", "get_schemas_by_link", {linkData: link}, function (res) {
            callback(res);
        });
    },

    reqNumberFieldMaxMinValue: function (table, fieldName, callback) {
        BI.requestAsync("fr_bi_configure", "number_max_min", {
            table: table,
            fieldName: fieldName
        }, function (res) {
            callback(res);
        })
    },

    reqFieldsInNewTable: function (table, id, callback) {
        BI.requestAsync("fr_bi_configure", "get_fields_new_table", {
            table: table,
            id: id
        }, function (res) {
            callback(res);
        })
    },

    reqTablesOfOnePackage: function (pId, callback) {
        BI.requestAsync("fr_bi_configure", "get_tables_of_one_package", {
            id: pId
        }, function (res) {
            callback(res);
        })
    },

    reqUpdateTablesOfOnePackage: function (data, callback) {
        BI.requestAsync("fr_bi_configure", "update_tables_in_package", data, function (res) {
            callback(res);
        })
    },

    reqSaveFileGetExcelData: function (data, callback) {
        BI.requestAsync("fr_bi_configure", "save_file_get_excel_data", data, function (res) {
            callback(res);
        })
    },

    reqExcelDataByFileName: function (data, callback) {
        BI.requestAsync("fr_bi_configure", "get_excel_data_by_file_name", data, function (res) {
            callback(res);
        })
    },

    reqSaveDataLink: function (data, callback) {
        BI.requestAsync("fr_bi_configure", "modify_data_link", data, function () {
            callback();
        })
    },

    reqCubePath: function (callback) {
        BI.requestAsync("fr_bi_configure", "get_cube_path", {}, function (res) {
            callback(res.cubePath);
        })
    },

    reqCheckCubePath: function (path, callback) {
        BI.requestAsync("fr_bi_configure", "check_cube_path", {fileName: path}, function (res) {
            callback(res.cubePath);
        })
    },

    reqLoginInfoInTableField: function (callback) {
        BI.requestAsync("fr_bi_configure", "get_login_info_in_table_field", {}, function (res) {
            callback(res);
        })
    },

    reqSaveLoginInfoInTableField: function (data, callback) {
        BI.requestAsync("fr_bi_configure", "save_login_info_in_table_field", data, function (res) {
            callback();
        })
    },

    reqServerSetPreviewBySql: function (data, callback) {
        BI.requestAsync("fr_bi_configure", "preview_server_link", data, function (res) {
            callback(res);
        })
    },

    reqAllTemplates: function (callback) {
        BI.requestAsync('fr_bi', 'get_folder_report_list', {}, function (items) {
            callback(items);
        })
    },

    reqWidgetsByTemplateId: function (tId, callback) {
        BI.requestAsync("fr_bi", "get_widget_from_template", {id: tId}, function (data) {
            callback(data);
        });
    },

    reqTranslationsRelationsFields: function (callback) {
        BI.requestAsync("fr_bi_configure", "get_translations_relations_fields_4_conf", {}, function (data) {
            callback(data);
        })
    },

    reqUpdatePreviewSqlResult: function(data, callback){
        BI.requestAsync("fr_bi_configure", "get_preview_table_update", data, function (res) {
            callback(res);
        })
    },

    reqModifyGlobalUpdateSetting: function(data, callback) {
        BI.requestAsync("fr_bi_configure", "modify_global_update_setting_action", data, function(res){
            callback(res);
        })
    },

    reqCubeLog: function(callback) {
        BI.requestAsync("fr_bi_configure", "get_cube_log", {}, function(res){
            callback(res);
        })
    },
    
    reqSavePackageAuthority: function (data, callback) {
        BI.requestAsync("fr_bi_configure", "save_package_authority", data, function (res) {
            callback(res);
        });
    },

    reqAllBusinessPackages: function(callback) {
        BI.requestAsync("fr_bi_configure", "get_all_business_packages", {}, function(res) {
            callback(res);
        });
    },

    getTableNamesOfAllPackages: function(callback) {
        BI.requestAsync("fr_bi_configure", "get_table_names_of_all_packages", {}, function(res) {
            callback(res);
        });
    }
    };
