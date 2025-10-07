package com.sf.xts.api.sdk.marketdata;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.sf.xts.api.sdk.main.api.MarketdataClient;
import com.sf.xts.api.sdk.marketdata.response.*;

//import okhttp3.OkHttpClient;
import com.sf.xts.api.sdk.marketdata.util.BinaryUtil;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It provides socket handling like connect join, disconnect, error, position, order and trade
 *
 * @author SymphonyFintech
 */
public class SocketHandler implements XTSAPIMarketdataEvents{

	//publishFormat: "JSON",    broadcastMode: "Full"
	Socket socket=null;
	BinaryUtil binaryUtil;
	private boolean networkInitialized=false;
	private List<XTSAPIMarketdataEvents> listeners = new ArrayList<XTSAPIMarketdataEvents>();
	public static Logger logger = LoggerFactory.getLogger(SocketHandler.class);


	public void addListner(XTSAPIMarketdataEvents obj) {
		listeners.clear();
		listeners.add(obj);
	}

	/**
	 * it will connect socket
	 * @param publishFormat provide publish format
	 * @param broadcastMode provide broadcast mode
	 * @throws Exception catch an Exception
	 */
	public void connectSocket(String publishFormat, String broadcastMode) throws Exception{
		//@TODO: Check inputs and throw exceptions
		if (!networkInitialized) {
			String url = MarketdataClient.commonURL;
			String[] transportArray={"websocket"};
			System.err.println("USER_ID = "+MarketdataClient.user);
			String queryString = "token="+MarketdataClient.authToken+"&userID="+MarketdataClient.user+"&publishFormat="+publishFormat+"&broadcastMode="+broadcastMode;
			System.out.println("QueryString :" +queryString);

			IO.Options options = new IO.Options();
			options.transports=transportArray;
			options.query = queryString;
			options.path="/"+MarketdataClient.prefixMD+"/socket.io";
			options.reconnection=true;
			options.forceNew=true;
			options.timeout=5000;
			ObjectMapper mapper = new ObjectMapper();
			System.out.println("Options :" + mapper.writeValueAsString(options));
			System.out.println("URL :" +url);
			socket = IO.socket(url, options);
//
		}

		socket.on("connect", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				logger.info("In Connect >> callback");
			}
		});

		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				System.out.println("hand shake");
				logger.info("In Event Connect >> hand shake");
			}
		});

		socket.on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				logger.info("In Event Connecting - connection success. IP, PORT is okay");
			}
		});

		socket.on("xts-binary-packet", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				BinaryReader br = null;
				try {
					int offset = 0;
					int count = 0;
					int currentsize = 0;
					boolean isNextPacket = true;
					byte [] data = (byte[]) args[0];
					int datalen =data.length;
					int packetCount = 0;

					while (isNextPacket) {
						byte[] nextData = new byte[datalen - offset];
						System.arraycopy(data, offset, nextData, 0, datalen - offset);
						br = new BinaryReader(nextData);
						ByteBuffer buffer = ByteBuffer.wrap(nextData);
						buffer.order(ByteOrder.LITTLE_ENDIAN);
						int isGzipCompressed = buffer.get();
						offset += 1;
						if (isGzipCompressed == 1) {
							nextData = new byte[datalen - offset];
							System.arraycopy(data, offset, nextData, 0, datalen - offset);
							buffer = ByteBuffer.wrap(nextData);
							buffer.order(ByteOrder.LITTLE_ENDIAN);
							int messageCode =Short.toUnsignedInt(buffer.getShort());
							int exchangeSegment = buffer.getShort();
							int exchangeInstrumentID = buffer.getInt();
							int bookType = buffer.getShort();
							int marketType = buffer.getShort();
							int uncompressedPacketSize = buffer.getShort();
							int compressedPacketSize = buffer.getShort();
							offset += 16;
							byte[] filteredByteArray = new byte[compressedPacketSize];
							System.arraycopy(data, offset, filteredByteArray, 0, compressedPacketSize);
							byte[] inflatedData = ZipInflate.pakoInflateRaw(filteredByteArray);
							currentsize = compressedPacketSize + offset;
							if (currentsize < datalen) {
								isNextPacket = true;
								packetCount = 1;
								offset = currentsize;
							} else {
								isNextPacket = false;
							}
							buffer = ByteBuffer.wrap(inflatedData);
							buffer.order(ByteOrder.LITTLE_ENDIAN);
							messageCode = buffer.getShort();
							//System.out.println("messageCode");
							//	System.out.println(messageCode);
							if ("1501".equals(String.valueOf(messageCode))) {
								//	System.out.println("triggred");
								TouchlineBinaryResposne touchline = BinaryUtil.deserializeTouchline(buffer,count);
								//System.out.println(touchline.toString());
								onMarketDataResponseTouchLine(touchline);

							} else if ("1502".equals(String.valueOf(messageCode))) {
								MarketDepthBinaryResponse marketDepth = BinaryUtil.deserializeMarketDepthEvent(buffer,count);
								onMarketDataResponseMarketDepth(marketDepth);
							} else if ("1510".equals(String.valueOf(messageCode))) {
								OpenInterestBinaryResponse openInterest = BinaryUtil.deserializeOpenInterest(buffer,count);
								onMarketDataResponseOpenInterest(openInterest);
							} /*else if ("1512".equals(String.valueOf(messageCode))) {
								BinaryUtil.deserializeLTPEvent(newInflatedData);
							}*/

						} else if (isGzipCompressed == 0) {
							int messageCode = Short.toUnsignedInt(buffer.getShort());
							int exchangeSegment =buffer.getShort();
							int exchangeInstrumentID = buffer.getInt();
							int bookType = buffer.getShort();
							int marketType = buffer.getShort();
							int uncompressedPacketSize = Short.toUnsignedInt(buffer.getShort());
							int compressedPacketSize = Short.toUnsignedInt(buffer.getShort());
							offset += 14;
							count = offset;

							if ("1501".equals(String.valueOf(messageCode))) {
								TouchlineBinaryResposne touchline =	BinaryUtil.deserializeTouchline(buffer, count);
								onMarketDataResponseTouchLine(touchline);
							} else if ("1502".equals(String.valueOf(messageCode))) {
								MarketDepthBinaryResponse marketDepth = BinaryUtil.deserializeMarketDepthEvent(buffer,count);
								onMarketDataResponseMarketDepth(marketDepth);
							} else if ("1510".equals(String.valueOf(messageCode))) {
								OpenInterestBinaryResponse openInterest = BinaryUtil.deserializeOpenInterest(buffer,count);
								onMarketDataResponseOpenInterest(openInterest);
							}

							currentsize = offset + uncompressedPacketSize;
							if (currentsize < datalen) {
								isNextPacket = true;
								packetCount = 1;
								offset = currentsize;
							} else {
								isNextPacket = false;
							}
						}
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
				finally {
					if (br != null) {
						try {
							br.close();  // Close the BinaryReader if it's AutoCloseable
						} catch (Exception e) {
							System.err.println("Failed to close BinaryReader: " + e.getMessage());
						}
					}
				}


                }






		});


		socket.on("1501-json-full", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				if(!args[0].toString().contains("\\")) {
					//	System.out.println("F_TouchLine"+args[0]);
					Gson gson = new Gson();
					MarketDataResponseTouchLine response = gson.fromJson((String)args[0], MarketDataResponseTouchLine.class);
					//onMarketDataResponseTouchLine(response);
				}else {
					System.out.println("F_Ma"
							+ "rketData Ignored"+args[0]);
				}
			}
		});

		socket.on("1105-json-partial", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				System.out.println("instrumentPropertyChangeEvent: "+args[0]);
				onInstrumentPropertyChangeEvent(args[0]);
			}
		});
		socket.on("1502-json-partial", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				System.out.println("P_MarketData"+args[0]);
			}
		});

		socket.on("1502-json-full", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				if(!args[0].toString().contains("\\")) {
					System.out.println("F_MarketData"+args[0]);
					Gson gson = new Gson();
					MarketDataResponseDepth response = gson.fromJson((String)args[0], MarketDataResponseDepth.class);
					onMarketDataResponseDepth(response);
				}else {
					System.out.println("F_Ma"
							+ "rketData Ignored"+args[0]);
				}
			}
		});

		socket.on("1504-json-full", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				if(!args[0].toString().contains("\\")) {
					System.out.println("F_INX "+args[0]);

					Gson gson = new Gson();
					MarketDataResponseIndex response = gson.fromJson((String)args[0], MarketDataResponseIndex.class);
					onMarketDataResponseIndex(response);
				}
			}
		});

		socket.on("1504-json-partial", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				// System.out.println(args.length);
				Gson gson = new Gson();
				//Response1 response = gson.fromJson((String)args[0], Response1.class);
				System.out.println("P_INX "+args[0]);
			}
		});

		socket.on("1505-json-full", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				if(!args[0].toString().contains("\\")) {
					System.out.println("F_Candle "+args[0]);
					Gson gson = new Gson();
					MarketDataResponseCandle response = gson.fromJson((String)args[0], MarketDataResponseCandle.class);
					onMarketDataResponseCandle(response);
				}
			}
		});

		socket.on("1505-json-partial", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				// System.out.println(args.length);
				Gson gson = new Gson();
				//Response1 response = gson.fromJson((String)args[0], Response1.class);
				System.out.println("P_Candle "+args[0]);
			}
		});

		socket.on("1510-json-full", new Emitter.Listener() {
			@Override
			public void call(Object... args) {

				if(!args[0].toString().contains("\\")) {
					System.out.println("F_openInterestEvent "+args[0]);
					Gson gson = new Gson();
					MarketDataResponseOI response = gson.fromJson((String)args[0], MarketDataResponseOI.class);
					onMarketDataResponseOI(response);
				}
			}
		});

		socket.on("1510-json-partial", new Emitter.Listener() {
			@Override
			public void call(Object... args) {

				System.out.println("P_openInterestEvent "+args[0]);
			}
		});

		socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				logger.info(" Secket Event  DISCONNECT: closed!!!!!!!!!! "+args[0]);
				disconnectSocket();
				onDisconnect();
			}

		});

		socket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
			@Override
			public void call(Object... args) {

				logger.info(" Secket Event : error!!!!!!!!!! "+args[0]);

			}
		});

		socket.on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				System.out.println("Message!!!!!!!!!!"+args[0]);
			}
		});

		socket.connect();
	}


	/* (non-Javadoc)
	 * @see com.symphonyfintech.xts.ally.Marketdata.XTSAPIMarketdataEvents#onMarketDataResponseCandle(com.symphonyfintech.xts.ally.Marketdata.response.MarketDataResponseCandle)
	 */
	@Override
	public void onMarketDataResponseCandle(MarketDataResponseCandle obj) {
		for (XTSAPIMarketdataEvents hl : listeners)
			hl.onMarketDataResponseCandle(obj);
	}

	/* (non-Javadoc)
	 * @see com.symphonyfintech.xts.ally.Marketdata.XTSAPIMarketdataEvents#onMarketDataResponseDepth(com.symphonyfintech.xts.ally.Marketdata.response.MarketDataResponseDepth)
	 */
	@Override
	public void onMarketDataResponseDepth(MarketDataResponseDepth obj) {
		for (XTSAPIMarketdataEvents hl : listeners)
			hl.onMarketDataResponseDepth(obj);
	}

	/* (non-Javadoc)
	 * @see com.symphonyfintech.xts.ally.Marketdata.XTSAPIMarketdataEvents#onMarketDataResponseIndex(com.symphonyfintech.xts.ally.Marketdata.response.MarketDataResponseIndex)
	 */
	@Override
	public void onMarketDataResponseIndex(MarketDataResponseIndex obj) {
		for (XTSAPIMarketdataEvents hl : listeners)
			hl.onMarketDataResponseIndex(obj);
	}

	/* (non-Javadoc)
	 * @see com.symphonyfintech.xts.ally.Marketdata.XTSAPIMarketdataEvents#onMarketDataResponseOI(com.symphonyfintech.xts.ally.Marketdata.response.MarketDataResponseOI)
	 */
	@Override
	public void onMarketDataResponseOI(MarketDataResponseOI obj) {
		for (XTSAPIMarketdataEvents hl : listeners)
			hl.onMarketDataResponseOI(obj);
	}

	@Override
	public void onMarketDataResponseTouchLine(TouchlineBinaryResposne touchlineBinaryResposne) {
		// TODO Auto-generated method stub
		for (XTSAPIMarketdataEvents hl : listeners)
			hl.onMarketDataResponseTouchLine(touchlineBinaryResposne);
	}

	@Override
	public void onMarketDataResponseMarketDepth(MarketDepthBinaryResponse marketDepthBinaryResposne) {
		for (XTSAPIMarketdataEvents hl : listeners)
			hl.onMarketDataResponseMarketDepth(marketDepthBinaryResposne);
	}



	@Override
	public void onMarketDataResponseOpenInterest(OpenInterestBinaryResponse openInterestBinaryResponse) {

	}

	@Override
	public void onInstrumentPropertyChangeEvent(Object args) {
		// TODO Auto-generated method stub
		for (XTSAPIMarketdataEvents hl : listeners)
			hl.onInstrumentPropertyChangeEvent(args);
	}

	@Override
	public void onDisconnect() {
		logger.info("Socket disconnected, notifying listeners.");
		for (XTSAPIMarketdataEvents hl : listeners)
			hl.onDisconnect();
	}

	public void disconnectSocket() {
		if (socket != null) {
			try {
				socket.disconnect();
				socket.close();
				socket = null;
				logger.info("Socket disconnected and closed");
			} catch (Exception e) {
				logger.error("Error while disconnecting socket", e);
			}
		}
		networkInitialized = false;
	}


}
