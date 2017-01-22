var React = require('react');
var TableColumnGroup = require('./TableColumnGroup');
var TableColumn = require('./TableColumn');

var DIR_SIGN = +1;
// A cell up to 5px outside of the visible area will still be considered visible
var CELL_VISIBILITY_TOLERANCE = 5; // used for flyouts

function renderToString(value) /*string*/ {
  if (value === null || value === undefined) {
    return '';
  } else {
    return String(value);
  }
}

function forEachColumn(children, callback) {
  React.Children.forEach(children, (child) => {
    if (child.type === TableColumnGroup) {
      forEachColumn(child.props.children, callback);
    } else if (child.type === TableColumn) {
      callback(child);
    }
  });
}

function mapColumns(children, callback) {
  var newChildren = [];
  React.Children.forEach(children, originalChild => {
    var newChild = originalChild;

    // The child is either a column group or a column. If it is a column group
    // we need to iterate over its columns and then potentially generate a
    // new column group
    if (originalChild.type === TableColumnGroup) {
      var haveColumnsChanged = false;
      var newColumns = [];

      forEachColumn(originalChild.props.children, originalcolumn => {
        var newColumn = callback(originalcolumn);
        if (newColumn !== originalcolumn) {
          haveColumnsChanged = true;
        }
        newColumns.push(newColumn);
      });

      // If the column groups columns have changed clone the group and supply
      // new children
      if (haveColumnsChanged) {
        newChild = React.cloneElement(originalChild, {
          children: newColumns
        });
      }
    } else if (originalChild.type === TableColumn) {
      newChild = callback(originalChild);
    }

    newChildren.push(newChild);
  });

  return newChildren;
}

var TableHelper = {
  DIR_SIGN,
  CELL_VISIBILITY_TOLERANCE,
  renderToString,
  forEachColumn,
  mapColumns
};

module.exports = TableHelper;
