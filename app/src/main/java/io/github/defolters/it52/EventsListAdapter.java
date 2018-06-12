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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mittsu.markedview.MarkedView;
import java.util.Calendar;


import io.github.defolters.it52.Model.Events;
import ru.noties.markwon.Markwon;


class EventsListViewHolder extends RecyclerView.ViewHolder
         {
    ItemClickListener itemClickListener;

    TextView title;
    TextView date;
    TextView place;
    TextView organizer;
    TextView description;
    ImageButton showMap;
    ImageButton createEvent;
    ImageButton shareAction;
    ImageButton arrow;
    ViewStub viewStub;
    String descriptionTempt;
//    MarkedView markedView;
    View holderView;
    boolean isExpanded;


    Context context;

    public EventsListViewHolder(View itemView) {
        super(itemView);

        isExpanded = false;
        title = (TextView) itemView.findViewById(R.id.title_event);
        date = (TextView) itemView.findViewById(R.id.date_event);
        place = (TextView) itemView.findViewById(R.id.place_event);
        organizer = (TextView) itemView.findViewById(R.id.organizer_event);
//        description = itemView.findViewById(R.id.description);
        showMap = itemView.findViewById(R.id.show_map);
        createEvent = itemView.findViewById(R.id.create_event);
        shareAction = itemView.findViewById(R.id.share_action);
        arrow = itemView.findViewById(R.id.arrow);
        viewStub = (ViewStub) itemView.findViewById(R.id.viewStub);
        //viewStub.setLayoutResource(R.layout.event_description);


//        markedView = itemView.findViewById(R.id.descriptionMarkdowns);
//        markedView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                context.startActivity(i);
//
//                return true;
//            }
//        });
//        markedView.setVisibility(View.INVISIBLE);

        //itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

//    @Override
//    public void onClick(View view) {
//        itemClickListener.onClick(view,getAdapterPosition(),false);
//    }

    public void setContext(Context context){
        this.context = context;
    }

    public void setButtons(final String link, final String dateString){
        showMap.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view){
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+place.getText());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                try {
                    context.startActivity(mapIntent);
                }
                catch (ActivityNotFoundException ErrVar) {
                    Toast.makeText(context, "Maps app not found", Toast.LENGTH_LONG).show();
                }
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
                catch (ActivityNotFoundException ErrVar) {
                    Toast.makeText(context, "Calendar app not found", Toast.LENGTH_LONG).show();
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

                try {
                    context.startActivity(Intent.createChooser(shareIntent, "Share"));
                }
                catch (ActivityNotFoundException ErrVar) {
                    Toast.makeText(context, "Share app not found", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void expand() {

        RotateAnimation arrowAnimation = 0 == 0 ?
                new RotateAnimation(0,180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f) :
                new RotateAnimation(180,0,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);

        arrowAnimation.setFillAfter(true);
        arrowAnimation.setDuration(350);

        //markedView.setMDText(descriptionTempt);
//        markedView.setVisibility(View.VISIBLE);
//        if (viewStub == null) {
//            holderView = viewStub.inflate();
//            description = holderView.findViewById(R.id.descriptions);
////            description.setText(descriptionTempt);
//            //Markwon.setMarkdown(description, descriptionTempt);
//        }
//        else {
//            viewStub.setVisibility(View.VISIBLE);
//            description = holderView.findViewById(R.id.descriptions);
//        }
        holderView = viewStub.inflate();
        description = holderView.findViewById(R.id.descriptions);
        Markwon.setMarkdown(description, descriptionTempt);
//        description.setText(descriptionTempt);
        //markedView.animate().translationY(markedView.getHeight());
        arrow.startAnimation(arrowAnimation);

        isExpanded = true;
    }

    public void collapse() {
        RotateAnimation arrowAnimation = 0 == 1 ?
                new RotateAnimation(0,180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f) :
                new RotateAnimation(180,0,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);

        arrowAnimation.setFillAfter(true);
        arrowAnimation.setDuration(350);

//        markedView.setMDText(descriptionTempt);
//        markedView.setVisibility(View.INVISIBLE);
        viewStub.setVisibility(View.GONE);

        //markedView.animate().translationY(0);
        arrow.startAnimation(arrowAnimation);

        isExpanded = false;
    }

    public void animate() {

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
        //holder.description.setText(events.get(position).getDescription());
        holder.descriptionTempt = events.get(position).getDescription();
//        holder.markedView.setMDText(holder.descriptionTempt);

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

        holder.arrow.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isExpanded()){
                    holder.collapse();
                }
                else{
                    holder.expand();
//                    TextView tv = holder.viewStub.getRootView().findViewById(R.id.description);
//                    tv.setText("Kek");
                }
//                View holderView = holder.viewStub.inflate();
//                holder.description = (TextView) holderView.findViewById(R.id.description);
//                holder.description.setText(holder.descriptionTempt);

//                holder.markedView = holderView.findViewById(R.id.descriptionMarkdown);
//                holder.markedView.setMDText(holder.descriptionTempt);




                Toast.makeText(context, "viewStub!", Toast.LENGTH_SHORT).show();
//                notifyDataSetChanged();


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
}
