var React = require('react');
var ReactComponentWithPureRenderMixin = require('react-addons-pure-render-mixin');
require('./ScrollableWheeler.css');
var {WheelHandler, translateDOMPositionXY} = require('core');
var {View} = require('lib');

var Scrollbar = require('./Scrollbar.js');
var ScrollHelper = require('./ScrollHelper');

var debounce = require('lodash/debounce');
var emptyFunction = require('fbjs/lib/emptyFunction');
var invariant = require('fbjs/lib/invariant');

var {PropTypes} = React;

var BORDER_HEIGHT = 1;

var ScrollableWheeler = React.createClass({

    propTypes: {

        width: PropTypes.number.isRequired,

        height: PropTypes.number,

        maxHeight: PropTypes.number,

        ownerHeight: PropTypes.number,

        scrollContentWidth: PropTypes.number,

        scrollContentWidthGetter: PropTypes.func,

        overflowX: PropTypes.oneOf(['hidden', 'auto']),
        overflowY: PropTypes.oneOf(['hidden', 'auto']),

        rowsCount: PropTypes.number.isRequired,

        rowHeight: PropTypes.number.isRequired,

        rowHeightGetter: PropTypes.func,

        headerHeight: PropTypes.number.isRequired,

        footerHeight: PropTypes.number,

        scrollLeft: PropTypes.number,

        scrollToColumn: PropTypes.number,

        scrollTop: PropTypes.number,

        scrollToRow: PropTypes.number,

        onScrollStart: PropTypes.func,

        onScrollEnd: PropTypes.func,

        onContentHeightChange: PropTypes.func
    },

    getDefaultProps() /*object*/ {
        return {
            footerHeight: 0,
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
            (props.footerHeight || 0);
        this._scrollHelper = new ScrollHelper(
            props.rowsCount,
            props.rowHeight,
            viewportHeight,
            props.rowHeightGetter
        );
        if (props.scrollTop) {
            this._scrollHelper.scrollTo(props.scrollTop);
        }
        this._didScrollStop = debounce(this._didScrollStop, 200).bind(this);

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

    componentDidMount() {
        this._reportContentHeight();
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

        var maxScrollY = this.state.maxScrollY;
        var showScrollbarX = state.maxScrollX > 0 && state.overflowX !== 'hidden';
        var showScrollbarY = maxScrollY > 0 && state.overflowY !== 'hidden';
        var scrollbarXHeight = showScrollbarX ? Scrollbar.SIZE : 0;
        var scrollbarYHeight = state.height - scrollbarXHeight -
            (2 * BORDER_HEIGHT) - state.footerHeight;

        var headerOffsetTop = 0;
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
                props.ownerHeight - state.footerHeight - scrollbarXHeight
            );

            scrollbarYHeight = Math.max(0, footOffsetTop - bodyOffsetTop);
        }

        var verticalScrollbar;
        if (showScrollbarY) {
            verticalScrollbar =
                <Scrollbar
                    size={scrollbarYHeight}
                    contentSize={scrollbarYHeight + maxScrollY}
                    onScroll={this._onVerticalScroll}
                    verticalTop={bodyOffsetTop}
                    position={state.scrollY}
                />;
        }

        var horizontalScrollbar;
        if (showScrollbarX) {
            var scrollbarXWidth = state.width;
            horizontalScrollbar =
                <HorizontalScrollbar
                    contentSize={scrollbarXWidth + state.maxScrollX}
                    offset={bottomSectionOffset}
                    onScroll={this._onHorizontalScroll}
                    position={state.scrollX}
                    size={scrollbarXWidth}
                />;
        }

        var topShadow;
        var bottomShadow;
        if (state.scrollY) {
            topShadow =
                <View
                    className={'ScrollableWheeler-topShadow'}
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
                    className={'ScrollableWheeler-bottomShadow'}
                    style={{top: footOffsetTop}}
                />;
        }

        var rowPositionGetter = this._scrollHelper.getRowPosition.bind(this._scrollHelper);
        var isScrolling = this._isScrolling;
        return (
            <View
                className={'ScrollableWheeler-main'}
                onWheel={this._wheelHandler.onWheel}
                style={{height: state.height, width: state.width, ...props.style}}>
                <View
                    className={'ScrollableWheeler-rowsContainer'}
                    style={{height: rowsContainerHeight, width: state.width}}>
                    {props.children({...this.state, rowPositionGetter, isScrolling})}
                    {topShadow}
                    {bottomShadow}
                </View>
                {verticalScrollbar}
                {horizontalScrollbar}
            </View>
        );
    },

    _calculateState(/*object*/ props, /*?object*/ oldState) /*object*/ {
        invariant(
            props.height !== undefined || props.maxHeight !== undefined,
            'You must set either a height or a maxHeight'
        );

        var firstRowIndex = (oldState && oldState.firstRowIndex) || 0;
        var firstRowOffset = (oldState && oldState.firstRowOffset) || 0;
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

        if (oldState && props.rowsCount !== oldState.rowsCount) {
            // Number of rows changed, try to scroll to the row from before the
            // change
            var viewportHeight =
                (props.height === undefined ? props.maxHeight : props.height) -
                (props.headerHeight || 0) -
                (props.footerHeight || 0);
            this._scrollHelper = new ScrollHelper(
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

        var useMaxHeight = props.height === undefined;
        var height = Math.round(useMaxHeight ? props.maxHeight : props.height);
        var totalHeightReserved = props.footerHeight + props.headerHeight +
            2 * BORDER_HEIGHT;
        var bodyHeight = height - totalHeightReserved;
        var scrollContentHeight = this._scrollHelper.getContentHeight();
        var totalHeightNeeded = scrollContentHeight + totalHeightReserved;
        var scrollContentWidth = props.scrollContentWidthGetter && props.scrollContentWidthGetter();
        scrollContentWidth = scrollContentWidth || props.scrollContentWidth || props.width;

        var horizontalScrollbarVisible = scrollContentWidth > props.width &&
            props.overflowX !== 'hidden';

        if (horizontalScrollbarVisible) {
            bodyHeight -= Scrollbar.SIZE;
            totalHeightNeeded += Scrollbar.SIZE;
            totalHeightReserved += Scrollbar.SIZE;
        }

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

            ...props,

            firstRowIndex,
            firstRowOffset,
            horizontalScrollbarVisible,
            maxScrollX,
            maxScrollY,
            reservedHeight: totalHeightReserved,
            scrollContentHeight,
            scrollX,
            scrollY,

            // These properties may overwrite properties defined in
            // columnInfo and props
            bodyHeight,
            height
        };

        return newState;
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
                this.setState({
                    firstRowIndex: scrollState.index,
                    firstRowOffset: scrollState.offset,
                    scrollY: scrollState.position,
                    scrollContentHeight: scrollState.contentHeight,
                    maxScrollY: maxScrollY
                });
            } else if (deltaX && this.props.overflowX !== 'hidden') {
                x += deltaX;
                x = x < 0 ? 0 : x;
                x = x > this.state.maxScrollX ? this.state.maxScrollX : x;
                this.setState({
                    scrollX: x
                });
            }

            this._didScrollStop();
        }
    },


    _onHorizontalScroll(/*number*/ scrollPos) {
        if (this.isMounted() && scrollPos !== this.state.scrollX) {
            if (!this._isScrolling) {
                this._didScrollStart();
            }
            this.setState({
                scrollX: scrollPos
            });
            this._didScrollStop();
        }
    },

    _onVerticalScroll(/*number*/ scrollPos) {
        if (this.isMounted() && scrollPos !== this.state.scrollY) {
            if (!this._isScrolling) {
                this._didScrollStart();
            }
            var scrollState = this._scrollHelper.scrollTo(Math.round(scrollPos));
            this.setState({
                firstRowIndex: scrollState.index,
                firstRowOffset: scrollState.offset,
                scrollY: scrollState.position,
                scrollContentHeight: scrollState.contentHeight
            });
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

var HorizontalScrollbar = React.createClass({
    mixins: [ReactComponentWithPureRenderMixin],
    propTypes: {
        contentSize: PropTypes.number.isRequired,
        offset: PropTypes.number.isRequired,
        onScroll: PropTypes.func.isRequired,
        position: PropTypes.number.isRequired,
        size: PropTypes.number.isRequired
    },

    render() /*object*/ {
        var outerContainerStyle = {
            height: Scrollbar.SIZE,
            width: this.props.size
        };
        var innerContainerStyle = {
            height: Scrollbar.SIZE,
            position: 'absolute',
            overflow: 'hidden',
            width: this.props.size
        };
        translateDOMPositionXY(innerContainerStyle, 0, this.props.offset);

        return (
            <View
                className={'ScrollableWheeler-horizontalScrollbar'}
                style={outerContainerStyle}>
                <View className={''} style={innerContainerStyle}>
                    <Scrollbar
                        {...this.props}
                        isOpaque={true}
                        orientation="horizontal"
                        offset={undefined}
                    />
                </View>
            </View>
        );
    }
});

export default ScrollableWheeler;
