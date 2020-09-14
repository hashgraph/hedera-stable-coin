/*
 * This file is generated by jOOQ.
 */
package com.hedera.hashgraph.stablecoin.app.db.routines;


import com.hedera.hashgraph.stablecoin.app.db.Public;

import org.jooq.Field;
import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.Internal;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TimeBucketGapfill1 extends AbstractRoutine<Short> {

    private static final long serialVersionUID = -1867787009;

    /**
     * The parameter <code>public.time_bucket_gapfill.RETURN_VALUE</code>.
     */
    public static final Parameter<Short> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.SQLDataType.SMALLINT, false, false);

    /**
     * The parameter <code>public.time_bucket_gapfill.bucket_width</code>.
     */
    public static final Parameter<Short> BUCKET_WIDTH = Internal.createParameter("bucket_width", org.jooq.impl.SQLDataType.SMALLINT, false, false);

    /**
     * The parameter <code>public.time_bucket_gapfill.ts</code>.
     */
    public static final Parameter<Short> TS = Internal.createParameter("ts", org.jooq.impl.SQLDataType.SMALLINT, false, false);

    /**
     * The parameter <code>public.time_bucket_gapfill.start</code>.
     */
    public static final Parameter<Short> START = Internal.createParameter("start", org.jooq.impl.SQLDataType.SMALLINT.defaultValue(org.jooq.impl.DSL.field("NULL::smallint", org.jooq.impl.SQLDataType.SMALLINT)), true, false);

    /**
     * The parameter <code>public.time_bucket_gapfill.finish</code>.
     */
    public static final Parameter<Short> FINISH = Internal.createParameter("finish", org.jooq.impl.SQLDataType.SMALLINT.defaultValue(org.jooq.impl.DSL.field("NULL::smallint", org.jooq.impl.SQLDataType.SMALLINT)), true, false);

    /**
     * Create a new routine call instance
     */
    public TimeBucketGapfill1() {
        super("time_bucket_gapfill", Public.PUBLIC, org.jooq.impl.SQLDataType.SMALLINT);

        setReturnParameter(RETURN_VALUE);
        addInParameter(BUCKET_WIDTH);
        addInParameter(TS);
        addInParameter(START);
        addInParameter(FINISH);
        setOverloaded(true);
    }

    /**
     * Set the <code>bucket_width</code> parameter IN value to the routine
     */
    public void setBucketWidth(Short value) {
        setValue(BUCKET_WIDTH, value);
    }

    /**
     * Set the <code>bucket_width</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setBucketWidth(Field<Short> field) {
        setField(BUCKET_WIDTH, field);
    }

    /**
     * Set the <code>ts</code> parameter IN value to the routine
     */
    public void setTs(Short value) {
        setValue(TS, value);
    }

    /**
     * Set the <code>ts</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setTs(Field<Short> field) {
        setField(TS, field);
    }

    /**
     * Set the <code>start</code> parameter IN value to the routine
     */
    public void setStart(Short value) {
        setValue(START, value);
    }

    /**
     * Set the <code>start</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setStart(Field<Short> field) {
        setField(START, field);
    }

    /**
     * Set the <code>finish</code> parameter IN value to the routine
     */
    public void setFinish(Short value) {
        setValue(FINISH, value);
    }

    /**
     * Set the <code>finish</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setFinish(Field<Short> field) {
        setField(FINISH, field);
    }
}
