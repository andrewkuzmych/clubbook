//
//  MainMenuCollectionViewCell.h
//  Clubbook
//
//  Created by Anton Semenyuk on 12/29/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MainMenuCollectionViewCell : UICollectionViewCell
@property (weak, nonatomic) IBOutlet UILabel *menuLabel;
@property (weak, nonatomic) IBOutlet UIView *circleView;
@property (weak, nonatomic) IBOutlet UIImageView *icon;
@property (weak, nonatomic) IBOutlet UILabel *notificationNumberLabel;
@property (weak, nonatomic) IBOutlet UILabel *bigNotificationNumber;

-(void)setIconBackground:(UIColor*) color;

@end
