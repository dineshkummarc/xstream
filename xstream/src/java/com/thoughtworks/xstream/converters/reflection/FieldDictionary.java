package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedHashMap;

public class FieldDictionary {

    private static final Map keyedByFieldNameCache = new TreeMap();
    private static final Map keyedByFieldKeyCache = new TreeMap();

    public Iterator serializableFieldsFor(Class cls) {
        return buildMap(cls, true).values().iterator();
    }

    public Field field(Class cls, String name, Class definedIn) {
        Map fields = buildMap(cls, definedIn != null);
        Field field = (Field) fields.get(definedIn != null ? (Object) new FieldKey(name, definedIn, 0) : (Object) name);
        if (field == null) {
            throw new ObjectAccessException("No such field " + cls.getName() + "." + name);
        } else {
            return field;
        }
    }

    private Map buildMap(Class cls, boolean tupleKeyed) {
        final String clsName = cls.getName();
        if (!keyedByFieldNameCache.containsKey(clsName)) {
            final Map keyedByFieldName = new TreeMap();
            final Map keyedByFieldKey = new LinkedHashMap();
            while (!Object.class.equals(cls)) {
                Field[] fields = cls.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    if (field.getName().startsWith("this$")) {
                        continue;
                    }
                    field.setAccessible(true);
                    if (!keyedByFieldName.containsKey(field.getName())) {
                        keyedByFieldName.put(field.getName(), field);
                    }
                    keyedByFieldKey.put(new FieldKey(field.getName(), field.getDeclaringClass(), i), field);
                }
                cls = cls.getSuperclass();
            }
            keyedByFieldNameCache.put(clsName, keyedByFieldName);
            keyedByFieldKeyCache.put(clsName, keyedByFieldKey);
        }
        return (Map) (tupleKeyed ? keyedByFieldKeyCache.get(clsName) : keyedByFieldNameCache.get(clsName));
    }

    private static class FieldKey {
        private String fieldName;
        private Class declaringClass;
        private Integer depth;
        private int order;

        public FieldKey(String fieldName, Class declaringClass, int order) {
            this.fieldName = fieldName;
            this.declaringClass = declaringClass;
            this.order = order;
            Class c = declaringClass;
            int i = 0;
            while (c.getSuperclass() != null) {
                i++;
                c = c.getSuperclass();
            }
            depth = new Integer(i);
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FieldKey)) return false;

            final FieldKey fieldKey = (FieldKey) o;

            if (declaringClass != null ? !declaringClass.equals(fieldKey.declaringClass) : fieldKey.declaringClass != null) return false;
            if (fieldName != null ? !fieldName.equals(fieldKey.fieldName) : fieldKey.fieldName != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (fieldName != null ? fieldName.hashCode() : 0);
            result = 29 * result + (declaringClass != null ? declaringClass.hashCode() : 0);
            return result;
        }

        public String toString() {
            return "FieldKey{" +
                    "order=" + order +
                    ", depth=" + depth +
                    ", declaringClass=" + declaringClass +
                    ", fieldName='" + fieldName + "'" +
                    "}";
        }


    }

}
