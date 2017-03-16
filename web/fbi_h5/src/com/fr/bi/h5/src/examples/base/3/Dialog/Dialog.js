import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import {requestAnimationFrame, emptyFunction} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch
} from 'lib'

import {Dialog} from 'base'


class DialogDemo extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {};

    state = {};

    componentDidMount() {
    }

    render() {
        const {...props} = this.props;
        return <Dialog
            title={'title'}
            choose1={'choose1'}
            choose2={'choose2'}
            visible={true}
        >
        </Dialog>
    }

}
mixin.onClass(DialogDemo, PureRenderMixin);
const style = StyleSheet.create({
    region: {
        position: 'absolute'
    }
});
export default DialogDemo
