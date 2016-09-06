var React = require('react');
var ReactDOM = require('react-dom');
var ReactComponentWithPureRenderMixin = require('react-addons-pure-render-mixin');
var {WheelHandler, translateDOMPositionXY, emptyFunction, invariant, shallowEqual} = require('core');
var {View, PanResponder, Easing, Animated}  = require('lib');
var TableBufferedRows = require('./TableBufferedRows');
var TableColumnResizeHandle = require('./TableColumnResizeHandle');
var TableRow = require('./TableRow');
var TableScrollHelper = require('./TableScrollHelper');
var TableWidthHelper = require('./TableWidthHelper');
var TableCellDefault = require('./TableCellDefault');
var TableColumn = require('./TableColumn');
var TableColumnGroup = require('./TableColumnGroup');

var cn = require('classnames');
var debounce = require('lodash/debounce');

var {PropTypes} = React;
var ReactChildren = React.Children;

var EMPTY_OBJECT = {};
var BORDER_HEIGHT = 1;
var HEADER = 'header';
var FOOTER = 'footer';
var CELL = 'cell';

var Table = React.createClass({

    mixins: [ReactComponentWithPureRenderMixin],
    propTypes: {

        width: PropTypes.number.isRequired,

        height: PropTypes.number,

        maxHeight: PropTypes.number,

        ownerHeight: PropTypes.number,

        overflowX: PropTypes.oneOf(['hidden', 'auto']),
        overflowY: PropTypes.oneOf(['hidden', 'auto']),

        rowsCount: PropTypes.number.isRequired,

        rowHeight: PropTypes.number.isRequired,

        rowHeightGetter: PropTypes.func,

        rowClassNameGetter: PropTypes.func,

        groupHeaderHeight: PropTypes.number,

        headerHeight: PropTypes.number.isRequired,

        footerHeight: PropTypes.number,

        scrollLeft: PropTypes.number,

        scrollToColumn: PropTypes.number,

        scrollTop: PropTypes.number,

        scrollToRow: PropTypes.number,

        onScrollStart: PropTypes.func,

        onScrollEnd: PropTypes.func,

        onContentHeightChange: PropTypes.func,

        onRowClick: PropTypes.func,

        onRowDoubleClick: PropTypes.func,

        onRowMouseDown: PropTypes.func,

        onRowMouseEnter: PropTypes.func,

        onRowMouseLeave: PropTypes.func,

        onColumnResizeEndCallback: PropTypes.func,

        isColumnResizing: PropTypes.bool
    },

    getDefaultProps() /*object*/ {
        return {
            footerHeight: 0,
            groupHeaderHeight: 0,
            headerHeight: 0,
            scrollLeft: 0,
            scrollTop: 0
        };
    },

    getInitialState() /*object*/ {
        var props = this.props;
        var viewportHeight =
            (props.height === undefined ? props.maxHeight : props.height) -
            (props.headerHeight || 0) -
            (props.footerHeight || 0) -
            (props.groupHeaderHeight || 0);
        this._scrollHelper = new TableScrollHelper(
            props.rowsCount,
            props.rowHeight,
            viewportHeight,
            props.rowHeightGetter
        );
        if (props.scrollTop) {
            this._scrollHelper.scrollTo(props.scrollTop);
        }
        this._didScrollStop = debounce(this._didScrollStop, 200).bind(this);

        this.trans = new Animated.ValueXY({x: 0, y: 0});
        this.offset = new Animated.Value(0);
        return this._calculateState(this.props);
    },

    componentWillMount() {
        var scrollToRow = this.props.scrollToRow;
        if (scrollToRow !== undefined && scrollToRow !== null) {
            this._rowToScrollTo = scrollToRow;
        }
        var scrollToColumn = this.props.scrollToColumn;
        if (scrollToColumn !== undefined && scrollToColumn !== null) {
            this._columnToScrollTo = scrollToColumn;
        }

        this._wheelHandler = new WheelHandler(
            this._onWheel,
            this._shouldHandleWheelX,
            this._shouldHandleWheelY
        );

        this._panResponder = PanResponder.create({
            onStartShouldSetPanResponder: this._handleStartShouldSetPanResponder,
            onMoveShouldSetPanResponder: ()=>true,
            onPanResponderGrant: this._handlePanResponderGrant,
            onPanResponderMove: this._handlePanResponderMove,
            onPanResponderRelease: this._handlePanResponderEnd,
            onPanResponderTerminate: this._handlePanResponderEnd
        });
    },

    _handleStartShouldSetPanResponder(e) {
        return e.nativeEvent.target === ReactDOM.findDOMNode(this.refs['fixedDataTable'])
    },

    _handlePanResponderGrant(e, gestureState) {
        this.offset.setOffset(this.offset.__getAnimatedValue());
        this.offset.setValue(0);
        this.trans.setOffset({x: this.trans.x.__getAnimatedValue(), y: this.trans.y.__getAnimatedValue()});
        this.trans.setValue({x: 0, y: 0});
        e.stopPropagation();
        e.preventDefault();
    },

    setScrolling(){
        ReactDOM.findDOMNode(this.refs['rowsContainer']).style.transitionDuration = '0ms';
        this.refs['bufferedRows'].setScrolling();
        this.refs['header'] && this.refs['header'].setScrolling();
        this.refs['footer'] && this.refs['footer'].setScrolling();
        this.refs['group_header'] && this.refs['group_header'].setScrolling();
    },

    setScrollEnd(){
        ReactDOM.findDOMNode(this.refs['rowsContainer']).style.transitionDuration = '300ms';
        this.refs['bufferedRows'].setScrollEnd();
        this.refs['header'] && this.refs['header'].setScrollEnd();
        this.refs['footer'] && this.refs['footer'].setScrollEnd();
        this.refs['group_header'] && this.refs['group_header'].setScrollEnd();
    },

    _handlePanResponderMove(e, gestureState) {
        var scrollX = this.state.scrollX, scrollY = this.state.scrollY, offsetX = this.state.offsetX;
        var maxScrollX = this.state.maxScrollX, maxScrollY = this.state.maxScrollY, maxOffsetScroll = this.state.offsetWidth - this.state.width;
        var dx = gestureState.dx, dy = gestureState.dy;
        this.setScrolling();

        scrollX -= dx;
        scrollY -= dy;
        offsetX -= dx;
        if (scrollX < 0) {
            scrollX = 0 - Math.pow(0 - scrollX, 0.8);
        }
        if (scrollX > maxScrollX) {
            scrollX = maxScrollX + Math.pow(scrollX - maxScrollX, 0.8);
        }
        if (scrollY < 0) {
            scrollY = 0 - Math.pow(0 - scrollY, 0.8);
        }
        if (scrollY > maxScrollY) {
            scrollY = maxScrollY + Math.pow(scrollY - maxScrollY, 0.8);
        }
        if (offsetX < 0) {
            offsetX = 0 - Math.pow(0 - offsetX, 0.8);
        }
        if (offsetX > maxOffsetScroll) {
            offsetX = maxOffsetScroll + Math.pow(offsetX - maxOffsetScroll, 0.8);
        }
        if (Math.abs(dy) > Math.abs(dx)) {
            if (!this._lockX && !this._lockA) {
                this._lockY = true;
                this.trans.setValue({x: 0, y: this.state.scrollY - scrollY});
            }
        } else {
            if (!this._lockY) {
                var x = this.state.scrollX;
                if (dx < 0 && x === 0) {
                    this._onSwipeContainer(-(this.state.scrollX - scrollX));
                }
                //if (dx < 0 && x === 0) {
                //    this.setState({
                //        width: this.state.width - dx
                //    }, ()=> {
                //        this.offset.setValue(this.state.offsetX - offsetX);
                //    });
                //    return;
                //}
                else {
                    x += Math.round(-dx);

                    if (!this._lockA && (x > 0 && x <= this.state.maxScrollX)) {
                        this._lockX = true;
                        this.trans.setValue({x: this.state.scrollX - scrollX, y: 0});
                    } else {
                        if (!this._lockX) {
                            this._lockA = true;
                            this.offset.setValue(this.state.offsetX - offsetX);
                        }
                    }
                }
            }
        }

        //this._onWheel(-gestureState.dx + this.dx, -gestureState.dy + this.dy);
        e.stopPropagation();
        e.preventDefault();
        return false;
    },

    _handlePanResponderEnd(e, gestureState) {
        var dx = gestureState.dx, dy = gestureState.dy, vx = gestureState.vx, vy = gestureState.vy;
        this.setScrollEnd();
        if (!this._lockX && !this._lockY) {
            this.offset.flattenOffset();
            this._onSwipe(-dx - vx * 200);
        } else {
            this.trans.flattenOffset();
            this._onWheel(this._lockX ? (-dx - vx * 200) : 0, this._lockY ? (-dy - vy * 200) : 0);
        }
        this._lockX = false;
        this._lockY = false;
        this._lockA = false;
    },

    _shouldHandleWheelX(/*number*/ delta) /*boolean*/ {
        if (this.props.overflowX === 'hidden') {
            return false;
        }

        delta = Math.round(delta);
        if (delta === 0) {
            return false;
        }

        return (
            (delta < 0 && this.state.scrollX > 0) ||
            (delta >= 0 && this.state.scrollX < this.state.maxScrollX)
        );
    },

    _shouldHandleWheelY(/*number*/ delta) /*boolean*/ {
        if (this.props.overflowY === 'hidden' || delta === 0) {
            return false;
        }

        delta = Math.round(delta);
        if (delta === 0) {
            return false;
        }

        return (
            (delta < 0 && this.state.scrollY > 0) ||
            (delta >= 0 && this.state.scrollY < this.state.maxScrollY)
        );
    },

    _reportContentHeight() {
        var scrollContentHeight = this.state.scrollContentHeight;
        var reservedHeight = this.state.reservedHeight;
        var requiredHeight = scrollContentHeight + reservedHeight;
        var contentHeight;
        var useMaxHeight = this.props.height === undefined;
        if (useMaxHeight && this.props.maxHeight > requiredHeight) {
            contentHeight = requiredHeight;
        } else if (this.state.height > requiredHeight && this.props.ownerHeight) {
            contentHeight = Math.max(requiredHeight, this.props.ownerHeight);
        } else {
            contentHeight = this.state.height + this.state.maxScrollY;
        }
        if (contentHeight !== this._contentHeight &&
            this.props.onContentHeightChange) {
            this.props.onContentHeightChange(contentHeight);
        }
        this._contentHeight = contentHeight;
    },

    _resetAnimate(){
        var headerOffsetTop = this.state.useGroupHeader ? this.state.groupHeaderHeight : 0;
        var bodyOffsetTop = headerOffsetTop + this.state.headerHeight;
        this.trans.setValue({x: 0, y: bodyOffsetTop});
        this.trans.setOffset({x: 0, y: 0});
        this.offset.setValue(0);
        this.offset.setOffset(0);
    },

    componentDidMount() {
        this._reportContentHeight();
        this._resetAnimate();
    },

    componentWillReceiveProps(/*object*/ nextProps) {
        var scrollToRow = nextProps.scrollToRow;
        if (scrollToRow !== undefined && scrollToRow !== null) {
            this._rowToScrollTo = scrollToRow;
        }
        var scrollToColumn = nextProps.scrollToColumn;
        if (scrollToColumn !== undefined && scrollToColumn !== null) {
            this._columnToScrollTo = scrollToColumn;
        }

        var newOverflowX = nextProps.overflowX;
        var newOverflowY = nextProps.overflowY;
        if (newOverflowX !== this.props.overflowX ||
            newOverflowY !== this.props.overflowY) {
            this._wheelHandler = new WheelHandler(
                this._onWheel,
                newOverflowX !== 'hidden', // Should handle horizontal scroll
                newOverflowY !== 'hidden' // Should handle vertical scroll
            );
        }

        // In the case of controlled scrolling, notify.
        if (this.props.ownerHeight !== nextProps.ownerHeight ||
            this.props.scrollTop !== nextProps.scrollTop) {
            this._didScrollStart();
        }
        this._didScrollStop();

        this.setState(this._calculateState(nextProps, this.state));
    },

    componentDidUpdate() {
        this._reportContentHeight();
    },

    render() /*object*/ {
        var state = this.state;
        var props = this.props;

        var groupHeader;
        if (state.useGroupHeader) {
            groupHeader = (
                <TableRow
                    ref="group_header"
                    key="group_header"
                    isScrolling={this._isScrolling}
                    className={cn(
                        'fixedDataTableLayout-header',
                        'public-fixedDataTable-header'
                    )}
                    width={state.offsetWidth}
                    height={state.groupHeaderHeight}
                    index={0}
                    zIndex={1}
                    offsetTop={0}
                    scrollLeft={state.scrollX}
                    fixedColumns={state.groupHeaderFixedColumns}
                    scrollableColumns={state.groupHeaderScrollableColumns}
                    onColumnResize={this._onColumnResize}
                    trans={this.trans}
                />
            );
        }

        var maxScrollY = this.state.maxScrollY;
        var scrollbarYHeight = state.height -
            (2 * BORDER_HEIGHT) - state.footerHeight;

        var headerOffsetTop = state.useGroupHeader ? state.groupHeaderHeight : 0;
        var bodyOffsetTop = headerOffsetTop + state.headerHeight;
        scrollbarYHeight -= bodyOffsetTop;
        var bottomSectionOffset = 0;
        var footOffsetTop = props.maxHeight != null
            ? bodyOffsetTop + state.bodyHeight
            : bodyOffsetTop + scrollbarYHeight;
        var rowsContainerHeight = footOffsetTop + state.footerHeight;

        if (props.ownerHeight !== undefined && props.ownerHeight < state.height) {
            bottomSectionOffset = props.ownerHeight - state.height;

            footOffsetTop = Math.min(
                footOffsetTop,
                props.ownerHeight - state.footerHeight
            );

            scrollbarYHeight = Math.max(0, footOffsetTop - bodyOffsetTop);
        }

        var dragKnob =
            <TableColumnResizeHandle
                height={state.height}
                initialWidth={state.columnResizingData.width || 0}
                minWidth={state.columnResizingData.minWidth || 0}
                maxWidth={state.columnResizingData.maxWidth || Number.MAX_VALUE}
                visible={!!state.isColumnResizing}
                leftOffset={state.columnResizingData.left || 0}
                knobHeight={state.headerHeight}
                initialEvent={state.columnResizingData.initialEvent}
                onColumnResizeEnd={props.onColumnResizeEndCallback}
                columnKey={state.columnResizingData.key}
            />;

        var footer = null;
        if (state.footerHeight) {
            footer =
                <TableRow
                    ref="footer"
                    key="footer"
                    isScrolling={this._isScrolling}
                    className={cn(
                        'fixedDataTableLayout-footer',
                        'public-fixedDataTable-footer'
                    )}
                    width={state.offsetWidth}
                    height={state.footerHeight}
                    index={-1}
                    zIndex={1}
                    offsetTop={footOffsetTop}
                    fixedColumns={state.footFixedColumns}
                    scrollableColumns={state.footScrollableColumns}
                    scrollLeft={state.scrollX}
                    trans={this.trans}
                />;
        }

        var rows = this._renderRows(bodyOffsetTop);

        var header =
            <TableRow
                ref="header"
                key="header"
                isScrolling={this._isScrolling}
                className={cn(
                    'fixedDataTableLayout-header',
                    'public-fixedDataTable-header'
                )}
                width={state.offsetWidth}
                height={state.headerHeight}
                index={-1}
                zIndex={1}
                offsetTop={headerOffsetTop}
                scrollLeft={state.scrollX}
                fixedColumns={state.headFixedColumns}
                scrollableColumns={state.headScrollableColumns}
                onColumnResize={this._onColumnResize}
                trans={this.trans}
            />;

        var topShadow;
        var bottomShadow;
        if (state.scrollY) {
            topShadow =
                <View
                    className={cn(
                        'fixedDataTableLayout-topShadow',
                        'public-fixedDataTable-topShadow'
                    )}
                    style={{top: bodyOffsetTop}}
                />;
        }

        if (
            (state.ownerHeight != null &&
            state.ownerHeight < state.height &&
            state.scrollContentHeight + state.reservedHeight > state.ownerHeight) ||
            state.scrollY < maxScrollY
        ) {
            bottomShadow =
                <View
                    className={cn(
                        'fixedDataTableLayout-bottomShadow',
                        'public-fixedDataTable-bottomShadow'
                    )}
                    style={{top: footOffsetTop}}
                />;
        }

        return (
            <View
                ref={'fixedDataTable'}
                className={cn(
                    'fixedDataTableLayout-main',
                    'public-fixedDataTable-main'
                )}
                onWheel={this._wheelHandler.onWheel}
                style={{height: state.height, width: state.width}} {...this._panResponder.panHandlers}>
                <Animated.View
                    ref="rowsContainer"
                    className={'fixedDataTableLayout-rowsContainer'}
                    style={{
                        transform: [{
                            translateX: this.offset
                        }, {
                            translateY: 0
                        }, {
                            translateZ: 0
                        }],
                        height: rowsContainerHeight, width: state.offsetWidth,
                        transitionDuration: '300ms',
                        transitionTimingFunction: 'ease-out',
                        pointerEvents: this.isScrolling ? 'none' : 'auto'
                    }}>
                    {dragKnob}
                    {groupHeader}
                    {header}
                    {rows}
                    {footer}
                    {topShadow}
                    {bottomShadow}
                </Animated.View>
            </View>
        );
    },

    _renderRows(/*number*/ offsetTop) /*object*/ {
        var state = this.state;

        return (
            <TableBufferedRows
                ref="bufferedRows"
                isScrolling={this._isScrolling}
                defaultRowHeight={state.rowHeight}
                firstRowIndex={state.firstRowIndex}
                firstRowOffset={state.firstRowOffset}
                fixedColumns={state.bodyFixedColumns}
                height={state.bodyHeight}
                offsetTop={offsetTop}
                onRowClick={state.onRowClick}
                onRowDoubleClick={state.onRowDoubleClick}
                onRowMouseDown={state.onRowMouseDown}
                onRowMouseEnter={state.onRowMouseEnter}
                onRowMouseLeave={state.onRowMouseLeave}
                rowClassNameGetter={state.rowClassNameGetter}
                rowsCount={state.rowsCount}
                rowGetter={state.rowGetter}
                rowHeightGetter={state.rowHeightGetter}
                scrollLeft={state.scrollX}
                scrollableColumns={state.bodyScrollableColumns}
                showLastRowBorder={true}
                width={state.offsetWidth}
                trans={this.trans}
                rowPositionGetter={this._scrollHelper.getRowPosition}
            />
        );
    },

    /**
     * This is called when a cell that is in the header of a column has its
     * resizer knob clicked on. It displays the resizer and puts in the correct
     * location on the table.
     */
    _onColumnResize(/*number*/ combinedWidth,
                    /*number*/ leftOffset,
                    /*number*/ cellWidth,
                    /*?number*/ cellMinWidth,
                    /*?number*/ cellMaxWidth,
                    /*number|string*/ columnKey,
                    /*object*/ event) {
        this.setState({
            isColumnResizing: true,
            columnResizingData: {
                left: leftOffset + combinedWidth - cellWidth,
                width: cellWidth,
                minWidth: cellMinWidth,
                maxWidth: cellMaxWidth,
                initialEvent: {
                    clientX: event.clientX,
                    clientY: event.clientY,
                    preventDefault: emptyFunction
                },
                key: columnKey
            }
        });
    },

    _areColumnSettingsIdentical(oldColumns,
                                newColumns) {
        if (oldColumns.length !== newColumns.length) {
            return false;
        }
        for (var index = 0; index < oldColumns.length; ++index) {
            if (!shallowEqual(
                    oldColumns[index].props,
                    newColumns[index].props
                )) {
                return false;
            }
        }
        return true;
    },

    _populateColumnsAndColumnData(columns,
                                  columnGroups,
                                  oldState) {
        var canReuseColumnSettings = false;
        var canReuseColumnGroupSettings = false;

        if (oldState && oldState.columns) {
            canReuseColumnSettings =
                this._areColumnSettingsIdentical(columns, oldState.columns);
        }
        if (oldState && oldState.columnGroups && columnGroups) {
            canReuseColumnGroupSettings =
                this._areColumnSettingsIdentical(columnGroups, oldState.columnGroups);
        }

        var columnInfo = {};
        if (canReuseColumnSettings) {
            columnInfo.bodyFixedColumns = oldState.bodyFixedColumns;
            columnInfo.bodyScrollableColumns = oldState.bodyScrollableColumns;
            columnInfo.headFixedColumns = oldState.headFixedColumns;
            columnInfo.headScrollableColumns = oldState.headScrollableColumns;
            columnInfo.footFixedColumns = oldState.footFixedColumns;
            columnInfo.footScrollableColumns = oldState.footScrollableColumns;
        } else {
            var bodyColumnTypes = this._splitColumnTypes(columns);
            columnInfo.bodyFixedColumns = bodyColumnTypes.fixed;
            columnInfo.bodyScrollableColumns = bodyColumnTypes.scrollable;

            var headColumnTypes = this._splitColumnTypes(
                this._selectColumnElement(HEADER, columns)
            );
            columnInfo.headFixedColumns = headColumnTypes.fixed;
            columnInfo.headScrollableColumns = headColumnTypes.scrollable;

            var footColumnTypes = this._splitColumnTypes(
                this._selectColumnElement(FOOTER, columns)
            );
            columnInfo.footFixedColumns = footColumnTypes.fixed;
            columnInfo.footScrollableColumns = footColumnTypes.scrollable;
        }

        if (canReuseColumnGroupSettings) {
            columnInfo.groupHeaderFixedColumns = oldState.groupHeaderFixedColumns;
            columnInfo.groupHeaderScrollableColumns =
                oldState.groupHeaderScrollableColumns;
        } else {
            if (columnGroups) {
                var groupHeaderColumnTypes = this._splitColumnTypes(
                    this._selectColumnElement(HEADER, columnGroups)
                );
                columnInfo.groupHeaderFixedColumns = groupHeaderColumnTypes.fixed;
                columnInfo.groupHeaderScrollableColumns =
                    groupHeaderColumnTypes.scrollable;
            }
        }

        return columnInfo;
    },

    _calculateState(/*object*/ props, /*?object*/ oldState) /*object*/ {
        invariant(
            props.height !== undefined || props.maxHeight !== undefined,
            'You must set either a height or a maxHeight'
        );

        var children = [];
        ReactChildren.forEach(props.children, (child, index) => {
            if (child == null) {
                return;
            }
            invariant(
                child.type.__TableColumnGroup__ ||
                child.type.__TableColumn__,
                'child type should be <TableColumn /> or ' +
                '<TableColumnGroup />'
            );
            children.push(child);
        });

        var useGroupHeader = false;
        if (children.length && children[0].type.__TableColumnGroup__) {
            useGroupHeader = true;
        }

        var firstRowIndex = (oldState && oldState.firstRowIndex) || 0;
        var firstRowOffset = (oldState && oldState.firstRowOffset) || 0;
        var offsetX = (oldState && oldState.offsetX) || 0, offsetWidth = (oldState && oldState.offsetWidth) || props.width;
        var scrollX, scrollY;
        if (oldState && props.overflowX !== 'hidden') {
            scrollX = oldState.scrollX;
        } else {
            scrollX = props.scrollLeft;
        }
        if (oldState && props.overflowY !== 'hidden') {
            scrollY = oldState.scrollY;
        } else {
            scrollState = this._scrollHelper.scrollTo(props.scrollTop);
            firstRowIndex = scrollState.index;
            firstRowOffset = scrollState.offset;
            scrollY = scrollState.position;
        }

        if (this._rowToScrollTo !== undefined) {
            scrollState =
                this._scrollHelper.scrollRowIntoView(this._rowToScrollTo);
            firstRowIndex = scrollState.index;
            firstRowOffset = scrollState.offset;
            scrollY = scrollState.position;
            delete this._rowToScrollTo;
        }

        var groupHeaderHeight = useGroupHeader ? props.groupHeaderHeight : 0;

        if (oldState && props.rowsCount !== oldState.rowsCount) {
            // Number of rows changed, try to scroll to the row from before the
            // change
            var viewportHeight =
                (props.height === undefined ? props.maxHeight : props.height) -
                (props.headerHeight || 0) -
                (props.footerHeight || 0) -
                (props.groupHeaderHeight || 0);
            this._scrollHelper = new TableScrollHelper(
                props.rowsCount,
                props.rowHeight,
                viewportHeight,
                props.rowHeightGetter
            );
            var scrollState =
                this._scrollHelper.scrollToRow(firstRowIndex, firstRowOffset);
            firstRowIndex = scrollState.index;
            firstRowOffset = scrollState.offset;
            scrollY = scrollState.position;
        } else if (oldState && props.rowHeightGetter !== oldState.rowHeightGetter) {
            this._scrollHelper.setRowHeightGetter(props.rowHeightGetter);
        }

        var columnResizingData;
        if (props.isColumnResizing) {
            columnResizingData = oldState && oldState.columnResizingData;
        } else {
            columnResizingData = EMPTY_OBJECT;
        }

        var columns;
        var columnGroups;

        if (useGroupHeader) {
            var columnGroupSettings =
                TableWidthHelper.adjustColumnGroupWidths(
                    children,
                    props.width
                );
            columns = columnGroupSettings.columns;
            columnGroups = columnGroupSettings.columnGroups;
        } else {
            columns = TableWidthHelper.adjustColumnWidths(
                children,
                props.width
            );
        }

        var columnInfo = this._populateColumnsAndColumnData(
            columns,
            columnGroups,
            oldState
        );

        if (this._columnToScrollTo !== undefined) {
            // If selected column is a fixed column, don't scroll
            var fixedColumnsCount = columnInfo.bodyFixedColumns.length;
            if (this._columnToScrollTo >= fixedColumnsCount) {
                var totalFixedColumnsWidth = 0;
                var i, column;
                for (i = 0; i < columnInfo.bodyFixedColumns.length; ++i) {
                    column = columnInfo.bodyFixedColumns[i];
                    totalFixedColumnsWidth += column.props.width;
                }

                var scrollableColumnIndex = Math.min(
                    this._columnToScrollTo - fixedColumnsCount,
                    columnInfo.bodyScrollableColumns.length - 1
                );

                var previousColumnsWidth = 0;
                for (i = 0; i < scrollableColumnIndex; ++i) {
                    column = columnInfo.bodyScrollableColumns[i];
                    previousColumnsWidth += column.props.width;
                }

                var availableScrollWidth = props.width - totalFixedColumnsWidth;
                var selectedColumnWidth = columnInfo.bodyScrollableColumns[
                    scrollableColumnIndex
                    ].props.width;
                var minAcceptableScrollPosition =
                    previousColumnsWidth + selectedColumnWidth - availableScrollWidth;

                if (scrollX < minAcceptableScrollPosition) {
                    scrollX = minAcceptableScrollPosition;
                }

                if (scrollX > previousColumnsWidth) {
                    scrollX = previousColumnsWidth;
                }
            }
            delete this._columnToScrollTo;
        }

        var useMaxHeight = props.height === undefined;
        var height = Math.round(useMaxHeight ? props.maxHeight : props.height);
        var totalHeightReserved = props.footerHeight + props.headerHeight +
            groupHeaderHeight + 2 * BORDER_HEIGHT;
        var bodyHeight = height - totalHeightReserved;
        var scrollContentHeight = this._scrollHelper.getContentHeight();
        var totalHeightNeeded = scrollContentHeight + totalHeightReserved;
        var scrollContentWidth =
            TableWidthHelper.getTotalWidth(columns);

        var maxScrollX = Math.max(0, scrollContentWidth - props.width);
        var maxScrollY = Math.max(0, scrollContentHeight - bodyHeight);
        scrollX = Math.min(scrollX, maxScrollX);
        scrollY = Math.min(scrollY, maxScrollY);

        if (!maxScrollY) {
            // no vertical scrollbar necessary, use the totals we tracked so we
            // can shrink-to-fit vertically
            if (useMaxHeight) {
                height = totalHeightNeeded;
            }
            bodyHeight = totalHeightNeeded - totalHeightReserved;
        }

        this._scrollHelper.setViewportHeight(bodyHeight);

        // The order of elements in this object metters and bringing bodyHeight,
        // height or useGroupHeader to the top can break various features
        var newState = {
            isColumnResizing: oldState && oldState.isColumnResizing,
            // isColumnResizing should be overwritten by value from props if
            // avaialble

            ...columnInfo,
            ...props,

            columns,
            columnGroups,
            columnResizingData,
            firstRowIndex,
            firstRowOffset,
            maxScrollX,
            maxScrollY,
            reservedHeight: totalHeightReserved,
            scrollContentHeight,
            scrollContentWidth,
            offsetX,
            offsetWidth,
            scrollX,
            scrollY,

            // These properties may overwrite properties defined in
            // columnInfo and props
            bodyHeight,
            height,
            groupHeaderHeight,
            useGroupHeader
        };

        return newState;
    },

    _selectColumnElement(/*string*/ type, /*array*/ columns) /*array*/ {
        var newColumns = [];
        for (var i = 0; i < columns.length; ++i) {
            var column = columns[i];
            newColumns.push(React.cloneElement(
                column,
                {
                    cell: type ? column.props[type] : column.props[CELL]
                }
            ));
        }
        return newColumns;
    },

    _splitColumnTypes(/*array*/ columns) /*object*/ {
        var fixedColumns = [];
        var scrollableColumns = [];
        for (var i = 0; i < columns.length; ++i) {
            if (columns[i].props.fixed) {
                fixedColumns.push(columns[i]);
            } else {
                scrollableColumns.push(columns[i]);
            }
        }
        return {
            fixed: fixedColumns,
            scrollable: scrollableColumns
        };
    },

    _onSwipeContainer(deltaX){
        if (!this._isScrolling) {
            this._didScrollStart();
        }
        var x = this.state.offsetX;
        if (deltaX && this.props.overflowX !== 'hidden') {
            x += Math.round(deltaX);
            x = x < 0 ? 0 : x;
            var {...props} = this.props;
            props.width = props.width + x;
            var newState = this._calculateState(props);
            delete newState.width;
            newState.offsetWidth = Math.min(props.width, newState.scrollContentWidth);
            this.setState(newState);
            this.offset.setValue(-x);
        }
        this._didScrollStop();
    },

    _onSwipe(deltaX){
        if (this.isMounted()) {
            if (!this._isScrolling) {
                this._didScrollStart();
            }
            var x = this.state.offsetX;
            if (deltaX && this.props.overflowX !== 'hidden') {
                x += Math.round(deltaX);
                x = x < 0 ? 0 : x;
                x = x > this.state.offsetWidth - this.state.width ? this.state.offsetWidth - this.state.width : x;
                this.offset.setValue(-x);
                this.setState({
                    offsetX: x,
                })
            }
            this._didScrollStop();
        }
    },

    _onWheel(/*number*/ deltaX, /*number*/ deltaY) {
        if (this.isMounted()) {
            if (!this._isScrolling) {
                this._didScrollStart();
            }
            var x = this.state.scrollX;
            if (Math.abs(deltaY) > Math.abs(deltaX) &&
                this.props.overflowY !== 'hidden') {
                var scrollState = this._scrollHelper.scrollBy(Math.round(deltaY));
                var maxScrollY = Math.max(
                    0,
                    scrollState.contentHeight - this.state.bodyHeight
                );
                var headerOffsetTop = this.state.useGroupHeader ? this.state.groupHeaderHeight : 0;
                var bodyOffsetTop = headerOffsetTop + this.state.headerHeight;
                var y = scrollState.offset - this._scrollHelper.getRowPosition(scrollState.index) + bodyOffsetTop;
                this.trans.y.setValue(y);
                this.setState({
                    firstRowIndex: scrollState.index,
                    firstRowOffset: scrollState.offset,
                    scrollY: scrollState.position,
                    scrollContentHeight: scrollState.contentHeight,
                    maxScrollY: maxScrollY
                });
            } else if (deltaX && this.props.overflowX !== 'hidden') {
                x += Math.round(deltaX);
                x = x < 0 ? 0 : x;
                x = x > this.state.maxScrollX ? this.state.maxScrollX : x;
                this.trans.x.setValue(-x);
                this.setState({
                    scrollX: x
                });
            }
            this._didScrollStop();
        }
    },


    _didScrollStart() {
        if (this.isMounted() && !this._isScrolling) {
            this._isScrolling = true;
            if (this.props.onScrollStart) {
                this.props.onScrollStart(this.state.scrollX, this.state.scrollY);
            }
        }
    },

    _didScrollStop() {
        if (this.isMounted() && this._isScrolling) {
            this._isScrolling = false;
            this.setState({redraw: true});
            if (this.props.onScrollEnd) {
                this.props.onScrollEnd(this.state.scrollX, this.state.scrollY);
            }
        }
    }
});

Table.Cell = TableCellDefault;
Table.Column = TableColumn;
Table.ColumnGroup = TableColumnGroup;

module.exports = Table;
