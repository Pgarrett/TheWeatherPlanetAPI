/* AMCO @ 2018 */

package theweatherplanetapi.handler;

import ace.gson.Json;
import ace.gson.builders.JsonObjectBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.HashMap;
import theweatherplanetapi.utils.Constants;
import whiz.net.servers.HttpRequest;

public class GetForecastHandler extends AbstractHttpHandler {

    private final File _dataFile;

    public GetForecastHandler(final String route, final String template, final File dataFile) {
	super(GetForecastHandler.class, route, template);
	_dataFile = dataFile;
    }

    @Override protected JsonObject onGet(final HttpRequest request, final JsonObject body, final JsonObject parameters) {
	final HashMap<String, String> queryStringParams = request.getRequestQueryStringAsHashMap();
	if (queryStringParams.containsKey(Constants.DIA)) {
	    final int day = Integer.valueOf(queryStringParams.get(Constants.DIA));
	    if (day >= 0) {
		if (_dataFile.exists()) {
		    final JsonObject o = Json.readFileAsJsonObject(_dataFile);
		    if (assigned(o) && o.has(Constants.PRONOSTICO)) {
			final JsonArray forecast = Json.obtainJsonArray(o, Constants.PRONOSTICO);
			if (forecast.size() >= day) {
			    return new JsonObjectBuilder().add("status", 0).add("result", Json.obtainString(forecast.get(day).getAsJsonObject(), Constants.CONDICION)).getAsJsonObject();
			}
			return new JsonObjectBuilder().add("status", -5).add("result", "El día solicitado no se encuentra en el pronóstico.").getAsJsonObject();
		    }
		    return new JsonObjectBuilder().add("status", -4).add("result", "Error al leer el archivo de datos.").getAsJsonObject();
		}
		return new JsonObjectBuilder().add("status", -3).add("result", "No hay pronósticos generados al momento.").getAsJsonObject();
	    }
	    return new JsonObjectBuilder().add("status", -2).add("result", "El día buscado debe ser mayor o igual a 0").getAsJsonObject();
	}
	return new JsonObjectBuilder().add("status", -1).add("result", "No se encontró el parámetro esperado: 'dia'.").getAsJsonObject();
    }

}
