package com.example.kimdongun.promise;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kimdongun.promise.Main.MainActivity;
import com.example.kimdongun.promise.serverDB.OnFinishDBListener;
import com.example.kimdongun.promise.serverDB.PostDB;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.kimdongun.promise.Account.myAccount;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by KimDongun on 2016-10-03.
 */

public class AddFriendListViewAdapter extends BaseAdapter implements View.OnClickListener{

    public ArrayList<AddFriendListViewItem> myListItem; //아이템 리스트
    private Context myContext = null;
    private LayoutInflater myInflater;


    public AddFriendListViewAdapter(Context myContext){
        super();
        this.myContext = myContext;
        this.myListItem = new ArrayList<>();
    }
    @Override
    public int getCount() {
        return myListItem.size();
    }

    @Override
    public Object getItem(int index) {
        return myListItem.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }


    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder viewHolder;
        Button button_cut; //친구 끊기 버튼
        Button button_add; //친구 추가 버튼
        Button button_cancel; //요청 취소 버튼
        Button button_accept; //친구 수락 버튼
        Button button_refuse; //친구 거절 버튼

        View layout_none; //친구 추가 뷰
        View layout_request; //친구 요청 뷰
        View layout_accept; //친구 수락 뷰
        View layout_friend; //친구 뷰

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if(view == null){
            // 레이아웃을 inflate시켜 새로운 view 생성.
            myInflater = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = myInflater.inflate(R.layout.add_friend_listview_item, parent, false);
            // Holder pattern을 위한 wrapper 초기화 (row를 base 클래스로 지정)
            viewHolder = new ViewHolder(view);
            // row에 viewWrapper object를 tag함 (나중에 row를 재활용 할때 필요)
            view.setTag(viewHolder);
            button_cut = viewHolder.getButton_cut();
            button_add = viewHolder.getButton_add();
            button_cancel = viewHolder.getButton_cancel();
            button_accept = viewHolder.getButton_accept();
            button_refuse = viewHolder.getButton_refuse();

            layout_none = viewHolder.getLayout_none();
            layout_request = viewHolder.getLayout_request();
            layout_accept = viewHolder.getLayout_accept();
            layout_friend = viewHolder.getLayout_friend();
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
            button_cut = viewHolder.getButton_cut();
            button_add = viewHolder.getButton_add();
            button_cancel = viewHolder.getButton_cancel();
            button_accept = viewHolder.getButton_accept();
            button_refuse = viewHolder.getButton_refuse();

            layout_none = viewHolder.getLayout_none();
            layout_request = viewHolder.getLayout_request();
            layout_accept = viewHolder.getLayout_accept();
            layout_friend = viewHolder.getLayout_friend();
        }

        // getView() 호출시 인자로 전달된 position을 이용해 현재 사용중인 RowModel의 객체 model 얻기.
        final AddFriendListViewItem item = myListItem.get(index);

        // convertView(row)내부의 TextView의 text를 객체가 저장하고 있는 String으로  설정.
        //메일
        viewHolder.getMail().setText(item.mail);
        //이름
        viewHolder.getName().setText(item.name);
        //계정 종류
        if(item.type.equals("google")){
            Drawable drawable_t = myContext.getResources().getDrawable(R.drawable.ic_google);
            viewHolder.getType().setBackgroundDrawable(drawable_t);
        }else if(item.type.equals("facebook")){
            Drawable drawable_t = myContext.getResources().getDrawable(R.drawable.ic_facebook);
            viewHolder.getType().setBackgroundDrawable(drawable_t);
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

        //끊기 버튼
        button_cut.setOnClickListener(this);
        button_cut.setTag(index);
        //추가 버튼
        button_add.setOnClickListener(this);
        button_add.setTag(index);
        //요청 취소 버튼
        button_cancel.setOnClickListener(this);
        button_cancel.setTag(index);
        //요청 수락 버튼
        button_accept.setOnClickListener(this);
        button_accept.setTag(index);
        //요청 거절 버튼
        button_refuse.setOnClickListener(this);
        button_refuse.setTag(index);

        return view;
    }

    public void addItem(AddFriendListViewItem item){
        myListItem.add(new AddFriendListViewItem(item.mail, item.name, item.type, item.no, item.state));
    }

    public AddFriendListViewItem getItemAt(int no){
        for(int i = 0; i < myListItem.size(); i++){
            AddFriendListViewItem temp = myListItem.get(i);
            if(temp.no == no){
                return myListItem.get(i);
            }
        }
        return null;
    }

    public void removeAt(AddFriendListViewItem item){
        for(int i = 0; i < myListItem.size(); i++){
            AddFriendListViewItem temp = myListItem.get(i);
            if(temp.no == item.no){
                myListItem.remove(i);
                return;
            }
        }
    }

    public void removeAll(){
        myListItem.clear();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()){
            case R.id.button_cut: //친구 끊기 버튼
                AlertDialog.Builder alert_confirm0 = new AlertDialog.Builder(myContext);
                alert_confirm0.setMessage(myListItem.get((int)v.getTag()).mail + "와\n친구관계를 끊으시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final AddFriendListViewItem item = myListItem.get((int)v.getTag());

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
                                            Intent intent = new Intent(myContext, MainActivity.class);
                                            intent.putExtra("id", R.id.social);
                                            intent.putExtra("start_page", 0);
                                            ((AppCompatActivity)myContext).startActivity(intent);
                                            ((AppCompatActivity)myContext).overridePendingTransition(0,0);
                                            ((AppCompatActivity)myContext).finish();
                                        }else {
                                            Toast.makeText(myContext, "친구관계를 끊었습니다", Toast.LENGTH_LONG).show();
                                            AddFriendListViewItem item = myListItem.get((int) v.getTag());
                                            item.state = "none";
                                            myAccount.main_adapter.removeAt(item);
                                            myAccount.main_adapter.notifyDataSetChanged();
                                            notifyDataSetChanged();
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
                alert_confirm.setMessage(myListItem.get((int)v.getTag()).mail + "에게\n친구 요청을 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final AddFriendListViewItem item = myListItem.get((int)v.getTag());

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
                                            Intent intent = new Intent(myContext, MainActivity.class);
                                            intent.putExtra("id", R.id.social);
                                            intent.putExtra("start_page", 0);
                                            ((AppCompatActivity)myContext).startActivity(intent);
                                            ((AppCompatActivity)myContext).overridePendingTransition(0,0);
                                            ((AppCompatActivity)myContext).finish();
                                        }else {
                                            Toast.makeText(myContext, "친구 요청을 하였습니다", Toast.LENGTH_LONG).show();
                                            AddFriendListViewItem item = myListItem.get((int) v.getTag());
                                            item.state = "request";
                                            myAccount.request_adapter.addItem(item);
                                            myAccount.request_adapter.notifyDataSetChanged();
                                            notifyDataSetChanged();
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
                alert_confirm2.setMessage(myListItem.get((int)v.getTag()).mail + "에게 보낸\n친구 요청을 취소하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final AddFriendListViewItem item2 = myListItem.get((int)v.getTag());

                                HashMap<String, String> postData2 = new HashMap<String, String>();
                                postData2.put("to_no", String.valueOf(item2.no));
                                postData2.put("from_no", String.valueOf(myAccount.getIndex()));

                                PostDB postDB2 = new PostDB(myContext, "요청 취소 중");
                                postDB2.putData("delete_request_friend.php", postData2, new OnFinishDBListener() {
                                    @Override
                                    public void onSuccess(String output) {
                                        if(output.equals("null")){
                                            Toast.makeText(getApplicationContext(),"상대방이 이미 친구를 수락하였습니다.",Toast.LENGTH_LONG).show();
                                            myAccount.load_friend();
                                            Intent intent = new Intent(myContext, MainActivity.class);
                                            intent.putExtra("id", R.id.social);
                                            intent.putExtra("start_page", 0);
                                            ((AppCompatActivity)myContext).startActivity(intent);
                                            ((AppCompatActivity)myContext).overridePendingTransition(0,0);
                                            ((AppCompatActivity)myContext).finish();
                                        }else {
                                            Toast.makeText(myContext, "친구 요청을 취소 하였습니다", Toast.LENGTH_LONG).show();
                                            AddFriendListViewItem item = myListItem.get((int) v.getTag());
                                            item.state = "none";
                                            myAccount.request_adapter.removeAt(item);
                                            myAccount.request_adapter.notifyDataSetChanged();
                                            notifyDataSetChanged();
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
                alert_confirm3.setMessage(myListItem.get((int)v.getTag()).mail + "의\n친구 요청을 수락하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final AddFriendListViewItem item3 = myListItem.get((int)v.getTag());

                                HashMap<String, String> postData3 = new HashMap<String, String>();
                                postData3.put("to_no", String.valueOf(myAccount.getIndex()));
                                postData3.put("from_no", String.valueOf(item3.no));
                                postData3.put("mail", myAccount.getMail());

                                PostDB postDB3 = new PostDB(myContext, "친구 수락 중");
                                postDB3.putData("accept_friend.php", postData3, new OnFinishDBListener() {
                                    @Override
                                    public void onSuccess(String output) {
                                        if(output.equals("null")){
                                            Toast.makeText(getApplicationContext(),"상대방이 요청을 취소했습니다.",Toast.LENGTH_LONG).show();
                                            myAccount.load_friend();
                                            Intent intent = new Intent(myContext, MainActivity.class);
                                            intent.putExtra("id", R.id.social);
                                            intent.putExtra("start_page", 0);
                                            ((AppCompatActivity)myContext).startActivity(intent);
                                            ((AppCompatActivity)myContext).overridePendingTransition(0,0);
                                            ((AppCompatActivity)myContext).finish();
                                        }else {
                                            Toast.makeText(myContext, "친구 요청을 수락 하였습니다", Toast.LENGTH_LONG).show();
                                            AddFriendListViewItem item = myListItem.get((int) v.getTag());
                                            item.state = "friend";
                                            myAccount.accept_adapter.removeAt(item);
                                            myAccount.accept_adapter.notifyDataSetChanged();
                                            myAccount.main_adapter.addItem(item);
                                            myAccount.main_adapter.notifyDataSetChanged();
                                            notifyDataSetChanged();
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
                alert_confirm4.setMessage(myListItem.get((int)v.getTag()).mail + "의\n친구 요청을 거절하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final AddFriendListViewItem item4 = myListItem.get((int)v.getTag());

                                HashMap<String, String> postData4 = new HashMap<String, String>();
                                postData4.put("to_no", String.valueOf(myAccount.getIndex()));
                                postData4.put("from_no", String.valueOf(item4.no));
                                postData4.put("mail", myAccount.getMail());

                                PostDB postDB4 = new PostDB(myContext, "요청 거절 중");
                                postDB4.putData("refuse_friend.php", postData4, new OnFinishDBListener() {
                                    @Override
                                    public void onSuccess(String output) {
                                        if(output.equals("null")){
                                            Toast.makeText(getApplicationContext(),"상대방이 요청을 취소했습니다.",Toast.LENGTH_LONG).show();
                                            myAccount.load_friend();
                                            Intent intent = new Intent(myContext, MainActivity.class);
                                            intent.putExtra("id", R.id.social);
                                            intent.putExtra("start_page", 0);
                                            ((AppCompatActivity)myContext).startActivity(intent);
                                            ((AppCompatActivity)myContext).overridePendingTransition(0,0);
                                            ((AppCompatActivity)myContext).finish();
                                        }else {
                                            Toast.makeText(myContext, "친구 요청을 거절 하였습니다", Toast.LENGTH_LONG).show();
                                            AddFriendListViewItem item = myListItem.get((int) v.getTag());
                                            item.state = "none";
                                            myAccount.accept_adapter.removeAt(item);
                                            myAccount.accept_adapter.notifyDataSetChanged();
                                            notifyDataSetChanged();
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

    class ViewHolder{
        private View base;
        private TextView mail; //메일
        private TextView name; //이름
        private ImageView type; //계정 종류
        private Button button_add; //친구 추가 버튼
        private Button button_cancel; //요청 취소 버튼
        private Button button_accept; //친구 수락 버튼
        private Button button_refuse; //친구 거절 버튼
        private Button button_cut; //친구 끊기 버튼

        private View layout_none; //친구 추가 뷰
        private View layout_request; //친구 요청 뷰
        private View layout_accept; //친구 수락 뷰
        private View layout_friend; //친구 뷰

        ViewHolder(View base){
            this.base = base;
        }

        TextView getMail() {
            if (mail == null) {
                mail = (TextView)base.findViewById(R.id.textView_mail);
            }
            return mail;
        }

        TextView getName() {
            if (name == null) {
                name = (TextView)base.findViewById(R.id.textView_name);
            }
            return name;
        }

        ImageView getType() {
            if(type == null){
                type = (ImageView)base.findViewById(R.id.imageView_type);
            }
            return type;
        }

        Button getButton_add(){
            if(button_add == null){
                button_add = (Button)base.findViewById(R.id.button_add);
            }
            return button_add;
        }

        Button getButton_cancel(){
            if(button_cancel == null){
                button_cancel = (Button)base.findViewById(R.id.button_cancel);
            }
            return button_cancel;
        }

        Button getButton_accept(){
            if(button_accept == null){
                button_accept = (Button)base.findViewById(R.id.button_accept);
            }
            return button_accept;
        }

        Button getButton_refuse(){
            if(button_refuse == null){
                button_refuse = (Button)base.findViewById(R.id.button_refuse);
            }
            return button_refuse;
        }

        Button getButton_cut(){
            if(button_cut == null) {
                button_cut = (Button) base.findViewById(R.id.button_cut);
            }
            return button_cut;
        }

        View getLayout_none(){
            if(layout_none == null){
                layout_none = base.findViewById(R.id.layout_none);
            }
            return layout_none;
        }

        View getLayout_request(){
            if(layout_request == null){
                layout_request = base.findViewById(R.id.layout_request);
            }
            return layout_request;
        }

        View getLayout_accept(){
            if(layout_accept == null){
                layout_accept = base.findViewById(R.id.layout_accept);
            }
            return layout_accept;
        }

        View getLayout_friend(){
            if(layout_friend == null){
                layout_friend = base.findViewById(R.id.layout_friend);
            }
            return layout_friend;
        }
    }

    public void setMyContext(Context contect){
        this.myContext = contect;
    }
}
