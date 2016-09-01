import {Component, PropTypes} from 'react'
import mixin from 'react-mixin'
import ReactComponentWithPureRenderMixin from 'react-addons-pure-render-mixin'

class ScrollSync extends Component {
    static propTypes = {
        children: PropTypes.func.isRequired
    };

    state = {
        clientHeight: 0,
        clientWidth: 0,
        scrollHeight: 0,
        scrollLeft: 0,
        scrollTop: 0,
        scrollWidth: 0
    };

    constructor(props, context) {
        super(props, context);

        this._onScroll = this._onScroll.bind(this)
    }

    render() {
        const {children} = this.props;
        const {clientHeight, clientWidth, scrollHeight, scrollLeft, scrollTop, scrollWidth} = this.state;

        return children({
            clientHeight,
            clientWidth,
            onScroll: this._onScroll,
            scrollHeight,
            scrollLeft,
            scrollTop,
            scrollWidth
        })
    }

    _onScroll({clientHeight, clientWidth, scrollHeight, scrollLeft, scrollTop, scrollWidth}) {
        this.setState({clientHeight, clientWidth, scrollHeight, scrollLeft, scrollTop, scrollWidth})
    }
}
mixin.onClass(ScrollSync, ReactComponentWithPureRenderMixin);

export default ScrollSync
