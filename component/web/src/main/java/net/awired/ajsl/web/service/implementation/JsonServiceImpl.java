/**
 *
 *     Copyright (C) norad.fr
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package net.awired.ajsl.web.service.implementation;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import net.awired.ajsl.web.service.interfaces.JsonService;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

@Service
public class JsonServiceImpl implements JsonService {

    protected ObjectMapper mapper;

    public JsonServiceImpl() {
        super();
        mapper = new ObjectMapper();

//        mapper.getSerializationConfig().set(Feature.DEFAULT_VIEW_INCLUSION, false);
        //      mapper.getSerializationConfig().setSerializationView(Type.class);

        CustomSerializerFactory serializerFactory = new CustomSerializerFactory();
        //        serializerFactory.addGenericMapping(Type.class, new IdEntitySerializer<Type, Long>());
        //        serializerFactory.addGenericMapping(Engine.class, new IdEntitySerializer<Engine, Long>());
        //        serializerFactory.addGenericMapping(Aggregator.class, new IdEntitySerializer<Aggregator, Long>());
        //        serializerFactory.addGenericMapping(Displayer.class, new IdEntitySerializer<Displayer, Long>());

        //mapper.getSerializerProvider().
        //StdSerializerProvider sp = new StdSerializerProvider();
        //sp.setNullValueSerializer(new NullSerializer());
        //sp.setKeySerializer(new AsearchSerializer());
        // And then configure mapper to use it
        //mapper.setSerializerProvider(sp);

        //        mapper = new ObjectMapper();

        //        mapper.setSerializerFactory(serializerFactory);
        //mapper.setSerializerFactory(new myserializer());

        // TODO Auto-generated constructor stub
    }

    //    public class IdEntitySerializer<ENTITY extends IdEntity<KEY_TYPE>, KEY_TYPE extends Serializable> extends
    //            JsonSerializer<ENTITY> {
    //        @Override
    //        public void serialize(ENTITY value, JsonGenerator paramJsonGenerator,
    //                SerializerProvider paramSerializerProvider) throws IOException, JsonProcessingException {
    //
    //            KEY_TYPE key = value.getId();
    //            if (key instanceof Long) {
    //                paramJsonGenerator.writeNumber((Long) key);
    //            } else if (key instanceof Integer) { // TODO remove as Long can contain Int ?
    //                paramJsonGenerator.writeNumber((Integer) key);
    //            } else {
    //                paramJsonGenerator.writeString(key.toString());
    //            }
    //
    //        }
    //    }

    //objectMapper.writeValueUsingView(out, infoInstance, Views.Public.class); // short-cut
    // or full version:
    //        objectMapper.getSerializationConfig().setSerializationView(Views.Public.class);
    //        objectMapper.writeValue(out, beanInstance); // will use active view set via Config
    // (note: can also pre-construct config object with 'mapper.copySerializationConfig'; reuse configuration)

    //    public final class AsearchSerializer extends JsonSerializer<Object> {
    //
    //        @Override
    //        public void serialize(Object value, JsonGenerator paramJsonGenerator,
    //                SerializerProvider paramSerializerProvider) throws IOException, JsonProcessingException {
    //
    //            //String keyStr = (value.getClass() == String.class) ? (String) value : value.toString();
    //
    //            paramJsonGenerator.writeNumber(42);
    //        }
    //    }
    //
    //    //custom JSON serializer
    //
    //    // and NullSerializer can be something as simple as:
    //    public class NullSerializer extends JsonSerializer<Object> {
    //        public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
    //                JsonProcessingException {
    //            // any JSON value you want...
    //            //jgen.writeString("");
    //            //            jgen.w
    //        }
    //    }
    //
    //    // and NullSerializer can be something as simple as:
    //    public class LongSerializer extends JsonSerializer<Long> {
    //        public void serialize(Long value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
    //                JsonProcessingException {
    //            // any JSON value you want...
    //            jgen.writeNumber(value);
    //        }
    //    }    

    @Override
    public Writer serializeFromView(Object object, Class<?> view) {
        Writer writer = new StringWriter();
        serializeFromView(object, writer, view);
        return writer;
    }

    @Override
    public void serializeFromView(Object object, Writer writer, Class<?> view) {
        try {
            ObjectWriter w = mapper.viewWriter(view);
            w.writeValue(writer, object);
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public StringWriter serialize(Object object) {
        StringWriter writer = new StringWriter();
        // serialize
        serialize(object, writer);
        return writer;
    }

    @Override
    public void serialize(Object object, Writer writer) {
        try {

            mapper.writeValue(writer, object);
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public <E> E deserialize(String datas, TypeReference<E> ref) {
        try {
            return (E) mapper.readValue(datas, ref);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <E> E deserialize(String datas, JavaType javaType) {
        try {
            return (E) mapper.readValue(datas, javaType);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <E> E deserialize(String datas, Class<E> clazz) {
        try {
            return mapper.readValue(datas, clazz);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
