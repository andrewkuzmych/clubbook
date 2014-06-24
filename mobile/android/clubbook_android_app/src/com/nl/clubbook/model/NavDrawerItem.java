package com.nl.clubbook.model;

public class NavDrawerItem {
	
	private String title;
	private int icon;
	private String count = "0";
	// boolean to set visiblity of the counter
    private String profileAvatar;
	private boolean isCounterVisible = false;
    private boolean isProfile = false;
    private String background = "#240E25";


	public NavDrawerItem(String title, int icon){
		this.title = title;
		this.icon = icon;
        // this.background = background;
	}
	
	public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count){
		this.title = title;
		this.icon = icon;
		this.isCounterVisible = isCounterVisible;
		this.count = count;
        // this.background = background;
	}

    public NavDrawerItem(String profile_name, String profile_avatar, boolean isProfile){
        this.title = profile_name;
        this.profileAvatar = profile_avatar;
        this.isProfile = isProfile;
    }

    public String getProfileAvatar() {
        return profileAvatar;
    }

    public String getTitle(){
		return this.title;
	}
	
	public int getIcon(){
		return this.icon;
	}
	
	public String getCount(){
		return this.count;
	}
	
	public boolean getCounterVisibility(){
		return this.isCounterVisible;
	}

    public boolean getProfileVisibility(){
        return this.isProfile;
    }
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setIcon(int icon){
		this.icon = icon;
	}
	
	public void setCount(String count){
		this.count = count;
	}
	
	public void setCounterVisibility(boolean isCounterVisible){
		this.isCounterVisible = isCounterVisible;
	}

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}
