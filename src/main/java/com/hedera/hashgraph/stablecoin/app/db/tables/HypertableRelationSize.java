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
public class HypertableRelationSize extends TableImpl<Record> {

    private static final long serialVersionUID = 266502493;

    /**
     * The reference instance of <code>public.hypertable_relation_size</code>
     */
    public static final HypertableRelationSize HYPERTABLE_RELATION_SIZE = new HypertableRelationSize();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>public.hypertable_relation_size.table_bytes</code>.
     */
    public final TableField<Record, Long> TABLE_BYTES = createField(DSL.name("table_bytes"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.hypertable_relation_size.index_bytes</code>.
     */
    public final TableField<Record, Long> INDEX_BYTES = createField(DSL.name("index_bytes"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.hypertable_relation_size.toast_bytes</code>.
     */
    public final TableField<Record, Long> TOAST_BYTES = createField(DSL.name("toast_bytes"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.hypertable_relation_size.total_bytes</code>.
     */
    public final TableField<Record, Long> TOTAL_BYTES = createField(DSL.name("total_bytes"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * Create a <code>public.hypertable_relation_size</code> table reference
     */
    public HypertableRelationSize() {
        this(DSL.name("hypertable_relation_size"), null);
    }

    /**
     * Create an aliased <code>public.hypertable_relation_size</code> table reference
     */
    public HypertableRelationSize(String alias) {
        this(DSL.name(alias), HYPERTABLE_RELATION_SIZE);
    }

    /**
     * Create an aliased <code>public.hypertable_relation_size</code> table reference
     */
    public HypertableRelationSize(Name alias) {
        this(alias, HYPERTABLE_RELATION_SIZE);
    }

    private HypertableRelationSize(Name alias, Table<Record> aliased) {
        this(alias, aliased, new Field[1]);
    }

    private HypertableRelationSize(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.function());
    }

    public <O extends Record> HypertableRelationSize(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, HYPERTABLE_RELATION_SIZE);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public HypertableRelationSize as(String alias) {
        return new HypertableRelationSize(DSL.name(alias), this, parameters);
    }

    @Override
    public HypertableRelationSize as(Name alias) {
        return new HypertableRelationSize(alias, this, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public HypertableRelationSize rename(String name) {
        return new HypertableRelationSize(DSL.name(name), null, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public HypertableRelationSize rename(Name name) {
        return new HypertableRelationSize(name, null, parameters);
    }

    /**
     * Call this table-valued function
     */
    public HypertableRelationSize call(Object mainTable) {
        return new HypertableRelationSize(DSL.name(getName()), null, new Field[] { 
              DSL.val(mainTable, org.jooq.impl.DefaultDataType.getDefaultDataType("\"pg_catalog\".\"regclass\""))
        });
    }

    /**
     * Call this table-valued function
     */
    public HypertableRelationSize call(Field<Object> mainTable) {
        return new HypertableRelationSize(DSL.name(getName()), null, new Field[] { 
              mainTable
        });
    }
}
