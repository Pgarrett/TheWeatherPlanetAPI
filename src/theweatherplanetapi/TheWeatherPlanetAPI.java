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
import theweatherplanetapi.utils.Constants;
import whiz.net.servers.HttpHost;

public class TheWeatherPlanetAPI {

    private static HttpHost _httpHost;

    private static Logger _logger = Logger.getLogger(TheWeatherPlanetAPI.class);

    private static int _port;
    private static int _threads;
    private static int _yearsToForecast;
    private static File _dataDirectory;
    private static File _dataFile;

    private static boolean initServer() {
	if ((_httpHost = new HttpHost(
	    _port,
	    _threads,
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
	final JsonObject config = Json.readFileAsJsonObject(Constants.CONFIG_FILE);
	if (Ace.assigned(config)) {
	    if (config.has("port") && config.has("threads") && config.has(Constants.DATA_DIRECTORY) && config.has(Constants.DATA_FILE) && config.has(Constants.YEARS)) {
		return config;
	    }
	}
	return null;
    }

    private static void start() {
	_httpHost.registerHandler(new VersionHandler("/api/version"));
	_httpHost.registerHandler(new GetForecastHandler("/api/forecast", "/api/forecast", _dataFile));
	_httpHost.start();
    }

    private static boolean init() {
	if (!new File(Constants.LOG4J_FILE).exists()) {
	    System.err.println(Strings.concat("No se encontró el archivo de configuración: '", Constants.LOG4J_FILE, "'."));
	    System.err.println(Constants.APPLICATION_EXIT);
	    return false;
	}
	PropertyConfigurator.configureAndWatch(Constants.LOG4J_FILE);
	if (Constants.CONFIG_FILE.exists()) {
	    final JsonObject config = readConfig();
	    if (Ace.assigned(config)) {
		_port = Json.obtainInteger(config, Constants.PORT);
		_threads = Json.obtainInteger(config, Constants.THREADS);
		_yearsToForecast = Json.obtainInteger(config, Constants.YEARS);
		_dataDirectory = new File(Json.obtainString(config, Constants.DATA_DIRECTORY));
		if (_dataDirectory.exists()) {
		    _dataFile = new File(_dataDirectory, Json.obtainString(config, Constants.DATA_FILE));
		    return true;
		}
		_logger.error(Strings.concat("El directorio '", Json.obtainString(config, Constants.DATA_DIRECTORY), "' no existe."));
		return false;
	    } else {
		_logger.error("La configuración no es correcta, por favor verificar.");
		return false;
	    }
	} else {
	    _logger.error(Strings.concat("No se encontró el archivo de configuración: ", Constants.CONFIG_FILE.getAbsolutePath(), "."));
	    return false;
	}
    }

    public static void main(final String[] args) throws IOException {
	if (args.length > 0 && (args[0].equals(Constants.V_ARG) || args[0].equals(Constants.VERSION_ARG))) {
	    System.out.println(Constants.INIT_MESSAGE);
	    System.exit(0);
	}
	if (init()) {
	    _logger.info(Constants.INIT_MESSAGE);
	    _logger.info("Application start.");
	    if (initServer()) {
		start();
	    } else {
		_logger.error("No fue posible inicializar el servidor.");
		System.exit(-1);
	    }
	} else {
	    System.exit(-1);
	}
    }

}
