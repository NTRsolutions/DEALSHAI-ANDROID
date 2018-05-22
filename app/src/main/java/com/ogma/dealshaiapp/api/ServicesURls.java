package com.ogma.dealshaiapp.api;

public class ServicesURls {
    //    public static String BaseUrlLocal = "http://10.1.1.11/dealshai/api/";
//    private static String BaseUrl = "http://www.ogmaconceptions.com/demo/dealshai/api/";
//    private static String BaseUrl = "https://www.dealshai.in/api/";
    private static String BaseUrl = "https://dealshai.in/apiv1/";

    public static String LoginApi = BaseUrl + "user_login";
    public static String CheckOtp = BaseUrl + "check_otp";
    public static String AddName = BaseUrl + "add_name";
    public static String HitLikes = BaseUrl + "like";
    public static String IndexData = BaseUrl + "home";
    public static String DetailsData = BaseUrl + "deals_view";
    public static String OfferDetailsData = BaseUrl + "offer_page";
    public static String Quick_View_Coupons = BaseUrl + "quick_view";
    public static String SingleCategoryPackages = BaseUrl + "inner_page";
    public static String SingleCategoryPackagesForFoodie = BaseUrl + "subcategory";
    public static String DealshaiPlusPackage = BaseUrl + "package";
    public static String DealshaiPlusPackageDetails = BaseUrl + "dealshai_plus_step1";
    public static String PlankarleStepOne = BaseUrl + "plankarle_step1";
    public static String PlankarleStepTwo = BaseUrl + "plankarle_step2_get_category";
    public static String PlankarleStepTwoSub = BaseUrl + "plankarle_step2_get_merchant";
    public static String PlankarleViewPlan = BaseUrl + "view_plankarle_select_plan";
    public static String PrePayment = BaseUrl + "prepayment";
    public static String Complete_Payment = BaseUrl + "complete_payment";
    public static String Complete_Paytm_Payment = BaseUrl + "complete_paytm_payment";
    public static String Ordered_List = BaseUrl + "payment_details";
    public static String QRCode = BaseUrl + "payment_item_details";
    public static String City = BaseUrl + "city";
    public static String CityArea = BaseUrl + "city_area";
    public static String SearchProduct = BaseUrl + "search";
    public static String MyLikes = BaseUrl + "my_likes";
    public static String UnreadNotification = BaseUrl + "notification";
    public static String Notification = BaseUrl + "read_notification";
    public static String MoreOutletLocation = BaseUrl + "outlet_location";
    public static String EditAndSaveUserData = BaseUrl + "edit_user";
    public static String GetCurrentLocation = BaseUrl + "get_location_landing";
    public static String GetLocation = BaseUrl + "get_location";
    public static String GetVoucherList = BaseUrl + "voucher_list";
    public static String ApplyPromoCode = BaseUrl + "apply_pomo_code";
    public static String ApplyVoucher = BaseUrl + "apply_voucher";
    public static String ReferCode = BaseUrl + "refer_code";
}