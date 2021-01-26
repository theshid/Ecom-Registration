package com.ecomtrading.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.AppExecutor;

import java.util.ArrayList;
import java.util.List;

public class EcomAdapter extends RecyclerView.Adapter<EcomAdapter.ViewHolder> {
    Context context;
    private ArrayList<CommunityInformation> informationList = new ArrayList<>();
    ListActivity listActivity;
    FragmentManager fm;
    MyDatabase database;



    public EcomAdapter(ListActivity listActivity, ArrayList<CommunityInformation> informationList,
                       Context context, FragmentManager fm) {

        this.listActivity = listActivity;
        this.informationList = informationList;
        this.database = MyDatabase.getInstance(context);
        this.fm = fm;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommunityInformation communityInformation = informationList.get(position);

        holder.name.setText(communityInformation.getCommunity_name());
        holder.district.setText(communityInformation.getGeographical_district());
        holder.accessibility.setText(communityInformation.getAccessibility());
        holder.distance.setText(communityInformation.getDistance());
        holder.connected.setText(communityInformation.getConnected_to_ecg());
        holder.date_license.setText(communityInformation.getDate_licence().toString());
        holder.latitude.setText(communityInformation.getLatitude().toString());
        holder.longitude.setText(communityInformation.getLongitude().toString());
        holder.image.setText(communityInformation.getCommunity_name());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }

    private void showDialog(){

    }



    @Override
    public int getItemCount() {

        return informationList != null ? informationList.size() :0;

    }

    public void updateList(ArrayList<CommunityInformation> newList) {
        this.informationList = newList;
        notifyDataSetChanged();

    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, district, accessibility, distance, connected, date_license, latitude, longitude, image;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.community_name);
            district = itemView.findViewById(R.id.geographical_district);
            accessibility = itemView.findViewById(R.id.accessibility);
            distance = itemView.findViewById(R.id.distance_ecom);
            connected = itemView.findViewById(R.id.connected_ecg);
            date_license = itemView.findViewById(R.id.license_date);
            latitude = itemView.findViewById(R.id.latitude);
            longitude = itemView.findViewById(R.id.longitude);
            image = itemView.findViewById(R.id.photo);
            cardView = itemView.findViewById(R.id.ripple);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int elementId = getAdapterPosition();

                    showDialog();
                }

                private void showDialog() {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("Menu");
                    dialog.setMessage("Choose your option");


                    LayoutInflater inflater = LayoutInflater.from(context);
                    View option_layout = inflater.inflate(R.layout.dialog_layout, null);

                    dialog.setView(option_layout);


                    dialog.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });


                    dialog.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });


                    dialog.show();

                }
            });


        }
    }

    class PopupClickListener implements PopupMenu.OnMenuItemClickListener {

        public PopupClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    EditCommunityFragment.newInstance(id).show(fm, "Dialog Fragment");
                    return true;
                case R.id.action_delete:
                    databaseHelper.deleteCommunity(id);
                    activity.refreshList();
                    return true;
                default:
            }
            return false;
        }
    }

}
