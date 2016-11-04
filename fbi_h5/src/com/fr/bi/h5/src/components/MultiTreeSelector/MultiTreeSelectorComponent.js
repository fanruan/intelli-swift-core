import mixin from 'react-mixin'
import ReactDOM from 'react-dom'

import {ReactComponentWithImmutableRenderMixin, requestAnimationFrame, emptyFunction} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch
} from 'lib'

import {Table, AutoSizer} from 'base'

import {TemplateFactory, WidgetFactory} from 'data'

import {MultiTreeSelectorWidget} from 'widgets'


class MultiTreeSelectorComponent extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {};

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
        const {...props} = this.props;
        const wId = props.wId;
        const template = TemplateFactory.createTemplate(props.$template);
        const widget = template.getWidgetByWidgetId(wId);
        return <MultiTreeSelectorWidget
            style={styles.wrapper}
            floors={widget.getTreeFloors()}
            value={widget.getSelectedTreeValue()}
            itemsCreator={(options)=> {
                return widget.getData(options);
            }}
            onValueChange={(value)=> {
                widget.setWidgetValue(value);
                template.set$Widget(wId, widget.$get());
                this.props.onValueChange(template.$get());
            }}
            width={props.width}
            height={props.height}
        >
        </MultiTreeSelectorWidget>
    }
}
mixin.onClass(MultiTreeSelectorComponent, ReactComponentWithImmutableRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        backgroundColor: '#fff'
    }
});
export default MultiTreeSelectorComponent
