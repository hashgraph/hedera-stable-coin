/*
 * This file is generated by jOOQ.
 */
package com.hedera.hashgraph.stablecoin.app.db.tables;


import com.hedera.hashgraph.stablecoin.app.db.Keys;
import com.hedera.hashgraph.stablecoin.app.db.Public;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TransactionConstruct extends TableImpl<Record> {

    private static final long serialVersionUID = 204335008;

    /**
     * The reference instance of <code>public.transaction_construct</code>
     */
    public static final TransactionConstruct TRANSACTION_CONSTRUCT = new TransactionConstruct();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>public.transaction_construct.timestamp</code>.
     */
    public final TableField<Record, Long> TIMESTAMP = createField(DSL.name("timestamp"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.transaction_construct.token_name</code>.
     */
    public final TableField<Record, String> TOKEN_NAME = createField(DSL.name("token_name"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.transaction_construct.token_symbol</code>.
     */
    public final TableField<Record, String> TOKEN_SYMBOL = createField(DSL.name("token_symbol"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.transaction_construct.token_decimal</code>.
     */
    public final TableField<Record, BigInteger> TOKEN_DECIMAL = createField(DSL.name("token_decimal"), org.jooq.impl.SQLDataType.DECIMAL_INTEGER.precision(78).nullable(false), this, "");

    /**
     * The column <code>public.transaction_construct.total_supply</code>.
     */
    public final TableField<Record, BigInteger> TOTAL_SUPPLY = createField(DSL.name("total_supply"), org.jooq.impl.SQLDataType.DECIMAL_INTEGER.precision(78).nullable(false), this, "");

    /**
     * The column <code>public.transaction_construct.supply_manager</code>.
     */
    public final TableField<Record, byte[]> SUPPLY_MANAGER = createField(DSL.name("supply_manager"), org.jooq.impl.SQLDataType.BLOB.nullable(false), this, "");

    /**
     * The column <code>public.transaction_construct.compliance_manager</code>.
     */
    public final TableField<Record, byte[]> COMPLIANCE_MANAGER = createField(DSL.name("compliance_manager"), org.jooq.impl.SQLDataType.BLOB.nullable(false), this, "");

    /**
     * The column <code>public.transaction_construct.enforcement_manager</code>.
     */
    public final TableField<Record, byte[]> ENFORCEMENT_MANAGER = createField(DSL.name("enforcement_manager"), org.jooq.impl.SQLDataType.BLOB.nullable(false), this, "");

    /**
     * Create a <code>public.transaction_construct</code> table reference
     */
    public TransactionConstruct() {
        this(DSL.name("transaction_construct"), null);
    }

    /**
     * Create an aliased <code>public.transaction_construct</code> table reference
     */
    public TransactionConstruct(String alias) {
        this(DSL.name(alias), TRANSACTION_CONSTRUCT);
    }

    /**
     * Create an aliased <code>public.transaction_construct</code> table reference
     */
    public TransactionConstruct(Name alias) {
        this(alias, TRANSACTION_CONSTRUCT);
    }

    private TransactionConstruct(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private TransactionConstruct(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> TransactionConstruct(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, TRANSACTION_CONSTRUCT);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.TRANSACTION_CONSTRUCT_PKEY;
    }

    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.TRANSACTION_CONSTRUCT_PKEY);
    }

    @Override
    public TransactionConstruct as(String alias) {
        return new TransactionConstruct(DSL.name(alias), this);
    }

    @Override
    public TransactionConstruct as(Name alias) {
        return new TransactionConstruct(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TransactionConstruct rename(String name) {
        return new TransactionConstruct(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TransactionConstruct rename(Name name) {
        return new TransactionConstruct(name, null);
    }
}
