package io.github.defolters.it52;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
//    CircleImageView source_image;

    public EventsListViewHolder(View itemView) {
        super(itemView);

//        source_image = (CircleImageView) itemView.findViewById(R.id.source_image);
        title = (TextView) itemView.findViewById(R.id.title_event);
        date = (TextView) itemView.findViewById(R.id.date_event);
        place = (TextView) itemView.findViewById(R.id.place_event);
        organizer = (TextView) itemView.findViewById(R.id.organizer_event);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
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
        View itemView = inflater.inflate(R.layout.event_layout,parent,false);
        return new EventsListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EventsListViewHolder holder, int position) {

        holder.title.setText(events.get(position).getTitle());

        String dateString = events.get(position).getStarted_at();
        holder.date.setText(dateString.split("T")[0]);

        //holder.date.setText(events.get(position).getStarted_at());
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
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
