/* AMCO @ 2018 */

package theweatherplanetapi.handler;

import ace.gson.builders.JsonObjectBuilder;
import com.google.gson.JsonObject;
import java.util.HashMap;
import theweatherplanetapi.TheWeatherPlanetAPI;
import whiz.net.servers.HttpRequest;

public class GetForecastHandler extends AbstractHttpHandler {

    public GetForecastHandler(final String route, final String template) {
	super(GetForecastHandler.class, route, template);
    }

    @Override protected JsonObject onGet(final HttpRequest request, final JsonObject body, final JsonObject parameters) {
	final HashMap<String, String> queryStringParams = request.getRequestQueryStringAsHashMap();
	return new JsonObjectBuilder().add("version", TheWeatherPlanetAPI.VERSION).getAsJsonObject();
    }

}