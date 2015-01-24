//SelectedActivity.java
//Shows selected activity with options
package nl.mprog.friendzone10794913;

//BELANGRIJKSTE ACTIVITY!!!
//TODO: zorg dat gebruiker voorkeur kan aangeven
//TODO: best scoring option wordt weergegeven
//TODO: onthoud user option
//TODO: link nieuw optie aan activity

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SelectedActivity extends ActionBarActivity {

    private TextView txtDate;
    private String date;
    private TextView txtDateTitle;
    private String dateTitle;
    private TextView txtBestTitle;
    private String bestTitle;
    private TextView txtBest;
    private String best;
    private TextView txtOptions;
    private String options;
    private List<ParseObject> objectList;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected);
        new RemoteDataTask().execute();

        // Set textview
        dateTitle = "Date:";
        txtDateTitle = (TextView) findViewById(R.id.dateTitle);
        txtDateTitle.setText(dateTitle);

        // Set textview
        date = getIntent().getStringExtra("date");
        txtDate = (TextView) findViewById(R.id.date);
        txtDate.setText(date);

        // Set textview
        bestTitle = "Best:";
        txtBestTitle = (TextView) findViewById(R.id.bestTitle);
        txtBestTitle.setText(bestTitle);


        // Set textview
        options = "Options:";
        txtOptions = (TextView) findViewById(R.id.options);
        txtOptions.setText(options);

        // Getting object reference to listview of main.xml
        ListView listView = (ListView) findViewById(R.id.listview);

        // Instantiating array adapter to populate the listView
        // The layout android.R.layout.simple_list_item_single_choice creates radio button for each listview item
        list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, list);
        listView.setAdapter(adapter);
    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            //select relation of current user
            ParseObject current = ParseUser.getCurrentUser();
            ParseRelation relation = current.getRelation("groups");
            ParseQuery query = relation.getQuery();

            ParseQuery innerQuery = relation.getQuery();
            ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Activity");
            query2.whereMatchesQuery("groups", innerQuery);
            query2.whereEqualTo("objectId", getIntent().getStringExtra("objectId"));
            ParseQuery<ParseObject> query3 = new ParseQuery<ParseObject>("Option");
            query3.whereMatchesQuery("activities", query2);

            ParseQuery<ParseObject> bestQuery = ParseQuery.getQuery("Option");
            bestQuery.orderByDescending("votes");
            bestQuery.whereMatchesQuery("activities", query2);
            bestQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (object == null) {
                        System.out.println("The getFirst request failed.");
                    } else {
                        System.out.println("Retrieved the object.");
                        best = object.get("option_name").toString();
                        txtBest = (TextView) findViewById(R.id.best);
                        txtBest.setText(best);
                    }
                }
            });

            try {
                objectList = query3.find();
            } catch (ParseException error) {
                Log.e("Error", error.getMessage());
                error.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            for (ParseObject query : objectList) {
                list.add((String) query.get("option_name"));
                adapter.notifyDataSetChanged();
            }
//            Capture button clicks on ListView items
//            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view,
//                                        int position, long id) {
//                    // Send single item click data to SingleItemView Class
//                    Intent i = new Intent(MainActivity.this, SelectedActivity.class);
//                    // Pass data "name" followed by the position
//                    i.putExtra("date", ob.get(position).getDate("date").toString());
//                    // Open SingleItemView.java Activity
//                    startActivity(i);
//                }
//            });
        }
    }
}

