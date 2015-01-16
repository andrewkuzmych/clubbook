//
//  NewsFeedCell.m
//  Clubbook
//
//  Created by Anton Semenyuk on 1/14/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "NewsFeedCell.h"
#import "NewsPhotoCell.h"

@implementation NewsFeedCell

-(id) initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {

    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

-(void)setCollectionViewDataSourceDelegate:(id<UICollectionViewDataSource, UICollectionViewDelegate>)dataSourceDelegate index:(NSInteger)index
{
    self.photosView.dataSource = dataSourceDelegate;
    self.photosView.delegate = dataSourceDelegate;
    self.photosView.tag = index;
    
    [self.photosView reloadData];
}

@end
