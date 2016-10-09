import * as types from '../constants/ActionTypes'

export function updateTemplate($template) {
    return {type: types.UPDATE_TEMPLATE, $template}
}
