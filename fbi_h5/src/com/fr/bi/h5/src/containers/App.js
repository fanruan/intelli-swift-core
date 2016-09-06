/* CAUTION: When using the generators, this file is modified in some places.
 *          This is done via AST traversal - Some of your formatting may be lost
 *          in the process - no functionality should be broken though.
 *          This modifications only run once when the generator is invoked - if
 *          you edit them, they are not updated again.
 */

import React, {
  Component,
  PropTypes,
  Text,
  View,
  ListView,
  TouchableBounce,
  TouchableHighlight,
  TouchableOpacity,
  TouchableWithoutFeedback
} from 'lib';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import {Template} from 'data';
import * as TodoActions from '../actions/todos';
require('styles/App.css');

import Main from '../components/Main.js'

//import PanResponderDemo from '../examples/base/2/PanResponder/PanResponder'
//import ViewDemo from '../examples/base/2/View/View'
//import ScrollViewDemo from '../examples/base/2/ScrollView/ScrollView'
//import ListViewDemo from '../examples/base/2/ListView/ListView';
//import PickerDemo from '../examples/base/2/Picker/Picker'
import DatePickerIOSDemo from '../examples/base/2/DatePickerIOS/DatePickerIOS'
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


// import UIExplorerApp from '../examples/UIExplorer/UIExplorerApp.web'

/* Populated by react-webpack-redux:reducer */
class App extends Component {
  render() {
    const {template, actions} = this.props;
    return (
      <DatePickerIOSDemo template={new Template(template)} actions={actions} />
    )
  }
}

/* Populated by react-webpack-redux:reducer
 *
 * HINT: if you adjust the initial type of your reducer, you will also have to
 *       adjust it here.
 */
App.propTypes = {
  actions: PropTypes.object.isRequired,
  template: PropTypes.object.isRequired
};
function mapStateToProps(state) {
  /* Populated by react-webpack-redux:reducer */
  const props = {
    template: state.template
  };
  return props;
}
function mapDispatchToProps(dispatch) {
  /* Populated by react-webpack-redux:action */
  const actionMap = {actions: bindActionCreators(TodoActions, dispatch)};
  return actionMap;
}
export default connect(mapStateToProps, mapDispatchToProps)(App);
