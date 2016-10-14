import mixin from 'react-mixin'
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import React, {
    Component,
    StyleSheet,
    PropTypes,
    Portal,
    Text,
    View,
    ListView,
    Fetch,
    TouchableBounce,
    TouchableHighlight,
    TouchableOpacity,
    TouchableWithoutFeedback
} from 'lib';

import {ReactComponentWithImmutableRenderMixin} from 'core';
import {Template} from 'data';
import {Layout, AbsoluteLayout} from 'layout';
import * as TodoActions from '../actions/template';

import Main from '../components/Main.js'

//import PanResponderDemo from '../examples/base/2/PanResponder/PanResponder'
//import ViewDemo from '../examples/base/2/View/View'
//import ScrollViewDemo from '../examples/base/2/ScrollView/ScrollView'
//import ListViewDemo from '../examples/base/2/ListView/ListView';
//import PickerDemo from '../examples/base/2/Picker/Picker'
//import DatePickerIOSDemo from '../examples/base/2/DatePickerIOS/DatePickerIOS'
//import ViewPagerDemo from '../examples/base/2/ViewPager/ViewPager'
//import NavigatorDemo from '../examples/base/2/Navigator/Navigator'
//
//import AutoSizerDemo from '../examples/base/3/AutoSizer/AutoSizer';
//import WheelerDemo from '../examples/base/3/Wheeler/Wheeler';
//import SwiperDemo from '../examples/base/3/Swiper/Swiper';
//
//
//import ScrollSyncDemo from '../examples/base/3/ScrollSync/ScrollSync';
//import CellMeasurerDemo from '../examples/base/3/CellMeasurer/CellMeasurer';
//import ColumnSizerDemo from '../examples/base/3/ColumnSizer/ColumnSizer';
//import GridDemo from '../examples/base/3/Grid/Grid';
//import CollectionDemo from '../examples/base/3/Collection/Collection';
//import VirtualScrollDemo from '../examples/base/3/VirtualScroll/VirtualScroll';
//import InfiniteLoaderDemo from '../examples/base/3/InfiniteLoader/InfiniteLoader';
//import ArrowKeyStepperDemo from '../examples/base/3/ArrowKeyStepper/ArrowKeyStepper';
//import GiftedListViewDemo from '../examples/base/3/GiftedListView/GiftedListView'
//import SideMenu from '../examples/base/3/SideMenu/SideMenu'
//import SortableDemo from '../examples/base/3/Sortable/Sortable'
//import Animatable from '../examples/base/3/Animatable/Animatable'
//
//import TableResizeExample from '../examples/base/3/Table/ResizeExample'
//import TableColumnGroupsExample from '../examples/base/3/Table/ColumnGroupsExample'
//import TableFilterExample from '../examples/base/3/Table/FilterExample'
//import TableFlexGrowExample from '../examples/base/3/Table/FlexGrowExample'
//import TableObjectDataExample from '../examples/base/3/Table/ObjectDataExample'
//import TableSortExample from '../examples/base/3/Table/SortExample'

// import DialogDemo from '../examples/base/3/Dialog/Dialog'

//import UIExplorerApp from '../examples/UIExplorer/UIExplorerApp.web'
// import Game2048 from '../examples/2048/Game2048'

import LayoutDemo from '../examples/base/Layout'


class App extends Component {
    static childContextTypes = {
        actions: PropTypes.object,
        $template: PropTypes.object
    };

    getChildContext() {
        const {actions, $template} = this.props;
        return {
            actions,
            $template
        };
    }

    constructor(props, context) {
        super(props, context);
    }

    componentDidMount() {
        setInterval(() => {
            Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=update_session', {
                method: "POST",
                body: JSON.stringify({_t: new Date(), sessionID: BH.sessionID})
            });
        }, 30000);
        window.onbeforeunload = ()=> {
            Fetch(BH.servletURL + '?op=closesessionid', {
                method: "POST",
                body: JSON.stringify({_t: new Date(), sessionID: BH.sessionID})
            });
        };
    }

    render() {
        return (
            <View style={styles.wrapper}>
                <Layout box='mean' style={styles.wrapper}>
                    <Main $template={this.props.$template}/>
                </Layout>
                <Portal />
            </View>
        )
    }
}

const styles = StyleSheet.create({
    wrapper: {
        position: 'absolute',
        left: 0,
        top: 0,
        right: 0,
        bottom: 0
    }
});

App.propTypes = {
    actions: PropTypes.object.isRequired,
    $template: PropTypes.object.isRequired
};
mixin.onClass(App, ReactComponentWithImmutableRenderMixin);

function mapStateToProps(state) {
    const props = {
        $template: state.get('template')
    };
    return props;
}
function mapDispatchToProps(dispatch) {
    const actionMap = {actions: bindActionCreators(TodoActions, dispatch)};
    return actionMap;
}
export default connect(mapStateToProps, mapDispatchToProps)(App);
