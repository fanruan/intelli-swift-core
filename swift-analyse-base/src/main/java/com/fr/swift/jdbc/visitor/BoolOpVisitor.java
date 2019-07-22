package com.fr.swift.jdbc.visitor;

import com.fr.swift.jdbc.antlr4.SwiftSqlParser;
import com.fr.swift.jdbc.creator.FilterBeanCreator;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NotFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NullFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.StringOneValueFilterBean;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Collection;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-19
 */
public class BoolOpVisitor extends AbstractParseTreeVisitor<FilterBeanCreator<FilterInfoBean>> {
    private static final String LIKE = "%";

    @Override
    public FilterBeanCreator<FilterInfoBean> visitChildren(RuleNode node) {
        if (node instanceof SwiftSqlParser.BoolOpContext) {
            final int type = ((SwiftSqlParser.BoolOpContext) node).getStart().getType();
            switch (type) {
                case SwiftSqlParser.EQ:
                    return new BaseFilterBeanCreator(type) {
                        @Override
                        public FilterInfoBean create(String column, Object value) {
                            return new InFilterBean(column, value);
                        }
                    };
                case SwiftSqlParser.NEQ:
                    return new BaseFilterBeanCreator(type) {
                        @Override
                        public FilterInfoBean create(String column, Object value) {
                            return new NotFilterBean(new InFilterBean(column, value));
                        }
                    };
                case SwiftSqlParser.GREATER:
                    return new BaseFilterBeanCreator(type) {
                        @Override
                        public FilterInfoBean create(String column, Object value) {
                            return NumberInRangeFilterBean.builder(column).setStart(value.toString(), false).build();
                        }
                    };
                case SwiftSqlParser.GEQ:
                    return new BaseFilterBeanCreator(type) {
                        @Override
                        public FilterInfoBean create(String column, Object value) {
                            return NumberInRangeFilterBean.builder(column).setStart(value.toString(), true).build();
                        }
                    };
                case SwiftSqlParser.LESS:
                    return new BaseFilterBeanCreator(type) {
                        @Override
                        public FilterInfoBean create(String column, Object value) {
                            return NumberInRangeFilterBean.builder(column).setEnd(value.toString(), false).build();
                        }
                    };
                case SwiftSqlParser.LEQ:
                    return new BaseFilterBeanCreator(type) {
                        @Override
                        public FilterInfoBean create(String column, Object value) {
                            return NumberInRangeFilterBean.builder(column).setEnd(value.toString(), true).build();
                        }
                    };
                case SwiftSqlParser.LIKE:
                    return new BaseFilterBeanCreator(type) {
                        @Override
                        public FilterInfoBean create(String column, Object value) {
                            String s = value.toString();
                            SwiftDetailFilterType filterType = SwiftDetailFilterType.STRING_LIKE;
                            if (s.startsWith(LIKE) && s.startsWith(LIKE)) {
                                filterType = SwiftDetailFilterType.STRING_LIKE;
                                s = s.substring(1, s.length() - 1);
                            } else if (s.startsWith(LIKE)) {
                                filterType = SwiftDetailFilterType.STRING_ENDS_WITH;
                                s = s.substring(1);
                            } else if (s.endsWith(LIKE)) {
                                filterType = SwiftDetailFilterType.STRING_STARTS_WITH;
                                s = s.substring(0, s.length() - 1);
                            } else {
                                // do nothing
                            }
                            return new StringOneValueFilterBean(column, filterType, s);
                        }
                    };
                default:
            }
        }
        return null;
    }

    @Override
    public FilterBeanCreator<FilterInfoBean> visitTerminal(TerminalNode node) {
        int type = node.getSymbol().getType();
        switch (type) {
            case SwiftSqlParser.BETWEEN:
                return new BaseFilterBeanCreator(type) {
                    @Override
                    public FilterInfoBean create(String column, Object value) {
                        List<String> values = (List<String>) value;
                        return NumberInRangeFilterBean.builder(column)
                                .setStart(values.get(0), true)
                                .setEnd(values.get(1), true)
                                .build();
                    }
                };
            case SwiftSqlParser.NOT:
                return new BaseFilterBeanCreator(type) {
                    @Override
                    public FilterInfoBean create(String column, Object value) {
                        return new NotFilterBean((FilterInfoBean) value);
                    }
                };
            case SwiftSqlParser.NULL:
                return new BaseFilterBeanCreator(type) {
                    @Override
                    public FilterInfoBean create(String column, Object value) {
                        return new NullFilterBean();
                    }
                };
            case SwiftSqlParser.IN:
                return new BaseFilterBeanCreator(type) {
                    @Override
                    public FilterInfoBean create(String column, Object value) {
                        return new InFilterBean(column, (Collection<String>) value);
                    }
                };
            default:
        }
        return null;
    }

    private abstract class BaseFilterBeanCreator implements FilterBeanCreator<FilterInfoBean> {
        private int type;

        public BaseFilterBeanCreator(int type) {
            this.type = type;
        }

        @Override
        public int type() {
            return type;
        }
    }
}
