package com.java.xuhaotian.mainpage.fragment;

import static com.java.xuhaotian.Consts.JSON;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.java.xuhaotian.Consts;
import com.java.xuhaotian.R;
import com.java.xuhaotian.adapter.AskAnswerMsgAdapter;
import com.java.xuhaotian.AskAnswerMsg;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AskAnswerFragment extends Fragment {
    private ArrayList<AskAnswerMsg> msgList = new ArrayList<>();
    private RecyclerView msgRecyclerView;
    private AskAnswerMsgAdapter adapter;
    private EditText mEtAskMsg;
    private Button mBtnAskAns;
    private Button mBtnAskAnsDel;
    private static final int SEND = 0;
    private static final int RECEIVE = 1;
    private String ask;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == RECEIVE){
                AskAnswerMsg msgLeft=new AskAnswerMsg(ask,AskAnswerMsg.TYPE_RECEIVE);
                Log.d("test", "--sending:" + ask + "---");
                msgList.add(msgLeft);
                adapter.notifyItemChanged(msgList.size()-1);
                msgRecyclerView.scrollToPosition(msgList.size()-1);
                //adapter = new AskAnswerMsgAdapter(msgList);
                //msgRecyclerView.setAdapter(adapter);

            }
            return false;
        }
    }) ;

    public void initMsg() {
        AskAnswerMsg msgLeft = new AskAnswerMsg("请提问",AskAnswerMsg.TYPE_RECEIVE);
        msgList.add(msgLeft);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ask_answer, container, false);
        Log.d("AskAns", "---create Ask Answer---");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        msgRecyclerView=view.findViewById(R.id.messages_view);
        mEtAskMsg = view.findViewById(R.id.et_AskMsg);
        mBtnAskAns = view.findViewById(R.id.btn_AskAns);
        mBtnAskAnsDel = view.findViewById(R.id.btn_AskAnsDel);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new AskAnswerMsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        initMsg();

        mBtnAskAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ask = mEtAskMsg.getText().toString();
                AskAnswerMsg msgRight=new AskAnswerMsg(ask,AskAnswerMsg.TYPE_SEND);
                msgList.add(msgRight);
                adapter.notifyItemChanged(msgList.size()-1);
                msgRecyclerView.scrollToPosition(msgList.size()-1);
                mEtAskMsg.setText("");
                Thread askThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!"".equals(ask)) {

                                JSONObject askJSON = new JSONObject();
                                askJSON.put("inputQuestion", ask);
                                askJSON.put("token", Consts.getToken());
                                OkHttpClient client = new OkHttpClient();
                                RequestBody body = RequestBody.create(String.valueOf(askJSON), JSON);
                                Request request = new Request.Builder()
                                        .url(Consts.backendURL + "inputQuestion")
                                        .post(body)
                                        .build();
                                Response response = client.newCall(request).execute();

                                if (response.code() == 200) {
                                    Log.d("test", "--sending success---");
                                    JSONArray ansJSON = new JSONArray(response.body().string());
                                    ask = ansJSON.getJSONObject(0).getString("value");
                                    if (ask.equals("") ){ask = ansJSON.getJSONObject(0).getString("message");}
                                } else {
                                    ask = getString(R.string.connectError);
                                    Log.d("test", "--sending fail:" + response.code() + "---");
                                }

                                Message msg2 = new Message();
                                msg2.what = RECEIVE;
                                handler.sendMessage(msg2);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.d("test", "--sending fail---");
                        }
                    }
                });
                askThread.start();
                try {
                    askThread.join();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        mBtnAskAnsDel.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 msgList.clear();
                                                 adapter.notifyDataSetChanged();
                                             }
                                         }

        );

    }
}
