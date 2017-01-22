import Template from './Template'
export default {
    createTemplate: (...props)=> {
        return new Template(...props);
    }
};