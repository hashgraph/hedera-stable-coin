/*
 * This file is generated by jOOQ.
 */
package com.hedera.hashgraph.stablecoin.app.db;


import com.hedera.hashgraph.stablecoin.app.db.tables.AddDimension;
import com.hedera.hashgraph.stablecoin.app.db.tables.AddressTransaction;
import com.hedera.hashgraph.stablecoin.app.db.tables.AlterJobSchedule;
import com.hedera.hashgraph.stablecoin.app.db.tables.ChunkRelationSize;
import com.hedera.hashgraph.stablecoin.app.db.tables.ChunkRelationSizePretty;
import com.hedera.hashgraph.stablecoin.app.db.tables.CreateHypertable;
import com.hedera.hashgraph.stablecoin.app.db.tables.DropChunks;
import com.hedera.hashgraph.stablecoin.app.db.tables.FlywaySchemaHistory;
import com.hedera.hashgraph.stablecoin.app.db.tables.HypertableApproximateRowCount;
import com.hedera.hashgraph.stablecoin.app.db.tables.HypertableRelationSize;
import com.hedera.hashgraph.stablecoin.app.db.tables.HypertableRelationSizePretty;
import com.hedera.hashgraph.stablecoin.app.db.tables.IndexesRelationSize;
import com.hedera.hashgraph.stablecoin.app.db.tables.IndexesRelationSizePretty;
import com.hedera.hashgraph.stablecoin.app.db.tables.ShowChunks;
import com.hedera.hashgraph.stablecoin.app.db.tables.ShowTablespaces;
import com.hedera.hashgraph.stablecoin.app.db.tables.Transaction;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionApproveAllowance;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionApproveExternalTransfer;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionBurn;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionChangeComplianceManager;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionChangeEnforcementManager;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionChangeSupplyManager;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionConstruct;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionDecreaseAllowance;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionExternalTransfer;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionExternalTransferFrom;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionFreeze;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionIncreaseAllowance;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionMint;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionProposeOwner;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionSetKycPassed;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionTransfer;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionTransferFrom;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionUnfreeze;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionUnsetKycPassed;
import com.hedera.hashgraph.stablecoin.app.db.tables.TransactionWipe;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;
import org.jooq.types.YearToSecond;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Public extends SchemaImpl {

    private static final long serialVersionUID = 1239173150;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>public.add_dimension</code>.
     */
    public final AddDimension ADD_DIMENSION = AddDimension.ADD_DIMENSION;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static Result<Record> ADD_DIMENSION(Configuration configuration, Object mainTable, String columnName, Integer numberPartitions, Object chunkTimeInterval, String partitioningFunc, Boolean ifNotExists) {
        return configuration.dsl().selectFrom(com.hedera.hashgraph.stablecoin.app.db.tables.AddDimension.ADD_DIMENSION.call(mainTable, columnName, numberPartitions, chunkTimeInterval, partitioningFunc, ifNotExists)).fetch();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static AddDimension ADD_DIMENSION(Object mainTable, String columnName, Integer numberPartitions, Object chunkTimeInterval, String partitioningFunc, Boolean ifNotExists) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.AddDimension.ADD_DIMENSION.call(mainTable, columnName, numberPartitions, chunkTimeInterval, partitioningFunc, ifNotExists);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static AddDimension ADD_DIMENSION(Field<Object> mainTable, Field<String> columnName, Field<Integer> numberPartitions, Field<Object> chunkTimeInterval, Field<String> partitioningFunc, Field<Boolean> ifNotExists) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.AddDimension.ADD_DIMENSION.call(mainTable, columnName, numberPartitions, chunkTimeInterval, partitioningFunc, ifNotExists);
    }

    /**
     * The table <code>public.address_transaction</code>.
     */
    public final AddressTransaction ADDRESS_TRANSACTION = AddressTransaction.ADDRESS_TRANSACTION;

    /**
     * The table <code>public.alter_job_schedule</code>.
     */
    public final AlterJobSchedule ALTER_JOB_SCHEDULE = AlterJobSchedule.ALTER_JOB_SCHEDULE;

    /**
     * Call <code>public.alter_job_schedule</code>.
     */
    public static Result<Record> ALTER_JOB_SCHEDULE(Configuration configuration, Integer jobId, YearToSecond scheduleInterval, YearToSecond maxRuntime, Integer maxRetries, YearToSecond retryPeriod, Boolean ifExists, OffsetDateTime nextStart) {
        return configuration.dsl().selectFrom(com.hedera.hashgraph.stablecoin.app.db.tables.AlterJobSchedule.ALTER_JOB_SCHEDULE.call(jobId, scheduleInterval, maxRuntime, maxRetries, retryPeriod, ifExists, nextStart)).fetch();
    }

    /**
     * Get <code>public.alter_job_schedule</code> as a table.
     */
    public static AlterJobSchedule ALTER_JOB_SCHEDULE(Integer jobId, YearToSecond scheduleInterval, YearToSecond maxRuntime, Integer maxRetries, YearToSecond retryPeriod, Boolean ifExists, OffsetDateTime nextStart) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.AlterJobSchedule.ALTER_JOB_SCHEDULE.call(jobId, scheduleInterval, maxRuntime, maxRetries, retryPeriod, ifExists, nextStart);
    }

    /**
     * Get <code>public.alter_job_schedule</code> as a table.
     */
    public static AlterJobSchedule ALTER_JOB_SCHEDULE(Field<Integer> jobId, Field<YearToSecond> scheduleInterval, Field<YearToSecond> maxRuntime, Field<Integer> maxRetries, Field<YearToSecond> retryPeriod, Field<Boolean> ifExists, Field<OffsetDateTime> nextStart) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.AlterJobSchedule.ALTER_JOB_SCHEDULE.call(jobId, scheduleInterval, maxRuntime, maxRetries, retryPeriod, ifExists, nextStart);
    }

    /**
     * The table <code>public.chunk_relation_size</code>.
     */
    public final ChunkRelationSize CHUNK_RELATION_SIZE = ChunkRelationSize.CHUNK_RELATION_SIZE;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static Result<Record> CHUNK_RELATION_SIZE(Configuration configuration, Object mainTable) {
        return configuration.dsl().selectFrom(com.hedera.hashgraph.stablecoin.app.db.tables.ChunkRelationSize.CHUNK_RELATION_SIZE.call(mainTable)).fetch();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static ChunkRelationSize CHUNK_RELATION_SIZE(Object mainTable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.ChunkRelationSize.CHUNK_RELATION_SIZE.call(mainTable);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static ChunkRelationSize CHUNK_RELATION_SIZE(Field<Object> mainTable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.ChunkRelationSize.CHUNK_RELATION_SIZE.call(mainTable);
    }

    /**
     * The table <code>public.chunk_relation_size_pretty</code>.
     */
    public final ChunkRelationSizePretty CHUNK_RELATION_SIZE_PRETTY = ChunkRelationSizePretty.CHUNK_RELATION_SIZE_PRETTY;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static Result<Record> CHUNK_RELATION_SIZE_PRETTY(Configuration configuration, Object mainTable) {
        return configuration.dsl().selectFrom(com.hedera.hashgraph.stablecoin.app.db.tables.ChunkRelationSizePretty.CHUNK_RELATION_SIZE_PRETTY.call(mainTable)).fetch();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static ChunkRelationSizePretty CHUNK_RELATION_SIZE_PRETTY(Object mainTable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.ChunkRelationSizePretty.CHUNK_RELATION_SIZE_PRETTY.call(mainTable);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static ChunkRelationSizePretty CHUNK_RELATION_SIZE_PRETTY(Field<Object> mainTable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.ChunkRelationSizePretty.CHUNK_RELATION_SIZE_PRETTY.call(mainTable);
    }

    /**
     * The table <code>public.create_hypertable</code>.
     */
    public final CreateHypertable CREATE_HYPERTABLE = CreateHypertable.CREATE_HYPERTABLE;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static Result<Record> CREATE_HYPERTABLE(Configuration configuration, Object mainTable, String timeColumnName, String partitioningColumn, Integer numberPartitions, String associatedSchemaName, String associatedTablePrefix, Object chunkTimeInterval, Boolean createDefaultIndexes, Boolean ifNotExists, String partitioningFunc, Boolean migrateData, String chunkTargetSize, String chunkSizingFunc, String timePartitioningFunc) {
        return configuration.dsl().selectFrom(com.hedera.hashgraph.stablecoin.app.db.tables.CreateHypertable.CREATE_HYPERTABLE.call(mainTable, timeColumnName, partitioningColumn, numberPartitions, associatedSchemaName, associatedTablePrefix, chunkTimeInterval, createDefaultIndexes, ifNotExists, partitioningFunc, migrateData, chunkTargetSize, chunkSizingFunc, timePartitioningFunc)).fetch();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static CreateHypertable CREATE_HYPERTABLE(Object mainTable, String timeColumnName, String partitioningColumn, Integer numberPartitions, String associatedSchemaName, String associatedTablePrefix, Object chunkTimeInterval, Boolean createDefaultIndexes, Boolean ifNotExists, String partitioningFunc, Boolean migrateData, String chunkTargetSize, String chunkSizingFunc, String timePartitioningFunc) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.CreateHypertable.CREATE_HYPERTABLE.call(mainTable, timeColumnName, partitioningColumn, numberPartitions, associatedSchemaName, associatedTablePrefix, chunkTimeInterval, createDefaultIndexes, ifNotExists, partitioningFunc, migrateData, chunkTargetSize, chunkSizingFunc, timePartitioningFunc);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static CreateHypertable CREATE_HYPERTABLE(Field<Object> mainTable, Field<String> timeColumnName, Field<String> partitioningColumn, Field<Integer> numberPartitions, Field<String> associatedSchemaName, Field<String> associatedTablePrefix, Field<Object> chunkTimeInterval, Field<Boolean> createDefaultIndexes, Field<Boolean> ifNotExists, Field<String> partitioningFunc, Field<Boolean> migrateData, Field<String> chunkTargetSize, Field<String> chunkSizingFunc, Field<String> timePartitioningFunc) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.CreateHypertable.CREATE_HYPERTABLE.call(mainTable, timeColumnName, partitioningColumn, numberPartitions, associatedSchemaName, associatedTablePrefix, chunkTimeInterval, createDefaultIndexes, ifNotExists, partitioningFunc, migrateData, chunkTargetSize, chunkSizingFunc, timePartitioningFunc);
    }

    /**
     * The table <code>public.drop_chunks</code>.
     */
    public final DropChunks DROP_CHUNKS = DropChunks.DROP_CHUNKS;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static Result<Record> DROP_CHUNKS(Configuration configuration, Object olderThan, String tableName, String schemaName, Boolean cascade, Object newerThan, Boolean verbose, Boolean cascadeToMaterializations) {
        return configuration.dsl().selectFrom(com.hedera.hashgraph.stablecoin.app.db.tables.DropChunks.DROP_CHUNKS.call(olderThan, tableName, schemaName, cascade, newerThan, verbose, cascadeToMaterializations)).fetch();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static DropChunks DROP_CHUNKS(Object olderThan, String tableName, String schemaName, Boolean cascade, Object newerThan, Boolean verbose, Boolean cascadeToMaterializations) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.DropChunks.DROP_CHUNKS.call(olderThan, tableName, schemaName, cascade, newerThan, verbose, cascadeToMaterializations);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static DropChunks DROP_CHUNKS(Field<Object> olderThan, Field<String> tableName, Field<String> schemaName, Field<Boolean> cascade, Field<Object> newerThan, Field<Boolean> verbose, Field<Boolean> cascadeToMaterializations) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.DropChunks.DROP_CHUNKS.call(olderThan, tableName, schemaName, cascade, newerThan, verbose, cascadeToMaterializations);
    }

    /**
     * The table <code>public.flyway_schema_history</code>.
     */
    public final FlywaySchemaHistory FLYWAY_SCHEMA_HISTORY = FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY;

    /**
     * The table <code>public.hypertable_approximate_row_count</code>.
     */
    public final HypertableApproximateRowCount HYPERTABLE_APPROXIMATE_ROW_COUNT = HypertableApproximateRowCount.HYPERTABLE_APPROXIMATE_ROW_COUNT;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static Result<Record> HYPERTABLE_APPROXIMATE_ROW_COUNT(Configuration configuration, Object mainTable) {
        return configuration.dsl().selectFrom(com.hedera.hashgraph.stablecoin.app.db.tables.HypertableApproximateRowCount.HYPERTABLE_APPROXIMATE_ROW_COUNT.call(mainTable)).fetch();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static HypertableApproximateRowCount HYPERTABLE_APPROXIMATE_ROW_COUNT(Object mainTable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.HypertableApproximateRowCount.HYPERTABLE_APPROXIMATE_ROW_COUNT.call(mainTable);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static HypertableApproximateRowCount HYPERTABLE_APPROXIMATE_ROW_COUNT(Field<Object> mainTable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.HypertableApproximateRowCount.HYPERTABLE_APPROXIMATE_ROW_COUNT.call(mainTable);
    }

    /**
     * The table <code>public.hypertable_relation_size</code>.
     */
    public final HypertableRelationSize HYPERTABLE_RELATION_SIZE = HypertableRelationSize.HYPERTABLE_RELATION_SIZE;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static Result<Record> HYPERTABLE_RELATION_SIZE(Configuration configuration, Object mainTable) {
        return configuration.dsl().selectFrom(com.hedera.hashgraph.stablecoin.app.db.tables.HypertableRelationSize.HYPERTABLE_RELATION_SIZE.call(mainTable)).fetch();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static HypertableRelationSize HYPERTABLE_RELATION_SIZE(Object mainTable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.HypertableRelationSize.HYPERTABLE_RELATION_SIZE.call(mainTable);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static HypertableRelationSize HYPERTABLE_RELATION_SIZE(Field<Object> mainTable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.HypertableRelationSize.HYPERTABLE_RELATION_SIZE.call(mainTable);
    }

    /**
     * The table <code>public.hypertable_relation_size_pretty</code>.
     */
    public final HypertableRelationSizePretty HYPERTABLE_RELATION_SIZE_PRETTY = HypertableRelationSizePretty.HYPERTABLE_RELATION_SIZE_PRETTY;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static Result<Record> HYPERTABLE_RELATION_SIZE_PRETTY(Configuration configuration, Object mainTable) {
        return configuration.dsl().selectFrom(com.hedera.hashgraph.stablecoin.app.db.tables.HypertableRelationSizePretty.HYPERTABLE_RELATION_SIZE_PRETTY.call(mainTable)).fetch();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static HypertableRelationSizePretty HYPERTABLE_RELATION_SIZE_PRETTY(Object mainTable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.HypertableRelationSizePretty.HYPERTABLE_RELATION_SIZE_PRETTY.call(mainTable);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static HypertableRelationSizePretty HYPERTABLE_RELATION_SIZE_PRETTY(Field<Object> mainTable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.HypertableRelationSizePretty.HYPERTABLE_RELATION_SIZE_PRETTY.call(mainTable);
    }

    /**
     * The table <code>public.indexes_relation_size</code>.
     */
    public final IndexesRelationSize INDEXES_RELATION_SIZE = IndexesRelationSize.INDEXES_RELATION_SIZE;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static Result<Record> INDEXES_RELATION_SIZE(Configuration configuration, Object mainTable) {
        return configuration.dsl().selectFrom(com.hedera.hashgraph.stablecoin.app.db.tables.IndexesRelationSize.INDEXES_RELATION_SIZE.call(mainTable)).fetch();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static IndexesRelationSize INDEXES_RELATION_SIZE(Object mainTable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.IndexesRelationSize.INDEXES_RELATION_SIZE.call(mainTable);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static IndexesRelationSize INDEXES_RELATION_SIZE(Field<Object> mainTable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.IndexesRelationSize.INDEXES_RELATION_SIZE.call(mainTable);
    }

    /**
     * The table <code>public.indexes_relation_size_pretty</code>.
     */
    public final IndexesRelationSizePretty INDEXES_RELATION_SIZE_PRETTY = IndexesRelationSizePretty.INDEXES_RELATION_SIZE_PRETTY;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static Result<Record> INDEXES_RELATION_SIZE_PRETTY(Configuration configuration, Object mainTable) {
        return configuration.dsl().selectFrom(com.hedera.hashgraph.stablecoin.app.db.tables.IndexesRelationSizePretty.INDEXES_RELATION_SIZE_PRETTY.call(mainTable)).fetch();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static IndexesRelationSizePretty INDEXES_RELATION_SIZE_PRETTY(Object mainTable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.IndexesRelationSizePretty.INDEXES_RELATION_SIZE_PRETTY.call(mainTable);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static IndexesRelationSizePretty INDEXES_RELATION_SIZE_PRETTY(Field<Object> mainTable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.IndexesRelationSizePretty.INDEXES_RELATION_SIZE_PRETTY.call(mainTable);
    }

    /**
     * The table <code>public.show_chunks</code>.
     */
    public final ShowChunks SHOW_CHUNKS = ShowChunks.SHOW_CHUNKS;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static Result<Record> SHOW_CHUNKS(Configuration configuration, Object hypertable, Object olderThan, Object newerThan) {
        return configuration.dsl().selectFrom(com.hedera.hashgraph.stablecoin.app.db.tables.ShowChunks.SHOW_CHUNKS.call(hypertable, olderThan, newerThan)).fetch();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static ShowChunks SHOW_CHUNKS(Object hypertable, Object olderThan, Object newerThan) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.ShowChunks.SHOW_CHUNKS.call(hypertable, olderThan, newerThan);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static ShowChunks SHOW_CHUNKS(Field<Object> hypertable, Field<Object> olderThan, Field<Object> newerThan) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.ShowChunks.SHOW_CHUNKS.call(hypertable, olderThan, newerThan);
    }

    /**
     * The table <code>public.show_tablespaces</code>.
     */
    public final ShowTablespaces SHOW_TABLESPACES = ShowTablespaces.SHOW_TABLESPACES;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static Result<Record> SHOW_TABLESPACES(Configuration configuration, Object hypertable) {
        return configuration.dsl().selectFrom(com.hedera.hashgraph.stablecoin.app.db.tables.ShowTablespaces.SHOW_TABLESPACES.call(hypertable)).fetch();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static ShowTablespaces SHOW_TABLESPACES(Object hypertable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.ShowTablespaces.SHOW_TABLESPACES.call(hypertable);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static ShowTablespaces SHOW_TABLESPACES(Field<Object> hypertable) {
        return com.hedera.hashgraph.stablecoin.app.db.tables.ShowTablespaces.SHOW_TABLESPACES.call(hypertable);
    }

    /**
     * The table <code>public.transaction</code>.
     */
    public final Transaction TRANSACTION = Transaction.TRANSACTION;

    /**
     * The table <code>public.transaction_approve_allowance</code>.
     */
    public final TransactionApproveAllowance TRANSACTION_APPROVE_ALLOWANCE = TransactionApproveAllowance.TRANSACTION_APPROVE_ALLOWANCE;

    /**
     * The table <code>public.transaction_approve_external_transfer</code>.
     */
    public final TransactionApproveExternalTransfer TRANSACTION_APPROVE_EXTERNAL_TRANSFER = TransactionApproveExternalTransfer.TRANSACTION_APPROVE_EXTERNAL_TRANSFER;

    /**
     * The table <code>public.transaction_burn</code>.
     */
    public final TransactionBurn TRANSACTION_BURN = TransactionBurn.TRANSACTION_BURN;

    /**
     * The table <code>public.transaction_change_compliance_manager</code>.
     */
    public final TransactionChangeComplianceManager TRANSACTION_CHANGE_COMPLIANCE_MANAGER = TransactionChangeComplianceManager.TRANSACTION_CHANGE_COMPLIANCE_MANAGER;

    /**
     * The table <code>public.transaction_change_enforcement_manager</code>.
     */
    public final TransactionChangeEnforcementManager TRANSACTION_CHANGE_ENFORCEMENT_MANAGER = TransactionChangeEnforcementManager.TRANSACTION_CHANGE_ENFORCEMENT_MANAGER;

    /**
     * The table <code>public.transaction_change_supply_manager</code>.
     */
    public final TransactionChangeSupplyManager TRANSACTION_CHANGE_SUPPLY_MANAGER = TransactionChangeSupplyManager.TRANSACTION_CHANGE_SUPPLY_MANAGER;

    /**
     * The table <code>public.transaction_construct</code>.
     */
    public final TransactionConstruct TRANSACTION_CONSTRUCT = TransactionConstruct.TRANSACTION_CONSTRUCT;

    /**
     * The table <code>public.transaction_decrease_allowance</code>.
     */
    public final TransactionDecreaseAllowance TRANSACTION_DECREASE_ALLOWANCE = TransactionDecreaseAllowance.TRANSACTION_DECREASE_ALLOWANCE;

    /**
     * The table <code>public.transaction_external_transfer</code>.
     */
    public final TransactionExternalTransfer TRANSACTION_EXTERNAL_TRANSFER = TransactionExternalTransfer.TRANSACTION_EXTERNAL_TRANSFER;

    /**
     * The table <code>public.transaction_external_transfer_from</code>.
     */
    public final TransactionExternalTransferFrom TRANSACTION_EXTERNAL_TRANSFER_FROM = TransactionExternalTransferFrom.TRANSACTION_EXTERNAL_TRANSFER_FROM;

    /**
     * The table <code>public.transaction_freeze</code>.
     */
    public final TransactionFreeze TRANSACTION_FREEZE = TransactionFreeze.TRANSACTION_FREEZE;

    /**
     * The table <code>public.transaction_increase_allowance</code>.
     */
    public final TransactionIncreaseAllowance TRANSACTION_INCREASE_ALLOWANCE = TransactionIncreaseAllowance.TRANSACTION_INCREASE_ALLOWANCE;

    /**
     * The table <code>public.transaction_mint</code>.
     */
    public final TransactionMint TRANSACTION_MINT = TransactionMint.TRANSACTION_MINT;

    /**
     * The table <code>public.transaction_propose_owner</code>.
     */
    public final TransactionProposeOwner TRANSACTION_PROPOSE_OWNER = TransactionProposeOwner.TRANSACTION_PROPOSE_OWNER;

    /**
     * The table <code>public.transaction_set_kyc_passed</code>.
     */
    public final TransactionSetKycPassed TRANSACTION_SET_KYC_PASSED = TransactionSetKycPassed.TRANSACTION_SET_KYC_PASSED;

    /**
     * The table <code>public.transaction_transfer</code>.
     */
    public final TransactionTransfer TRANSACTION_TRANSFER = TransactionTransfer.TRANSACTION_TRANSFER;

    /**
     * The table <code>public.transaction_transfer_from</code>.
     */
    public final TransactionTransferFrom TRANSACTION_TRANSFER_FROM = TransactionTransferFrom.TRANSACTION_TRANSFER_FROM;

    /**
     * The table <code>public.transaction_unfreeze</code>.
     */
    public final TransactionUnfreeze TRANSACTION_UNFREEZE = TransactionUnfreeze.TRANSACTION_UNFREEZE;

    /**
     * The table <code>public.transaction_unset_kyc_passed</code>.
     */
    public final TransactionUnsetKycPassed TRANSACTION_UNSET_KYC_PASSED = TransactionUnsetKycPassed.TRANSACTION_UNSET_KYC_PASSED;

    /**
     * The table <code>public.transaction_wipe</code>.
     */
    public final TransactionWipe TRANSACTION_WIPE = TransactionWipe.TRANSACTION_WIPE;

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.<Table<?>>asList(
            AddDimension.ADD_DIMENSION,
            AddressTransaction.ADDRESS_TRANSACTION,
            AlterJobSchedule.ALTER_JOB_SCHEDULE,
            ChunkRelationSize.CHUNK_RELATION_SIZE,
            ChunkRelationSizePretty.CHUNK_RELATION_SIZE_PRETTY,
            CreateHypertable.CREATE_HYPERTABLE,
            DropChunks.DROP_CHUNKS,
            FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY,
            HypertableApproximateRowCount.HYPERTABLE_APPROXIMATE_ROW_COUNT,
            HypertableRelationSize.HYPERTABLE_RELATION_SIZE,
            HypertableRelationSizePretty.HYPERTABLE_RELATION_SIZE_PRETTY,
            IndexesRelationSize.INDEXES_RELATION_SIZE,
            IndexesRelationSizePretty.INDEXES_RELATION_SIZE_PRETTY,
            ShowChunks.SHOW_CHUNKS,
            ShowTablespaces.SHOW_TABLESPACES,
            Transaction.TRANSACTION,
            TransactionApproveAllowance.TRANSACTION_APPROVE_ALLOWANCE,
            TransactionApproveExternalTransfer.TRANSACTION_APPROVE_EXTERNAL_TRANSFER,
            TransactionBurn.TRANSACTION_BURN,
            TransactionChangeComplianceManager.TRANSACTION_CHANGE_COMPLIANCE_MANAGER,
            TransactionChangeEnforcementManager.TRANSACTION_CHANGE_ENFORCEMENT_MANAGER,
            TransactionChangeSupplyManager.TRANSACTION_CHANGE_SUPPLY_MANAGER,
            TransactionConstruct.TRANSACTION_CONSTRUCT,
            TransactionDecreaseAllowance.TRANSACTION_DECREASE_ALLOWANCE,
            TransactionExternalTransfer.TRANSACTION_EXTERNAL_TRANSFER,
            TransactionExternalTransferFrom.TRANSACTION_EXTERNAL_TRANSFER_FROM,
            TransactionFreeze.TRANSACTION_FREEZE,
            TransactionIncreaseAllowance.TRANSACTION_INCREASE_ALLOWANCE,
            TransactionMint.TRANSACTION_MINT,
            TransactionProposeOwner.TRANSACTION_PROPOSE_OWNER,
            TransactionSetKycPassed.TRANSACTION_SET_KYC_PASSED,
            TransactionTransfer.TRANSACTION_TRANSFER,
            TransactionTransferFrom.TRANSACTION_TRANSFER_FROM,
            TransactionUnfreeze.TRANSACTION_UNFREEZE,
            TransactionUnsetKycPassed.TRANSACTION_UNSET_KYC_PASSED,
            TransactionWipe.TRANSACTION_WIPE);
    }
}