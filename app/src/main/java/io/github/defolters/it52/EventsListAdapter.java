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
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import io.github.defolters.it52.Model.Events;
import ru.noties.markwon.Markwon;


class EventViewHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView date;
    private TextView place;
    private TextView organizer;
    private ImageButton showMap;
    private ImageButton createEvent;
    private ImageButton openInBrowser;
    private ImageButton shareAction;
    private ImageButton arrow;
    private ViewStub viewStub;
    private String descriptionTemp;
    private boolean isExpanded;

    public EventViewHolder(View itemView) {
        super(itemView);

        isExpanded = false;
        title = itemView.findViewById(R.id.title_event);
        date = itemView.findViewById(R.id.date_event);
        place = itemView.findViewById(R.id.place_event);
        organizer = itemView.findViewById(R.id.organizer_event);
        showMap = itemView.findViewById(R.id.show_map);
        createEvent = itemView.findViewById(R.id.create_event);
        openInBrowser = itemView.findViewById(R.id.browser_action);
        shareAction = itemView.findViewById(R.id.share_action);
        arrow = itemView.findViewById(R.id.arrow);
        viewStub = itemView.findViewById(R.id.viewStub);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void expand() {
        animate(true);

        if (viewStub.getParent() != null) {
            View holderView = viewStub.inflate();
            TextView description = holderView.findViewById(R.id.descriptions);
            Markwon.setMarkdown(description, descriptionTemp);
        } else {
            viewStub.setVisibility(View.VISIBLE);
        }

        isExpanded = true;
    }

    public void collapse() {
        animate(false);
        viewStub.setVisibility(View.GONE);

        isExpanded = false;
    }

    private void animate(final boolean isExpanding) {
        RotateAnimation arrowAnimation = isExpanding ?
                new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f) :
                new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);

        arrowAnimation.setFillAfter(true);
        arrowAnimation.setDuration(350);

        arrow.startAnimation(arrowAnimation);
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getDate() {
        return date;
    }

    public TextView getPlace() {
        return place;
    }

    public TextView getOrganizer() {
        return organizer;
    }

    public ImageButton getShowMap() {
        return showMap;
    }

    public ImageButton getCreateEvent() {
        return createEvent;
    }

    public ImageButton getOpenInBrowser() {
        return openInBrowser;
    }

    public ImageButton getShareAction() {
        return shareAction;
    }

    public ImageButton getArrow() {
        return arrow;
    }

    public void setDescriptionTemp(String descriptionTemp) {
        this.descriptionTemp = descriptionTemp;
    }

}

public class EventsListAdapter extends RecyclerView.Adapter<EventViewHolder>{
    private Context context;
    private Events events;

    public EventsListAdapter(Context context, Events events) {
        this.context = context;
        this.events = events;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.event_cardview,parent,false);

        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, final int position) {

        final ISO8601Parser parser = ISO8601Parser.getISO8601Parser();
        parser.setDate(events.get(position).getStarted_at());
        String month = Util.monthToString(parser.getMonth());
        final String text = parser.getDay() + " " + month + " " + parser.getYear() +
                ", " + events.get(position).getStarted_at().split("T")[1].substring(0,5);

        holder.getDate().setText(text);
        holder.getTitle().setText(events.get(position).getTitle());
        holder.getPlace().setText(events.get(position).getPlace());
        holder.setDescriptionTemp(events.get(position).getDescription());

        if (!(events.get(position).getOrganizer() == null)){
            holder.getOrganizer().setText(events.get(position).getOrganizer().getFull_name());
        }

        // Setting onClickListeners
        holder.getShowMap().setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + events.get(position).getPlace());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                try {
                    context.startActivity(mapIntent);
                } catch (ActivityNotFoundException ErrVar) {
                    Toast.makeText(context, "Maps app not found", Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.getCreateEvent().setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(parser.getYear(), parser.getMonth() - 1,
                        parser.getDay(), parser.getHour(), parser.getMinute());

                Intent calendarIntentVar = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, events.get(position).getTitle())
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, events.get(position).getPlace());

                try {
                    context.startActivity(calendarIntentVar);
                } catch (ActivityNotFoundException ErrVar) {
                    Toast.makeText(context, "Calendar app not found", Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.getShareAction().setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, events.get(position).getUrl());
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, events.get(position).getTitle() + " " + text);

                try {
                    context.startActivity(Intent.createChooser(shareIntent, "Share"));
                } catch (ActivityNotFoundException ErrVar) {
                    Toast.makeText(context, "Share app not found", Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.getOpenInBrowser().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(events.get(position).getUrl()));

                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException ErrVar) {
                    Toast.makeText(context, "Browser app is not found", Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.getArrow().setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isExpanded()) {
                    holder.collapse();
                } else {
                    holder.expand();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
