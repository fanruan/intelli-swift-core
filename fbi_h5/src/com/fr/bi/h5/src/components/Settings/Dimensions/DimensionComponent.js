import mixin from 'react-mixin'
import {findDOMNode} from 'react-dom'
import Immutable from 'immutable'

import {
    ReactComponentWithPureRenderMixin, ReactComponentWithImmutableRenderMixin,
    cn, sc, math, isNil, emptyFunction, shallowEqual, immutableShallowEqual, isEqual, isEmpty, each,
    translateDOMPositionXY, requestAnimationFrame
} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Portal,
    PixelRatio,
    ListView,
    View,
    Fetch,
    Promise,
    TouchableHighlight
} from 'lib'

import {Colors, Sizes, TemplateFactory, WidgetFactory, DimensionFactory} from 'data'

import {Layout, CenterLayout, HorizontalCenterLayout, VerticalCenterLayout} from 'layout';

import {Button, IconButton, TextButton, Table, ActionSheet} from 'base'

import {MultiSelectorWidget} from 'widgets'

import DimensionComponentHelper from './DimensionComponentHelper'
import DimensionSortComponent from './Sort/DimensionSortComponent'


class DimensionComponent extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        wId: '',
        $widget: null,
        dId: '',
        value: {}
    };

    state = {};

    _getNextState(props, state = {}) {

    }

    componentWillMount() {

    }

    componentDidMount() {

    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        this._helper = new DimensionComponentHelper(props, this.context);
        return <Button onPress={()=> {
            this.props.onValueChange(this._helper.switchSelect());
        }}>
            <Layout main='justify' box='mean' style={styles.wrapper}>
                <Layout cross='center' box='first'>
                    <IconButton style={styles.icon} invalid={true} selected={this._helper.isUsed()}
                                className={'single-select-font'}/>
                    <Text numberOfLines={1} style={sc([styles.disabledText, !this._helper.isUsed()])}
                          effect={false}>{props.value.text}</Text>
                </Layout>
                <Layout cross='center' box='last'>
                    <Text numberOfLines={1}
                          style={[sc([styles.disabledText, !this._helper.isUsed()]), styles.sortTargetName]}
                          effect={false}>{this._helper.getSortTargetName()}</Text>
                    <IconButton style={styles.sortIcon} onPress={()=> {
                        Portal.showModal('DimensionSort', <ActionSheet
                            onClose={(tag)=> {
                                if (tag === '取消') {

                                } else if (tag === '确定') {

                                }
                                Portal.closeModal('DimensionSort')
                            }}
                        >
                            <DimensionSortComponent

                            />
                        </ActionSheet>)
                    }}
                                className={this._helper.getSortTargetTypeFont()}/>
                </Layout>
            </Layout>
        </Button>
    }

    componentWillReceiveProps(nextProps) {

    }

    componentWillUpdate(nextProps, nextState) {

    }

    componentDidUpdate(prevProps, prevState) {

    }

    componentWillUnmount() {

    }

}
mixin.onClass(DimensionComponent, ReactComponentWithImmutableRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        paddingLeft: 20,
        paddingRight: 20,
        height: Sizes.ITEM_HEIGHT,
        borderBottomWidth: 1 / PixelRatio.get(),
        borderBottomColor: Colors.BORDER
    },
    icon: {
        width: 40,
    },

    sortIcon: {
        width: 20
    },

    sortTargetName: {
        paddingLeft: 10,
        paddingRight: 10,
        textAlign: 'right'
    },

    disabledText: {
        color: Colors.DISABLED
    }
});
export default DimensionComponent
