package com.nl.clubbook.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nl.clubbook.R;
import com.nl.clubbook.model.NavDrawerItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class NavDrawerListAdapter extends ArrayAdapter<NavDrawerItem> {

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
	private Context context;
	private List<NavDrawerItem> navDrawerItems;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();
    int layoutResID;

    //public CustomDrawerAdapter(Context context, int layoutResourceID,
    //                           List<DrawerItem> listItems) {
	public NavDrawerListAdapter(Context context,  int layoutResourceID, List<NavDrawerItem> navDrawerItems){
        super(context, layoutResourceID, navDrawerItems);
		this.context = context;
		this.navDrawerItems = navDrawerItems;
        this.layoutResID = layoutResourceID;

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_list_image)
                .showImageForEmptyUri(R.drawable.default_list_image)
                .showImageOnFail(R.drawable.default_list_image)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
	}

	/*@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}*/

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            //convertView = mInflater.inflate(R.layout.drawer_list_item, null);
            convertView = mInflater.inflate(layoutResID, parent, false);
        }

        RelativeLayout profile = (RelativeLayout) convertView.findViewById(R.id.profile);
        RelativeLayout menu_item = (RelativeLayout) convertView.findViewById(R.id.menu_item);

        if(navDrawerItems.get(position).getProfileVisibility())
        {
            profile.setVisibility(View.VISIBLE);
            menu_item.setVisibility(View.GONE);

            ImageView profile_image = (ImageView) convertView.findViewById(R.id.profile_image);
            profile_image.setImageResource(navDrawerItems.get(position).getIcon());

            TextView profile_name =(TextView) convertView.findViewById(R.id.profile_name);
            profile_name.setText(navDrawerItems.get(position).getTitle());

            if (navDrawerItems.get(position).getProfileAvatar() != null)
                imageLoader.displayImage(navDrawerItems.get(position).getProfileAvatar(), profile_image, options, animateFirstListener);

        } else {
            profile.setVisibility(View.GONE);
            menu_item.setVisibility(View.VISIBLE);

            ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
            TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
            TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

            imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
            txtTitle.setText(navDrawerItems.get(position).getTitle());

            // displaying count
            // check whether it set visible or not
            if(navDrawerItems.get(position).getCounterVisibility()){
                txtCount.setText(navDrawerItems.get(position).getCount());
            }else{
                // hide the counter view
                txtCount.setVisibility(View.GONE);
            }

        }
        
        return convertView;
	}

}
