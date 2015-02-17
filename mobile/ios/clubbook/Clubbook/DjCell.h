//
//  DjCellTableViewCell.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/17/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface DjCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIImageView *avatarView;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *musicStyleLabel;
@property (weak, nonatomic) IBOutlet UIButton *websiteButton;

@property (strong, nonatomic) NSString* webSiteString;

@end
