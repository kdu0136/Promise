package com.example.kimdongun.promise;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kimdongun.promise.Promise.ContentActivity;
import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;

import java.util.Calendar;
import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by KimDongun on 2016-10-05.
 */

public class AlramDialog extends Dialog implements View.OnClickListener, DialogInterface.OnDismissListener{

    private Context myContext;

    private EditText editText_time;
    private Button button_commit;

    public int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_alram);

        editText_time = (EditText)findViewById(R.id.editText_time);
        button_commit = (Button)findViewById(R.id.button_commit);

        editText_time.setText(String.valueOf(time));

        button_commit.setOnClickListener(this);
    }

    public AlramDialog(Context context, int time) {
        super(context);
        this.myContext = context;
        this.time = time;
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_commit: //수정 버튼
                if(editText_time.getText().toString().equals("")){
                    Toast.makeText(myContext, "알람 시간을 설정하세요",Toast.LENGTH_LONG).show();
                }else {
                    time = Integer.valueOf(editText_time.getText().toString());

                    Calendar calendar = Calendar.getInstance();  //현재 시간
                    calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),
                            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

                    String nowTIme = String.valueOf(calendar.get(Calendar.YEAR)) + "/" + String.valueOf(calendar.get(Calendar.MONTH)) + "/" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                    String nowDay = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(calendar.get(Calendar.MINUTE));
                    Log.d("Time", "time: "+ nowTIme + " " + nowDay);

                    Calendar calendar2 = Calendar.getInstance();  //알람 시간
                    calendar2.set(((ContentActivity)myContext).getListViewItem().dYear,
                            ((ContentActivity)myContext).getListViewItem().dMonth,
                            ((ContentActivity)myContext).getListViewItem().dDay,
                            ((ContentActivity)myContext).getListViewItem().dHour,
                            ((ContentActivity)myContext).getListViewItem().dMin);
                    calendar2.add(Calendar.MINUTE, -1*time);

                    String nowTIme3 = String.valueOf(calendar2.get(Calendar.YEAR)) + "/" + String.valueOf(calendar2.get(Calendar.MONTH)) + "/" + String.valueOf(calendar2.get(Calendar.DAY_OF_MONTH));
                    String nowDay3 = String.valueOf(calendar2.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(calendar2.get(Calendar.MINUTE));
                    Log.d("Time", "time3: "+ nowTIme3 + " " + nowDay3);

                    if(calendar2.before(calendar)){ //알람시간이 현재 시간보다 이전
                        Toast.makeText(myContext, "알람 시간이 현재 시간보다 이후가 되게 설정하세요",Toast.LENGTH_LONG).show();
                    }else {
                        HashMap<String, String> postData = new HashMap<>();
                        postData.put("no", String.valueOf(myAccount.getIndex()));
                        postData.put("pro_no", String.valueOf(((ContentActivity) myContext).getListViewItem().index));
                        postData.put("t_alarm", String.valueOf(time));

                        PostDB postDB = new PostDB();
                        postDB.putData("update_alram_time.php", postData, new OnFinishDBListener() {
                            @Override
                            public void onSuccess(String output) {
                                Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_LONG).show();
                                dismiss();
                            }
                        });
                    }
                }
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dismiss();
    }
}
