package io.github.defolters.it52;

import io.github.defolters.it52.Model.Events;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface EventsService {
    @GET("api/v1/events.json")
    Call<Events> getEvents();
}

