package com.ecomtrading.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.ecomtrading.android.ui.EditFragment;
import com.ecomtrading.android.utils.AppExecutor;
import com.ecomtrading.android.utils.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EcomAdapter extends RecyclerView.Adapter<EcomAdapter.ViewHolder> {
    Context context;
    private List<CommunityInformation> informationList = new ArrayList<>();
    ListActivity listActivity;
    FragmentManager fm;
    private int id ;
    private int position;
    MyDatabase database;
    AppExecutor appExecutor;


    public EcomAdapter(ListActivity listActivity, List<CommunityInformation> informationList,
                       Context context, FragmentManager fm) {

        this.listActivity = listActivity;
        this.informationList = informationList;
        this.database = MyDatabase.getInstance(context);
        this.fm = fm;
        this.context = context;

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
        holder.district.setText(String.valueOf(communityInformation.getGeographical_district()));
        holder.accessibility.setText(String.valueOf(communityInformation.getAccessibility()));
        holder.distance.setText(String.valueOf(communityInformation.getDistance()));
        holder.connected.setText(String.valueOf(communityInformation.getConnected_to_ecg()));
        holder.date_license.setText(String.valueOf(communityInformation.getDate_licence()));
        holder.latitude.setText(String.valueOf(communityInformation.getLatitude()));
        holder.longitude.setText(String.valueOf(communityInformation.getLongitude()));
        holder.image.setImageBitmap(ConversionUtils.base64ToBitmap(communityInformation.getImage()));
        if (!communityInformation.getSent_server()) {
            holder.img_status.setImageResource(R.drawable.ic_error);
        } else {
            holder.img_status.setImageResource(R.drawable.ic_check_circle);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Fragment","value of"+ position);
                showPopupMenu(v,communityInformation.getCommunity_id(),holder.getAdapterPosition() );
            }
        });


    }

    private void showDialog() {

    }


    @Override
    public int getItemCount() {

        return informationList != null ? informationList.size() : 0;

    }

    public void updateList(List<CommunityInformation> newList) {
        this.informationList = newList;
        notifyDataSetChanged();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, district, accessibility, distance, connected, date_license, latitude, longitude;
        CardView cardView;
        CircleImageView image;
        ImageView img_status;

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
            img_status = itemView.findViewById(R.id.image_status);



        }
    }

    private void showPopupMenu(View view, int localid, int position) {
        // inflate menu
        id = localid;
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_list, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupClickListener());
        popup.show();
    }

    class PopupClickListener implements PopupMenu.OnMenuItemClickListener {

        public PopupClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    Log.d("Fragment",String.valueOf(id));
                    EditFragment.newInstance(id).show(fm, "Dialog Fragment");
                    return true;
                case R.id.action_delete:
                    appExecutor = AppExecutor.getInstance();
                    appExecutor.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Fragment","value of"+ position);
                            CommunityInformation communityInformation = informationList.get(position);
                            database.dao().deleteCommunityInfo(communityInformation);
                        }
                    });

                    listActivity.refreshList();
                    return true;
                default:
            }
            return false;
        }
    }

}
