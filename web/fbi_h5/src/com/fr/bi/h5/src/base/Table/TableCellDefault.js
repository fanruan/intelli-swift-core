var React = require('react');
var {View} = require('lib');

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
            <View
                {...props}
                className={cn(
          'fixedDataTableCellLayout-wrap1',
          'public-fixedDataTableCell-wrap1',
          className
        )}
                style={innerStyle}>
                <View
                    className={cn(
            'fixedDataTableCellLayout-wrap2',
            'public-fixedDataTableCell-wrap2'
          )}>
                    <View
                        className={cn(
              'fixedDataTableCellLayout-wrap3',
              'public-fixedDataTableCell-wrap3'
            )}>
                        <View className={'public-fixedDataTableCell-cellContent'}>
                            {children}
                        </View>
                    </View>
                </View>
            </View>
        );
    }
});

module.exports = TableCellDefault;
