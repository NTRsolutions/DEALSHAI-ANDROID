package com.ogma.dealshaiapp.network;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.api.ServicesURls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebServiceHandler {
    private Context context;
    private static final MediaType MEDIA_TYPE_ALL = MediaType.parse("image/*");
    private Dialog progressDialog;
    public WebServiceListener serviceListener;

    public WebServiceHandler(Context context) {
        this.context = context;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(80, TimeUnit.SECONDS)
                .build();

        progressDialog = new Dialog(context);
        progressDialog.setContentView(View.inflate(context, R.layout.progress_dialog, null));
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCancelable(false);
    }

    //User Login Request
    public void loginUser(String emailId) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("email", emailId);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.LoginApi);
    }

    //OTP Received and Checked
    public void checkOTP(String otp, String emailId) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("email", emailId);
        params.put("otp", otp);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.CheckOtp);
    }

    public void add_name_and_referral_code(String userId, String name, String referral_code, String mobile) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("name", name);
        params.put("phone", mobile);
        params.put("referral_code", referral_code);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.AddName);
    }

    //Home Page Information
    public void getIndexData(String latitude, String longitude, String userId) {
//        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("lat", latitude);
        params.put("long", longitude);
        params.put("user_id", userId);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.IndexData);
    }

    //Get Cities List
    public void getCities() {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("lat", "");
        params.put("lng", "");
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.City);
    }

    //Get Areas List Of A City
    public void getCityArea(String cityId) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("city_id", cityId);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.CityArea);
    }

    //Blind Search Result of Merchant List
    public void getSearchResult(String text) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("text", text);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.SearchProduct);
    }

    //Details and Coupon List of A Merchant
    public void getDetailsData(String merchant_id, String userId) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("merchant_id", merchant_id);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.DetailsData);
    }

    //Merchant Coupons List
    public void getCoupons(int merchantId, String userId) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));
        params.put("merchant_id", String.valueOf(merchantId));
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.Quick_View_Coupons);
    }

    //Like Merchant Package
    public void hitLike(String merchant_id, String userId) {

        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", merchant_id);
        params.put("user_id", userId);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.HitLikes);
    }

    //My Liked Items List
    public void getLikedProduct(String userId) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.MyLikes);
    }

    //Details from Index Page Banner
    public void getOfferDetailsData(String couponId, String userId) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("coupon_id", couponId);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.OfferDetailsData);
    }

    //Individual Category Merchant's List
    public void getSingleCategoryPackages(String id, String latitude, String longitude, String sortPram) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("category_id", String.valueOf(id));
        params.put("lat", latitude);
        params.put("long", longitude);
        params.put("sort_id", sortPram);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.SingleCategoryPackages);
    }

    //Foodie Subcategory Merchant's List
    public void getSingleCategoryPackagesFoodie(String subCategoryId, String latitude, String longitude, String sortPram) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("category_id", String.valueOf(subCategoryId));
        params.put("lat", latitude);
        params.put("long", longitude);
        params.put("sort_id", sortPram);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.SingleCategoryPackagesForFoodie);
    }

    //Dealshai Plus Packages List
    public void getDealshaiData() {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("id", "");
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.DealshaiPlusPackage);
    }

    //Details of a Package Of Dealshai Plus
    public void getDealshaiPlusDetails(String packageId) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("package_id", packageId);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.DealshaiPlusPackageDetails);
    }

    //My Order List
    public void getPurchaseList(String userId) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.Ordered_List);
    }

    //Purchased Item's Data
    public void getQrcode(String oderId) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("order_id", oderId);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.QRCode);
    }

    //List Of Categories of Plankarle
    public void getPlankarleCategories() {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("", "");
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.PlankarleStepOne);
    }

    //Selected Categories List from Plankarle
    public void getPlankarleLoadTabs(JSONObject category) {
        newCall(category, ServicesURls.PlankarleStepTwo);
    }

    //Merchant List Of Individual Categories Of PlanKarle
    public void getPlankarleLoadDeals(String categoryId, String subCatId, String searchText) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("category_id", categoryId);
        if (subCatId == null)
            params.put("sub_category", "");
        else
            params.put("sub_category", subCatId);
        if (searchText == null || searchText.trim().equals(""))
            params.put("search_text", "");
        else
            params.put("search_text", searchText);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.PlankarleStepTwoSub);
    }

    //Preview of Added Deals in My Plan in Plankarle
    public void getViewPlanData(JSONArray jsonArray) {
        progressDialog.show();
        Map<String, JSONArray> params = new HashMap<>();
        params.put("coupons", jsonArray);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.PlankarleViewPlan);
    }

    //Notification
    public void getNotification(String userId) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.Notification);
    }

    //Get Count Of Unread Notification
    public void getUnreadNotification(String userId) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.UnreadNotification);
    }

    public void getOutlets(String merchantId) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.MoreOutletLocation);
    }

    public void getLocationInfo(String lat, String lng) {
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("lat", lat);
        params.put("lng", lng);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.GetLocation);
    }

    public void saveUserDetails(String userId, String name, String email, String phone, String date, String gender, File image) {
        RequestBody requestBody;
        if (image != null) {
            requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("user_id", userId)
                    .addFormDataPart("name", name)
                    .addFormDataPart("email", email)
                    .addFormDataPart("phone", phone)
                    .addFormDataPart("date", date)
                    .addFormDataPart("gender", gender)
                    .addFormDataPart("upload_image", "upload_image.jpg", RequestBody.create(MEDIA_TYPE_ALL, image))
                    .build();
        } else {
            requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("user_id", userId)
                    .addFormDataPart("name", name)
                    .addFormDataPart("email", email)
                    .addFormDataPart("phone", phone)
                    .addFormDataPart("date", date)
                    .addFormDataPart("gender", gender)
                    .build();
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(ServicesURls.EditAndSaveUserData)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                serviceListener.onResponse(response.body().string());
            }
        });
    }

    public void getCurrentLocationInfo(String lat, String lng) {
//        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("lat", lat);
        params.put("lng", lng);
        JSONObject parameter = new JSONObject(params);

        newCall(parameter, ServicesURls.GetCurrentLocation);
    }

    public void getOrderDetails(String userId, String payableAmount, JSONArray deals, int quantity, String totalAmount, String voucherId, String voucherType, String promoId, String promoType) {
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("total_price", payableAmount);
            jsonObject.put("deals", deals);
            jsonObject.put("quantity", quantity);
            jsonObject.put("total_purchase_amount", totalAmount);
            jsonObject.put("voucher_id", voucherId);
            jsonObject.put("voucher_type", voucherType);
            jsonObject.put("promo_id", promoId);
            jsonObject.put("promo_type", promoType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        newCall(jsonObject, ServicesURls.PrePayment);
    }

    public void getVoucherList(String userId) {
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        newCall(jsonObject, ServicesURls.GetVoucherList);
    }

    public void applyPromoCode(String userId, String promoCode, String totalAmount, String promoId, String promoType, String voucherId, String voucherType) {
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("pomo_code", promoCode);
            jsonObject.put("total_amount", totalAmount);
            jsonObject.put("voucher_id", voucherId);
            jsonObject.put("voucher_type", voucherType);
            jsonObject.put("promo_id", promoId);
            jsonObject.put("promo_type", promoType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        newCall(jsonObject, ServicesURls.ApplyPromoCode);
    }

    public void applyVoucher(String userId, String voucherId, String voucherType, String totalAmount, String promoId, String promoType) {
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("total_price", totalAmount);
            jsonObject.put("voucher_id", voucherId);
            jsonObject.put("voucher_type", voucherType);
            jsonObject.put("promo_id", promoId);
            jsonObject.put("promo_type", promoType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        newCall(jsonObject, ServicesURls.ApplyVoucher);
    }

    public void completePaytmPayment(String userId, String status, String checksumhash, String bankname, String orderid, String txnamount, String txndate, String mid, String txnid, String respcode, String paymentmode, String banktxnid, String currency, String gatewayname, String respmsg, String voucherId, String voucherType, String promoId, String promoType, String totalAmount) {
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("STATUS", status);
            jsonObject.put("CHECKSUMHASH", checksumhash);
            jsonObject.put("BANKNAME", bankname);
            jsonObject.put("ORDERID", orderid);
            jsonObject.put("TXNAMOUNT", txnamount);
            jsonObject.put("TXNDATE", txndate);
            jsonObject.put("MID", mid);
            jsonObject.put("TXNID", txnid);
            jsonObject.put("RESPCODE", respcode);
            jsonObject.put("PAYMENTMODE", paymentmode);
            jsonObject.put("BANKTXNID", banktxnid);
            jsonObject.put("CURRENCY", currency);
            jsonObject.put("GATEWAYNAME", gatewayname);
            jsonObject.put("RESPMSG", respmsg);
            jsonObject.put("voucher_id", voucherId);
            jsonObject.put("voucher_type", voucherType);
            jsonObject.put("promo_id", promoId);
            jsonObject.put("promo_type", promoType);
            jsonObject.put("total_purchase_amount", totalAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("completePaymentRequest", String.valueOf(jsonObject));

        newCall(jsonObject, ServicesURls.Complete_Paytm_Payment);

    }

    public void completePayment(String userId, String orderid, String txnamount, String voucherId, String voucherType, String promoId, String promoType, String totalAmount) {

        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("ORDERID", orderid);
            jsonObject.put("TXNAMOUNT", txnamount);
            jsonObject.put("voucher_id", voucherId);
            jsonObject.put("voucher_type", voucherType);
            jsonObject.put("promo_id", promoId);
            jsonObject.put("promo_type", promoType);
            jsonObject.put("total_purchase_amount", totalAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("completePaymentRequest", String.valueOf(jsonObject));

        newCall(jsonObject, ServicesURls.Complete_Payment);
    }

    public void getReferralMessage(String userId) {
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        newCall(jsonObject, ServicesURls.ReferCode);
    }

    private void newCall(JSONObject jsonObject, String api) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

//        OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(80, TimeUnit.SECONDS)
                .build();
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(api)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
                .build();

        Log.e("Request", String.valueOf(jsonObject));

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                serviceListener.onResponse(response.body().string());
            }
        });
    }
}