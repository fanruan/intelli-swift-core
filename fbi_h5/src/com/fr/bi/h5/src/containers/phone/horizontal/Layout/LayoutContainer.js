import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import {requestAnimationFrame, ReactComponentWithImmutableRenderMixin} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    ViewPagerAndroid,
    View,
    Fetch
} from 'lib'
import {Layout} from 'layout'

import {AutoSizer} from 'base'
import {Colors, TemplateFactory, WidgetFactory} from 'data'

import ChartPaneComponent from '../../../../components/Chart/ChartPaneComponent.js'
import TablePaneComponent from '../../../../components/Table/TablePaneComponent.js'
import DetailTablePaneComponent from '../../../../components/DetailTable/DetailTablePaneComponent.js'
import MultiSelectorComponent from '../../../../components/MultiSelector/MultiSelectorComponent.js'
import MultiTreeSelectorComponent from '../../../../components/MultiTreeSelector/MultiTreeSelectorComponent.js'
import ContentComponent from '../../../../components/Content/ContentComponent'
import ImageComponent from '../../../../components/Image/ImageComponent'
import YearMonthComponent from '../../../../components/YearMonth/YearMonthComponent'
import YearComponent from '../../../../components/Year/YearComponent'
import YearQuarterComponent from '../../../../components/YearQuarter/YearQuarterComponent'
import WebComponent from '../../../../components/Web/WebCompontent'

import LayoutContainerHelper from './LayoutContainerHelper'

class LayoutContainer extends Component {
    static propTypes = {};

    constructor(props, context) {
        super(props, context);
    }

    _onPageScroll() {

    }

    _onPageSelected() {

    }

    render() {
        const {...props} = this.props;
        this._helper = new LayoutContainerHelper(props);

        const rows = this._helper.getAllSortedStatisticWidgetIds();
        return <ViewPagerAndroid
            style={styles.viewPager}
            initialPage={0}
            onPageScroll={this._onPageScroll.bind(this)}
            onPageSelected={this._onPageSelected.bind(this)}
        >
            {rows.map((wId)=> {
                return this._renderRow(wId);
            })}
        </ViewPagerAndroid>;
    }

    _renderRow(wId, sectionID, rowID) {
        const $widget = this._helper.get$WidgetByWidgetId(wId);
        const type = this._helper.getWidgetTypeByWidgetId(wId);
        const props = {
            $widget,
            $template: this._helper.get$Template(),
            wId,
            width: this.props.width - 20,
            height: this.props.height - 20
        };
        let component = null;
        switch (type) {
            case BICst.WIDGET.TABLE:
                component = <TablePaneComponent {...props} />;
                break;
            //case BICst.WIDGET.CROSS_TABLE:
            //case BICst.WIDGET.COMPLEX_TABLE:
            //
            case BICst.WIDGET.DETAIL:
                component = <DetailTablePaneComponent {...props} />;
                break;

            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.PIE :
            case BICst.WIDGET.DONUT :
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
            case BICst.WIDGET.FUNNEL:
                component = <ChartPaneComponent {...props} />;
                break;
            case BICst.WIDGET.NUMBER:
            case BICst.WIDGET.DATE:
            case BICst.WIDGET.YEAR:
            case BICst.WIDGET.QUARTER:
            case BICst.WIDGET.MONTH:
                component = <YearMonthComponent {...props} />;
                break;
            case BICst.WIDGET.YMD:
            case BICst.WIDGET.QUERY:
            case BICst.WIDGET.RESET:
                break;
            case BICst.WIDGET.CONTENT:
                component = <ContentComponent {...props} />;
                break;
            case BICst.WIDGET.IMAGE:
                component = <ImageComponent {...props}/>;
                break;
            case BICst.WIDGET.WEB:
                component = <WebComponent {...props}/>;
                break;
            case BICst.WIDGET.STRING:
                component = <MultiSelectorComponent {...props} />;
                break;
            case BICst.WIDGET.TREE:
                component = <MultiTreeSelectorComponent {...props} />;
                break;
            default:
                break;
        }
        return <Layout flex box='mean' style={styles.wrapper}>
            {component}
        </Layout>
    }
}
mixin.onClass(LayoutContainer, ReactComponentWithImmutableRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        padding: 10
    },
    viewPager: {
        flex: 1
    }
});
export default LayoutContainer
