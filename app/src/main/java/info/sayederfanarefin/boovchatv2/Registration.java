package info.sayederfanarefin.boovchatv2;

/**
 * Created by SayedErfan on 8/12/2015.
 */
public class Registration {
    @com.google.gson.annotations.SerializedName("id")
    private String mId;
    @com.google.gson.annotations.SerializedName("registrationId")
    private String mRegistrationId;

    public String getId() { return mId; }
    public final void setId(String id) { mId = id; }

    public String getRegistrationId() { return mRegistrationId; }
    public final void setRegistrationId(String registrationId) { mRegistrationId = registrationId; }
}