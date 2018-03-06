package com.ogma.dealshaiapp.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

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
    private RequestBody requestBody;
    private Request request;
    private Context context;
    private static final MediaType MEDIA_TYPE_ALL = MediaType.parse("image/*");
    private ProgressDialog progressDialog;
    public WebServiceListener serviceListener;

    public WebServiceHandler(Context context) {
        this.context = context;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(80, TimeUnit.SECONDS)
                .build();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    //User Login Request
    public void loginUser(String mobile, String emailId) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("phone", mobile);
        params.put("email", emailId);
        // params.put("token", String.valueOf(token));
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.LoginApi)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //OTP Received and Checked
    public void checkOTP(String otp, String mobile, String emailId) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("phone", mobile);
        params.put("email", emailId);
        params.put("otp", otp);
        // params.put("token", String.valueOf(token));
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.CheckOtp)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    public void add_name_and_referral_code(String userId, String name, String referral_code) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("user_id", userId);
        params.put("referral_code", referral_code);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.AddName)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Home Page Information
    public void getIndexData(String latitude, String longitude, String userId) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("lat", latitude);
        params.put("long", longitude);
        params.put("user_id", userId);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.IndexData)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Get Cities List
    public void getCities() {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("lat", "");
        params.put("lng", "");
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.City)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Get Areas List Of A City
    public void getCityArea(String cityId) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("city_id", cityId);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.CityArea)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Blind Search Result of Merchant List
    public void getSearchResult(String text) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("text", text);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.SearchProduct)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Details and Coupon List of A Merchant
    public void getDetailsData(String merchant_id) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", merchant_id);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.DetailsData)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Merchant Coupons List
    public void getCoupons(int merchantId) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", String.valueOf(merchantId));
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.Quick_View_Coupons)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Like Merchant Package
    public void hitLike(String merchant_id, String userId) {

        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", merchant_id);
        params.put("user_id", userId);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.HitLikes)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //My Liked Items List
    public void getLikedProduct(String userId) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.MyLikes)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Details from Index Page Banner
    public void getOfferDetailsData(String couponId) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("coupon_id", couponId);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.OfferDetailsData)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Individual Category Merchant's List
    public void getSingleCategoryPackages(String id, String latitude, String longitude, String sortPram) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("category_id", String.valueOf(id));
        params.put("lat", latitude);
        params.put("long", longitude);
        params.put("sort_id", sortPram);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.SingleCategoryPackages)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Foodie Subcategory Merchant's List
    public void getSingleCategoryPackagesFoodie(String subCategoryId, String latitude, String longitude, String sortPram) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("category_id", String.valueOf(subCategoryId));
        params.put("lat", latitude);
        params.put("long", longitude);
        params.put("sort_id", sortPram);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.SingleCategoryPackagesForFoodie)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Dealshai Plus Packages List
    public void getDealshaiData() {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("id", "");
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.DealshaiPlusPackage)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Details of a Package Of Dealshai Plus
    public void getDealshaiPlusDetails(String packageId) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("package_id", packageId);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.DealshaiPlusPackageDetails)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //My Order List
    public void getPurchaseList(String userId) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.Ordered_List)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Purchased Item's Data
    public void getQrcode(String oderId) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("order_id", oderId);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.QRCode)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //List Of Categories of Plankarle
    public void getPlankarleCategories() {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("", "");
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.PlankarleStepOne)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Selected Categories List from Plankarle
    public void getPlankarleLoadTabs(JSONObject category) {
//        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, category.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.PlankarleStepTwo)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Merchant List Of Individual Categories Of PlanKarle
    public void getPlankarleLoadDeals(String categoryId, String subCatId, String searchText) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
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

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.PlankarleStepTwoSub)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Preview of Added Deals in My Plan in Plankarle
    public void getViewPlanData(JSONArray jsonArray) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, JSONArray> params = new HashMap<>();
        params.put("coupons", jsonArray);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.PlankarleViewPlan)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Notification
    public void getNotification(String userId) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.Notification)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    //Get Count Of Unread Notification
    public void getUnreadNotification(String userId) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.UnreadNotification)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    public void getOutlets(String merchantId) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.MoreOutletLocation)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    public void getLocationInfo(String lat, String lng) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("lat", lat);
        params.put("lng", lng);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.GetLocation)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    public void saveUserDetails(String userId, String name, String email, String phone, String date, String gender, File image) {
        RequestBody requestBody = null;
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
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("lat", lat);
        params.put("lng", lng);
        JSONObject parameter = new JSONObject(params);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.GetCurrentLocation)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    public void getOrderDetails(String userId, String totalAmount, JSONArray deals) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("total_price", totalAmount);
            jsonObject.put("deals", deals);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.PrePayment)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    public void getVoucherList(String userId) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.GetVoucherList)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    public void applyPromoCode(String userId, String promoCode, String totalAmount) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("pomo_code", promoCode);
            jsonObject.put("total_amount", totalAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.ApplyPromoCode)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    public void applyVoucher(String userId, String voucherId, String voucherType, String totalAmount) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("voucher_id", voucherId);
            jsonObject.put("type", voucherType);
            jsonObject.put("total_price", totalAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.ApplyVoucher)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    public void completePaytmPayment(String userId, String status, String checksumhash, String bankname, String orderid, String txnamount, String txndate, String mid, String txnid, String respcode, String paymentmode, String banktxnid, String currency, String gatewayname, String respmsg, String voucherId, String voucherType, String promoId, String promoType, String totalAmount) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
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
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.Complete_Paytm_Payment)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    public void completePayment(String userId, String orderid, String txnamount, String voucherId, String voucherType, String promoId, String promoType, String totalAmount) {

        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
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
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.Complete_Payment)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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

    public void getReferralMessage(String userId) {
        progressDialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(ServicesURls.ReferCode)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
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
}