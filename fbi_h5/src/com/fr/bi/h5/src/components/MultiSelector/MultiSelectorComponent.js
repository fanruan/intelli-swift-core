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

import {MultiSelectorWidget} from 'widgets'


class MultiSelectorComponent extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        onValueChange: emptyFunction
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
        const {...props} = this.props;
        const wId = props.wId;
        const template = TemplateFactory.createTemplate(props.$template);
        const widget = template.getWidgetByWidgetId(wId);
        return <MultiSelectorWidget
            style={styles.wrapper}
            type={widget.getSelectType()}
            value={widget.getSelectValue()}
            itemsCreator={(options)=> {
                return widget.getData(options);
            }}
            width={props.width}
            height={props.height}
            onValueChange={(value)=> {
                widget.setWidgetValue(value);
                template.set$Widget(wId, widget.$get());
                this.props.onValueChange(template.$get());
            }}
        >
        </MultiSelectorWidget>
    }
}
mixin.onClass(MultiSelectorComponent, ReactComponentWithImmutableRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        backgroundColor: '#fff'
    }
});
export default MultiSelectorComponent
