package com.example.warhammer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Fractions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fractions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String[] fractionsArr = getResources().getStringArray(R.array.fractions_names);
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OutputActivity.class);
                Cursor table = null;
                DBHelper sql = new DBHelper(getApplicationContext());
                table = sql.getSavesTable();
                if (table != null && table.getCount() > 0) {
                    ArrayList<String> outputId = new ArrayList<String>();
                    ArrayList<String> outputName = new ArrayList<String>();
                    ArrayList<String> outputAlb = new ArrayList<String>();
                    table.moveToFirst();
                    while (!table.isLast()) {
                        outputId.add(table.getString(0));
                        outputName.add(table.getString(1));
                        outputAlb.add(table.getString(2));
                        table.moveToNext();
                    }
                    outputId.add(table.getString(0));
                    outputName.add(table.getString(1));
                    outputAlb.add(table.getString(2));
                    intent.putExtra("id", outputId);
                    intent.putExtra("name", outputName);
                    intent.putExtra("alb", outputAlb);
                    startActivity(intent);
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Нет закладок",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
                fab.setEnabled(false);
            }
        });
        RecyclerView list = (RecyclerView) findViewById(R.id.fractions_list);
        list.setAdapter(new CustomAdapter1(this, R.layout.list_item, fractionsArr));
        list.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void onResume() {
        super.onResume();
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OutputActivity.class);
                Cursor table = null;
                DBHelper sql = new DBHelper(getApplicationContext());
                table = sql.getSavesTable();
                if (table != null && table.getCount() > 0) {
                    ArrayList<String> outputId = new ArrayList<String>();
                    ArrayList<String> outputName = new ArrayList<String>();
                    ArrayList<String> outputAlb = new ArrayList<String>();
                    table.moveToFirst();
                    while (!table.isLast()) {
                        outputId.add(table.getString(0));
                        outputName.add(table.getString(1));
                        outputAlb.add(table.getString(2));
                        table.moveToNext();
                    }
                    outputId.add(table.getString(0));
                    outputName.add(table.getString(1));
                    outputAlb.add(table.getString(2));
                    intent.putExtra("id", outputId);
                    intent.putExtra("name", outputName);
                    intent.putExtra("alb", outputAlb);
                    startActivity(intent);
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Нет закладок",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
                fab.setEnabled(true);
            }
        });
    }

    private class CustomAdapter1 extends RecyclerView.Adapter<CustomAdapter1.ViewHolder> {

        private LayoutInflater inflater;
        Context context;
        private int number;
        String[] fractionsArr;

        CustomAdapter1(Context context, int textRes,  String[] fractionsArr) {
            this.number = fractionsArr.length;
            this.fractionsArr = fractionsArr;
            this.inflater = LayoutInflater.from(context);
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = inflater.inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.nameView.setText(fractionsArr[position]);
            holder.imageView.setImageResource(iconFract[position]);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("key", fractionID[position]);
                    i.putExtra("flag", false);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return number;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            View view;
            final ImageView imageView;
            final TextView nameView;

            ViewHolder(final View view) {
                super(view);
                this.view = view;
                imageView = (ImageView) view.findViewById(R.id.icon);
                nameView = (TextView) view.findViewById(R.id.label);
            }
        }
    }

    private String necronID = "222401877";
    private String demonsID = "222401920";
    private String tauID = "222401895";
    private String guardID = "222401931";
    private String knightsID = "222401908";
    private String wolfsID = "240229103";
    private String orksID = "222401924";
    private String marinesID = "222402011";
    private String chaosMarinesID = "222401962";
    private String bloodAngelsID = "222401939";
    private String mechsID = "222401887";
    private String greyKnightsID = "222401873";
    private String darkAngelsID = "222401988";
    private String darkEldarID = "222401861";
    private String eldarID = "222401845";
    private String tiranidsID = "222401851";

    final String[] fractionID = {
            demonsID,
            guardID,
            knightsID,
            tauID,
            wolfsID,
            marinesID,
            chaosMarinesID,
            orksID,
            bloodAngelsID,
            mechsID,
            necronID,
            greyKnightsID,
            darkAngelsID,
            darkEldarID,
            tiranidsID,
            eldarID,
    };
    final int[] iconFract = {
            R.drawable.demons,
            R.drawable.astra,
            R.drawable.guard,
            R.drawable.tau,
            R.drawable.spacewolves,
            R.drawable.spacemarines,
            R.drawable.chaosspacemarines,
            R.drawable.orks,
            R.drawable.bloodangels,
            R.drawable.mechanicus,
            R.drawable.necrons,
            R.drawable.greyknights,
            R.drawable.darkangels,
            R.drawable.darkeldar,
            R.drawable.tiranids,
            R.drawable.eldar,
    };

   /* private ListView.OnItemClickListener listview = new ListView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("key", fractionID[position]);
            startActivity(i);
        }
    };*/
}
