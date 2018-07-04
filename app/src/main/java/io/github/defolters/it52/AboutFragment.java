package io.github.defolters.it52;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment implements View.OnClickListener {

    private View view;
//    private LinearLayout googlePlay;
    private LinearLayout it52Site;
    private LinearLayout it52Telegram;
    private LinearLayout developerTelegram;
    private LinearLayout developerEmail;
    private LinearLayout sourcecode;
    private LinearLayout openSource;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_about, container, false);

//        googlePlay = view.findViewById(R.id.google_play);
        it52Site = view.findViewById(R.id.it52_site);
        it52Telegram = view.findViewById(R.id.it52_telegram);
        developerTelegram = view.findViewById(R.id.developer_telegram);
        developerEmail = view.findViewById(R.id.developer_email);
        sourcecode = view.findViewById(R.id.sourcecode);
        openSource = view.findViewById(R.id.open_source);

//        googlePlay.setOnClickListener(this);
        it52Site.setOnClickListener(this);
        it52Telegram.setOnClickListener(this);
        developerTelegram.setOnClickListener(this);
        developerEmail.setOnClickListener(this);
        sourcecode.setOnClickListener(this);
        openSource.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();

        switch (view.getId()) {
            case R.id.it52_site:
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Util.URL));

                try {
                    getActivity().startActivity(intent);
                }
                    catch (ActivityNotFoundException ErrVar) {
                        Toast.makeText(getActivity(), "Browser app is not found", Toast.LENGTH_LONG).show();
                    }

                break;

            case R.id.it52_telegram:
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Util.IT52_TELEGRAM));

                try {
                    getActivity().startActivity(intent);
                }
                    catch (ActivityNotFoundException ErrVar) {
                        Toast.makeText(getActivity(), "Browser app is not found", Toast.LENGTH_LONG).show();
                    }

                break;

            case R.id.developer_telegram:
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Util.DEVELOPER_TELEGRAM));

                try {
                    getActivity().startActivity(intent);
                }
                    catch (ActivityNotFoundException ErrVar) {
                        Toast.makeText(getActivity(), "Browser app is not found", Toast.LENGTH_LONG).show();
                    }

                break;

            case R.id.developer_email:
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:"+Util.DEVELOPER_EMAIL));
//                intent.setData(Uri.parse(Util.GOOGLE_PLAY));

                try {
                    getActivity().startActivity(intent);
                }
                catch (ActivityNotFoundException ErrVar) {
                    Toast.makeText(getActivity(), "Email app is not found", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.sourcecode:
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Util.GIT_HUB));

                try {
                    getActivity().startActivity(intent);
                }
                catch (ActivityNotFoundException ErrVar) {
                    Toast.makeText(getActivity(), "Browser app is not found", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.open_source:
                final Dialog dialog = new Dialog(getActivity(), R.style.DialogFullscreenWithTitle);
                dialog.setTitle(R.string.about_licenses);
                dialog.setContentView(R.layout.open_source_licenses);

                final WebView webView = dialog.findViewById(R.id.webview_open_source_licenses);
                webView.loadUrl("file:///android_asset/open_source_license.html");

                Button button = dialog.findViewById(R.id.open_source_licenses_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
        }

    }
}
