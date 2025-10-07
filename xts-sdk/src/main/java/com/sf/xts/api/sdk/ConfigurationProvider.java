package com.sf.xts.api.sdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.hibernate.boot.cfgxml.internal.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It provides all configuration for URL and its end points
 * 
 * @author SymphonyFintech
 */
public abstract class ConfigurationProvider {
	static{    
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
		SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
		System.setProperty("current.date.time", dateFormat.format(new Date()));
		System.setProperty("current.day", dayFormat.format(new Date()));
	}
	public static Logger logger = LoggerFactory.getLogger(ConfigurationProvider.class);

	String propFileName = "config.properties";
	public static String commonURL = null;
	public static String source = null;
	
	public static String prefixMD = null;
	public static String marketDataURL = null;
	public static String loginMD = null;
	public static String loginMDBigul = null;
	public static String loginMDValidate = null;
	public static String logoutMD = null;
	public static String clientConfig = null;
	public static String ohlc = null;
	public static String equitySymbol = null;
	public static String expiryDate = null;
	public static String futureSymbol = null;
	public static String optionSymbol = null;
	public static String optionType = null;
	public static String series = null;
	public static String indexList = null;
	public static String master = null;
	public static String quote = null;
	public static String subscription = null;
	public static String unsubscription = null;
	public static String instrumentByID = null;
	public static String searchInstrument = null;


	public static String prefixINT = null;
	public static String interactiveURL = null;
	public static String loginINT = null;
	public static String logoutINT = null;
	public static String profile = null;
	public static String balance = null;
	public static String marketStatus = null;
	public static String holdings = null;
	public static String positions = null;
	public static String positionConvert = null;
	public static String squareOff = null;
	public static String tradeBook = null;
	public static String orderBook = null;
	public static String cover = null;
	public static String exchangeMessage = null;

	public static String sslType = null;
	public static SSLConnectionSocketFactory sslSocketFactory;
	public static String indexData = null;
	public static String indexDataMasterValues = null;
	public static String exchangeSegmentList = null;
	public static String  seriesList = null;
	public static String  holidays = null;


	

	/**
	 * it fetches configuration values from configuration file available in the path
	 * @return boolean - true / false
	 * @throws IOException 
	 */
	public boolean loadConfiguration() throws IOException {

		InputStream inputStream = null;

		try {

            Properties prop = System.getProperties();
            String profile = System.getProperty("spring.profiles.active");
            String resourcePath;
            if (profile != null && !profile.trim().isEmpty())
				resourcePath = "/config-" + profile + ".properties";
            else
                resourcePath = "/config.properties";

            logger.info("config.properties file path : " + resourcePath);
            inputStream = ConfigLoader.class.getResourceAsStream(resourcePath);
            prop.load(inputStream);
            // get the property value and print it out
            commonURL = prop.getProperty("COMMON_URL");
            System.out.println("path :" + commonURL);
            source = prop.getProperty("SOURCE");
            prefixMD = prop.getProperty("PREFIX_MD");
            marketDataURL = commonURL.concat(prefixMD);
            loginMD = prop.getProperty("LOGIN_MD");
            loginMDValidate = prop.getProperty("LOGIN_MD_VALIDATE");
            loginMDBigul = prop.getProperty("LOGIN_MD_APPKEY");
            logoutMD = prop.getProperty("LOGOUT_MD");
            clientConfig = prop.getProperty("CLIENT_CONFIG_MD");
            ohlc = prop.getProperty("OHLC_MD");
            equitySymbol = prop.getProperty("EQUITY_SYMBOL_MD");
            expiryDate = prop.getProperty("EXPIRY_DATE_MD");
            futureSymbol = prop.getProperty("FUTURE_SYMBOL_MD");
            optionSymbol = prop.getProperty("OPTION_SYMBOL_MD");
            optionType = prop.getProperty("OPTION_TYPE_MD");
            series = prop.getProperty("SERIES_MD");
            indexList = prop.getProperty("INDEX_LIST_MD");
            master = prop.getProperty("MASTER_MD");
            quote = prop.getProperty("QUOTE_MD");
            subscription = prop.getProperty("SUBSCRIPTION_MD");
            unsubscription = prop.getProperty("UNSUBSCRIPTION_MD");
            instrumentByID = prop.getProperty("INSTRUMENT_BY_ID");
            searchInstrument = prop.getProperty("SEARCH_INSTRUMENT");

            prefixINT = prop.getProperty("PREFIX_INT");
            interactiveURL = commonURL.concat(prefixINT);
            loginINT = prop.getProperty("LOGIN_INT");
            logoutINT = prop.getProperty("LOGOUT_INT");
            profile = prop.getProperty("PROFILE");
            balance = prop.getProperty("BALANCE");
            marketStatus = prop.getProperty("MARKET_STATUS");
            holdings = prop.getProperty("HOLDINGS");
            positions = prop.getProperty("POSITIONS");
            positionConvert = prop.getProperty("POSITION_CONVERT");
            squareOff = prop.getProperty("SQUAREOFF");
            tradeBook = prop.getProperty("TRADEBOOK");
            orderBook = prop.getProperty("ORDERBOOK");
            cover = prop.getProperty("COVER");
            exchangeMessage = prop.getProperty("EXCHANGE_MESSAGE");
            indexData = prop.getProperty("INDEX_DATA");
            indexDataMasterValues = prop.getProperty("INDEX_DATA_MASTER_VAULES");
            exchangeSegmentList = prop.getProperty("EXCHANGE_SEGMENT_ID_LIST");
            seriesList = prop.getProperty("SERIES_LIST");
            System.out.println("series :" + seriesList);
            sslType = prop.getProperty("SSL_TYPE");
            holidays = prop.getProperty("HOLIDAYS");
            // Trust own CA and all self-signed certificates
			/*SSLContext sslcontext = SSLContexts.custom()
					.loadTrustMaterial(new File(prop.getProperty("SSL_CERT_PATH")), "changeit".toCharArray(), new TrustSelfSignedStrategy())
					.build();

			sslSocketFactory = new SSLConnectionSocketFactory(
					sslcontext,
					new String[] { sslType },
					null,
					SSLConnectionSocketFactory.getDefaultHostnameVerifier());
*/
            return true;
        } catch (Exception e) {
				copyFileUsingFileChannels();
				System.exit(1);
		} finally {
			inputStream.close();
			
		}
		return false;
	}

	/**
	 * copy file if config.properties is not present
	 * @throws IOException catch IOException
	 */
	private void copyFileUsingFileChannels()
			throws IOException {

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		byte[] buffer = new byte[inputStream.available()];
		inputStream.read(buffer);
		
		File targetFile = new File(propFileName);
		OutputStream outStream = new FileOutputStream(targetFile);
		outStream.write(buffer);
		outStream.close();
		logger.info("Default config.properties created, at " + propFileName + ". Make proper changes and try again.");
	}
}
