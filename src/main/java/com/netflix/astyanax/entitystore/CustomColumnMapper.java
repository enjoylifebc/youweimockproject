package com.netflix.astyanax.entitystore;

import java.lang.reflect.Field;
import java.util.Iterator;

import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.Serializer;
import com.netflix.astyanax.serializers.IntegerSerializer;

// customized column mapper
class CustomColumnMapper extends AbstractColumnMapper {
	
	private final Serializer<Integer> serializer;

	CustomColumnMapper(final Field field) {
	    super(field);
		// this.serializer = MappingUtils.getSerializerForField(field);
	    this.serializer = IntegerSerializer.get();
	}

	@Override
	public String getColumnName() {
		return columnName;
	}
	
	Serializer<?> getSerializer() {
	    return serializer;
	}
	
	
	@Override
	public boolean fillMutationBatch(Object entity, ColumnListMutation<String> clm, String prefix) throws Exception {
		// change object to customized class
		Object value = (Object) field.get(entity);
		if(value == null) {
			if(columnAnnotation.nullable())
				return false; // skip
			else
				throw new IllegalArgumentException("cannot write non-nullable column with null value: " + columnName);
		}
		@SuppressWarnings("rawtypes")
		final Serializer valueSerializer = serializer;
		// clm.putColumn(prefix + columnName, new Integer(value.toString()), valueSerializer, null);
		return true;
	}
	
    @Override
    public boolean setField(Object entity, Iterator<String> name, com.netflix.astyanax.model.Column<String> column) throws Exception {
        if (name.hasNext()) 
            return false;
        final Integer fieldValue = column.getValue(serializer); 
        this.field.set(entity, fieldValue);
        return true;
    }

    @Override
    public void validate(Object entity) throws Exception {
        if (field.get(entity) == null && !columnAnnotation.nullable())
            throw new IllegalArgumentException("cannot find non-nullable column: " + columnName);
    }
}
