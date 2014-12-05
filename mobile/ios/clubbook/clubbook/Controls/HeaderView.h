//
//  HeaderView.h
//  Clubbook
//
//  Created by Andrew on 6/30/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CbButton.h"
#import <MapKit/MapKit.h>
#import "AddFriendButton.h"

@interface HeaderView : UICollectionReusableView<UIScrollViewDelegate>


@property (weak, nonatomic) IBOutlet AddFriendButton *yesterdayButton;
@property (weak, nonatomic) IBOutlet CbButton *checkinButton;
@property (weak, nonatomic) IBOutlet UILabel *checkinCountLabel;
@property (weak, nonatomic) IBOutlet UILabel *checkinCountTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *friendsCountLabel;
@property (weak, nonatomic) IBOutlet UILabel *friendsCountTitleLabel;

@property (weak, nonatomic) IBOutlet UILabel *openTodayLabel;
@property (weak, nonatomic) IBOutlet UILabel *workingHoursLabel;
@property (weak, nonatomic) IBOutlet UIImageView *clubAvatarImage;
@property (weak, nonatomic) IBOutlet UILabel *clubNameText;
@property (weak, nonatomic) IBOutlet UILabel *clubAddressText;
@property (weak, nonatomic) IBOutlet UILabel *clubDistanceText;
@property (weak, nonatomic) IBOutlet UIView *clubDetailsView;
@property (weak, nonatomic) IBOutlet UIButton *clubInfoButton;
@property (weak, nonatomic) IBOutlet UILabel *infoWallLabel;

@end
