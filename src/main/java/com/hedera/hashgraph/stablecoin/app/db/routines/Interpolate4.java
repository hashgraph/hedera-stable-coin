/*
 * This file is generated by jOOQ.
 */
package com.hedera.hashgraph.stablecoin.app.db.routines;


import com.hedera.hashgraph.stablecoin.app.db.Public;

import org.jooq.Field;
import org.jooq.Parameter;
import org.jooq.Record;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.Internal;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Interpolate4 extends AbstractRoutine<Float> {

    private static final long serialVersionUID = 882907786;

    /**
     * The parameter <code>public.interpolate.RETURN_VALUE</code>.
     */
    public static final Parameter<Float> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.SQLDataType.REAL, false, false);

    /**
     * The parameter <code>public.interpolate.value</code>.
     */
    public static final Parameter<Float> VALUE = Internal.createParameter("value", org.jooq.impl.SQLDataType.REAL, false, false);

    /**
     * The parameter <code>public.interpolate.prev</code>.
     */
    public static final Parameter<Record> PREV = Internal.createParameter("prev", org.jooq.impl.SQLDataType.RECORD.defaultValue(org.jooq.impl.DSL.field("NULL::record", org.jooq.impl.SQLDataType.RECORD)), true, false);

    /**
     * The parameter <code>public.interpolate.next</code>.
     */
    public static final Parameter<Record> NEXT = Internal.createParameter("next", org.jooq.impl.SQLDataType.RECORD.defaultValue(org.jooq.impl.DSL.field("NULL::record", org.jooq.impl.SQLDataType.RECORD)), true, false);

    /**
     * Create a new routine call instance
     */
    public Interpolate4() {
        super("interpolate", Public.PUBLIC, org.jooq.impl.SQLDataType.REAL);

        setReturnParameter(RETURN_VALUE);
        addInParameter(VALUE);
        addInParameter(PREV);
        addInParameter(NEXT);
        setOverloaded(true);
    }

    /**
     * Set the <code>value</code> parameter IN value to the routine
     */
    public void setValue(Float value) {
        setValue(VALUE, value);
    }

    /**
     * Set the <code>value</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setValue(Field<Float> field) {
        setField(VALUE, field);
    }

    /**
     * Set the <code>prev</code> parameter IN value to the routine
     */
    public void setPrev(Record value) {
        setValue(PREV, value);
    }

    /**
     * Set the <code>prev</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setPrev(Field<Record> field) {
        setField(PREV, field);
    }

    /**
     * Set the <code>next</code> parameter IN value to the routine
     */
    public void setNext(Record value) {
        setValue(NEXT, value);
    }

    /**
     * Set the <code>next</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setNext(Field<Record> field) {
        setField(NEXT, field);
    }
}
