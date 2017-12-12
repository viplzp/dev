package io.flysium.framework.http.converter.json;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;

/**
 * json数据转换器
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class CustomJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

	@Override
	protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException {
		JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
		JsonGenerator generator = this.objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);
		try {
			if (object instanceof String) {
				generator.writeRaw((String) object);
			} else {
				writePrefix(generator, object);
				Class<?> serializationView = null;
				Object value = object;
				if (value instanceof MappingJacksonValue) {
					MappingJacksonValue container = (MappingJacksonValue) object;
					value = container.getValue();
					serializationView = container.getSerializationView();
				}
				if (serializationView != null) {
					this.objectMapper.writerWithView(serializationView).writeValue(generator, value);
				} else {
					this.objectMapper.writeValue(generator, value);
				}
				writeSuffix(generator, object);
			}

			generator.flush();

		} catch (JsonProcessingException ex) {
			throw new HttpMessageNotWritableException("Could not write content: " + ex.getMessage(), ex);
		}
	}

	/**
	 * 重写该方法，我们只需要加上Object resultObj = this.process( obj, javaClazz,
	 * inputMessage ); 并返回resultObj, process方法里面我们过滤白名单和进行xss处理
	 */
	@Override
	public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException {
		JavaType javaType = getJavaType(type, contextClass);

		Object obj = this.readInternal(javaType, inputMessage);

		return this.process(obj, javaType, inputMessage);
	}

	protected Object readInternal(JavaType javaType, HttpInputMessage inputMessage) throws IOException {
		Class<?> javaClazz = javaType.getRawClass();
		if (javaClazz != null) {
			if (javaClazz.equals(String.class)) {
				return IOUtils.toString(inputMessage.getBody());
			} else if (javaClazz.equals(int.class) || javaClazz.equals(Integer.class)) {
				String value = IOUtils.toString(inputMessage.getBody());
				return Integer.parseInt(value);
			} else if (javaClazz.equals(long.class) || javaClazz.equals(Long.class)) {
				String value = IOUtils.toString(inputMessage.getBody());
				return Long.valueOf(value);
			} else if (javaClazz.equals(float.class) || javaClazz.equals(Float.class)) {
				String value = IOUtils.toString(inputMessage.getBody());
				return Float.valueOf(value);
			} else if (javaClazz.equals(double.class) || javaClazz.equals(Double.class)) {
				String value = IOUtils.toString(inputMessage.getBody());
				return Double.valueOf(value);
			} else if (javaClazz.equals(boolean.class) || javaClazz.equals(Boolean.class)) {
				String value = IOUtils.toString(inputMessage.getBody());
				return Boolean.valueOf(value);
			}
		}
		return readJavaType(javaType, inputMessage);
	}

	// 这个就是父类的readJavaType方法，由于父类该方法是private的，所以我们copy一个用
	private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
		try {
			return this.objectMapper.readValue(inputMessage.getBody(), javaType);
		} catch (IOException ex) {
			throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
		}
	}

	protected Object process(Object obj, JavaType javaType, HttpInputMessage inputMessage) {
		// 暂不做任何处理，后续如果有xss防范，可以在此判定与过滤
		return obj;
	}

}