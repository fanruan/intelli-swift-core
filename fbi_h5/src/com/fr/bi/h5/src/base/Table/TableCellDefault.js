var React = require('react');

var cn = require('classnames');

var {PropTypes} = React;

var TableCellDefault = React.createClass({
  propTypes: {

    height: PropTypes.number,

    width: PropTypes.number,

    columnKey: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number
    ])
  },

  render() {
    var {
      height,
      width,
      style,
      className,
      children,
      columnKey, // Unused but should not be passed through
      rowIndex, // Unused but should not be passed through
      ...props
    } = this.props;

    var innerStyle = {
      height,
      width,
      ...style
    };

    return (
      <div
        {...props}
        className={cn(
          'fixedDataTableCellLayout-wrap1',
          'public-fixedDataTableCell-wrap1',
          className
        )}
        style={innerStyle}>
        <div
          className={cn(
            'fixedDataTableCellLayout-wrap2',
            'public-fixedDataTableCell-wrap2'
          )}>
          <div
            className={cn(
              'fixedDataTableCellLayout-wrap3',
              'public-fixedDataTableCell-wrap3'
            )}>
            <div className={'public-fixedDataTableCell-cellContent'}>
              {children}
            </div>
          </div>
        </div>
      </div>
    );
  }
});

module.exports = TableCellDefault;
