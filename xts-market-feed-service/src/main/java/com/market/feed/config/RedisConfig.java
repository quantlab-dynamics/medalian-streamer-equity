package com.market.feed.config;

import com.esotericsoftware.kryo.Kryo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.market.feed.model.MarketData;
import com.market.feed.model.SyntheticForList;
import com.market.feed.model.SyntheticPrice;
import com.sf.xts.api.sdk.marketdata.master.MasterResponseFO;
import com.sf.xts.api.sdk.marketdata.response.MarketDepthBinaryResponse;
import com.sf.xts.api.sdk.marketdata.response.OpenInterestBinaryResponse;
import com.sf.xts.api.sdk.marketdata.response.TouchlineBinaryResposne;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class RedisConfig {

	private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);


	// Generic method to create RedisTemplate with Jackson2Json serializer
	private <T> RedisTemplate<String, T> createJsonRedisTemplate(RedisConnectionFactory redisConnectionFactory,
			Class<T> type) {
		RedisTemplate<String, T> template = new RedisTemplate<>();
		try {
			template.setConnectionFactory(redisConnectionFactory);

			// Use Jackson2JsonRedisSerializer for value serialization
			Jackson2JsonRedisSerializer<T> jsonSerializer = new Jackson2JsonRedisSerializer<>(type);
			template.setKeySerializer(new StringRedisSerializer());
			template.setValueSerializer(jsonSerializer);
		} catch (Exception e) {
			logger.error("Error creating RedisTemplate with Jackson2JsonRedisSerializer for type: " + type.getName(),
					e);
			throw new RuntimeException("Failed to create RedisTemplate for type: " + type.getName(), e);
		}

		return template;
	}

	// Generic method to create RedisTemplate with Kryo serializer for binary data
	private <T> RedisTemplate<String, T> createKryoRedisTemplate(RedisConnectionFactory redisConnectionFactory,
			Class<T> type) {
		RedisTemplate<String, T> template = new RedisTemplate<>();
		try {
			template.setConnectionFactory(redisConnectionFactory);

			KryoRedisSerializer<T> kryoSerializer = new KryoRedisSerializer<>(type);
			template.setKeySerializer(new StringRedisSerializer());
			template.setValueSerializer(kryoSerializer);
		} catch (Exception e) {
			logger.error("Error creating RedisTemplate with KryoRedisSerializer for type: " + type.getName(), e);
			throw new RuntimeException("Failed to create RedisTemplate for type: " + type.getName(), e);
		}
		return template;
	}

	@Bean
	public RedisTemplate<String, String> redisTemplateList2(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template;
	}


	@Bean
	public RedisTemplate<String, Object> redisTemplateObject(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());



		// ObjectMapper with JavaTimeModule
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		// Jackson serializer using JavaType
		JavaType javaType = objectMapper.getTypeFactory().constructType(Object.class);
		Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, javaType);

		redisTemplate.setValueSerializer(serializer);
		redisTemplate.setHashValueSerializer(serializer);

		// Used for Object data
		return redisTemplate;
	}

	@Bean
	public RedisTemplate<String, MarketData> redisTemplateMarketData(
			RedisConnectionFactory redisConnectionFactory) {
		try {
			return createJsonRedisTemplate(redisConnectionFactory, MarketData.class);
		} catch (Exception e) {
			logger.error("Error creating RedisTemplate for TouchlineBinaryResposne", e);
			throw new RuntimeException("Failed to create RedisTemplate for TouchlineBinaryResposne", e);
		}
	}

	@Bean
	public RedisTemplate<String, MasterResponseFO> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		try {
			return createJsonRedisTemplate(redisConnectionFactory, MasterResponseFO.class);
		} catch (Exception e) {
			logger.error("Error creating RedisTemplate for MasterResponseFO", e);
			throw new RuntimeException("Failed to create RedisTemplate for MasterResponseFO", e);
		}
	}

	@Bean
	public RedisTemplate<String, SyntheticPrice> redisTemplateSynthetic(RedisConnectionFactory redisConnectionFactory) {
		try {
			return createJsonRedisTemplate(redisConnectionFactory, SyntheticPrice.class);
		} catch (Exception e) {
			logger.error("Error creating RedisTemplate for MasterResponseFO", e);
			throw new RuntimeException("Failed to create RedisTemplate for MasterResponseFO", e);
		}
	}

	@Bean
	public RedisTemplate<String, SyntheticForList> redisTemplateSyntheticList(RedisConnectionFactory redisConnectionFactory) {
		try {
			return createJsonRedisTemplate(redisConnectionFactory, SyntheticForList.class);
		} catch (Exception e) {
			logger.error("Error creating RedisTemplate for MasterResponseFO", e);
			throw new RuntimeException("Failed to create RedisTemplate for MasterResponseFO", e);
		}
	}

	@Bean
	public RedisTemplate<String, TouchlineBinaryResposne> redisTemplate1(
			RedisConnectionFactory redisConnectionFactory) {
		try {
			return createJsonRedisTemplate(redisConnectionFactory, TouchlineBinaryResposne.class);
		} catch (Exception e) {
			logger.error("Error creating RedisTemplate for TouchlineBinaryResposne", e);
			throw new RuntimeException("Failed to create RedisTemplate for TouchlineBinaryResposne", e);
		}
	}
	@Bean
	public RedisTemplate<String, String> redisTemplateList(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template;
	}

	@Bean
	public RedisTemplate<String, MarketDepthBinaryResponse> redisTemplate2(
			RedisConnectionFactory redisConnectionFactory) {
		try {
			return createKryoRedisTemplate(redisConnectionFactory, MarketDepthBinaryResponse.class);
		} catch (Exception e) {
			logger.error("Error creating RedisTemplate for MarketDepthBinaryResponse", e);
			throw new RuntimeException("Failed to create RedisTemplate for MarketDepthBinaryResponse", e);
		}
	}

	@Bean
	public RedisTemplate<String, OpenInterestBinaryResponse> redisTemplate3(
			RedisConnectionFactory redisConnectionFactory) {
		try {
			return createKryoRedisTemplate(redisConnectionFactory, OpenInterestBinaryResponse.class);
		} catch (Exception e) {
			logger.error("Error creating RedisTemplate for OpenInterestBinaryResponse", e);
			throw new RuntimeException("Failed to create RedisTemplate for OpenInterestBinaryResponse", e);
		}
	}

	// Custom Kryo Redis serializer class
	public static class KryoRedisSerializer<T> implements RedisSerializer<T> {

		private static final Logger logger = LoggerFactory.getLogger(KryoRedisSerializer.class);
		private final Kryo kryo;
		private final Class<T> type;

		public KryoRedisSerializer(Class<T> type) {
			this.kryo = new Kryo();
			this.type = type;
		}

		@Override
		public byte[] serialize(T t) throws org.springframework.data.redis.serializer.SerializationException {
			try {
				if (t == null) {
					return new byte[0];
				}
				try (Output output = new Output(256, -1)) {
					kryo.writeObject(output, t);
					return output.toBytes();
				}
			} catch (Exception e) {
				logger.error("Error serializing object of type: " + type.getName(), e);
				throw new org.springframework.data.redis.serializer.SerializationException(
						"Failed to serialize object of type: " + type.getName(), e);
			}
		}

		@Override
		public T deserialize(byte[] bytes) throws org.springframework.data.redis.serializer.SerializationException {
			try {
				if (bytes == null || bytes.length == 0) {
					return null;
				}
				try (Input input = new Input(bytes)) {
					return kryo.readObject(input, type);
				}
			} catch (Exception e) {
				logger.error("Error deserializing bytes to object of type: " + type.getName(), e);
				throw new org.springframework.data.redis.serializer.SerializationException(
						"Failed to deserialize bytes to object of type: " + type.getName(), e);
			}
		}
	}
}
