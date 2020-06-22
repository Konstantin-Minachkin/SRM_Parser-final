package com.example.warhammer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class OutputActivity extends AppCompatActivity {
    RecyclerView list;
    Button back;
    ArrayList<String> ids = new ArrayList<String>();
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> albms = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.output_activity);
        ids = getIntent().getStringArrayListExtra("id");
        names = getIntent().getStringArrayListExtra("name");
        albms = getIntent().getStringArrayListExtra("alb");
        list = (RecyclerView) findViewById(R.id.outList);
        back = (Button) findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        list.setAdapter(new CustomAdapter(this, ids.size()));
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private LayoutInflater inflater;
        Context context;
        private int number;

        CustomAdapter(Context context, int number) {
            this.number = number;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = inflater.inflate(R.layout.list_saves, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.name.setText(names.get(position));
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("key", albms.get(position));
                    i.putExtra("flag", true);
                    i.putExtra("name", names.get(position));
                    startActivity(i);
                }
            });

            holder.del.setOnClickListener(new View.OnClickListener() {
                AlertDialog.Builder ad = new AlertDialog.Builder(context,
                        android.support.v7.appcompat.R.style.Base_Theme_AppCompat_Dialog_MinWidth);
                @Override
                public void onClick(View v) {
                    ad.setTitle("Точно удалить?");  // заголовок
                    ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            DBHelper sql = new DBHelper(getApplicationContext());
                            sql.delete(Integer.valueOf(ids.get(position)));
                            ids.remove(ids.get(position));
                            names.remove(names.get(position));
                            albms.remove(albms.get(position));
                            list.setAdapter(new CustomAdapter(context, ids.size()));
                            if (ids.isEmpty()) onBackPressed();
                        }
                    });
                    ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                        }
                    });
                    ad.setCancelable(true);
                    ad.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return number;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            View view;
            final TextView name;
            ImageButton del;

            ViewHolder(final View view) {
                super(view);
                this.view = view;
                name = (TextView) view.findViewById(R.id.name);
                del = (ImageButton) view.findViewById(R.id.delete);
            }
        }
    }
}
