package com.fr.swift.query.result.group;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.node.resultset.MergeGroupQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.structure.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author anchore
 * @date 2019/7/1
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({GroupResultQuery.class})
public class GroupResultQueryTest {

    @Test
    public void merge() throws Exception {
        List<Query<QueryResultSet<GroupPage>>> queries = Arrays.<Query<QueryResultSet<GroupPage>>>asList(mock(Query.class), mock(Query.class));
        List<Aggregator> aggregators = mock(List.class);
        List<Pair<SortType, ColumnTypeConstants.ClassType>> comparators = Arrays.asList(
                Pair.of(SortType.DESC, ClassType.LONG),
                Pair.of(SortType.ASC, ClassType.STRING)
        );
        boolean[] isGlobalIndexed = new boolean[0];

        List<QueryResultSet<GroupPage>> resultSets = new ArrayList<QueryResultSet<GroupPage>>();
        for (Query<QueryResultSet<GroupPage>> query : queries) {
            QueryResultSet<GroupPage> resultSet = mock(QueryResultSet.class);
            resultSets.add(resultSet);
            when(query.getQueryResult()).thenReturn(resultSet);
        }

        MergeGroupQueryResultSet mergeGroupQueryResultSet = mock(MergeGroupQueryResultSet.class);
        whenNew(MergeGroupQueryResultSet.class)
                .withArguments(eq(1), eq(isGlobalIndexed), eq(resultSets), eq(aggregators), argThat(new ArgumentMatcher<List<Comparator<?>>>() {
                    @Override
                    public boolean matches(List<Comparator<?>> argument) {
                        assertThat(argument).hasSize(2)
                                .extracting("comparator")
                                .containsExactly(Comparators.desc(), Comparators.STRING_ASC);
                        return true;
                    }
                }))
                .thenReturn(mergeGroupQueryResultSet);

        assertThat(new GroupResultQuery(1, queries, aggregators, comparators, isGlobalIndexed).merge(resultSets))
                .isEqualTo(mergeGroupQueryResultSet);
    }
}