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

import {MultiSelectorWidget} from 'widgets'


class MultiSelectorComponent extends Component {
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
        const items = [];
        const widget = new Widget(props.$$widget);
        for (let i = 0; i < 1000; i++) {
            items.push({
                value: i
            })
        }
        return <MultiSelectorWidget
            style={styles.wrapper}
            type={widget.getSelectType()}
            value={widget.getSelectValue()}
            itemsCreator={()=> {
                const wi = widget.createJson();
                return Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=widget_setting', {
                    method: "POST",
                    body: JSON.stringify({widget: wi, sessionID: BH.sessionID})
                }).then(function (response) {
                    return response.json();
                })
            }}
            width={props.width}
            height={props.height}
        >
        </MultiSelectorWidget>
    }
}
mixin.onClass(MultiSelectorComponent, PureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        backgroundColor: '#fff'
    }
});
export default MultiSelectorComponent
