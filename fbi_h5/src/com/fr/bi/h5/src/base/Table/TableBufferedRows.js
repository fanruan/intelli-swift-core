var React = require('react');
var ReactDOM = require('react-dom');
var TableRowBuffer = require('./TableRowBuffer');
var TableRow = require('./TableRow');

var cn = require('classnames');
var {emptyFunction, translateDOMPositionXY} = require('core');
var {View, Animated, Easing} = require('lib');

var {PropTypes} = React;

var TableBufferedRows = React.createClass({

    propTypes: {
        isScrolling: PropTypes.bool,
        defaultRowHeight: PropTypes.number.isRequired,
        firstRowIndex: PropTypes.number.isRequired,
        firstRowOffset: PropTypes.number.isRequired,
        fixedColumns: PropTypes.array.isRequired,
        height: PropTypes.number.isRequired,
        offsetTop: PropTypes.number.isRequired,
        onRowClick: PropTypes.func,
        onRowDoubleClick: PropTypes.func,
        onRowMouseDown: PropTypes.func,
        onRowMouseEnter: PropTypes.func,
        onRowMouseLeave: PropTypes.func,
        rowClassNameGetter: PropTypes.func,
        rowsCount: PropTypes.number.isRequired,
        rowHeightGetter: PropTypes.func,
        rowPositionGetter: PropTypes.func.isRequired,
        scrollLeft: PropTypes.number.isRequired,
        scrollableColumns: PropTypes.array.isRequired,
        showLastRowBorder: PropTypes.bool,
        width: PropTypes.number.isRequired
    },

    getInitialState() /*object*/ {
        this._rowBuffer =
            new TableRowBuffer(
                this.props.rowsCount,
                this.props.defaultRowHeight,
                this.props.height,
                this._getRowHeight
            );
        return ({
            rowsToRender: this._rowBuffer.getRows(
                this.props.firstRowIndex,
                this.props.firstRowOffset
            )
        });
    },

    componentWillMount() {
        this._staticRowArray = [];
    },

    componentDidMount() {
        setTimeout(this._updateBuffer, 1000);
    },

    componentWillReceiveProps(/*object*/ nextProps) {
        if (nextProps.rowsCount !== this.props.rowsCount ||
            nextProps.defaultRowHeight !== this.props.defaultRowHeight ||
            nextProps.height !== this.props.height) {
            this._rowBuffer =
                new TableRowBuffer(
                    nextProps.rowsCount,
                    nextProps.defaultRowHeight,
                    nextProps.height,
                    this._getRowHeight
                );
        }
        if (this.props.isScrolling && !nextProps.isScrolling) {
            this._updateBuffer();
        } else {
            this.setState({
                rowsToRender: this._rowBuffer.getRows(
                    nextProps.firstRowIndex,
                    nextProps.firstRowOffset
                )
            });
        }
    },

    _updateBuffer() {
        if (this.isMounted()) {
            this.setState({
                rowsToRender: this._rowBuffer.getRowsWithUpdatedBuffer()
            });
        }
    },

    shouldComponentUpdate() /*boolean*/ {
        // Don't add PureRenderMixin to this component please.
        return true;
    },

    componentWillUnmount() {
        this._staticRowArray.length = 0;
    },

    render() /*object*/ {
        var props = this.props;
        var rowClassNameGetter = props.rowClassNameGetter || emptyFunction;
        var rowPositionGetter = props.rowPositionGetter;

        var rowsToRender = this.state.rowsToRender;
        this._staticRowArray.length = rowsToRender.length;

        for (var i = 0; i < rowsToRender.length; ++i) {
            var rowIndex = rowsToRender[i];
            var currentRowHeight = this._getRowHeight(rowIndex);
            var rowOffsetTop = rowPositionGetter(rowIndex);

            var hasBottomBorder =
                rowIndex === props.rowsCount - 1 && props.showLastRowBorder;

            this._staticRowArray[i] =
                <TableRow
                    ref={`row_${i}`}
                    key={i}
                    isScrolling={props.isScrolling}
                    index={rowIndex}
                    width={props.width}
                    height={currentRowHeight}
                    scrollLeft={Math.round(props.scrollLeft)}
                    offsetTop={Math.round(rowOffsetTop)}
                    fixedColumns={props.fixedColumns}
                    scrollableColumns={props.scrollableColumns}
                    onClick={props.onRowClick}
                    onDoubleClick={props.onRowDoubleClick}
                    onMouseDown={props.onRowMouseDown}
                    onMouseEnter={props.onRowMouseEnter}
                    onMouseLeave={props.onRowMouseLeave}
                    trans={props.trans}
                    className={cn(
                        rowClassNameGetter(rowIndex),
                        'public-fixedDataTable-bodyRow',
                        cn({
                            'fixedDataTableLayout-hasBottomBorder': hasBottomBorder,
                            'public-fixedDataTable-hasBottomBorder': hasBottomBorder
                        })
                    )}
                />;
        }

        //var firstRowPosition = props.rowPositionGetter(props.firstRowIndex);

        var style = {
            position: 'absolute',
            transitionDuration: '300ms',
            transitionTimingFunction: 'ease-out',
            pointerEvents: props.isScrolling ? 'none' : 'auto'
        };

        //translateDOMPositionXY(
        //  style,
        //  0,
        //  props.firstRowOffset - firstRowPosition + props.offsetTop
        //);

        return <Animated.View ref="wrapper" style={{
            transform: [{
                translateX: 0
            }, {
                translateY: this.props.trans.y
            }, {
                translateZ: 0
            }], ...style
        }}>{this._staticRowArray}</Animated.View>;
    },

    setScrolling(){
        for (var i = 0; i < this.state.rowsToRender.length; ++i) {
            this.refs[`row_${i}`].setScrolling();
        }
        ReactDOM.findDOMNode(this.refs['wrapper']).style.transitionDuration = '0ms';
    },

    setScrollEnd(){
        for (var i = 0; i < this.state.rowsToRender.length; ++i) {
            this.refs[`row_${i}`].setScrollEnd();
        }
        ReactDOM.findDOMNode(this.refs['wrapper']).style.transitionDuration = '300ms';
    },

    _getRowHeight(/*number*/ index) /*number*/ {
        return this.props.rowHeightGetter ?
            this.props.rowHeightGetter(index) :
            this.props.defaultRowHeight;
    }
});

module.exports = TableBufferedRows;
