//
//  FilterMenuTableViewCell.h
//  Clubbook
//
//  Created by Anton Semenyuk on 12/11/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FilterMenuTableViewCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIImageView *iconImage;
@property (weak, nonatomic) IBOutlet UILabel *filterLabel;

-(void) setSelectedImage:(BOOL) selected;

@end
