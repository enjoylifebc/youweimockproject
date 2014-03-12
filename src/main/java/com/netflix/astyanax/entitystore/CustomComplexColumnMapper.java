package com.netflix.astyanax.entitystore;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.Serializer;
import com.netflix.astyanax.serializers.SerializerTypeInferer;

public class CustomComplexColumnMapper extends AbstractColumnMapper {
    private final Class<?>           keyClazz;
    private final Class<?>           valueClazz;
    private final Serializer<?>      keySerializer;
    private final Serializer<Object> valueSerializer;
    
	private final static EntityMapper<CustomObject,Long> customMapper = new EntityMapper(CustomObject.class,5000);	
	
    public CustomComplexColumnMapper(Field field) {
        super(field);
        
        ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
        this.keyClazz         = (Class<?>) stringListType.getActualTypeArguments()[0];
        this.keySerializer    = SerializerTypeInferer.getSerializer(this.keyClazz);

        this.valueClazz       = (Class<?>) stringListType.getActualTypeArguments()[1];
        this.valueSerializer  = SerializerTypeInferer.getSerializer(this.valueClazz);
    }

    @Override
    public String getColumnName() {
        return this.columnName;
    }

    @Override
    public boolean fillMutationBatch(Object entity, ColumnListMutation<String> clm, String prefix) throws Exception {
        Map<String, CustomObject> map = (Map<String, CustomObject>) field.get(entity);
        if (map == null) {
            if (columnAnnotation.nullable())
                return false; // skip
            else
                throw new IllegalArgumentException("cannot write non-nullable column with null value: " + columnName);
        }
        
        for (Entry<String, CustomObject> entry : map.entrySet()) {
        	CustomObject obj = entry.getValue();        	
        	for (ColumnMapper mapper : customMapper.getColumnList()){
        		mapper.fillMutationBatch(obj, clm, prefix + columnName + "." + entry.getKey()+".");
        	}            
        }
        return true;
    }
    
    @Override
    public boolean setField(Object entity, Iterator<String> name, com.netflix.astyanax.model.Column<String> column) throws Exception {
        Map<String, CustomObject> map = (Map<String, CustomObject>) field.get(entity);                
        if (map == null) {
            map = Maps.newLinkedHashMap();
            field.set(entity,  map);
        }
                
        String key = name.next();
        String colname = name.next(); 
        
        CustomObject execution = map.get(key);
        if (execution == null){
        	execution = new CustomObject();
        	map.put(key, execution);
        }                
        
        
        ColumnMapper mapper = customMapper.getColumnMapper(colname);
        mapper.setField(execution, Collections.<String>emptyList().iterator(), column);
        
        return true;
    }

    @Override
    public void validate(Object entity) throws Exception {
    }

}
