/** @flow */
import React, {Component, PropTypes} from 'react'
import mixin from 'react-mixin'
import ReactComponentWithPureRenderMixin from 'react-addons-pure-render-mixin'
import {Keys} from 'core'
import {View} from 'lib'

class ArrowKeyStepper extends Component {
    static propTypes = {
        children: PropTypes.func.isRequired,
        className: PropTypes.string,
        columnCount: PropTypes.number.isRequired,
        rowCount: PropTypes.number.isRequired
    };

    constructor(props, context) {
        super(props, context);

        this.state = {
            scrollToColumn: 0,
            scrollToRow: 0
        };

        this._columnStartIndex = 0;
        this._columnStopIndex = 0;
        this._rowStartIndex = 0;
        this._rowStopIndex = 0;

        this._onKeyDown = this._onKeyDown.bind(this);
        this._onSectionRendered = this._onSectionRendered.bind(this);
    }

    render() {
        const {children} = this.props;
        const {scrollToColumn, scrollToRow} = this.state;

        return (
            <View
                {...this.props}
                onKeyDown={this._onKeyDown}
            >
                {children({
                    onSectionRendered: this._onSectionRendered,
                    scrollToColumn,
                    scrollToRow
                })}
            </View>
        )
    }

    _onKeyDown(event) {
        const {columnCount, rowCount} = this.props;

        // The above cases all prevent default event event behavior.
        // This is to keep the grid from scrolling after the snap-to update.
        switch (event.keyCode) {
            case Keys.DOWN:
                event.preventDefault();
                this.setState({
                    scrollToRow: Math.min(this._rowStopIndex + 1, rowCount - 1)
                });
                break;
            case Keys.LEFT:
                event.preventDefault();
                this.setState({
                    scrollToColumn: Math.max(this._columnStartIndex - 1, 0)
                });
                break;
            case Keys.RIGHT:
                event.preventDefault();
                this.setState({
                    scrollToColumn: Math.min(this._columnStopIndex + 1, columnCount - 1)
                });
                break;
            case Keys.UP:
                event.preventDefault();
                this.setState({
                    scrollToRow: Math.max(this._rowStartIndex - 1, 0)
                });
                break;
            default:
                break;
        }
    }

    _onSectionRendered({columnStartIndex, columnStopIndex, rowStartIndex, rowStopIndex}) {
        this._columnStartIndex = columnStartIndex;
        this._columnStopIndex = columnStopIndex;
        this._rowStartIndex = rowStartIndex;
        this._rowStopIndex = rowStopIndex;
    }
}

mixin.onClass(ArrowKeyStepper, ReactComponentWithPureRenderMixin);

export default ArrowKeyStepper
