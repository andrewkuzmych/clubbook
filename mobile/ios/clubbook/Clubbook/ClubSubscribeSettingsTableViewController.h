//
//  ClubSubscribeSettingsTableViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 1/5/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Place.h"
#import "BaseTableViewController.h"

@interface ClubSubscribeSettingsTableViewController : BaseTableViewController
@property (strong, nonatomic) Place* place;
@property (weak, nonatomic) IBOutlet UILabel *clubTitleLabel;
@property (weak, nonatomic) IBOutlet UISwitch *favoriteButton;

@end
