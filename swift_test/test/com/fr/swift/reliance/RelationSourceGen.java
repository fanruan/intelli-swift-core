package com.fr.swift.reliance;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SourcePath;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.relation.RelationPathSourceImpl;
import com.fr.swift.source.relation.RelationSourceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/4/18
 */
public class RelationSourceGen {
    public static List<DataSource> genDataSource() {
        return Arrays.asList(
                new TableDBSource("A", "allTest"),
                new TableDBSource("B", "allTest"),
                new TableDBSource("C", "allTest"),
                new TableDBSource("D", "allTest"),
                new TableDBSource("E", "allTest"),
                new TableDBSource("F", "allTest"),
                new TableDBSource("G", "allTest")
        );
    }

    public static List<RelationSource> genRelationSource(List<DataSource> dataSources, boolean three) {
        if (three) {
            return Arrays.asList(
                    new RelationSourceImpl(dataSources.get(0).getSourceKey(), dataSources.get(1).getSourceKey(), Arrays.asList("AB"), Arrays.asList("BA")),
                    new RelationSourceImpl(dataSources.get(1).getSourceKey(), dataSources.get(2).getSourceKey(), Arrays.asList("BC"), Arrays.asList("CB")),
                    new RelationSourceImpl(dataSources.get(0).getSourceKey(), dataSources.get(2).getSourceKey(), Arrays.asList("AC"), Arrays.asList("CA")),
                    new RelationSourceImpl(dataSources.get(1).getSourceKey(), dataSources.get(3).getSourceKey(), Arrays.asList("BD"), Arrays.asList("DB")),
                    new RelationSourceImpl(new SourceKey("undefined"), dataSources.get(1).getSourceKey(), Arrays.asList("undefined"), Arrays.asList("BA"))
            );
        } else {
            return Arrays.asList(
                    new RelationSourceImpl(dataSources.get(0).getSourceKey(), dataSources.get(1).getSourceKey(), Arrays.asList("AB"), Arrays.asList("BA")),
                    new RelationSourceImpl(dataSources.get(1).getSourceKey(), dataSources.get(2).getSourceKey(), Arrays.asList("BC"), Arrays.asList("CB")),
                    new RelationSourceImpl(dataSources.get(2).getSourceKey(), dataSources.get(3).getSourceKey(), Arrays.asList("CD"), Arrays.asList("DC")),
                    new RelationSourceImpl(dataSources.get(0).getSourceKey(), dataSources.get(2).getSourceKey(), Arrays.asList("AC"), Arrays.asList("CA")),
                    new RelationSourceImpl(dataSources.get(1).getSourceKey(), dataSources.get(3).getSourceKey(), Arrays.asList("BD"), Arrays.asList("DB"))
            );
        }
    }

    public static List<SourcePath> calPath(List<RelationSource> relationSources) {
        Map<SourceKey, SourcePath> map = new HashMap<>();
        for (RelationSource source : relationSources) {
            RelationPathSourceImpl pathSource = new RelationPathSourceImpl(new ArrayList<RelationSource>(Arrays.asList(source)));
            if (!map.containsKey(pathSource.getSourceKey())) {
                map.put(pathSource.getSourceKey(), pathSource);
            }
        }
        map = calPath(map);
        return new ArrayList<>(map.values());
    }

    private static Map<SourceKey, SourcePath> calPath(Map<SourceKey, SourcePath> map) {
        Map<SourceKey, SourcePath> result = new HashMap<>(map);
        int size = result.size();

        Iterator<Map.Entry<SourceKey, SourcePath>> mapIter = map.entrySet().iterator();
        List<RelationPathSourceImpl> pathList = new ArrayList<>();
        while (mapIter.hasNext()) {
            SourcePath base = mapIter.next().getValue();
            Iterator<Map.Entry<SourceKey, SourcePath>> iterator = result.entrySet().iterator();
            while (iterator.hasNext()) {
                SourcePath current = iterator.next().getValue();
                if (base.getForeignSource().equals(current.getPrimarySource())) {
                    List<RelationSource> list = new ArrayList<>(base.getRelations());
                    list.addAll(current.getRelations());
                    RelationPathSourceImpl pathSource = new RelationPathSourceImpl(list);
                    pathList.add(pathSource);
                }
            }
        }
        for (SourcePath path : pathList) {
            result.put(path.getSourceKey(), path);
        }
        if (result.size() != size) {
            result.putAll(calPath(result));
        }
        return result;
    }

    public static SourcePath genSinglePath(List<DataSource> dataSources) {
        return new RelationPathSourceImpl(Arrays.asList(
                new RelationSourceImpl(dataSources.get(0).getSourceKey(), dataSources.get(1).getSourceKey(), Arrays.asList("AB"), Arrays.asList("BA")),
                new RelationSourceImpl(dataSources.get(1).getSourceKey(), dataSources.get(2).getSourceKey(), Arrays.asList("BC"), Arrays.asList("CB")),
                new RelationSourceImpl(dataSources.get(2).getSourceKey(), dataSources.get(3).getSourceKey(), Arrays.asList("CD"), Arrays.asList("DC"))));
    }
}
