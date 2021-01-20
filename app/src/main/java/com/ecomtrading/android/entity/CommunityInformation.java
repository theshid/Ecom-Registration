package com.ecomtrading.android.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ecomtrading.android.converters.DateConverter;
import com.ecomtrading.android.converters.TimestampConverter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity(tableName = "community_information")
public class CommunityInformation implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("remoteID")
    int community_id;
    @SerializedName("communityName")
    @Expose
    String community_name;
    @SerializedName("geographicalDistrict")
    @Expose
    int geographical_district;
    @SerializedName("accessibility")
    @Expose
    int accessibility;
    @SerializedName("distanceToECOM")
    @Expose
    int distance;
    @SerializedName("connectedToECG")
    @Expose
    String connected_to_ecg;
    @SerializedName("licenseDate")
    @Expose
    @TypeConverters(DateConverter.class)
    Long date_licence;
    @SerializedName("latitude")
    @Expose
    Double latitude;
    @SerializedName("longitude")
    @Expose
    Double longitude;
    @SerializedName("photo")
    @Expose
    String image;
    @SerializedName("createdBy")
    @Expose
    String createdBy;
    @SerializedName("createdDate")
    @Expose
    @TypeConverters(TimestampConverter.class)
    String createdDate;
    @SerializedName("updateBy")
    @Expose
    String updateBy;
    @SerializedName("updateDate")
    @Expose
    @TypeConverters(TimestampConverter.class)
    String updateDate;

    public CommunityInformation(int community_id, String community_name, int geographical_district, int accessibility, int distance, String connected_to_ecg, Long date_licence, Double latitude, Double longitude, String image, String createdBy, String createdDate, String updateBy, String updateDate) {
        this.community_id = community_id;
        this.community_name = community_name;
        this.geographical_district = geographical_district;
        this.accessibility = accessibility;
        this.distance = distance;
        this.connected_to_ecg = connected_to_ecg;
        this.date_licence = date_licence;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.updateBy = updateBy;
        this.updateDate = updateDate;
    }

    @Ignore
    public CommunityInformation(String community_name, int geographical_district, int accessibility, int distance, String connected_to_ecg, Long date_licence, Double latitude, Double longitude, String image, String createdBy, String createdDate, String updateBy, String updateDate) {
        this.community_name = community_name;
        this.geographical_district = geographical_district;
        this.accessibility = accessibility;
        this.distance = distance;
        this.connected_to_ecg = connected_to_ecg;
        this.date_licence = date_licence;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.updateBy = updateBy;
        this.updateDate = updateDate;
    }

    public int getCommunity_id() {
        return community_id;
    }

    public void setCommunity_id(int community_id) {
        this.community_id = community_id;
    }

    public String getCommunity_name() {
        return community_name;
    }

    public void setCommunity_name(String community_name) {
        this.community_name = community_name;
    }

    public int getGeographical_district() {
        return geographical_district;
    }

    public void setGeographical_district(int geographical_district) {
        this.geographical_district = geographical_district;
    }

    public int getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(int accessibility) {
        this.accessibility = accessibility;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getConnected_to_ecg() {
        return connected_to_ecg;
    }

    public void setConnected_to_ecg(String connected_to_ecg) {
        this.connected_to_ecg = connected_to_ecg;
    }

    public Long getDate_licence() {
        return date_licence;
    }

    public void setDate_licence(Long date_licence) {
        this.date_licence = date_licence;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setAge(String image) {
        this.image = image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
