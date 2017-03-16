var React = require('react');

var {PropTypes} = React;

var TableColumn = React.createClass({
  statics: {
    __TableColumn__: true
  },

  propTypes: {
    align: PropTypes.oneOf(['left', 'center', 'right']),

    fixed: PropTypes.bool,

    header: PropTypes.oneOfType([
      PropTypes.node,
      PropTypes.func,
    ]),

    cell: PropTypes.oneOfType([
      PropTypes.node,
      PropTypes.func
    ]),

    footer: PropTypes.oneOfType([
      PropTypes.node,
      PropTypes.func
    ]),

    columnKey: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number
    ]),

    width: PropTypes.number.isRequired,

    minWidth: PropTypes.number,

    maxWidth: PropTypes.number,

    flexGrow: PropTypes.number,

    isResizable: PropTypes.bool,

    allowCellsRecycling: PropTypes.bool
  },

  getDefaultProps() /*object*/ {
    return {
      allowCellsRecycling: false,
      fixed: false,
    };
  },

  render() {
    if (__DEV__) {
      throw new Error(
        'Component <TableColumn /> should never render'
      );
    }
    return null;
  },
});

module.exports = TableColumn;
