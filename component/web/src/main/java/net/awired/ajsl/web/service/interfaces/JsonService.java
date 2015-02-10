package net.awired.ajsl.web.service.interfaces;

import java.io.StringWriter;
import java.io.Writer;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

public interface JsonService {

    Writer serializeFromView(Object object, Class<?> view);

    void serializeFromView(Object object, Writer writer, Class<?> view);

    StringWriter serialize(Object object);

    void serialize(Object object, Writer writer);

    <E> E deserialize(String datas, TypeReference<E> ref);

    <E> E deserialize(String datas, Class<E> clazz);

    <E> E deserialize(String datas, JavaType javaType);
}
