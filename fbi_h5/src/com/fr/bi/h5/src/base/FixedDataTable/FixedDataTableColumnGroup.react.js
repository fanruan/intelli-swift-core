var React = require('react');

var {PropTypes} = React;

var FixedDataTableColumnGroup = React.createClass({
  statics: {
    __TableColumnGroup__: true
  },

  propTypes: {
    align: PropTypes.oneOf(['left', 'center', 'right']),

    fixed: PropTypes.bool,

    header: PropTypes.oneOfType([
      PropTypes.node,
      PropTypes.func
    ])

  },

  getDefaultProps() /*object*/ {
    return {
      fixed: false
    };
  },

  render() {
    if (__DEV__) {
      throw new Error(
        'Component <FixedDataTableColumnGroup /> should never render'
      );
    }
    return null;
  }
});

module.exports = FixedDataTableColumnGroup;
