/*
 * This file is generated by jOOQ.
 */
package com.hedera.hashgraph.stablecoin.app.db.routines;


import com.hedera.hashgraph.stablecoin.app.db.Public;

import java.time.OffsetDateTime;

import org.jooq.Field;
import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.Internal;
import org.jooq.types.YearToSecond;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TimeBucket5 extends AbstractRoutine<OffsetDateTime> {

    private static final long serialVersionUID = 448951039;

    /**
     * The parameter <code>public.time_bucket.RETURN_VALUE</code>.
     */
    public static final Parameter<OffsetDateTime> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.SQLDataType.TIMESTAMPWITHTIMEZONE, false, false);

    /**
     * The parameter <code>public.time_bucket.bucket_width</code>.
     */
    public static final Parameter<YearToSecond> BUCKET_WIDTH = Internal.createParameter("bucket_width", org.jooq.impl.SQLDataType.INTERVAL, false, false);

    /**
     * The parameter <code>public.time_bucket.ts</code>.
     */
    public static final Parameter<OffsetDateTime> TS = Internal.createParameter("ts", org.jooq.impl.SQLDataType.TIMESTAMPWITHTIMEZONE, false, false);

    /**
     * The parameter <code>public.time_bucket.origin</code>.
     */
    public static final Parameter<OffsetDateTime> ORIGIN = Internal.createParameter("origin", org.jooq.impl.SQLDataType.TIMESTAMPWITHTIMEZONE, false, false);

    /**
     * Create a new routine call instance
     */
    public TimeBucket5() {
        super("time_bucket", Public.PUBLIC, org.jooq.impl.SQLDataType.TIMESTAMPWITHTIMEZONE);

        setReturnParameter(RETURN_VALUE);
        addInParameter(BUCKET_WIDTH);
        addInParameter(TS);
        addInParameter(ORIGIN);
        setOverloaded(true);
    }

    /**
     * Set the <code>bucket_width</code> parameter IN value to the routine
     */
    public void setBucketWidth(YearToSecond value) {
        setValue(BUCKET_WIDTH, value);
    }

    /**
     * Set the <code>bucket_width</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setBucketWidth(Field<YearToSecond> field) {
        setField(BUCKET_WIDTH, field);
    }

    /**
     * Set the <code>ts</code> parameter IN value to the routine
     */
    public void setTs(OffsetDateTime value) {
        setValue(TS, value);
    }

    /**
     * Set the <code>ts</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setTs(Field<OffsetDateTime> field) {
        setField(TS, field);
    }

    /**
     * Set the <code>origin</code> parameter IN value to the routine
     */
    public void setOrigin(OffsetDateTime value) {
        setValue(ORIGIN, value);
    }

    /**
     * Set the <code>origin</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setOrigin(Field<OffsetDateTime> field) {
        setField(ORIGIN, field);
    }
}
