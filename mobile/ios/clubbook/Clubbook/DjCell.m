//
//  DjCellTableViewCell.m
//  Clubbook
//
//  Created by Anton Semenyuk on 2/17/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "DjCell.h"

@implementation DjCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
- (IBAction)handleWebsiteButton:(id)sender {
    if (self.webSiteString != nil) {
      [[UIApplication sharedApplication] openURL:[NSURL URLWithString:self.webSiteString]];
    }
}

@end
