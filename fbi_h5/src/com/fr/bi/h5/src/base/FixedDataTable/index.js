var FixedDataTable = require('./FixedDataTable.react');
var FixedDataTableCellDefault = require('./FixedDataTableCellDefault.react');
var FixedDataTableColumn = require('./FixedDataTableColumn.react');
var FixedDataTableColumnGroup = require('./FixedDataTableColumnGroup.react');
require('./FixedDataTable.css');

var FixedDataTableRoot = {
  Cell: FixedDataTableCellDefault,
  Column: FixedDataTableColumn,
  ColumnGroup: FixedDataTableColumnGroup,
  Table: FixedDataTable
};

module.exports = FixedDataTableRoot;
