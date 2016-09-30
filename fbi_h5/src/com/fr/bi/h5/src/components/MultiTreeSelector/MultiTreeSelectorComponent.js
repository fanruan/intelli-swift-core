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

import {Table, AutoSizer} from 'base'

import {Template, Widget} from 'data'

import {MultiTreeSelectorWidget} from 'widgets'


class MultiTreeSelectorComponent extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {};

    state = {};

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillReceiveProps() {

    }

    componentWillUpdate() {

    }

    render() {
        const {...props} = this.props;
        const widget = new Widget(props.$$widget);
        return <MultiTreeSelectorWidget
            style={styles.wrapper}
            floors={widget.getTreeFloors()}
            value={widget.getSelectedTreeValue()}
            itemsCreator={(options)=> {
                return widget.getData(options);
            }}
            width={props.width}
            height={props.height}
        >
        </MultiTreeSelectorWidget>
    }
}
mixin.onClass(MultiTreeSelectorComponent, PureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        backgroundColor: '#fff'
    }
});
export default MultiTreeSelectorComponent
