package com.fr.swift.cloud.source.load;

import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.function.Function;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyon on 2019/3/12.
 */
public class JSLineParser implements LineParser<String> {

    private static Function<Object, Object> compile(String function) {
        final ContextFactory contextFactory = ContextFactory.getGlobal();
        final Context context = contextFactory.enterContext();
        context.setOptimizationLevel(9);

        final ScriptableObject scope = context.initStandardObjects();

        final org.mozilla.javascript.Function fn = context.compileFunction(scope, function, "fn", 1, null);
        Context.exit();

        return new Function<Object, Object>() {
            @Override
            public Object apply(Object input) {
                // ideally we need a close() function to discard the context once it is not used anymore
                Context cx = Context.getCurrentContext();
                if (cx == null) {
                    cx = contextFactory.enterContext();
                }

                final Object res = fn.call(cx, scope, scope, new Object[]{input});
                return res != null ? Context.toObject(res, scope) : null;
            }
        };
    }

    private final Function<Object, Object> fn;
    private List<SwiftMetaDataColumn> fields;

    public JSLineParser(final String function, List<SwiftMetaDataColumn> fields) {
        this.fn = compile(function);
        this.fields = fields;
    }

    @Override
    public Map<String, Object> parseToMap(String input) {
        try {
            final Object compiled = fn.apply(input);
            if (!(compiled instanceof Map)) {
                throw new ParseException("JavaScript parsed value must be in {key: value} format!");
            }
            Map<String, Object> map = (Map<String, Object>) compiled;
            Map<String, Object> result = new LinkedHashMap<String, Object>();
            for (SwiftMetaDataColumn column : fields) {
                if (!map.containsKey(column.getName())) {
                    throw new ParseException("JavaScript parsed keyValues must contain key: " + column.getName());
                }
                Object value = map.get(column.getName());
                result.put(column.getName(), ParseUtils.convert(value == null ? null : value.toString(), column));
            }
            if (result.size() != fields.size()) {
                throw new ParseException("invalid numbers of JavaScript parsed keyValues, expected: " + fields.size()
                        + ", actual: " + result.size());
            }
            return result;
        } catch (Exception e) {
            throw new ParseException("Unable to parse row: " + input);
        }
    }

    @Override
    public List<SwiftMetaDataColumn> getFields() {
        return fields;
    }
}
