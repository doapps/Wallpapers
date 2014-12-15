package com.example.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.util.UtilFonts;
import com.jakkash.hdwallpaper.R;

/**
 * Created by jonathan on 15/12/2014.
 */
public class Dialog_Rate extends AlertDialog {
    private Interface_Rate interface_rate;

    public Dialog_Rate(Context context) {
        super(context);
        initDialog();
    }

    public Dialog_Rate(Context context, int theme) {
        super(context, theme);
        initDialog();
    }

    public Dialog_Rate(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initDialog();
    }

    private void initDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.dialog_rate, null);
        setView(view);

        setCancelable(false);

        TextView txt_dialog_title = (TextView) view.findViewById(R.id.txt_dialog_title);
        TextView txt_dialog_content = (TextView)view.findViewById(R.id.txt_dialog_content);
        TextView txt_dialog_ok = (TextView)view.findViewById(R.id.txt_dialog_ok);
        TextView txt_dialog_cancel = (TextView)view.findViewById(R.id.txt_dialog_cancel);
        txt_dialog_title.setTypeface(UtilFonts.setFight(getContext()));
        txt_dialog_content.setTypeface(UtilFonts.setFight(getContext()));
        txt_dialog_ok.setTypeface(UtilFonts.setFight(getContext()));
        txt_dialog_cancel.setTypeface(UtilFonts.setFight(getContext()));

        txt_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interface_rate.getRate(true, 1);
            }
        });
        txt_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interface_rate.getRate(true, 2);
            }
        });

    }

    /**Inner Interface */
    public interface Interface_Rate{
        void getRate(boolean status, int option);
    }
    public void setInterface_rate(Interface_Rate interface_rate){
        this.interface_rate = interface_rate;
    }
}
