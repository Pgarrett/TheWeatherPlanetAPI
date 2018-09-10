/* AMCO @ 2018 */

package theweatherplanetapi;

import java.io.IOException;
import theweatherplanetapi.handler.GetForecastHandler;
import theweatherplanetapi.handler.VersionHandler;
import whiz.net.servers.HttpHost;

public class TheWeatherPlanetAPI {

    public static final String VERSION = "1.0.0";

    private static HttpHost _httpHost;

    public static void main(final String[] args) throws IOException {
	final int port = 9090;
	final int threads = 5;
	if ((_httpHost = new HttpHost(
	    port,
	    threads,
	    false,
	    null) {
	    @Override public void onStartListening() {
		System.out.println("api.start");
	    }

	    @Override public void onStopListening() {
		System.out.println("api.stop");
	    }
	}.setName("The Weather Planet API")).hadException()) {
	    System.out.println("application exit");
	    ((Exception) _httpHost.getLastException()).printStackTrace();
	    System.exit(-1);
	}
	_httpHost.registerHandler(new VersionHandler("/api/version"));
	_httpHost.registerHandler(new GetForecastHandler("/api/forecast", "/api/forecast"));
	_httpHost.start();
    }

}
