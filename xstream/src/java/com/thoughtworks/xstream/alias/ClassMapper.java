package com.thoughtworks.xstream.alias;

public interface ClassMapper extends Mapper {

    String lookupName(Class type);

    Class lookupType(String elementName);

    String mapNameFromXML( String xmlName );

    String mapNameToXML( String javaName );

    /**
     * Place holder type used for null values.
     */
    class Null {}

    /**
     * @deprecated As of 1.1.1, use {@link #defaultImplementationOf(Class)}
     */
    Class lookupDefaultType(Class baseType);

    /**
     * @deprecated As of 1.1.1, use {@link AliasingMapper#addAlias(String, Class)} for creating an alias and
     *             {@link DefaultImplementationsMapper#addDefaultImplementation(Class, Class)} for specifiny a
     *             default implementation.
     */
    void alias(String elementName, Class type, Class defaultImplementation);

}
