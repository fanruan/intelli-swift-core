import mixin from 'react-mixin'
import ReactDOM from 'react-dom'

import {
    ReactComponentWithImmutableRenderMixin,
    cn,
    sc,
    isNil,
    requestAnimationFrame,
    emptyFunction,
    shallowEqual,
    isEqual,
    each
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
    Promise
} from 'lib'

import {Layout, CenterLayout} from 'layout'

import {Colors, Template, TemplateFactory, WidgetFactory} from 'data'

import {Icon, Table, AutoSizer, TextLink} from 'base'

import {MultiSelectorWidget} from 'widgets'


class TableCell extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static contextTypes = {
        $template: React.PropTypes.object,
        actions: React.PropTypes.object
    };

    static propTypes = {};

    static defaultProps = {
        layer: 0
    };

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
        const {$widget, wId, ...props} = this.props, {...state} = this.state;
        const widget = WidgetFactory.createWidget($widget, wId, TemplateFactory.createTemplate(this.context.$template));
        const dId = props.dId;
        const linkedWidgets = [];
        if (!widget.isDimDimensionByDimensionId(dId)) {
            const linkage = widget.getWidgetLinkage();
            linkage.forEach((link)=> {
                if (link.from === dId) {
                    linkedWidgets.push(link.to);
                }
            });
        }

        return <Layout flex cross={'center'} style={[{width: this.props.width, height: this.props.height}, {
            paddingLeft: props.layer * 30 + 4
        }]}>
            {this._renderContent(linkedWidgets)}
        </Layout>
    }

    _renderContent(linkedWidgets) {
        if (linkedWidgets.length > 0) {
            return <TextLink onPress={()=> {
                linkedWidgets.forEach((toWid)=>{
                    let clicked = {};
                    clicked[this.props.dId] = this.props.clicked;
                    this.context.actions.widgetLinkage(toWid, clicked);
                });
            }} style={styles.linkage} numberOfLines={2}>{this.props.text}</TextLink>
        } else {
            return <Text
                numberOfLines={2}
            >{this.props.text}</Text>
        }
    }

}
mixin.onClass(TableCell, ReactComponentWithImmutableRenderMixin);
const styles = StyleSheet.create({
    linkage: {
        borderBottom: '1px solid #d4dadd'
    }
});
export default TableCell;
