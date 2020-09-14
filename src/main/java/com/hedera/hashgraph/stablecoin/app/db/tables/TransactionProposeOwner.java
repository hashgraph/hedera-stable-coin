/*
 * This file is generated by jOOQ.
 */
package com.hedera.hashgraph.stablecoin.app.db.tables;


import com.hedera.hashgraph.stablecoin.app.db.Keys;
import com.hedera.hashgraph.stablecoin.app.db.Public;

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
public class TransactionProposeOwner extends TableImpl<Record> {

    private static final long serialVersionUID = -1547707725;

    /**
     * The reference instance of <code>public.transaction_propose_owner</code>
     */
    public static final TransactionProposeOwner TRANSACTION_PROPOSE_OWNER = new TransactionProposeOwner();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>public.transaction_propose_owner.timestamp</code>.
     */
    public final TableField<Record, Long> TIMESTAMP = createField(DSL.name("timestamp"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.transaction_propose_owner.address</code>.
     */
    public final TableField<Record, byte[]> ADDRESS = createField(DSL.name("address"), org.jooq.impl.SQLDataType.BLOB.nullable(false), this, "");

    /**
     * Create a <code>public.transaction_propose_owner</code> table reference
     */
    public TransactionProposeOwner() {
        this(DSL.name("transaction_propose_owner"), null);
    }

    /**
     * Create an aliased <code>public.transaction_propose_owner</code> table reference
     */
    public TransactionProposeOwner(String alias) {
        this(DSL.name(alias), TRANSACTION_PROPOSE_OWNER);
    }

    /**
     * Create an aliased <code>public.transaction_propose_owner</code> table reference
     */
    public TransactionProposeOwner(Name alias) {
        this(alias, TRANSACTION_PROPOSE_OWNER);
    }

    private TransactionProposeOwner(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private TransactionProposeOwner(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> TransactionProposeOwner(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, TRANSACTION_PROPOSE_OWNER);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.TRANSACTION_PROPOSE_OWNER_PKEY;
    }

    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.TRANSACTION_PROPOSE_OWNER_PKEY);
    }

    @Override
    public TransactionProposeOwner as(String alias) {
        return new TransactionProposeOwner(DSL.name(alias), this);
    }

    @Override
    public TransactionProposeOwner as(Name alias) {
        return new TransactionProposeOwner(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TransactionProposeOwner rename(String name) {
        return new TransactionProposeOwner(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TransactionProposeOwner rename(Name name) {
        return new TransactionProposeOwner(name, null);
    }
}
