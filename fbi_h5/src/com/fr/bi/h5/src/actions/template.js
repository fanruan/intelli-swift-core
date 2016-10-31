import * as types from '../constants/ActionTypes'

export function updateTemplate($template) {
    return {type: types.UPDATE_TEMPLATE, $template}
}

export function updateWidget($widget, wId) {
    return {type: types.UPDATE_WIDGET, $widget, wId}
}

export function widgetLinkage(wId, clicked) {
    return {type: types.WIDGET_LINKAGE, wId, clicked}
}