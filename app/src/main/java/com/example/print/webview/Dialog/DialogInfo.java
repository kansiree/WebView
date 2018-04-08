package com.example.print.webview.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.TextView;

import com.example.print.webview.R;

/**
 * Created by print on 11/20/2017.
 */

public class DialogInfo extends Dialog {

    String text;
    TextView textView;
    Context context;
    public DialogInfo(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialoginfo);

        textView = (TextView)findViewById(R.id.txt_info);
        textView.setText(getText());
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
            }
        });
    }

    public void setText(String text){
        this.text=text;
    }

    private String getText(){
        return text;
    }

}
