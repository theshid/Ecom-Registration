package com.ecomtrading.android.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecomtrading.android.R;
import com.ecomtrading.android.api.ApiClient;
import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.ui.edit_fragment.EditFragment;
import com.ecomtrading.android.ui.list_activity.ListActivity;
import com.ecomtrading.android.utils.AppExecutor;
import com.ecomtrading.android.utils.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EcomAdapter extends RecyclerView.Adapter<EcomAdapter.ViewHolder> {
    Context context;
    private List<CommunityInformation> informationList = new ArrayList<>();
    ListActivity listActivity;
    FragmentManager fm;
    private int id ;
    private int position;
    private int positionInArray;
    MyDatabase database;
    AppExecutor appExecutor;
    String date_creation, user_name;
    ApiService apiService;


    public EcomAdapter(ListActivity listActivity, List<CommunityInformation> informationList,
                       Context context, FragmentManager fm) {

        this.listActivity = listActivity;
        this.informationList = informationList;
        this.database = MyDatabase.getInstance(context);
        this.fm = fm;
        this.context = context;
        this.apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("NewApi")
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
            holder.textView_status.setText("Item not sent");
            holder.btn.setVisibility(View.VISIBLE);
        } else {
            holder.img_status.setImageResource(R.drawable.ic_check_circle);
            holder.textView_status.setText("Item sent");
            holder.btn.setVisibility(View.GONE);
           // holder.img_status.setBackgroundTintList(AppCompatResources.getColorStateList(context,R.color.md_green_700));
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Fragment","value of"+ position);
                positionInArray = holder.getAdapterPosition();
                showPopupMenu(v,communityInformation.getCommunity_id(),communityInformation.getCreatedDate() ,
                        communityInformation.getCreatedBy());
            }
        });

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppExecutor executor = AppExecutor.getInstance();
                Call<ResponseBody> call = apiService.sendInformation(communityInformation.getCommunity_name(),
                        communityInformation.getGeographical_district(),communityInformation.getAccessibility(),
                        communityInformation.getDistance(),communityInformation.getConnected_to_ecg(),
                        communityInformation.getDate_licence(),communityInformation.getLatitude(),
                        communityInformation.getLongitude(),communityInformation.getImage(),
                        communityInformation.getCreatedBy(),communityInformation.getCreatedDate(),
                        communityInformation.getUpdateBy(),communityInformation.getUpdateDate());

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        communityInformation.setSent_server(response.code() == 200);
                        executor.diskIO().execute(new Runnable() {
                            @Override
                            public void run() {

                                database.dao().updateInformation(communityInformation.getCommunity_name(),
                                        communityInformation.getCommunity_id(),
                                        communityInformation.getGeographical_district(),
                                        communityInformation.getAccessibility(),
                                        communityInformation.getDistance(),
                                        communityInformation.getConnected_to_ecg(),
                                        communityInformation.getDate_licence(),
                                        communityInformation.getLatitude(),
                                        communityInformation.getLongitude(),
                                        communityInformation.getImage(),
                                        communityInformation.getUpdateBy(),
                                        communityInformation.getUpdateDate(),
                                        communityInformation.getSent_server());
                                Log.d("Adapter", "executor");

                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        executor.diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                communityInformation.setSent_server(false);
                                database.dao().updateInformation(communityInformation.getCommunity_name(),
                                        communityInformation.getCommunity_id(),
                                        communityInformation.getGeographical_district(),
                                        communityInformation.getAccessibility(),
                                        communityInformation.getDistance(),
                                        communityInformation.getConnected_to_ecg(),
                                        communityInformation.getDate_licence(),
                                        communityInformation.getLatitude(),
                                        communityInformation.getLongitude(),
                                        communityInformation.getImage(),
                                        communityInformation.getUpdateBy(),
                                        communityInformation.getUpdateDate(),
                                        communityInformation.getSent_server());
                                Log.d("Adapter", "executor");

                            }
                        });
                    }
                });
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
        TextView name, district, accessibility, distance, connected, date_license, latitude, longitude,
        textView_status;
        CardView cardView;
        AppCompatImageView image;
        ImageView img_status;
        Button btn;

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
            textView_status = itemView.findViewById(R.id.textview_status);
            btn = itemView.findViewById(R.id.btn_resend);



        }
    }

    private void showPopupMenu(View view, int localid, String createdDate,String name_user) {
        // inflate menu
        id = localid;
        user_name = name_user;
        date_creation = createdDate;
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
            CommunityInformation communityInformation = informationList.get(positionInArray);
            switch (item.getItemId()) {
                case R.id.action_edit:
                    Log.d("Fragment",String.valueOf(id));
                    EditFragment.newInstance(id,communityInformation).show(fm, "Dialog Fragment");
                    return true;
                case R.id.action_delete:
                    appExecutor = AppExecutor.getInstance();
                    appExecutor.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Fragment","value of"+ position);

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
