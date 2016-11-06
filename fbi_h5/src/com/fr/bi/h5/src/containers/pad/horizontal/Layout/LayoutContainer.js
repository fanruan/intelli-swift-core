import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import {requestAnimationFrame, ReactComponentWithImmutableRenderMixin, translateDOMPositionXY} from 'core'
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
    static contextTypes = {
        actions: React.PropTypes.object,
        $template: React.PropTypes.object
    };
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
        const wIds = this._helper.getAllWidgetIds();
        return <View style={styles.container}>
            {wIds.map((wId)=> {
                return this._renderRow(wId);
            })}
        </View>
    }

    _renderRow(wId, sectionID, rowID) {
        const type = this._helper.getWidgetTypeByWidgetId(wId);
        const layout = this._helper.getWidgetLayoutByWidgetId(wId);
        let style = {width: layout.width, height: layout.height};
        translateDOMPositionXY(style, layout.left, layout.top);
        const props = {
            $widget: this._helper.get$WidgetByWidgetId(wId),
            $template: this._helper.get$Template(),
            wId,
            width: layout.width - 20,
            height: layout.height - 20,
            onValueChange: ($template)=> {
                this.context.actions.query($template)
            }
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
        return <Layout flex box='mean' style={[styles.wrapper, style]}>
            {component}
        </Layout>
    }
}
mixin.onClass(LayoutContainer, ReactComponentWithImmutableRenderMixin);
const styles = StyleSheet.create({
    container: {
        position: 'relative',
        overflow: 'auto',
        flex: 1
    },
    wrapper: {
        position: 'absolute',
        padding: 10
    }
});
export default LayoutContainer
