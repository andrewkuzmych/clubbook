//
//  FilterMenuTableViewCell.m
//  Clubbook
//
//  Created by Anton Semenyuk on 12/11/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "FilterMenuTableViewCell.h"
#import <QuartzCore/QuartzCore.h>

@implementation FilterMenuTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)awakeFromNib
{
    // Initialization code
    [super awakeFromNib];
}


- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    // Configure the view for the selected state
}

-(void) setSelectedImage:(BOOL) selected {
    self.iconImage.layer.cornerRadius = 10;
    self.iconImage.layer.masksToBounds = YES;
    self.iconImage.image = [[UIImage alloc] init];
    if (!selected) {
       self.iconImage.backgroundColor = [UIColor colorWithWhite:0.810 alpha:1.000];
    }
    else {
        self.iconImage.backgroundColor = [UIColor colorWithRed:0.328 green:0.000 blue:0.458 alpha:1.000];
    }
    
}

@end
