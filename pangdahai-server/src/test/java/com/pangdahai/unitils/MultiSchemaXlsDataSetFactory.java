package com.pangdahai.unitils;

import com.google.common.collect.Maps;
import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.excel.XlsDataSet;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.util.MultiSchemaDataSet;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author gsj
 *         email:t06000020@hongtu56.com
 * @Description 描述一下这个文件
 * @Date 12-6-11上午11:02
 * @since 1.0.0
 */
public class MultiSchemaXlsDataSetFactory implements DataSetFactory {
    private static Pattern pattern = Pattern.compile("\\.");
    protected String defaultSchemaName;

    public void init(Properties configuration, String defaultSchemaName) {
        this.defaultSchemaName = defaultSchemaName;
    }

    public MultiSchemaDataSet createDataSet(File... dataSetFiles) {
        Map<String, List<ITable>> tableMap = getTables(dataSetFiles);

        MultiSchemaDataSet dataSets = new MultiSchemaDataSet();
        for (Map.Entry<String, List<ITable>> entry : tableMap.entrySet()) {
            List<ITable> tables = entry.getValue();
            try {
                DefaultDataSet ds = new DefaultDataSet(tables.toArray(new ITable[] {}));
                dataSets.setDataSetForSchema(entry.getKey(), ds);
            } catch (AmbiguousTableNameException e) {
                throw new UnitilsException(String.format("使用指定表[%s]重新构造DataSet失败", tables), e);
            }
        }

        return dataSets;
    }

    private Map<String, List<ITable>> getTables(File... dataSetFiles) {
        Map<String, List<ITable>> tableMap = Maps.newConcurrentMap();
        // 需要根据schema把Table重新组合一下
        try {
            for (File file : dataSetFiles) {
                IDataSet dataSet = new XlsDataSet(new FileInputStream(file));
                String[] tableNames = dataSet.getTableNames();
                for (String each : tableNames) {
                    // 这个实际上不是schema, 是对应的spring的datasouceId
                    String schema = null;
                    String tableName;
                    String[] temp = pattern.split(each);
                    if (temp.length == 2) {
                        schema = temp[0];
                        tableName = temp[1];
                    } else {
                        schema = this.defaultSchemaName;
                        tableName = each;
                    }

                    ITable table = dataSet.getTable(each);
                    if (!tableMap.containsKey(schema)) {
                        tableMap.put(schema, new ArrayList<ITable>());
                    }
                    tableMap.get(schema).add(new XslTableWrapper(tableName, table));
                }
            }
        } catch (Exception e) {
            throw new UnitilsException("Unable to create DbUnit dataset for data set files: "
                    + Arrays.toString(dataSetFiles), e);
        }
        return tableMap;
    }

    public String getDataSetFileExtension() {
        return "xls";
    }
}
