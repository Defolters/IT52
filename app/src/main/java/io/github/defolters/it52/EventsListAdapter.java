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


class EventsListViewHolder extends RecyclerView.ViewHolder {
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
    private Context context;

    public EventsListViewHolder(View itemView) {
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

    public void setContext(Context context) {
        this.context = context;
    }

    public void setButtons(final String link, final String dateString) {
        showMap.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + place.getText());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                try {
                    context.startActivity(mapIntent);
                } catch (ActivityNotFoundException ErrVar) {
                    Toast.makeText(context, "Maps app not found", Toast.LENGTH_LONG).show();
                }
            }
        });

        createEvent.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                ISO8601Parser parser = ISO8601Parser.getISO8601Parser();
                parser.setDate(dateString);

                Calendar calendar = Calendar.getInstance();
                calendar.set(parser.getYear(), parser.getMonth() - 1,
                                parser.getDay(), parser.getHour(), parser.getMinute());

                Intent calendarIntentVar = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, title.getText())
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, place.getText());

                try {
                    context.startActivity(calendarIntentVar);
                } catch (ActivityNotFoundException ErrVar) {
                    Toast.makeText(context, "Calendar app not found", Toast.LENGTH_LONG).show();
                }
            }
        });

        shareAction.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, link);
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title.getText() + " " + date.getText());

                try {
                    context.startActivity(Intent.createChooser(shareIntent, "Share"));
                } catch (ActivityNotFoundException ErrVar) {
                    Toast.makeText(context, "Share app not found", Toast.LENGTH_LONG).show();
                }
            }
        });

        openInBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link));

                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException ErrVar) {
                    Toast.makeText(context, "Browser app is not found", Toast.LENGTH_LONG).show();
                }
            }
        });

        arrow.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded()) {
                    collapse();
                } else {
                    expand();
                }
            }
        });
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

    public void animate(final boolean isExpanding) {
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

    public void setDescriptionTemp(String descriptionTemp) {
        this.descriptionTemp = descriptionTemp;
    }
}

public class EventsListAdapter extends RecyclerView.Adapter<EventsListViewHolder>{
    private Context context;
    private Events events;

    public EventsListAdapter(Context context, Events events) {
        this.context = context;
        this.events = events;
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
        holder.descriptionTempt = events.get(position).getDescription();

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

        holder.arrow.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isExpanded()){
                    holder.collapse();
                }
                else{
                    holder.expand();
                }
            }
        });

        // здесь установить кнопки??!?!"?!
        holder.setContext(context);
        holder.setButtons(events.get(position).getUrl(), events.get(position).getStarted_at());
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
