package com.example.kimdongun.promise;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;

import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by KimDongun on 2016-10-05.
 */

public class ContentFriendDialog extends Dialog implements View.OnClickListener, DialogInterface.OnDismissListener{

    private Context myContext;
    private AddFriendListViewItem item;

    private TextView textView_email; //메일
    private TextView textView_name; //이름
    private ImageView imageView_type; //계정 종류

    private Button button_add; //친구 추가 버튼
    private Button button_cancel; //요청 취소 버튼
    private Button button_accept; //친구 수락 버튼
    private Button button_refuse; //친구 거절 버튼
    private Button button_cut; //친구 끊기 버튼

    private View layout_none; //친구 추가 뷰
    private View layout_request; //친구 요청 뷰
    private View layout_accept; //친구 수락 뷰
    private View layout_friend; //친구 뷰
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_contentfriend);

        textView_email = (TextView)findViewById(R.id.textView_email);
        textView_name = (TextView)findViewById(R.id.textView_name);
        imageView_type = (ImageView) findViewById(R.id.imageView_type);

        button_add = (Button)findViewById(R.id.button_add);
        button_cancel = (Button)findViewById(R.id.button_cancel);
        button_accept = (Button)findViewById(R.id.button_accept);
        button_refuse = (Button)findViewById(R.id.button_refuse);
        button_cut = (Button)findViewById(R.id.button_cut);

        layout_none = findViewById(R.id.layout_none);
        layout_request = findViewById(R.id.layout_request);
        layout_accept = findViewById(R.id.layout_accept);
        layout_friend = findViewById(R.id.layout_friend);

        //메일
        textView_email.setText(item.mail);
        //이름
        textView_name.setText(item.name);
        //계정 종류
        if(item.type.equals("google")){
            Drawable drawable_t = myContext.getResources().getDrawable(R.drawable.ic_google);
            imageView_type.setBackgroundDrawable(drawable_t);
        }else if(item.type.equals("facebook")){
            Drawable drawable_t = myContext.getResources().getDrawable(R.drawable.ic_facebook);
            imageView_type.setBackgroundDrawable(drawable_t);
        }
        if(item.state.equals("none")){
            layout_none.setVisibility(View.VISIBLE);
            layout_request.setVisibility(View.INVISIBLE);
            layout_accept.setVisibility(View.INVISIBLE);
            layout_friend.setVisibility(View.INVISIBLE);
        }else if(item.state.equals("request")){
            layout_none.setVisibility(View.INVISIBLE);
            layout_request.setVisibility(View.VISIBLE);
            layout_accept.setVisibility(View.INVISIBLE);
            layout_friend.setVisibility(View.INVISIBLE);
        }else if(item.state.equals("accept")){
            layout_none.setVisibility(View.INVISIBLE);
            layout_request.setVisibility(View.INVISIBLE);
            layout_accept.setVisibility(View.VISIBLE);
            layout_friend.setVisibility(View.INVISIBLE);
        }else if(item.state.equals("friend")){
            layout_none.setVisibility(View.INVISIBLE);
            layout_request.setVisibility(View.INVISIBLE);
            layout_accept.setVisibility(View.INVISIBLE);
            layout_friend.setVisibility(View.VISIBLE);
        }

        button_add.setOnClickListener(this);
        button_cancel.setOnClickListener(this);
        button_accept.setOnClickListener(this);
        button_refuse.setOnClickListener(this);
        button_cut.setOnClickListener(this);
    }

    public ContentFriendDialog(Context context, AddFriendListViewItem item) {
        super(context);
        this.myContext = context;
        this.item = item;
        //setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_cut: //친구 끊기 버튼
                AlertDialog.Builder alert_confirm0 = new AlertDialog.Builder(myContext);
                alert_confirm0.setMessage(item.mail + "와\n친구관계를 끊으시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HashMap<String, String> postData = new HashMap<String, String>();
                                postData.put("no", String.valueOf(myAccount.getIndex()));
                                postData.put("f_no", String.valueOf(item.no));

                                PostDB postDB = new PostDB(myContext, "요청중");
                                postDB.putData("delete_friend.php", postData, new OnFinishDBListener() {
                                    @Override
                                    public void onSuccess(String output) {
                                        if(output.equals("null")){
                                            Toast.makeText(getApplicationContext(),"상대방과 친구 관계가 아닙니다.",Toast.LENGTH_LONG).show();
                                            myAccount.load_friend();
                                            dismiss();
                                        }else {
                                            Toast.makeText(myContext, "친구관계를 끊었습니다", Toast.LENGTH_LONG).show();
                                            item.state = "none";
                                            myAccount.main_adapter.removeAt(item);
                                            myAccount.main_adapter.notifyDataSetChanged();
                                            dismiss();
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert0 = alert_confirm0.create();
                alert0.show();
                break;

            case R.id.button_add: //친구 추가 버튼
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(myContext);
                alert_confirm.setMessage(item.mail + "에게\n친구 요청을 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HashMap<String, String> postData = new HashMap<String, String>();
                                postData.put("to_no", String.valueOf(item.no));
                                postData.put("from_no", String.valueOf(myAccount.getIndex()));
                                postData.put("mail", myAccount.getMail());

                                PostDB postDB = new PostDB(myContext, "요청중");
                                postDB.putData("request_friend.php", postData, new OnFinishDBListener() {
                                    @Override
                                    public void onSuccess(String output) {
                                        if(output.equals("null")){
                                            Toast.makeText(getApplicationContext(),"상대방이 친구를 요청한 상태입니다.",Toast.LENGTH_LONG).show();
                                            myAccount.load_friend();
                                            dismiss();
                                        }else {
                                            Toast.makeText(myContext, "친구 요청을 하였습니다", Toast.LENGTH_LONG).show();
                                            item.state = "request";
                                            myAccount.request_adapter.addItem(item);
                                            myAccount.request_adapter.notifyDataSetChanged();
                                            dismiss();
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
                break;

            case R.id.button_cancel: //요청 취소 버튼
                AlertDialog.Builder alert_confirm2 = new AlertDialog.Builder(myContext);
                alert_confirm2.setMessage(item.mail + "에게 보낸\n친구 요청을 취소하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HashMap<String, String> postData2 = new HashMap<String, String>();
                                postData2.put("to_no", String.valueOf(item.no));
                                postData2.put("from_no", String.valueOf(myAccount.getIndex()));

                                PostDB postDB2 = new PostDB(myContext, "요청 취소 중");
                                postDB2.putData("delete_request_friend.php", postData2, new OnFinishDBListener() {
                                    @Override
                                    public void onSuccess(String output) {
                                        if(output.equals("null")){
                                            Toast.makeText(getApplicationContext(),"상대방이 이미 친구를 수락하였습니다.",Toast.LENGTH_LONG).show();
                                            myAccount.load_friend();
                                            dismiss();
                                        }else {
                                            Toast.makeText(myContext, "친구 요청을 취소 하였습니다", Toast.LENGTH_LONG).show();
                                            item.state = "none";
                                            myAccount.request_adapter.removeAt(item);
                                            myAccount.request_adapter.notifyDataSetChanged();
                                            dismiss();
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert2 = alert_confirm2.create();
                alert2.show();
                break;

            case R.id.button_accept: //친구 수락 버튼
                AlertDialog.Builder alert_confirm3 = new AlertDialog.Builder(myContext);
                alert_confirm3.setMessage(item.mail + "의\n친구 요청을 수락하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HashMap<String, String> postData3 = new HashMap<String, String>();
                                postData3.put("to_no", String.valueOf(myAccount.getIndex()));
                                postData3.put("from_no", String.valueOf(item.no));
                                postData3.put("mail", myAccount.getMail());

                                PostDB postDB3 = new PostDB(myContext, "친구 수락 중");
                                postDB3.putData("accept_friend.php", postData3, new OnFinishDBListener() {
                                    @Override
                                    public void onSuccess(String output) {
                                        if(output.equals("null")){
                                            Toast.makeText(getApplicationContext(),"상대방이 요청을 취소했습니다.",Toast.LENGTH_LONG).show();
                                            myAccount.load_friend();
                                            dismiss();
                                        }else {
                                            Toast.makeText(myContext, "친구 요청을 수락 하였습니다", Toast.LENGTH_LONG).show();
                                            item.state = "friend";
                                            myAccount.accept_adapter.removeAt(item);
                                            myAccount.accept_adapter.notifyDataSetChanged();
                                            myAccount.main_adapter.addItem(item);
                                            myAccount.main_adapter.notifyDataSetChanged();
                                            dismiss();
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert3 = alert_confirm3.create();
                alert3.show();
                break;

            case R.id.button_refuse: //친구 거절 버튼
                AlertDialog.Builder alert_confirm4 = new AlertDialog.Builder(myContext);
                alert_confirm4.setMessage(item.mail + "의\n친구 요청을 거절하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HashMap<String, String> postData4 = new HashMap<String, String>();
                                postData4.put("to_no", String.valueOf(myAccount.getIndex()));
                                postData4.put("from_no", String.valueOf(item.no));
                                postData4.put("mail", myAccount.getMail());

                                PostDB postDB4 = new PostDB(myContext, "요청 거절 중");
                                postDB4.putData("refuse_friend.php", postData4, new OnFinishDBListener() {
                                    @Override
                                    public void onSuccess(String output) {
                                        if(output.equals("null")){
                                            Toast.makeText(getApplicationContext(),"상대방이 요청을 취소했습니다.",Toast.LENGTH_LONG).show();
                                            myAccount.load_friend();
                                            dismiss();
                                        }else {
                                            Toast.makeText(myContext, "친구 요청을 거절 하였습니다", Toast.LENGTH_LONG).show();
                                            item.state = "none";
                                            myAccount.accept_adapter.removeAt(item);
                                            myAccount.accept_adapter.notifyDataSetChanged();
                                            dismiss();
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert4 = alert_confirm4.create();
                alert4.show();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dismiss();
    }
}
