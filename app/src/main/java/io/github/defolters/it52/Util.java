package io.github.defolters.it52;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.esotericsoftware.minlog.Log;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

import io.github.defolters.it52.Model.Event;
import io.github.defolters.it52.Model.Events;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public static synchronized void loadEvents(boolean isRefreshed, final Context context,
                                               final RecyclerView listEvents,
                                               EventsService eventsService,
                                               final SwipeRefreshLayout swipeLayout,
                                               final boolean isComing) {

        if (!isRefreshed) {

            String cache = Paper.book().read("cache");
            if (cache != null && !cache.isEmpty() && !cache.equals("null")) // If have cache
            {
                Events events = new Gson().fromJson(cache, Events.class); // Convert cache from Json to Object
                EventsListAdapter adapter = new EventsListAdapter(context, processEvents(isComing, events)); //TODO: sort cards and pass them to different layouts
                adapter.notifyDataSetChanged();
                listEvents.setAdapter(adapter);
            } else // If not have cache
            {
//            dialog.show();
                //Fetch new data
                eventsService.getEvents().enqueue(new Callback<Events>() {
                    @Override
                    public void onResponse(Call<Events> call, Response<Events> response) {
                        EventsListAdapter adapter = new EventsListAdapter(context, processEvents(isComing,response.body()));
                        adapter.notifyDataSetChanged();
                        listEvents.setAdapter(adapter);

                        //Save to cache
                        Paper.book().write("cache", new Gson().toJson(response.body()));

//                    dialog.dismiss();

                    }

                    @Override
                    public void onFailure(Call<Events> call, Throwable t) {

                    }
                });
            }
        } else // If from Swipe to Refresh
        {

            swipeLayout.setRefreshing(true);
            //Fetch new data
            eventsService.getEvents().enqueue(new Callback<Events>() {
                @Override
                public void onResponse(Call<Events> call, Response<Events> response) {
                    EventsListAdapter adapter = new EventsListAdapter(context, processEvents(isComing,response.body()));
                    adapter.notifyDataSetChanged();
                    listEvents.setAdapter(adapter);

                    //Save to cache
                    Paper.book().write("cache", new Gson().toJson(response.body()));

                    //Dismiss refresh progressring
                    swipeLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<Events> call, Throwable t) {

                }
            });

        }
    }
    private static Events processEvents(boolean isComing, Events events){
        Events events1 = new Events();

        Calendar calendarToday = Calendar.getInstance();
        ISO8601Parser parser = ISO8601Parser.getISO8601Parser();

        for (Event event : events){
            //get current date
            parser.setDate(event.getStarted_at());

            Calendar calendarEvent = Calendar.getInstance();
            calendarEvent.set(parser.getYear(), parser.getMonth()-1, parser.getDay(), parser.getHour(), parser.getMinute());

            // if past event
            if (calendarToday.compareTo(calendarEvent) > 0){
                if (!isComing) {
                    events1.add(event);
                }
            }
            else{
                if (isComing){
                    events1.add(event);
                }
            }
        }
        return events1;
    }

    public static String monthToString(int monthInt){
        String month;

        switch (monthInt){
            case 1:
                month = "января";
                break;
            case 2:
                month = "февраля";
                break;
            case 3:
                month = "марта";
                break;
            case 4:
                month = "апреля";
                break;
            case 5:
                month = "мая";
                break;
            case 6:
                month = "июня";
                break;
            case 7:
                month = "июля";
                break;
            case 8:
                month = "августа";
                break;
            case 9:
                month = "сентября";
                break;
            case 10:
                month = "октября";
                break;
            case 11:
                month = "ноября";
                break;
            case 12:
                month = "декабря";
                break;
            default:
                month = "месяца";
                break;
        }

        return month;
    }
}
