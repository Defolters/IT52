package io.github.defolters.it52;

public class Util {
    private static final String URL="https://www.it52.info/";

    public static EventsService getEventsService() {
        return RetrofitClient.getClient(URL).create(EventsService.class);
    }

    public static String getAPIUrl()
    {
        /*StringBuilder apiUrl = new StringBuilder("https://newsapi.org/v2/top-headlines?sources=");
        return apiUrl.append(source)
                .append("&apiKey=")
                .append(apiKEY)
                .toString();*/
        return new String("https://www.it52.info/api/v1/events.json");
    }
}
