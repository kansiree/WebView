package com.example.print.webview.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.print.webview.R;

/**
 * Created by print on 11/16/2017.
 */

public class DialogConfirm extends Dialog {
    Dialog dialog;
    TextView headtext,txt_title,txt_title1;
    EditText edit,edit1;
    Button confirm,cancel;
    String headText;
    String title;
    String title1;
    Context context;
    EventListener event;
    DialogInfo dialogInfo ;
    public DialogConfirm(@NonNull Context context,EventListener eventListener) {
        super(context);
        this.context=context;
        this.event=eventListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialogconfirm);
        setView();
        dialogInfo = new DialogInfo(context);
        headtext.setText(getHeadText());
        txt_title.setText(getTitle());
        txt_title1.setText(getTitle1());
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event.onCancle();
                dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkText(edit)&&checkText(edit1)){
                    if(checkUserID(edit)||checkPassword(edit1)){
                        event.onConfirm();
                        dismiss();
                    }
                    else {
                        dialogInfo.setText("password หรือ userID ไม่ถูกต้อง");
                        dialogInfo.show();
                        System.out.println("dialogInfo: "+dialogInfo.isShowing());
                    }
                }
            }
        });
    }

    @Override
    public void onDetachedFromWindow() {
        System.out.println("onDetachedFromWindow");
        super.onDetachedFromWindow();
    }

    @Override
    public void onAttachedToWindow() {
        System.out.println("onAttachedToWindow");
        super.onAttachedToWindow();
    }

    @Override
    public boolean isShowing() {
        System.out.println("isShowing");

        return super.isShowing();
    }

    private void setView(){
        headtext = (TextView)findViewById(R.id.dialog_head_text);
        txt_title = (TextView)findViewById(R.id.dialog_title);
        txt_title1 = (TextView)findViewById(R.id.dialog_title1);
        edit = (EditText)findViewById(R.id.dialog_edit);
        edit1 = (EditText)findViewById(R.id.dialog_edit1);
        cancel  = (Button)findViewById(R.id.bt_NO_dialog_confirm);
        confirm = (Button)findViewById(R.id.bt_yes_dialog_confirm);
    }
    private Boolean checkText(EditText edit){
        if(!(edit.getText().toString().equals(""))&&(edit.getText()!=null)){
            return true;
        }
        else {
            DialogInfo dialogInfo = new DialogInfo(context);
            dialogInfo.setText("กรุณากรอกข้อมูลให้ครบถ้วน");
            dialogInfo.show();
        }
        return false;
    }

    private Boolean checkUserID(EditText userID){
        String txt_userID = userID.getText().toString();
        if(txt_userID.equalsIgnoreCase("Admin")){
            return true;
        }else {
            return false;
        }
    }

    private Boolean checkPassword(EditText passWord){
            String txt_passWord = passWord.getText().toString();
            if(txt_passWord.equalsIgnoreCase("Gosoft")){
                return true;
            }
        else {
            return false;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getHeadText() {
        return headText;
    }

    public void setHeadText(String headText) {
        this.headText = headText;
    }
}
