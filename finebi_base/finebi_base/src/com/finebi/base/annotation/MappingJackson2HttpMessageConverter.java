package com.finebi.base.annotation;

import com.fr.third.fasterxml.jackson.core.JsonEncoding;
import com.fr.third.fasterxml.jackson.core.JsonGenerator;
import com.fr.third.fasterxml.jackson.core.JsonProcessingException;
import com.fr.third.fasterxml.jackson.databind.JavaType;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;
import com.fr.third.fasterxml.jackson.databind.SerializationFeature;
import com.fr.third.springframework.http.HttpInputMessage;
import com.fr.third.springframework.http.HttpOutputMessage;
import com.fr.third.springframework.http.MediaType;
import com.fr.third.springframework.http.converter.AbstractHttpMessageConverter;
import com.fr.third.springframework.http.converter.GenericHttpMessageConverter;
import com.fr.third.springframework.http.converter.HttpMessageNotReadableException;
import com.fr.third.springframework.http.converter.HttpMessageNotWritableException;
import com.fr.third.springframework.util.Assert;
import com.fr.third.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 对象转json返回给前端
 * 主要是copy spring里面的MappingJackson2HttpMessageConverter
 * 因为spring里面用的是jackson里面的我们这边需要用fr-jackson
 * 没有特殊的必须的情况尽量不改spring内部的代码spring已经提供了足够的扩展点
 * 来应付各种需求
 */
public class MappingJackson2HttpMessageConverter extends AbstractHttpMessageConverter<Object>
        implements GenericHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    // Check for Jackson 2.3's overloaded canDeserialize/canSerialize variants with cause reference
    private static final boolean jackson23Available =
            ClassUtils.hasMethod(ObjectMapper.class, "canDeserialize", JavaType.class, AtomicReference.class);


    private ObjectMapper objectMapper = new ObjectMapper();

    private String jsonPrefix;

    private Boolean prettyPrint;


    /**
     * Construct a new {@code MappingJackson2HttpMessageConverter}.
     */
    public MappingJackson2HttpMessageConverter() {

        super(new MediaType("application", "json", DEFAULT_CHARSET),
              new MediaType("application", "*+json", DEFAULT_CHARSET));
    }


    /**
     * Set the {@code ObjectMapper} for this view.
     * If not set, a default {@link ObjectMapper#ObjectMapper() ObjectMapper} is used.
     * <p>Setting a custom-configured {@code ObjectMapper} is one way to take further
     * control of the JSON serialization process. For example, an extended
     * can be configured that provides custom serializers for specific types.
     * The other option for refining the serialization process is to use Jackson's
     * provided annotations on the types to be serialized, in which case a
     * custom-configured ObjectMapper is unnecessary.
     */
    public void setObjectMapper(ObjectMapper objectMapper) {

        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.objectMapper = objectMapper;
        configurePrettyPrint();
    }

    /**
     * Return the underlying {@code ObjectMapper} for this view.
     */
    public ObjectMapper getObjectMapper() {

        return this.objectMapper;
    }

    /**
     * Specify a custom prefix to use for this view's JSON output.
     * Default is none.
     *
     * @see #setPrefixJson
     */
    public void setJsonPrefix(String jsonPrefix) {

        this.jsonPrefix = jsonPrefix;
    }

    /**
     * Indicate whether the JSON output by this view should be prefixed with "{} &&". Default is false.
     * <p>Prefixing the JSON string in this manner is used to help prevent JSON Hijacking.
     * The prefix renders the string syntactically invalid as a script so that it cannot be hijacked.
     * This prefix does not affect the evaluation of JSON, but if JSON validation is performed on the
     * string, the prefix would need to be ignored.
     *
     * @see #setJsonPrefix
     */
    public void setPrefixJson(boolean prefixJson) {

        this.jsonPrefix = (prefixJson ? "{} && " : null);
    }

    /**
     * This is a shortcut for setting up an {@code ObjectMapper} as follows:
     * <pre class="code">
     * ObjectMapper mapper = new ObjectMapper();
     * mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
     * converter.setObjectMapper(mapper);
     * </pre>
     */
    public void setPrettyPrint(boolean prettyPrint) {

        this.prettyPrint = prettyPrint;
        configurePrettyPrint();
    }

    private void configurePrettyPrint() {

        if (this.prettyPrint != null) {
            this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, this.prettyPrint);
        }
    }


    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {

        return canRead(clazz, null, mediaType);
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {

        JavaType javaType = getJavaType(type, contextClass);
        if (!jackson23Available || !logger.isWarnEnabled()) {
            return (this.objectMapper.canDeserialize(javaType) && canRead(mediaType));
        }
        AtomicReference<Throwable> causeRef = new AtomicReference<Throwable>();
        if (this.objectMapper.canDeserialize(javaType, causeRef) && canRead(mediaType)) {
            return true;
        }
        Throwable cause = causeRef.get();
        if (cause != null) {
            String msg = "Failed to evaluate deserialization for type " + javaType;
            if (logger.isDebugEnabled()) {
                logger.warn(msg, cause);
            } else {
                logger.warn(msg + ": " + cause);
            }
        }
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {

        if (!jackson23Available || !logger.isWarnEnabled()) {
            return (this.objectMapper.canSerialize(clazz) && canWrite(mediaType));
        }
        AtomicReference<Throwable> causeRef = new AtomicReference<Throwable>();
        if (this.objectMapper.canSerialize(clazz, causeRef) && canWrite(mediaType)) {
            return true;
        }
        Throwable cause = causeRef.get();
        if (cause != null) {
            String msg = "Failed to evaluate serialization for type [" + clazz + "]";
            if (logger.isDebugEnabled()) {
                logger.warn(msg, cause);
            } else {
                logger.warn(msg + ": " + cause);
            }
        }
        return false;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // should not be called, since we override canRead/Write instead
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        JavaType javaType = getJavaType(clazz, null);
        return readJavaType(javaType, inputMessage);
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        JavaType javaType = getJavaType(type, contextClass);
        return readJavaType(javaType, inputMessage);
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {

        try {
            return this.objectMapper.readValue(inputMessage.getBody(), javaType);
        } catch (IOException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
        // The following has been deprecated as late as Jackson 2.2 (April 2013);
        // preserved for the time being, for Jackson 2.0/2.1 compatibility.
        @SuppressWarnings("deprecation")
        JsonGenerator jsonGenerator =
                this.objectMapper.getJsonFactory().createJsonGenerator(outputMessage.getBody(), encoding);

        // A workaround for JsonGenerators not applying serialization features
        // https://github.com/FasterXML/jackson-databind/issues/12
        if (this.objectMapper.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
            jsonGenerator.useDefaultPrettyPrinter();
        }

        try {
            if (this.jsonPrefix != null) {
                jsonGenerator.writeRaw(this.jsonPrefix);
            }
            this.objectMapper.writeValue(jsonGenerator, object);
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    /**
     * Return the Jackson {@link JavaType} for the specified type and context class.
     * <p>The default implementation returns {@code typeFactory.constructType(type, contextClass)},
     * but this can be overridden in subclasses, to allow for custom generic collection handling.
     * For instance:
     * <pre class="code">
     * protected JavaType getJavaType(Type type) {
     * if (type instanceof Class && List.class.isAssignableFrom((Class)type)) {
     * return TypeFactory.collectionType(ArrayList.class, MyBean.class);
     * } else {
     * return super.getJavaType(type);
     * }
     * }
     * </pre>
     *
     * @param type         the type to return the java type for
     * @param contextClass a context class for the target type, for example a class
     *                     in which the target type appears in a method signature, can be {@code null}
     *                     signature, can be {@code null}
     * @return the java type
     */
    protected JavaType getJavaType(Type type, Class<?> contextClass) {

        return this.objectMapper.getTypeFactory().constructType(type, contextClass);
    }

    /**
     * Determine the JSON encoding to use for the given content type.
     *
     * @param contentType the media type as requested by the caller
     * @return the JSON encoding to use (never {@code null})
     */
    protected JsonEncoding getJsonEncoding(MediaType contentType) {

        if (contentType != null && contentType.getCharSet() != null) {
            Charset charset = contentType.getCharSet();
            for (JsonEncoding encoding : JsonEncoding.values()) {
                if (charset.name().equals(encoding.getJavaName())) {
                    return encoding;
                }
            }
        }
        return JsonEncoding.UTF8;
    }

}
