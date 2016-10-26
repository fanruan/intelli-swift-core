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
        const linkStyle = [];
        if (!widget.isDimensionById(dId)) {
            const linkage = widget.getWidgetLinkage();
            const linkedWidgets = [];
            linkage.forEach((link)=> {
                if (link.from === dId) {
                    linkedWidgets.push(link.to);
                }
            });
            if (linkedWidgets.length > 0) {
                linkStyle.push(styles.linkage);
            }
        }

        return <Layout flex cross={'center'} style={[{width: this.props.width, height: this.props.height}, {
            paddingLeft: props.layer * 30 + 4
        }]}>
            {this._renderContent(props.text, linkStyle)}
        </Layout>
    }

    _renderContent(text, style) {
        if (style.length > 0) {
            return <TextLink onPress={()=> {
                console.log(text);
            }} style={style} numberOfLines={2}>{text}</TextLink>
        } else {
            return <Text
                numberOfLines={2}
            >{text}</Text>
        }
    }

}
mixin.onClass(TableCell, ReactComponentWithImmutableRenderMixin);
const styles = StyleSheet.create({
    linkage: {
        borderBottom: '1px solid #d4dadd'
    }
});
export default TableCell
