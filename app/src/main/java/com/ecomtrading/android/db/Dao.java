package com.ecomtrading.android.db;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ecomtrading.android.entity.CommunityInformation;

import java.util.List;

@androidx.room.Dao
public interface Dao {

    @Query("SELECT * FROM community_information")
    LiveData<List<CommunityInformation>> getAll();

    @Query("SELECT * FROM community_information WHERE community_id = :id")
    CommunityInformation getCommunity(int id);

    @Insert
    void insertCommunityInfo(CommunityInformation information);

    @Delete
    void deleteCommunityInfo(CommunityInformation information);

    @Query("UPDATE community_information SET community_name = :name, geographical_district = :geographical_district," +
            " accessibility = :accessibility, distance = :distance, connected_to_ecg = :connected_to_ecg," +
            " date_licence = :date_licence, latitude = :latitude, longitude= :longitude," +
            " image= :image, updateBy= :updateBy," +
            " updateDate= :updateDate, sent_server= :sent_server WHERE community_id = :id")
    void updateInformation(String name, int id, int geographical_district,
                           int accessibility, int distance, String connected_to_ecg,
                           String date_licence, Double latitude, Double longitude, String image,
                            String updateBy, String updateDate, Boolean sent_server);
}
