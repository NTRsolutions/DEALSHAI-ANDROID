package com.ogma.dealshaiapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.fragment.FragmentIndivisualCategories;
import com.ogma.dealshaiapp.model.FilterOptions;
import com.ogma.dealshaiapp.model.SubCategory;

import java.util.ArrayList;

public class FilterOption extends Dialog implements View.OnClickListener {

    private AppCompatActivity activity;
    private ArrayList<FilterOptions> arrayList;
    private ExpandableListView expandableListView;
    private FilterOptionsAdapter filterOptionsAdapter;
    private TextView tv_btn_apply;
    private TextView tv_btn_cancel;

    public FilterOption(@NonNull AppCompatActivity activity, ArrayList<FilterOptions> arrayList) {
        super(activity);
        this.arrayList = arrayList;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_filter);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setGravity(Gravity.START);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        expandableListView = findViewById(R.id.exlv_filters);
        expandableListView.setGroupIndicator(null);

        tv_btn_apply = findViewById(R.id.tv_btn_apply);
        tv_btn_cancel = findViewById(R.id.tv_btn_cancel);

        filterOptionsAdapter = new FilterOptionsAdapter(getContext(), arrayList);
        expandableListView.setAdapter(filterOptionsAdapter);

        tv_btn_apply.setOnClickListener(this);
        tv_btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_apply:
                break;
            case R.id.tv_btn_cancel:
                dismiss();
                break;
        }
    }

    public class FilterOptionsAdapter extends BaseExpandableListAdapter {

        private ArrayList<FilterOptions> arrayList;
        private Context context;

        FilterOptionsAdapter(Context context, ArrayList<FilterOptions> scheduleClassGroupsArrayList) {
            this.arrayList = scheduleClassGroupsArrayList;
            this.context = context;
        }

        @Override
        public int getGroupCount() {
            return arrayList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (arrayList.get(groupPosition).getSubCatArrLst() != null) {
                return arrayList.get(groupPosition).getSubCatArrLst().size();
            }
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return arrayList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return arrayList.get(groupPosition).getSubCatArrLst().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            View groupView = convertView;
            FilterOptionsAdapter.ViewHolderGroup mHolder;

            if (groupView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                groupView = layoutInflater.inflate(R.layout.dialog_filter_category, parent, false);

                mHolder = new FilterOptionsAdapter.ViewHolderGroup();
                mHolder.tv_group_name = groupView.findViewById(R.id.tv_group_name);
                mHolder.iv_next = groupView.findViewById(R.id.iv_next);
                mHolder.ll_group = groupView.findViewById(R.id.ll_group);
                groupView.setTag(mHolder);
            } else
                mHolder = (FilterOptionsAdapter.ViewHolderGroup) groupView.getTag();

            FilterOptions filterOptions = (FilterOptions) getGroup(groupPosition);
            final String groupId = filterOptions.getCatId();
            final String groupTittle = filterOptions.getCatName();
            final ArrayList<SubCategory> subCategories = filterOptions.getSubCatArrLst();
            mHolder.tv_group_name.setText(" " + groupTittle);

            if (subCategories != null && subCategories.size() > 0) {
                mHolder.tv_group_name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.down_arrow, 0, 0, 0);
            } else
                mHolder.tv_group_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            mHolder.ll_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    FragmentIndivisualCategories fct = new FragmentIndivisualCategories();
                    FragmentManager manager = activity.getSupportFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putString("categoryId", groupId);
                    fct.setArguments(bundle);
                    manager.beginTransaction().replace(R.id.index_frame, fct).commitAllowingStateLoss();
                }
            });

            return groupView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            View childView = convertView;
            FilterOptionsAdapter.ViewHolderChild mHolder;

            if (childView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                childView = layoutInflater.inflate(R.layout.dialog_filter_subcategory, parent, false);
                mHolder = new FilterOptionsAdapter.ViewHolderChild();
                mHolder.tv_child_name = childView.findViewById(R.id.tv_child_name);
                childView.setTag(mHolder);
            } else
                mHolder = (FilterOptionsAdapter.ViewHolderChild) childView.getTag();

            FilterOptions filterOptions = (FilterOptions) getGroup(groupPosition);
            final String groupId = filterOptions.getCatId();
            SubCategory subCategory = (SubCategory) getChild(groupPosition, childPosition);
            final String childId = subCategory.getSubCatId();
            final String title = subCategory.getSubCatName();
            mHolder.tv_child_name.setText(subCategory.getSubCatName());

            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    FragmentIndivisualCategories fct = new FragmentIndivisualCategories();
                    FragmentManager manager = activity.getSupportFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putString("subCategoryId", childId);
                    bundle.putString("subCategoryName", title);
                    fct.setArguments(bundle);
                    manager.beginTransaction().replace(R.id.index_frame, fct).commitAllowingStateLoss();
                }
            });
            return childView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private class ViewHolderGroup {
            TextView tv_group_name;
            ImageView iv_next;
            LinearLayout ll_group;
        }

        private class ViewHolderChild {
            TextView tv_child_name;
        }
    }
}
