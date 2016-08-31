import React, {Component, PropTypes} from 'react'
import ReactDOM from 'react-dom'
import mixin from 'react-mixin'
import ReactComponentWithPureRenderMixin from 'react-addons-pure-render-mixin'
import {emptyFunction,requestAnimationFrame,cancelAnimationFrame} from 'core'
import {View} from 'lib'

const IS_SCROLLING_TIMEOUT = 150;

class WindowScroller extends Component {
    static propTypes = {
        children: PropTypes.func.isRequired,

        onResize: PropTypes.func.isRequired,

        onScroll: PropTypes.func.isRequired
    };

    static defaultProps = {
        onResize: emptyFunction,
        onScroll: emptyFunction
    };

    constructor(props) {
        super(props);

        this._onScrollWindow = this._onScrollWindow.bind(this);
        this._onResizeWindow = this._onResizeWindow.bind(this);
        this._enablePointerEventsAfterDelayCallback = this._enablePointerEventsAfterDelayCallback.bind(this);
    }

    state = {
        isScrolling: false,
        height: 0,
        scrollTop: 0
    };

    componentDidMount() {
        this._positionFromTop = ReactDOM.findDOMNode(this).getBoundingClientRect().top;

        this.setState({height: window.innerHeight});

        window.addEventListener('scroll', this._onScrollWindow, false);
        window.addEventListener('resize', this._onResizeWindow, false);
    }

    componentWillUnmount() {
        window.removeEventListener('scroll', this._onScrollWindow, false);
        window.removeEventListener('resize', this._onResizeWindow, false);
    }

    _setNextState(state) {
        if (this._setNextStateAnimationFrameId) {
            cancelAnimationFrame(this._setNextStateAnimationFrameId)
        }

        this._setNextStateAnimationFrameId = requestAnimationFrame(() => {
            this._setNextStateAnimationFrameId = null;
            this.setState(state)
        })
    }

    render() {
        const {children} = this.props;
        const {isScrolling, scrollTop, height} = this.state;

        return (
            <View>
                {children({
                    height,
                    isScrolling,
                    scrollTop
                })}
            </View>
        )
    }

    _enablePointerEventsAfterDelay() {
        if (this._disablePointerEventsTimeoutId) {
            clearTimeout(this._disablePointerEventsTimeoutId)
        }

        this._disablePointerEventsTimeoutId = setTimeout(
            this._enablePointerEventsAfterDelayCallback,
            IS_SCROLLING_TIMEOUT
        )
    }

    _enablePointerEventsAfterDelayCallback() {
        this._disablePointerEventsTimeoutId = null;

        document.body.style.pointerEvents = this._originalBodyPointerEvents;

        this._originalBodyPointerEvents = null;

        this.setState({
            isScrolling: false
        })
    }

    _onResizeWindow(event) {
        const {onResize} = this.props;

        const height = window.innerHeight || 0;

        this.setState({height});

        onResize({height})
    }

    _onScrollWindow(event) {
        const {onScroll} = this.props;

        // In IE10+ scrollY is undefined, so we replace that with the latter
        const scrollY = ('scrollY' in window)
            ? window.scrollY
            : document.documentElement.scrollTop;

        const scrollTop = Math.max(0, scrollY - this._positionFromTop);

        if (this._originalBodyPointerEvents == null) {
            this._originalBodyPointerEvents = document.body.style.pointerEvents;

            document.body.style.pointerEvents = 'none';

            this._enablePointerEventsAfterDelay()
        }

        const state = {
            isScrolling: true,
            scrollTop
        };

        if (!this.state.isScrolling) {
            this.setState(state)
        } else {
            this._setNextState(state)
        }

        onScroll({scrollTop})
    }
}
mixin.onClass(WindowScroller, ReactComponentWithPureRenderMixin);

export default WindowScroller
