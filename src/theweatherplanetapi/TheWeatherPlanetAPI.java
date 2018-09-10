/* AMCO @ 2018 */

package theweatherplanetapi;

import ace.Ace;
import ace.gson.Json;
import ace.text.Strings;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import theweatherplanetapi.handler.GetForecastHandler;
import theweatherplanetapi.handler.VersionHandler;
import whiz.net.servers.HttpHost;

public class TheWeatherPlanetAPI {

    public static final String VERSION = "1.0.0";

    private static final File CONFIG_FILE = new File("config/theweatherplanetapi.cfg");
    private static final String LOG4J_FILE = "config/log4j.properties";

    private static HttpHost _httpHost;

    private static Logger _logger = Logger.getLogger(TheWeatherPlanetAPI.class);

    private static void printVersionNumber() {
	_logger.info("\n########################################\n###  The Weather Planet Application  ###\n###           Version  " + VERSION + "         ###\n########################################");
    }

    private static boolean initServer(final int port, final int threads) {
	if ((_httpHost = new HttpHost(
	    port,
	    threads,
	    false,
	    null) {
	    @Override public void onStartListening() {
		_logger.info("Inicializando servidor.");
	    }

	    @Override public void onStopListening() {
		_logger.info("Finalizando servidor.");
	    }
	}.setName("The Weather Planet API")).hadException()) {
	    _logger.error("Error al inicializar el servidor.", (Exception) _httpHost.getLastException());
	    return false;
	}
	return true;
    }

    private static JsonObject readConfig() {
	final JsonObject config = Json.readFileAsJsonObject(CONFIG_FILE);
	if (Ace.assigned(config)) {
	    if (config.has("port") && config.has("threads")) {
		return config;
	    }
	}
	return null;
    }

    private static void start() {
	_httpHost.registerHandler(new VersionHandler("/api/version"));
	_httpHost.registerHandler(new GetForecastHandler("/api/forecast", "/api/forecast"));
	_httpHost.start();
    }

    public static void main(final String[] args) throws IOException {
	if (new File(LOG4J_FILE).exists()) {
	    PropertyConfigurator.configureAndWatch(LOG4J_FILE);
	    printVersionNumber();
	    if (CONFIG_FILE.exists()) {
		final JsonObject config = readConfig();
		if (Ace.assigned(config)) {
		    if (initServer(Json.obtainInteger(config, "port"), Json.obtainInteger(config, "threads"))) {
			start();
		    } else {
			_logger.error("No fue posible inicializar el servidor.");
			System.exit(-1);
		    }
		} else {
		    _logger.error("La configuración no es correcta, por favor verificar.");
		    System.exit(-1);
		}
	    } else {
		_logger.error(Strings.concat("No se encontró el archivo de configuración: ", CONFIG_FILE.getAbsolutePath(), "."));
		System.exit(-1);
	    }
	 } else {
	    System.err.println(Strings.concat("No se encontró el archivo de configuración: ", LOG4J_FILE, "."));
	    System.exit(-1);
	}
	
    }

}
