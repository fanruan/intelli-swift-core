package com.finebi.base.context;

import com.finebi.springframework.beans.factory.InitializingBean;
import com.finebi.springframework.context.ApplicationContext;
import com.finebi.springframework.context.ApplicationContextAware;
import com.finebi.springframework.core.OrderComparator;
import com.finebi.springframework.http.converter.ByteArrayHttpMessageConverter;
import com.finebi.springframework.http.converter.HttpMessageConverter;
import com.finebi.springframework.http.converter.StringHttpMessageConverter;
import com.finebi.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import com.finebi.springframework.http.converter.xml.SourceHttpMessageConverter;
import com.finebi.springframework.web.accept.ContentNegotiationManager;
import com.finebi.springframework.web.context.request.ServletWebRequest;
import com.finebi.springframework.web.method.ControllerAdviceBean;
import com.finebi.springframework.web.method.HandlerMethod;
import com.finebi.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import com.finebi.springframework.web.method.annotation.MapMethodProcessor;
import com.finebi.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import com.finebi.springframework.web.method.annotation.ModelMethodProcessor;
import com.finebi.springframework.web.method.support.HandlerMethodArgumentResolver;
import com.finebi.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import com.finebi.springframework.web.method.support.HandlerMethodReturnValueHandler;
import com.finebi.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import com.finebi.springframework.web.method.support.ModelAndViewContainer;
import com.finebi.springframework.web.servlet.ModelAndView;
import com.finebi.springframework.web.servlet.View;
import com.finebi.springframework.web.servlet.handler.AbstractHandlerMethodExceptionResolver;
import com.finebi.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import com.finebi.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler;
import com.finebi.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import com.finebi.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import com.finebi.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;
import com.finebi.springframework.web.servlet.mvc.method.annotation.ServletResponseMethodArgumentResolver;
import com.finebi.springframework.web.servlet.mvc.method.annotation.ViewMethodReturnValueHandler;
import com.finebi.springframework.web.servlet.mvc.method.annotation.ViewNameMethodReturnValueHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andrew_asa on 2017/12/24.
 */
public class ExcetionHanderResolver extends AbstractHandlerMethodExceptionResolver
        implements ApplicationContextAware, InitializingBean {

    private List<HandlerMethodArgumentResolver> customArgumentResolvers;

    private HandlerMethodArgumentResolverComposite argumentResolvers;

    private List<HandlerMethodReturnValueHandler> customReturnValueHandlers;

    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

    private List<HttpMessageConverter<?>> messageConverters;

    private ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();

    private ApplicationContext applicationContext;

    private final Map<Class<?>, ExceptionHandlerMethodResolver> exceptionHandlerCache =
            new ConcurrentHashMap<Class<?>, ExceptionHandlerMethodResolver>(64);

    private final Map<ControllerAdviceBean, ExceptionHandlerMethodResolver> exceptionHandlerAdviceCache =
            new LinkedHashMap<ControllerAdviceBean, ExceptionHandlerMethodResolver>();


    public ExcetionHanderResolver() {

        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setWriteAcceptCharset(false); // See SPR-7316

        this.messageConverters = new ArrayList<HttpMessageConverter<?>>();
        this.messageConverters.add(new ByteArrayHttpMessageConverter());
        this.messageConverters.add(stringHttpMessageConverter);
        this.messageConverters.add(new SourceHttpMessageConverter<Source>());
        this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());
    }


    /**
     * Provide resolvers for custom argument types. Custom resolvers are ordered
     * after built-in ones. To override the built-in support for argument
     * resolution use {@link #setArgumentResolvers} instead.
     */
    public void setCustomArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

        this.customArgumentResolvers = argumentResolvers;
    }

    /**
     * Return the custom argument resolvers, or {@code null}.
     */
    public List<HandlerMethodArgumentResolver> getCustomArgumentResolvers() {

        return this.customArgumentResolvers;
    }

    /**
     * Configure the complete list of supported argument types thus overriding
     * the resolvers that would otherwise be configured by default.
     */
    public void setArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

        if (argumentResolvers == null) {
            this.argumentResolvers = null;
        } else {
            this.argumentResolvers = new HandlerMethodArgumentResolverComposite();
            this.argumentResolvers.addResolvers(argumentResolvers);
        }
    }

    /**
     * Return the configured argument resolvers, or possibly {@code null} if
     * not initialized yet via {@link #afterPropertiesSet()}.
     */
    public HandlerMethodArgumentResolverComposite getArgumentResolvers() {

        return this.argumentResolvers;
    }

    /**
     * Provide handlers for custom return value types. Custom handlers are
     * ordered after built-in ones. To override the built-in support for
     * return value handling use {@link #setReturnValueHandlers}.
     */
    public void setCustomReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {

        this.customReturnValueHandlers = returnValueHandlers;
    }

    /**
     * Return the custom return value handlers, or {@code null}.
     */
    public List<HandlerMethodReturnValueHandler> getCustomReturnValueHandlers() {

        return this.customReturnValueHandlers;
    }

    /**
     * Configure the complete list of supported return value types thus
     * overriding handlers that would otherwise be configured by default.
     */
    public void setReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {

        if (returnValueHandlers == null) {
            this.returnValueHandlers = null;
        } else {
            this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();
            this.returnValueHandlers.addHandlers(returnValueHandlers);
        }
    }

    /**
     * Return the configured handlers, or possibly {@code null} if not
     * initialized yet via {@link #afterPropertiesSet()}.
     */
    public HandlerMethodReturnValueHandlerComposite getReturnValueHandlers() {

        return this.returnValueHandlers;
    }

    /**
     * Set the message body converters to use.
     * <p>These converters are used to convert from and to HTTP requests and responses.
     */
    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {

        this.messageConverters = messageConverters;
    }


    public List<HttpMessageConverter<?>> getMessageConverters() {

        return this.messageConverters;
    }


    public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {

        this.contentNegotiationManager = contentNegotiationManager;
    }

    public ContentNegotiationManager getContentNegotiationManager() {

        return this.contentNegotiationManager;
    }

    public Map<ControllerAdviceBean, ExceptionHandlerMethodResolver> getExceptionHandlerAdviceCache() {

        return Collections.unmodifiableMap(this.exceptionHandlerAdviceCache);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {

        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {

        return this.applicationContext;
    }


    @Override
    public void afterPropertiesSet() {

        if (this.argumentResolvers == null) {
            List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
            this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
        }
        if (this.returnValueHandlers == null) {
            List<HandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
            this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
        }
        initExceptionHandlerAdviceCache();
    }

    /**
     * Return the list of argument resolvers to use including built-in resolvers
     * and custom resolvers provided via {@link #setCustomArgumentResolvers}.
     */
    protected List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {

        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();

        // Type-based argument resolution
        resolvers.add(new ServletRequestMethodArgumentResolver());
        resolvers.add(new ServletResponseMethodArgumentResolver());

        // Custom arguments
        if (getCustomArgumentResolvers() != null) {
            resolvers.addAll(getCustomArgumentResolvers());
        }

        return resolvers;
    }

    /**
     * Return the list of return value handlers to use including built-in and
     * custom handlers provided via {@link #setReturnValueHandlers}.
     */
    protected List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {

        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>();

        // Single-purpose return value types
        handlers.add(new ModelAndViewMethodReturnValueHandler());
        handlers.add(new ModelMethodProcessor());
        handlers.add(new ViewMethodReturnValueHandler());
        handlers.add(new HttpEntityMethodProcessor(getMessageConverters(), this.contentNegotiationManager));

        // Annotation-based return value types
        handlers.add(new ModelAttributeMethodProcessor(false));
        handlers.add(new RequestResponseBodyMethodProcessor(getMessageConverters(), this.contentNegotiationManager));

        // Multi-purpose return value types
        handlers.add(new ViewNameMethodReturnValueHandler());
        handlers.add(new MapMethodProcessor());

        // Custom return value types
        if (getCustomReturnValueHandlers() != null) {
            handlers.addAll(getCustomReturnValueHandlers());
        }

        // Catch-all
        handlers.add(new ModelAttributeMethodProcessor(true));

        return handlers;
    }

    private void initExceptionHandlerAdviceCache() {

        if (getApplicationContext() == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Looking for exception mappings: " + getApplicationContext());
        }

        List<ControllerAdviceBean> beans = ControllerAdviceBean.findAnnotatedBeans(getApplicationContext());
        Collections.sort(beans, new OrderComparator());

        for (ControllerAdviceBean bean : beans) {
            ExceptionHandlerMethodResolver resolver = new ExceptionHandlerMethodResolver(bean.getBeanType());
            if (resolver.hasExceptionMappings()) {
                this.exceptionHandlerAdviceCache.put(bean, resolver);
                logger.info("Detected @ExceptionHandler methods in " + bean);
            }
        }
    }


    /**
     * Find an {@code @ExceptionHandler} method and invoke it to handle the raised exception.
     */
    @Override
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request,
                                                           HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {

        ServletInvocableHandlerMethod exceptionHandlerMethod = getExceptionHandlerMethod(handlerMethod, exception);
        if (exceptionHandlerMethod == null) {
            return null;
        }

        exceptionHandlerMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
        exceptionHandlerMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);

        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Invoking @ExceptionHandler method: " + exceptionHandlerMethod);
            }
            exceptionHandlerMethod.invokeAndHandle(webRequest, mavContainer, exception);
        } catch (Exception invocationEx) {
            if (logger.isErrorEnabled()) {
                logger.error("Failed to invoke @ExceptionHandler method: " + exceptionHandlerMethod, invocationEx);
            }
            return null;
        }

        if (mavContainer.isRequestHandled()) {
            return new ModelAndView();
        } else {
            ModelAndView mav = new ModelAndView().addAllObjects(mavContainer.getModel());
            mav.setViewName(mavContainer.getViewName());
            if (!mavContainer.isViewReference()) {
                mav.setView((View) mavContainer.getView());
            }
            return mav;
        }
    }

    protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {

        Class<?> handlerType = (handlerMethod != null ? handlerMethod.getBeanType() : null);

        if (handlerMethod != null) {
            ExceptionHandlerMethodResolver resolver = this.exceptionHandlerCache.get(handlerType);
            if (resolver == null) {
                resolver = new ExceptionHandlerMethodResolver(handlerType);
                this.exceptionHandlerCache.put(handlerType, resolver);
            }
            Method method = resolver.resolveMethod(exception);
            if (method != null) {
                return new ServletInvocableHandlerMethod(handlerMethod.getBean(), method);
            }
        }

        for (Map.Entry<ControllerAdviceBean, ExceptionHandlerMethodResolver> entry : this.exceptionHandlerAdviceCache.entrySet()) {
            if (entry.getKey().isApplicableToBeanType(handlerType)) {
                ExceptionHandlerMethodResolver resolver = entry.getValue();
                Method method = resolver.resolveMethod(exception);
                if (method != null) {
                    return new ServletInvocableHandlerMethod(entry.getKey().resolveBean(), method);
                }
            }
        }

        return null;
    }

    @Override
    protected boolean shouldApplyTo(HttpServletRequest request, Object handler) {

        return true;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {

        if (shouldApplyTo(request, handler)) {
            // Log exception, both at debug log level and at warn level, if desired.
            if (logger.isDebugEnabled()) {
                logger.debug("Resolving exception from handler [" + handler + "]: " + ex);
            }
            logException(ex, request);
            prepareResponse(ex, response);
            return doResolveException(request, response, handler, ex);
        }
        else {
            return null;
        }
    }

}