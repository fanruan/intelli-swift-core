BIConf.AddDataLinkModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BIConf.AddDataLinkModel.superclass._defaultConfig.apply(this, arguments), {

        });
    },

    _static: function(){
        return {
            codeItems: [
                {text: "", value: ""},
                {text: "BIG5", value: "BIG5"},
                {text: "EUC_JP", value: "EUC_JP"},
                {text: "EUC_KR", value: "EUC_KR"},
                {text: "GBK", value: "GBK"},
                {text: "ISO-8859-1", value: "ISO-8859-1"},
                {text: "UTF-8", value: "UTF-8"},
                {text: "UTF-16", value: "UTF-16"},
                {text: "CP850", value: "CP850"}
            ],
            databaseItems: [
                {text: "Oracle", value: "Oracle"},
                {text: "DB2", value: "DB2"},
                {text: "SQL Server", value: "SQL Server"},
                {text: "MySQL", value: "MySQL"},
                {text: "Sybase", value: "Sybase"},
                {text: "Derby", value: "Derby"},
                {text: "Postgre", value: "Postgre"},
                {text: "Others", value: "Others"}
            ],
            urls: {
                "oracle.jdbc.driver.OracleDriver" : "jdbc:oracle:thin:@localhost:1521:databaseName",

                "com.ibm.db2.jcc.DB2Driver":"jdbc:db2://localhost:50000/",

                "com.microsoft.sqlserver.jdbc.SQLServerDriver":"jdbc:sqlserver://localhost:1433;databaseName=",

                "com.mysql.jdbc.Driver":"jdbc:mysql://localhost/",

                "org.gjt.mm.mysql.Driver":"jdbc:mysql://localhost/",

                "com.sybase.jdbc2.jdbc.SybDriver":"jdbc:sybase:Tds:localhost:5000/",

                "org.apache.derby.jdbc.ClientDriver":"jdbc:derby://localhost:1527/",

                "org.postgresql.Driver":"jdbc:postgresql://localhost:5432/",

                "sun.jdbc.odbc.JdbcOdbcDriver":"jdbc:odbc:",

                "org.hsqldb.jdbcDriver":"jdbc:hsqldb:file:[PATH_TO_DB_FILES]",

                "com.inet.tds.TdsDriver":"jdbc:inetdae7:localhost:1433/",

                "COM.cloudscape.JDBCDriver":"jdbc:cloudscape:/cloudscape/",

                "com.internetcds.jdbc.tds.Driver": "jdbc:freetds:sqlserver://localhost/"
            },

            drivers: {
                "Oracle" : [
                    {text: "oracle.jdbc.driver.OracleDriver", value: "oracle.jdbc.driver.OracleDriver", textAlign: "left"}
                ],

                "DB2":[
                    {text: "com.ibm.db2.jcc.DB2Driver", value: "com.ibm.db2.jcc.DB2Driver", textAlign: "left"}
                ],

                "SQL Server":[
                    {text: "com.microsoft.sqlserver.jdbc.SQLServerDriver", value: "com.microsoft.sqlserver.jdbc.SQLServerDriver", textAlign: "left"}
                ],

                "MySQL":[
                    {text: "com.mysql.jdbc.Driver", value: "com.mysql.jdbc.Driver", textAlign: "left"},
                    {text: "org.gjt.mm.mysql.Driver", value: "org.gjt.mm.mysql.Driver", textAlign: "left"}
                ],

                "Sybase":[
                    {text: "com.sybase.jdbc2.jdbc.SybDriver", value: "com.sybase.jdbc2.jdbc.SybDriver", textAlign: "left"}
                ],

                "Derby":[
                    {text: "org.apache.derby.jdbc.ClientDriver", value: "org.apache.derby.jdbc.ClientDriver", textAlign: "left"}
                ],

                "Postgre":[
                    {text: "org.postgresql.Driver", value: "org.postgresql.Driver", textAlign: "left"}
                ],

                "Others":[
                    {text: "sun.jdbc.odbc.JdbcOdbcDriver", value: "sun.jdbc.odbc.JdbcOdbcDriver", textAlign: "left"},
                    {text: "org.hsqldb.jdbcDriver", value: "org.hsqldb.jdbcDriver", textAlign: "left"},
                    {text: "com.inet.tds.TdsDriver", value: "com.inet.tds.TdsDriver", textAlign: "left"},
                    {text: "COM.cloudscape.JDBCDriver", value: "COM.cloudscape.JDBCDriver", textAlign: "left"},
                    {text: "com.internetcds.jdbc.tds.Driver", value: "com.internetcds.jdbc.tds.Driver", textAlign: "left"}
                ]
            },

            "checkDataLinkName": function(name){
                var self = this;
                var links = Data.SharingPool.get("links");
                var isValid = true;
                BI.some(links, function(id, link){
                    if(link.name === name && id !== self.get("id")){
                        isValid = false;
                        return true;
                    }
                });
                return isValid;
            }
        }
    },

    _init : function() {
        BIConf.AddDataLinkModel.superclass._init.apply(this, arguments);
    },

    local: function(){
        if(this.has("test")){
            this.get("test");
            return true;
        }
        return false;
    },

    change: function(){
        var json = {
            name: this.get("name"),
            driver: this.get("driver"),
            url: this.get("url"),
            user: this.get("user"),
            password: this.get("password"),
            originalCharsetName: this.get("originalCharsetName"),
            newCharsetName: this.get("newCharsetName")
        };
        this.patch({
            timeout: 600000,
            data: {
                actionType: "update",
                linkData: json,
                oldName: this.oldName
            }
        });
    },

    patchURL: function(){
        return this.cmd("modify_data_link");
    },

    updateURL: function(){
        return this.cmd("test_data_link");
    },

    refresh: function(){
        this.oldName = this.get("name");
    }
});