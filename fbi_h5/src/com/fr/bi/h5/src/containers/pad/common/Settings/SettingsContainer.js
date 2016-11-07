import mixin from 'react-mixin'
import {findDOMNode} from 'react-dom'
import Immutable from 'immutable'

import {
    ReactComponentWithPureRenderMixin, ReactComponentWithImmutableRenderMixin,
    cn, sc, math, isNil, emptyFunction, shallowEqual, immutableShallowEqual, isEqual, isEmpty, each, map, clone,
    translateDOMPositionXY, requestAnimationFrame
} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    PixelRatio,
    ListView,
    View,
    Fetch,
    Promise,
    ScrollView,
    TouchableHighlight,
    TouchableWithoutFeedback
} from 'lib'

import {Colors, Sizes, TemplateFactory, WidgetFactory} from 'data'

import {Layout, CenterLayout} from 'layout'
import {
    Button,
    IconButton,
    TextButton,
    Icon,
    TextLink,
    Table,
    Overlay,
    Sortable,
    Collapsible,
    Dialog
} from 'base'

import SettingsComponent from '../../../../components/Settings/SettingsComponent'

class SettingsContainer extends Component {
    static contextTypes = {
        actions: React.PropTypes.object
    };

    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        onReturn: emptyFunction,
        onComplete: emptyFunction
    };

    _getNextState(props, state = {}) {

    }

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillReceiveProps(nextProps) {

    }

    render() {
        const {...props} = this.props;
        return <Dialog ref='overlay' title={'维度指标配置'} direction={'right'} onClose={(tag)=> {
            if (tag === '确定') {
                const {$widget} = this.refs['SettingsComponent'].getValue(), {wId} = this.props;
                this.props.onComplete({$widget, wId});
            } else {
                this.props.onReturn();
            }
        }}>
            <SettingsComponent ref='SettingsComponent' {...props} contentWidth={217}/>
        </Dialog>
    }

    componentWillUpdate(nextProps, nextState) {

    }

    componentDidUpdate(prevProps, prevState) {

    }

    componentWillUnmount() {

    }

}
mixin.onClass(SettingsContainer, ReactComponentWithPureRenderMixin);
export default SettingsContainer
