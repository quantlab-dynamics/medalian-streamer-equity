//package com.market.feed.config;
//import com.esotericsoftware.kryo.Kryo;
//import com.sf.xts.api.sdk.marketdata.master.MasterResponseFO;
//import com.sf.xts.api.sdk.marketdata.response.MarketDepthBinaryResponse;
//import com.sf.xts.api.sdk.marketdata.response.OpenInterestBinaryResponse;
//import com.sf.xts.api.sdk.marketdata.response.TouchlineBinaryResposne;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import com.esotericsoftware.kryo.io.Input;
//import com.esotericsoftware.kryo.io.Output;
//
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import java.nio.ByteBuffer;
//
//@Configuration
//public class RedisConfigKyro {
//
//
//	    @Bean
//	    public RedisTemplate<String, MasterResponseFO> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//	        RedisTemplate<String, MasterResponseFO> template = new RedisTemplate<>();
//	        template.setConnectionFactory(redisConnectionFactory);
//	        // Optional: Set custom serializers if necessary (e.g., Jackson2JsonRedisSerializer)
//	        RedisConfigKyro.KryoRedisSerializer<MasterResponseFO> kryoSerializer = new RedisConfigKyro.KryoRedisSerializer<>(MasterResponseFO.class);
//	        kryoSerializer.kryo.register(com.sf.xts.api.sdk.marketdata.master.MasterResponseFO.class);
//	        // Set Kryo serializer for both key and value
//	        template.setKeySerializer(new StringRedisSerializer());
//	        template.setValueSerializer(kryoSerializer);
//
//	        return template;
//	    }
//
//	    @Bean
//	    public RedisTemplate<String, TouchlineBinaryResposne> redisTemplate1(RedisConnectionFactory redisConnectionFactory) {
//	        RedisTemplate<String, TouchlineBinaryResposne> template2 = new RedisTemplate<>();
//	        template2.setConnectionFactory(redisConnectionFactory);
//	        // Optional: Set custom serializers if necessary (e.g., Jackson2JsonRedisSerializer)
//	        RedisConfigKyro.KryoRedisSerializer<TouchlineBinaryResposne> kryoSerializer = new RedisConfigKyro.KryoRedisSerializer<>(TouchlineBinaryResposne.class);
//	        kryoSerializer.kryo.register(com.sf.xts.api.sdk.marketdata.response.TouchlineBinaryResposne.class);
//	        kryoSerializer.kryo.register(com.sf.xts.api.sdk.marketdata.response.MarketDeptRowInfo.class);
//	        // kryoSerializer.kryo.register(java.nio.ByteBuffer.class);
//	        // Set Kryo serializer for both key and value
//	        template2.setKeySerializer(new StringRedisSerializer());
//	        template2.setValueSerializer(kryoSerializer);
//
//	        return template2;
//	    }
//
//	    @Bean
//	    public RedisTemplate<String, MarketDepthBinaryResponse> redisTemplate2(RedisConnectionFactory redisConnectionFactory) {
//	        RedisTemplate<String, MarketDepthBinaryResponse> template3 = new RedisTemplate<>();
//	        template3.setConnectionFactory(redisConnectionFactory);
//	        RedisConfigKyro.KryoRedisSerializer<MarketDepthBinaryResponse> kryoSerializer = new RedisConfigKyro.KryoRedisSerializer<>(MarketDepthBinaryResponse.class);
//	        kryoSerializer.kryo.register(com.sf.xts.api.sdk.marketdata.response.MarketDepthBinaryResponse.class);
//	        kryoSerializer.kryo.register(com.sf.xts.api.sdk.marketdata.response.MarketDeptRowInfo.class);
//
//	        template3.setKeySerializer(new StringRedisSerializer());
//	        template3.setValueSerializer(kryoSerializer);
//
//	        return template3;
//	    }
//
//	    @Bean
//	    public RedisTemplate<String, OpenInterestBinaryResponse> redisTemplate3(RedisConnectionFactory redisConnectionFactory) {
//	        RedisTemplate<String, OpenInterestBinaryResponse> template4 = new RedisTemplate<>();
//	        template4.setConnectionFactory(redisConnectionFactory);
//	        RedisConfigKyro.KryoRedisSerializer<OpenInterestBinaryResponse> kryoSerializer = new RedisConfigKyro.KryoRedisSerializer<>(OpenInterestBinaryResponse.class);
//	        kryoSerializer.kryo.register(com.sf.xts.api.sdk.marketdata.response.OpenInterestBinaryResponse.class);
//
//	        template4.setKeySerializer(new StringRedisSerializer());
//	        template4.setValueSerializer(kryoSerializer);
//
//	        return template4;
//	    }
//
//	    public static class KryoRedisSerializer<T> implements RedisSerializer<T> {
//	        private final Kryo kryo = new Kryo();
//	        private final Class<T> clazz;
//
//	        public KryoRedisSerializer(Class<T> clazz) {
//	            this.clazz = clazz;
//	        }
//
//	        @Override
//	        public byte[] serialize(T t) {
//	            try (Output output = new Output(4096, -1)) {
//	                kryo.writeClassAndObject(output, t);
//	                return output.toBytes();
//	            }
//	        }
//
//	        @Override
//	        public T deserialize(byte[] bytes) {
//	            try (Input input = new Input(bytes)) {
//	                return (T) kryo.readClassAndObject(input);
//	            }
//	        }
//	    }
//	}



