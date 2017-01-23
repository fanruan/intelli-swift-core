/**
 * 事件集合
 * @class FR.Events
 */
$.extend(FR, {
    Events: {

        /**
         * @static
         * @property 清除选择
         */
        NOSELECT: 'NOSELECT',

        /**
         * @static
         * @property 编辑数据
         */
        EDITDATA: 'EDITDATA',

        /**
         * @static
         * @property 移除数据
         */
        REMOVEDATA: 'REMOVEDATA',

        /**
         * @static
         * @property 移除自身
         */
        REMOVESELF: 'REMOVESELF',

        /**
         * @static
         * @property 窗体改变大小
         */
        RESIZE: "_resize",

        /**
         * @static
         * @property 编辑前事件
         */
        BEFOREEDIT: 'beforeedit',

        /**
         * @static
         * @property 编辑后事件
         */
        AFTEREDIT: 'afteredit',

        /**
         * @static
         * @property 值改变事件
         */
        VALUECHANGE: 'valuechange',

        /**
         * @static
         * @property 停止编辑事件
         */
        STOPEDIT: 'stopedit',

        /**
         * @static
         * @property 值改变事件
         */
        CHANGE: 'change',

        /**
         * @static
         * @property 下拉弹出菜单事件
         */
        EXPAND: 'expand',

        /**
         * @static
         * @property 关闭下拉菜单事件
         */
        COLLAPSE: 'collapse',

        /**
         * @static
         * @property 状态改变事件，一般是用在复选按钮和单选按钮
         */
        STATECHANGE: 'statechange',

        /**
         * @static
         * @property 文件上传回调事件
         */
        CALLBACK: 'callback',

        /**
         * @static
         * @property 点击事件
         */
        CLICK: 'click',

        /**
         * @static
         * @property 状态改变前事件
         */
        BEFORESTATECHANGE: 'beforestatechange',

        /**
         * @static
         * @property 树选中节点事件
         */
        DEALSELECTEDNODES: 'dealselectednodes',

        /**
         * @static
         * @property 树构建完成后事件
         */
        AFTERBUILD: 'afterbuild',

        /**
         * @static
         * @property 数据读取后事件
         */
        AFTERREAD: 'afterread',

        /**
         * @static
         * @property 增加新数据到原有数据时的事件
         */
        APPENDDATA: 'appenddata',

        /**
         * @static
         * @property 树初始化事件
         */
        DEFAULTINIT: 'defaultinit',

        /**
         * @static
         * @property 标签切换事件
         */
        TABCHANGE: 'tabchange',

        /**
         * @static
         * @property 标签切换前事件
         */
        TABCHANGESTART: 'tabchangestart',

        /**
         * @static
         * @property 滚动条滚动事件
         */
        SCROLLCHANGE: 'scrollchange',

        /**
         * @static
         * @property 初始化后事件
         */
        AFTERINIT: 'afterinit',

        /**
         * @static
         * @property 报表开始加载事件
         */
        STARTLOAD: 'startload',

        /**
         * @static
         * @property 报表加载后事件
         */
        AFTERLOAD: 'afterload',

        /**
         * @static
         * @property 报表初始化事件
         */
        INIT: 'init',

        /**
         * @static
         * @property 填报时单元格值改变事件
         */
        CELLVALUECHANGE: 'cellvaluechange',

        /**
         * @static
         * @property 提交前事件
         */
        BS: 'beforesubmit',

        /**
         * @static
         * @property 提交后事件
         */
        AS: 'aftersubmit',

        /**
         * @static
         * @property 提交完成事件
         */
        SC: 'submitcomplete',

        /**
         * @static
         * @property 提交失败事件
         */
        SF: 'submitfailure',

        /**
         * @static
         * @property 提交成功事件
         */
        SS: 'submitsuccess',

        /**
         * @static
         * @property 校验提交前事件
         */
        BVW: 'beforeverifywrite',

        /**
         * @static
         * @property 校验提交后事件
         */
        AVW: 'afterverifywrite',

        /**
         * @static
         * @property 校验后事件
         */
        AV: 'afterverify',

        /**
         * @static
         * @property 填报前事件
         */
        BW: 'beforewrite',

        /**
         * @static
         * @property 填报后事件
         */
        AW: 'afterwrite',

        /**
         * @static
         * @property 填报成功事件
         */
        WS: 'writesuccess',

        /**
         * @static
         * @property 填报失败事件
         */
        WF: 'writefailure',

        /**
         * @static
         * @property 添加行前事件
         */
        BA: 'beforeappend',

        /**
         * @static
         * @property 添加行后事件
         */
        AA: 'afterappend',

        /**
         * @static
         * @property 删除行前事件
         */
        BD: 'beforedelete',

        /**
         * @static
         * @property 删除行后事件
         */
        AD: 'beforedelete',

        /**
         * @static
         * @property 未提交离开事件
         */
        UC: 'unloadcheck',


        /**
         * @static
         * @property PDF导出前事件
         */
        BTOPDF: 'beforetopdf',

        /**
         * @static
         * @property PDF导出后事件
         */
        ATOPDF: 'aftertopdf',

        /**
         * @static
         * @property Excel导出前事件
         */
        BTOEXCEL: 'beforetoexcel',

        /**
         * @static
         * @property Excel导出后事件
         */
        ATOEXCEL: 'aftertoexcel',

        /**
         * @static
         * @property Word导出前事件
         */
        BTOWORD: 'beforetoword',

        /**
         * @static
         * @property Word导出后事件
         */
        ATOWORD: 'aftertoword',

        /**
         * @static
         * @property 图片导出前事件
         */
        BTOIMAGE: 'beforetoimage',

        /**
         * @static
         * @property 图片导出后事件
         */
        ATOIMAGE: 'aftertoimage',

        /**
         * @static
         * @property HTML导出前事件
         */
        BTOHTML: 'beforetohtml',

        /**
         * @static
         * @property HTML导出后事件
         */
        ATOHTML: 'aftertohtml',

        /**
         * @static
         * @property Excel导入前事件
         */
        BIMEXCEL: 'beforeimportexcel',

        /**
         * @static
         * @property Excel导出后事件
         */
        AIMEXCEL: 'afterimportexcel',

        /**
         * @static
         * @property PDF打印前事件
         */
        BPDFPRINT: 'beforepdfprint',

        /**
         * @static
         * @property PDF打印后事件
         */
        APDFPRINT: 'afterpdfprint',

        /**
         * @static
         * @property Flash打印前事件
         */
        BFLASHPRINT: 'beforeflashprint',

        /**
         * @static
         * @property Flash打印后事件
         */
        AFLASHPRINT: 'afterflashprint',

        /**
         * @static
         * @property Applet打印前事件
         */
        BAPPLETPRINT: 'beforeappletprint',

        /**
         * @static
         * @property Applet打印后事件
         */
        AAPPLETPRINT: 'afterappletprint',

        /**
         * @static
         * @property 服务器打印前事件
         */
        BSEVERPRINT: 'beforeserverprint',

        /**
         * @static
         * @property 服务器打印后事件
         */
        ASERVERPRINT: 'afterserverprint',

        /**
         * @static
         * @property 邮件发送前事件
         */
        BEMAIL: 'beforeemail',

        /**
         * @static
         * @property 邮件发送后事件
         */
        AEMAIL: 'afteremail',
        
        /**
         * @static
         * @property 邮件发送后事件
         */
        CARDCHANGE: 'cardchange'
    }
});