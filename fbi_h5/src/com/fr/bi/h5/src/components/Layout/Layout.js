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

import {AutoSizer} from 'base'
import {Colors, Template, Widget} from 'data'

import ChartComponent from '../Chart/ChartComponent.js'
import TableComponent from '../Table/TableComponent.js'
import DetailTableComponent from '../DetailTable/DetailTableComponent.js'
import MultiSelectorComponent from '../MultiSelector/MultiSelectorComponent.js'
import MultiTreeSelectorComponent from '../MultiTreeSelector/MultiTreeSelectorComponent.js'

class Layout extends Component {
    static propTypes = {};

    constructor(props, context) {
        super(props, context);
        const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
        this.template = new Template(props.$$template);
        const rows = this.template.getAllWidgetIds();
        this.state = {
            dataSource: ds.cloneWithRows(rows)
        }
    }

    _onPageScroll() {

    }

    _onPageSelected() {

    }

    render() {
        const {...props} = this.props;
        return <ViewPagerAndroid
            style={styles.viewPager}
            initialPage={0}
            onPageScroll={this._onPageScroll.bind(this)}
            onPageSelected={this._onPageSelected.bind(this)}
            ref={viewPager => {
                this.viewPager = viewPager;
            }}>
            {[<ListView
                {...props}
                initialListSize={Math.ceil(props.height / 270) + 1}
                dataSource={this.state.dataSource}
                renderRow={this._renderRow.bind(this)}
            />, <ListView
                {...props}
                initialListSize={Math.ceil(props.height / 270) + 1}
                dataSource={this.state.dataSource}
                renderRow={this._renderRow.bind(this)}
            />]}
        </ViewPagerAndroid>;
        // return <ListView
        //     {...props}
        //     initialListSize={Math.floor(props.height / 270) + 1}
        //     dataSource={this.state.dataSource}
        //     renderRow={this._renderRow.bind(this)}
        // />;
    }

    _renderRow(wId, sectionID, rowID) {
        const {$$template} = this.props;
        const $$widget = this.template.get$$WidgetById(wId);
        const type = new Widget($$widget).getType();
        const props = {
            key: wId,
            $$widget,
            $$template,
            wId,
            width: this.props.width - 40,
            height: 230
        };
        let component = null;
        switch (type) {
            case BICst.WIDGET.TABLE:
                component = <TableComponent {...props} />;
                break;
            //case BICst.WIDGET.CROSS_TABLE:
            //case BICst.WIDGET.COMPLEX_TABLE:
            //
            case BICst.WIDGET.DETAIL:
                component = <DetailTableComponent {...props} />;
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
                //case BICst.WIDGET.STRING:
                //case BICst.WIDGET.NUMBER:
                //case BICst.WIDGET.DATE:
                //case BICst.WIDGET.YEAR:
                //case BICst.WIDGET.QUARTER:
                //case BICst.WIDGET.MONTH:
                //case BICst.WIDGET.YMD:
                //case BICst.WIDGET.QUERY:
                //case BICst.WIDGET.RESET:
                //case BICst.WIDGET.CONTENT:
                //case BICst.WIDGET.IMAGE:
                //case BICst.WIDGET.WEB:
                component = <ChartComponent {...props} />;
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
        return <View>
            <View style={styles.wrapper}>
                {component}
            </View>
        </View>
    }
}
mixin.onClass(Layout, ReactComponentWithImmutableRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        margin: 20
    },
    viewPager: {
        flex: 1
    }
});
export default Layout
