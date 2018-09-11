/* AMCO @ 2018 */

package theweatherplanetapi.handler;

import ace.concurrency.Threads;
import ace.gson.builders.JsonObjectBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import theweatherplanet.Forecast;
import theweatherplanetapi.utils.Constants;
import whiz.net.servers.HttpRequest;

public class GenerateHandler extends AbstractHttpHandler {

    private final int _yearsToForecast;
    private final File _dataDirectory;
    private final File _dataFile;

    public GenerateHandler(final String route, final int yearsToForecast, final File dataDirectory, final File dataFile) {
	super(GenerateHandler.class, route, route);
	_yearsToForecast = yearsToForecast;
	_dataDirectory = dataDirectory;
	_dataFile = dataFile;
    }

    @Override protected JsonObject onGet(final HttpRequest request, final JsonObject body, final JsonObject parameters) {
	if (new File(_dataDirectory, Constants.START_FILE).exists()) {
	    return new JsonObjectBuilder().add("status", -1).add("result", "El sistema se encuentra generando pronósticos, reintente luego.").getAsJsonObject();
	}
	Threads.spawn(new Runnable() {
	    @Override public void run() {
		Forecast f = new Forecast(_yearsToForecast, _dataDirectory, _dataFile);
		f.predict();
	    }
	});
	return new JsonObjectBuilder().add("status", 0).add("result", "Proceso para generar pronósticos iniciado exitosamente.").getAsJsonObject();
    }

}
