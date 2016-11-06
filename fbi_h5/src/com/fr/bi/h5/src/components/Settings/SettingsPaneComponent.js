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
    Collapsible
} from 'base'

import SettingsComponent from './SettingsComponent'

class SettingsPaneComponent extends Component {
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

    _renderHeader() {
        const {$widget, wId} = this.props;
        const widget = WidgetFactory.createWidget($widget, wId, TemplateFactory.createTemplate(this.context.$template));
        return <Layout main='justify' cross='center' style={styles.header}>
            <TextLink onPress={()=> {
                this.refs['overlay'].close();
            }} style={styles.back}>{'返回'}</TextLink>
            <Text style={styles.name}>{widget.getName()}</Text>
            <TextLink onPress={()=> {
                this.refs['overlay'].close(true);
            }} style={styles.complete}>{'完成'}</TextLink>
        </Layout>
    }

    render() {
        const {...props} = this.props;
        return <Overlay ref='overlay' onClose={(tag)=> {
            if (tag === true) {
                const {$widget} = this.refs['SettingsComponent'].getValue(), {wId} = this.props;
                this.props.onComplete({$widget, wId});
            } else {
                this.props.onReturn();
            }
        }}>
            <Layout dir='top' box='first' style={styles.wrapper}>
                {this._renderHeader()}
                <SettingsComponent ref='SettingsComponent' {...props}/>
            </Layout>
        </Overlay>
    }

    componentWillUpdate(nextProps, nextState) {

    }

    componentDidUpdate(prevProps, prevState) {

    }

    componentWillUnmount() {

    }

}
mixin.onClass(SettingsPaneComponent, ReactComponentWithPureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        position: 'absolute',
        backgroundColor: '#ffffff',
        left: 10,
        right: 10,
        top: 30,
        bottom: 10
    },
    header: {
        paddingLeft: 20,
        paddingRight: 20,
        color: Colors.TEXT,
        backgroundColor: Colors.HIGHLIGHT,
        height: Sizes.HEADER_HEIGHT
    },
    sortableItems: {
        paddingLeft: 20,
        paddingRight: 20,
        height: Sizes.ITEM_HEIGHT,
        borderBottomWidth: 1 / PixelRatio.get(),
        borderBottomColor: Colors.BORDER
    },
    dragHandler: {
        opacity: 0.25
    },
    collapseHeader: {
        paddingLeft: 20,
        backgroundColor: '#d8f2fd',
        height: Sizes.ITEM_HEIGHT
    },

    sortChangeButton: {
        position: 'absolute',
        right: 20,
        bottom: 20,
        height: 20
    }
});
export default SettingsPaneComponent
