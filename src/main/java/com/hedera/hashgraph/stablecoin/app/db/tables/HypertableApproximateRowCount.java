/*
 * This file is generated by jOOQ.
 */
package com.hedera.hashgraph.stablecoin.app.db.tables;


import com.hedera.hashgraph.stablecoin.app.db.Public;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class HypertableApproximateRowCount extends TableImpl<Record> {

    private static final long serialVersionUID = 2062450486;

    /**
     * The reference instance of <code>public.hypertable_approximate_row_count</code>
     */
    public static final HypertableApproximateRowCount HYPERTABLE_APPROXIMATE_ROW_COUNT = new HypertableApproximateRowCount();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>public.hypertable_approximate_row_count.schema_name</code>.
     */
    public final TableField<Record, String> SCHEMA_NAME = createField(DSL.name("schema_name"), org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.hypertable_approximate_row_count.table_name</code>.
     */
    public final TableField<Record, String> TABLE_NAME = createField(DSL.name("table_name"), org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.hypertable_approximate_row_count.row_estimate</code>.
     */
    public final TableField<Record, Long> ROW_ESTIMATE = createField(DSL.name("row_estimate"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * Create a <code>public.hypertable_approximate_row_count</code> table reference
     */
    public HypertableApproximateRowCount() {
        this(DSL.name("hypertable_approximate_row_count"), null);
    }

    /**
     * Create an aliased <code>public.hypertable_approximate_row_count</code> table reference
     */
    public HypertableApproximateRowCount(String alias) {
        this(DSL.name(alias), HYPERTABLE_APPROXIMATE_ROW_COUNT);
    }

    /**
     * Create an aliased <code>public.hypertable_approximate_row_count</code> table reference
     */
    public HypertableApproximateRowCount(Name alias) {
        this(alias, HYPERTABLE_APPROXIMATE_ROW_COUNT);
    }

    private HypertableApproximateRowCount(Name alias, Table<Record> aliased) {
        this(alias, aliased, new Field[1]);
    }

    private HypertableApproximateRowCount(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.function());
    }

    public <O extends Record> HypertableApproximateRowCount(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, HYPERTABLE_APPROXIMATE_ROW_COUNT);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public HypertableApproximateRowCount as(String alias) {
        return new HypertableApproximateRowCount(DSL.name(alias), this, parameters);
    }

    @Override
    public HypertableApproximateRowCount as(Name alias) {
        return new HypertableApproximateRowCount(alias, this, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public HypertableApproximateRowCount rename(String name) {
        return new HypertableApproximateRowCount(DSL.name(name), null, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public HypertableApproximateRowCount rename(Name name) {
        return new HypertableApproximateRowCount(name, null, parameters);
    }

    /**
     * Call this table-valued function
     */
    public HypertableApproximateRowCount call(Object mainTable) {
        return new HypertableApproximateRowCount(DSL.name(getName()), null, new Field[] { 
              DSL.val(mainTable, org.jooq.impl.DefaultDataType.getDefaultDataType("\"pg_catalog\".\"regclass\"").defaultValue(org.jooq.impl.DSL.field("NULL::regclass", org.jooq.impl.SQLDataType.OTHER)))
        });
    }

    /**
     * Call this table-valued function
     */
    public HypertableApproximateRowCount call(Field<Object> mainTable) {
        return new HypertableApproximateRowCount(DSL.name(getName()), null, new Field[] { 
              mainTable
        });
    }
}
