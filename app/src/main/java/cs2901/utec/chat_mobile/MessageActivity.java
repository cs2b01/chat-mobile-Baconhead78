package cs2901.utec.chat_mobile;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;

    public Activity getActivity(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        String username = getIntent().getExtras().get("username").toString();
        setTitle("@"+username);
        mRecyclerView = findViewById(R.id.main_recycler_view);
        refresh();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        getChats();
    }

    public void onClickBtnSend(View v) {

        postMessage();
    }

    public void getChats(){
        final String userFromId = getIntent().getExtras().get("user_from_id").toString();
        String userToId = getIntent().getExtras().get("user_to_id").toString();
        String url = "http://10.0.2.2:5000/chats/<user_from_id>/<user_to_id>";
        url = url.replace("<user_from_id>", userFromId);
        url = url.replace("<user_to_id>", userToId);
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("response");
                            int uID = Integer.parseInt(userFromId);
                            mAdapter = new MyMessageAdapter(data, getActivity(), uID);
                            mRecyclerView.setAdapter(mAdapter);




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
        queue.add(request);
    }

    public void postMessage(){
        final String url = "http://10.0.2.2:8080/messages";
        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> params = new HashMap();
        final String user_from_id = getIntent().getExtras().get("user_from_id").toString();
        final String user_to_id = getIntent().getExtras().get("user_to_id").toString();
        final String content = ((EditText)findViewById(R.id.txtMessage)).getText().toString();
        params.put("user_from_id",user_from_id);
        params.put("user_to_id", user_to_id);
        params.put("content", content);
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // TODO

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                error.printStackTrace();

            }
        });
        queue.add(jsonObjectRequest);
        getChats();
        ((EditText) findViewById(R.id.txtMessage)).setText("");

    }
    public void refresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getChats();
                refresh();
            }
        }, 5000);

    }


}
