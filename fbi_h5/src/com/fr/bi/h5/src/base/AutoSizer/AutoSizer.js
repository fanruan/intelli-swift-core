import React, {Component, PropTypes} from 'react'
import ReactDOM from 'react-dom'
import mixin from 'react-mixin'
import ReactComponentWithPureRenderMixin from 'react-addons-pure-render-mixin'
import {detectElementResize, emptyFunction} from 'core'
import {View} from 'lib'

class AutoSizer extends Component {
    static propTypes = {
        children: PropTypes.func.isRequired,

        disableHeight: PropTypes.bool,

        disableWidth: PropTypes.bool,

        onResize: PropTypes.func.isRequired
    };

    static defaultProps = {
        onResize: emptyFunction
    };

    state = {
        height: 0,
        width: 0
    };

    constructor(props) {
        super(props);

        this._onResize = this._onResize.bind(this);
        this._onScroll = this._onScroll.bind(this);
        this._setRef = this._setRef.bind(this);
    }

    componentDidMount() {
        this._detectElementResize = detectElementResize;
        this._detectElementResize.addResizeListener(this._parentNode, this._onResize);

        this._onResize()
    }

    componentWillUnmount() {
        if (this._detectElementResize) {
            this._detectElementResize.removeResizeListener(this._parentNode, this._onResize)
        }
    }

    render() {
        const {children, disableHeight, disableWidth} = this.props;
        const {height, width} = this.state;

        const outerStyle = {overflow: 'visible'};

        if (!disableHeight) {
            outerStyle.height = 0
        }

        if (!disableWidth) {
            outerStyle.width = 0
        }

        return (
            <View
                ref={this._setRef}
                onScroll={this._onScroll}
                style={outerStyle}
            >
                {children({height, width})}
            </View>
        )
    }

    _onResize() {
        const {onResize} = this.props;

        const boundingRect = this._parentNode.getBoundingClientRect();
        const height = boundingRect.height || 0;
        const width = boundingRect.width || 0;

        const style = getComputedStyle(this._parentNode);
        const paddingLeft = parseInt(style.paddingLeft, 10) || 0;
        const paddingRight = parseInt(style.paddingRight, 10) || 0;
        const paddingTop = parseInt(style.paddingTop, 10) || 0;
        const paddingBottom = parseInt(style.paddingBottom, 10) || 0;

        this.setState({
            height: height - paddingTop - paddingBottom,
            width: width - paddingLeft - paddingRight
        });

        onResize({height, width})
    }

    _onScroll(event) {
        event.stopPropagation()
    }

    _setRef(autoSizer) {
        this._parentNode = ReactDOM.findDOMNode(autoSizer).parentNode
    }
}
mixin.onClass(AutoSizer, ReactComponentWithPureRenderMixin);

export default AutoSizer
