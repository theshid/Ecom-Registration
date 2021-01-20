package com.ecomtrading.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.AppExecutor;

import java.util.List;

public class EcomAdapter extends RecyclerView.Adapter<EcomAdapter.ViewHolder> {
    Context context;
    List<CommunityInformation> informationList;
    MyDatabase db = MyDatabase.getInstance(context);
    AppExecutor executor = AppExecutor.getInstance();
    private ItemAction mItemOnClickAction;

    // Member variable to handle item clicks
    //final private ItemClickListener mItemClickListener;

    public interface ItemAction {
        void onClick(int id);
    }

    public void setItemOnClickAction(ItemAction itemOnClickAction) {
        mItemOnClickAction = itemOnClickAction;

    }
    public EcomAdapter(Context context, List<CommunityInformation> informationList) {

        this.context = context;
        this.informationList = informationList;
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




    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    @Override
    public int getItemCount() {

        return informationList.size();
    }


    public void setClips(List<CommunityInformation> entries) {
        informationList = entries;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, district, accessibility, distance, connected, date_license, latitude, longitude, image;
        Button btn_delete, btn_edit, btn_update;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.row_name);
            district = itemView.findViewById(R.id.row_district);
            accessibility = itemView.findViewById(R.id.row_accesiblity);
            distance = itemView.findViewById(R.id.row_distance);
            connected = itemView.findViewById(R.id.row_connected_ecg);
            date_license = itemView.findViewById(R.id.row_date_license);
            latitude = itemView.findViewById(R.id.row_latitude);
            longitude = itemView.findViewById(R.id.row_longitude);
            image = itemView.findViewById(R.id.row_image);

            btn_delete = itemView.findViewById(R.id.btn_delete);
            btn_edit = itemView.findViewById(R.id.btn_edit);
            btn_update = itemView.findViewById(R.id.btn_update);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int elementId = getAdapterPosition();
                    //mItemClickListener.onItemClickListener(elementId);
                    mItemOnClickAction.onClick(informationList.get(getAdapterPosition()).getCommunity_id());
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

}
