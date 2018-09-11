/* AMCO @ 2018 */

package theweatherplanetapi.handler;

import ace.gson.builders.JsonObjectBuilder;
import com.google.gson.JsonObject;
import theweatherplanetapi.utils.Constants;
import whiz.net.servers.HttpRequest;

public class VersionHandler extends AbstractHttpHandler {

    public VersionHandler(final String route) {
	super(VersionHandler.class, route, route);
    }

    @Override protected JsonObject onGet(HttpRequest hr, JsonObject jo, JsonObject jo1) {
	return new JsonObjectBuilder().add("version", Constants.VERSION).getAsJsonObject();
    }

}
