package io.github.defolters.it52;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import io.github.defolters.it52.Model.Events;

class EventsListViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    ItemClickListener itemClickListener;

    TextView title;
    TextView date;
    TextView place;
    TextView organizer;
    ImageButton showMap;
    ImageButton createEvent;
    ImageButton shareAction;

    Context context;
//    CircleImageView source_image;

    public EventsListViewHolder(View itemView) {
        super(itemView);

//        source_image = (CircleImageView) itemView.findViewById(R.id.source_image);
        title = (TextView) itemView.findViewById(R.id.title_event);
        date = (TextView) itemView.findViewById(R.id.date_event);
        place = (TextView) itemView.findViewById(R.id.place_event);
        organizer = (TextView) itemView.findViewById(R.id.organizer_event);
        showMap = itemView.findViewById(R.id.show_map);
        createEvent = itemView.findViewById(R.id.create_event);
        shareAction = itemView.findViewById(R.id.share_action);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void setButtons(final String link, final String dateString){
        showMap.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view){
//                Toast.makeText(context, "Install Calender App", Toast.LENGTH_LONG).show();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+place.getText());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                context.startActivity(mapIntent);
            }
        });

        createEvent.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view){
                String date = dateString.split("T")[0];
                String time = dateString.split("T")[1];

                Calendar startTymVar = Calendar.getInstance();
                startTymVar.set(Integer.parseInt(date.split("-")[0]),
                        Integer.parseInt(date.split("-")[1])-1,
                        Integer.parseInt(date.split("-")[2]),
                        Integer.parseInt(time.substring(0,2)),
                        Integer.parseInt(time.substring(3,5)));

                Intent calendarIntentVar = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTymVar.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, title.getText())
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, place.getText());

                try
                {
                    context.startActivity(calendarIntentVar);
                }
                catch (ActivityNotFoundException ErrVar)
                {
                    Toast.makeText(context, "Install Calender App", Toast.LENGTH_LONG).show();
                }
            }
        });

        shareAction.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, link);
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title.getText()+" "+date.getText());
                context.startActivity(Intent.createChooser(shareIntent, "Share"));
            }
        });
    }
}

public class EventsListAdapter extends RecyclerView.Adapter<EventsListViewHolder>{
    private Context context;
    private Events events;

//    private IconBetterIdeaService mService;

    public EventsListAdapter(Context context, Events events) {
        this.context = context;
        this.events = events;

//        mService = Common.getIconService();
    }

    @Override
    public EventsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.event_cardview,parent,false);
        return new EventsListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EventsListViewHolder holder, int position) {

        holder.title.setText(events.get(position).getTitle());

        // Parsing date
        String dateString = events.get(position).getStarted_at();
        String date = dateString.split("T")[0];
        String time = dateString.split("T")[1];

        String month = Util.monthToString(ISO8601Parser.getISO8601Parser().setDate(dateString).getMonth());

        String text = date.split("-")[2] + " " + month + " " + date.split("-")[0] +
                ", " + time.substring(0,5);
        holder.date.setText(text);

        holder.place.setText(events.get(position).getPlace());

        if (!(events.get(position).getOrganizer() == null)){
            holder.organizer.setText(events.get(position).getOrganizer().getFull_name());
        }


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent detail = new Intent(context,EventViewer.class);
                detail.putExtra("webURL",events.get(position).getUrl());
                detail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                context.startActivity(detail);
            }
        });

        holder.setContext(context);
        holder.setButtons(events.get(position).getUrl(), events.get(position).getStarted_at());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
