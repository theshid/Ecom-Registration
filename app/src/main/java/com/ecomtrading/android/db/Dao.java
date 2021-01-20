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


    @Insert
    void insertCommunityInfo(CommunityInformation information);

    @Delete
    void deleteCommunityInfo(CommunityInformation information);

    @Query("UPDATE community_information SET community_name = :name WHERE community_id = :id")
    void updateInformation(String name, int id);
}
