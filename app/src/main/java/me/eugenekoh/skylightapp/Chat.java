package me.eugenekoh.skylightapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.eugenekoh.skylightapp.utils.Tools;
import me.eugenekoh.skylightapp.utils.ViewAnimation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.location.LocationListener;
//import android.content.Context;
//firestore
//chatbot


public class Chat extends AppCompatActivity implements LocationListener{


    final Conversation myConversationService =
            new Conversation(
                    "2018-09-20",
                    "3fedf5c4-fffc-41d2-bf74-3b27e9aca9bc",
                    "cPjgN6dSCn0H"
            );
    com.ibm.watson.developer_cloud.conversation.v1.model.Context context = new com.ibm.watson.developer_cloud.conversation.v1.model.Context();
    android.content.Context mContext = this;

    private Handler handler = new Handler();
    public ListView msgView;
    public List<HashMap<String, String>> aList;
    public ArrayAdapter<String> msgList;
    private Button conv;

    private LocationManager locationManager;
    String longitude;
    String latitude;
    String airportName, airportTerminal;
    private boolean talkToStaff = false;


    private TabLayout tab_layout;
    private ActionBar actionBar;
    private RelativeLayout relativeLayout;

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initComponent();
        initToolbar();

        msgView = (ListView) findViewById(R.id.listview);
        msgList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        msgView.setAdapter(msgList);
        aList = new ArrayList<HashMap<String, String>>();
        //final TextView conversation = (TextView)findViewById(R.id.conversation);
        final EditText userInput = (EditText)findViewById(R.id.user_input);
        conv = (Button)findViewById(R.id.button);

        //firestore
        //updateStaffChat();


        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        //db.setFirestoreSettings(settings);

        conversationAPI("", context, getString(R.string.workspace));
        destroyChat("Bob Silvers");
        conv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageResponse response = null;
                final String currentinput = String.valueOf(userInput.getText());
                displayMsg(currentinput);

                if (talkToStaff == true){
                    //firestore
                    sendStaffMessage();
                    updateStaffChat();
                }
                else{
                    conversationAPI(currentinput, context, getString(R.string.workspace));
                    userInput.setText("");
                }
            }
        });
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Chat");
        actionBar.setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_20);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    //GPS


    /* Request updates at startup */
    @Override
    public void onResume() {
        super.onResume();
        getLocation();
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(android.content.Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void CheckPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    public void onLocationChanged(Location location) {

        longitude = String.valueOf(location.getLongitude());
        latitude = String.valueOf(location.getLatitude());

    }


    public void onProviderDisabled(String provider) {
        Toast.makeText(Chat.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider!" + provider,
                Toast.LENGTH_SHORT).show();
    }









    public void conversationAPI(String input, com.ibm.watson.developer_cloud.conversation.v1.model.Context context, String workspaceId){



        MessageOptions newMessage = new MessageOptions.Builder().workspaceId(workspaceId)
                .input(new InputData.Builder(input).build()).context(context).build();

        myConversationService.message(newMessage).enqueue(new ServiceCallback<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                displayMsg(response);
                if (response.getIntents().get(0).getIntent().endsWith("checkflight")){
                    checkFlight();

                }
                else if (response.getIntents().get(0).getIntent().endsWith("checkhotel")){
                    displayMsg("hotel maps api");

                }
                else if (response.getIntents().get(0).getIntent().endsWith("directions")){
                    displayDirections();

                }
                else if (response.getIntents().get(0).getIntent().endsWith("talktostaff")){

                    displayMsgStaff("Hello! My name is Cindy. How can I help you?");
                    talkToStaff = true;
                }
            }
            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    public void displayMsg(MessageResponse msg){
        final MessageResponse mssg = msg;
        handler.post(new Runnable(){
            @Override
            public void run() {
                String text = mssg.getOutput().getText().get(0);
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("listview_title", "Bot");
                hm.put("listview_discription", text);
                hm.put("listview_image", Integer.toString(R.drawable.sia_pc));
                aList.add(hm);
                String[] from = {"listview_image", "listview_title", "listview_discription"};
                int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};
                SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_activity, from, to);
                msgList.add("Bot: " + text);
                msgView.setAdapter(simpleAdapter);
                msgView.setSelection(msgList.getCount()-1);
                msgView.smoothScrollToPosition(msgList.getCount() - 1);
                context = mssg.getContext();
            }
        });
    }

    public void displayMsg(String msg) {
        final String mssg = msg;
        handler.post(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("listview_title", "You");
                hm.put("listview_discription", mssg);
                hm.put("listview_image", Integer.toString(R.drawable.profile_pc));
                aList.add(hm);
                String[] from = {"listview_image", "listview_title", "listview_discription"};
                int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};
                SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_activity, from, to);
                msgList.add(mssg);
                msgView.setAdapter(simpleAdapter);
                msgView.setSelection(msgList.getCount()-1);
                msgView.smoothScrollToPosition(msgList.getCount() - 1);
            }
        });
    }

    public void displayMsgBot(String msg) {
        final String mssg = msg;
        handler.post(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("listview_title", "Bot");
                hm.put("listview_discription", mssg);
                hm.put("listview_image", Integer.toString(R.drawable.sia_pc));
                aList.add(hm);
                String[] from = {"listview_image", "listview_title", "listview_discription"};
                int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};
                SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_activity, from, to);
                msgList.add(mssg);
                msgView.setAdapter(simpleAdapter);
                msgView.setSelection(msgList.getCount()-1);
                msgView.smoothScrollToPosition(msgList.getCount() - 1);
            }
        });
    }

    public void displayMsgStaff(String msg) {
        final String mssg = msg;
        handler.post(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("listview_title", "Cindy");
                hm.put("listview_discription", mssg);
                hm.put("listview_image", Integer.toString(R.drawable.staff_pc));
                aList.add(hm);
                String[] from = {"listview_image", "listview_title", "listview_discription"};
                int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};
                SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_activity, from, to);
                msgList.add(mssg);
                msgView.setAdapter(simpleAdapter);
                msgView.setSelection(msgList.getCount()-1);
                msgView.smoothScrollToPosition(msgList.getCount() - 1);
            }
        });
    }

    public void checkFlight() {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"originAirportCode\": \"SIN\", \"destinationAirportCode\": \"DXB\", \"scheduledDepartureDate\": \"2018-08-15\"}");
        Request request = new Request.Builder()
                .url("https://apigw.singaporeair.com/appchallenge/api/flightroutestatus")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("apikey", "aghk73f4x5haxeby7z24d2rc")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "3c15c3e1-3bc4-4b7b-99fa-6508c1146c4e")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        String origCountry = object.getJSONObject("response").getJSONObject("origin").getString("cityName");
                        String destCountry = object.getJSONObject("response").getJSONObject("destination").getString("cityName");
                        String status = object.getJSONObject("response").getJSONArray("flights").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("flightStatus");
                        String flightCode = object.getJSONObject("response").getJSONArray("flights").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("operatingAirlineCode");
                        String flightNumber = object.getJSONObject("response").getJSONArray("flights").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("flightNumber");
                        String depInfo = object.getJSONObject("response").getJSONArray("flights").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("scheduledDepartureTime");
                        switch (status){
                            case "Arrived":
                                status = " has Arrived";
                                break;
                            case "Delayed":
                                status = " has been Delayed";
                                break;
                            default:
                        }
                        String depDate = depInfo.substring(0,10);
                        String depTime = depInfo.substring(11, 16);
                        displayMsgBot("Your flight " + flightCode + flightNumber + " from " + origCountry + " to " + destCountry + " will depart on " + depDate + " at " + depTime + "hours.");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public void displayDirections() {


        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"originAirportCode\": \"SIN\", \"destinationAirportCode\": \"DXB\", \"scheduledDepartureDate\": \"2018-08-15\"}");
        Request request = new Request.Builder()
                .url("https://apigw.singaporeair.com/appchallenge/api/flightroutestatus")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("apikey", "aghk73f4x5haxeby7z24d2rc")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "3c15c3e1-3bc4-4b7b-99fa-6508c1146c4e")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        airportName = object.getJSONObject("response").getJSONArray("flights").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("origin").getString("airportName");
                        airportTerminal = object.getJSONObject("response").getJSONArray("flights").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("origin").getString("airportTerminal");
                        String depInfo = object.getJSONObject("response").getJSONArray("flights").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("scheduledDepartureTime");


                        OkHttpClient client2 = new OkHttpClient();

                        Request request2 = new Request.Builder()
                                .url("https://maps.googleapis.com/maps/api/directions/json?origin="+ latitude+","+longitude +"&destination=" + airportName + "%20Airport%20Terminal" + airportTerminal + "&key=AIzaSyA7TpA2UY9SjoZKfTN8gnJrVg1HJJFRmyQ")
                                .get()
                                .addHeader("Cache-Control", "no-cache")
                                .addHeader("Postman-Token", "a875df6a-ce3f-465c-924a-8b951ab23d66")
                                .build();
                        client2.newCall(request2).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response2) throws IOException {
                                if (response2.isSuccessful()) {
                                    try {
                                        JSONObject object2 = new JSONObject(response2.body().string());
                                        String travelDuration = object2.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
                                        displayMsgBot("It will take approximately " + travelDuration + " by car to get to " + airportName + " Airport Terminal " + airportTerminal+" from your current location." );
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


    public void sendStaffMessage(){

        final String TAG = "XiaoQi";

        final EditText userInput = (EditText)findViewById(R.id.user_input);
        final String currentinput = String.valueOf(userInput.getText());
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final Map<String, Object> user_input = new HashMap<>();
        Date date = new Date();

        user_input.put("created", date.toString());
        user_input.put("id", date.getTime());
        user_input.put("sender", "Bob Silvers");
        user_input.put("text", currentinput);

        final Map<String, Object> newMessage = new HashMap<>();

        db.collection("conversations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String match = "";
                            Boolean foundUser = false;
                            List documents = task.getResult().getDocuments();
                            //for (int i = 0;  i <task.getResult().size(); i++){
                            //QueryDocumentSnapshot document = (QueryDocumentSnapshot) documents.get(i);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, "document customer " + (document.get("customer")));
                                //Log.d(TAG, "sender " + user_input.get("sender"));

                                if (document.get("customer").equals(user_input.get("sender"))){
                                    match = document.getId();
                                    //Log.d(TAG, "name exists " + document.get("customer"));
                                    int counter = 0;

                                    ArrayList list = new ArrayList();

                                    if(document.getData().get("messages") instanceof HashMap){
                                        list.add(document.getData().get("messages"));
                                    }
                                    else{
                                        list = (ArrayList) document.getData().get("messages");
                                    }

                                    //Log.d(TAG, "list size " + list.size());
                                    for (int j = 0; j < list.size(); j++) {
                                        counter++;
                                    }
                                    //Log.d(TAG, "counter" + counter);

                                    DocumentReference messageRef = db.collection("conversations").document(match);

                                    String counter_string = Integer.toString(counter);
                                    newMessage.put(counter_string, user_input);
                                    //Log.d(TAG, "newMessage: " + newMessage);

                                    //Log.d(TAG, "messageREf " + messageRef.getId());
                                    messageRef
                                            .update("messages", FieldValue.arrayUnion(user_input))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error updating document", e);
                                                }
                                            });
                                    //return;
                                    foundUser = true;
                                }
                            }
                            if (foundUser == false){

                                Date date = new Date();
                                Map<String, Object> newDoc = new HashMap<>();

                                newDoc.put("created", date.toString());
                                newDoc.put("customer", "Bob Silvers");
                                newDoc.put("id", date.getTime());
                                newDoc.put("messages", user_input);


                                db.collection("conversations")
                                        .add(newDoc)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                                String id;
                                                id = documentReference.getId();
                                                DocumentReference messageRef = db.collection("conversations").document(id);
                                                messageRef
                                                        .update("messages", FieldValue.arrayUnion(user_input));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });


                            }



                            //Log.d(TAG, "counter: " + counter);
                        }

                    }
                });
        userInput.setText("");
    }


    public void updateStaffChat(){

        final String TAG = "XiaoQi";
        final ArrayList indexes = new ArrayList();


        db.collection("conversations")
                .whereEqualTo("customer", "Bob Silvers")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            Log.w(TAG, "\n");
                            return;
                        }

                        ArrayList messages = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            //Log.w(TAG, "doc" + doc.getData().get("messages"));
                            if (doc.get("customer").equals("Bob Silvers")) {
                                ArrayList list = new ArrayList();

                                if(doc.getData().get("messages") instanceof HashMap){
                                    list.add(doc.getData().get("messages"));
                                }
                                else{
                                    list = (ArrayList) doc.getData().get("messages");
                                }

                                for (int i = 0; i < list.size(); i++){
                                    HashMap hmap = new HashMap();
                                    hmap = (HashMap) list.get(i);
                                    Log.d(TAG, "text: " + hmap);
                                    if (!(indexes.contains(hmap.get("id")))){
                                        messages.add(hmap.get("text"));
                                        indexes.add(hmap.get("id"));
                                        String sender = (String) hmap.get("sender");
                                        String output;

                                        if (sender.equals("Staff")){
                                            output = (String) hmap.get("text");
                                            displayMsgStaff(output);
                                        }
                                        else{
                                            output = (String) hmap.get("text");
                                        }
                                        //Log.d(TAG, "text: " + output);
                                    }

                                    //Log.d(TAG, "list: " + hmap.get("text"));
                                }
                                //messages.add();
                            }
                        }
                    }
                });
    }

    public void destroyChat(final String user){
        final String TAG = "XiaoQi";
        Log.d(TAG, "destorycaht");

        Query conversation_query = db.collection("conversations")
                .whereEqualTo("customer", user);
        conversation_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        DocumentReference messageRef = db.collection("conversations").document(document.getId());
                        messageRef.delete();
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }


    private void initComponent() {
        relativeLayout = (RelativeLayout) findViewById(R.id.chatlayout);
        tab_layout = (TabLayout) findViewById(R.id.tab_layout);

        tab_layout .addTab(tab_layout.newTab().setIcon(R.drawable.ic_chat), 0);
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_timeline), 1);

        // set icon color pre-selected
        tab_layout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.deep_orange_500), PorterDuff.Mode.SRC_IN);
        tab_layout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_IN);

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.deep_orange_500), PorterDuff.Mode.SRC_IN);
                switch (tab.getPosition()) {
                    case 0:
                        break;
                    case 1:
                        startActivity(new Intent(Chat.this, Travel.class));
                        finish();
                        break;
                }

                ViewAnimation.fadeOutIn(relativeLayout);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }
}